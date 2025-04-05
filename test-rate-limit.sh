#!/bin/bash

# 테스트 설정
BASE_URL=${API_URL:-"http://localhost:8080"}
TEST_ENDPOINT="/api/v1/categories"
REQUESTS_PER_TEST=35     # 테스트당 보낼 요청 수 (30개 이후에 레이트 리밋 초과 예상)
REQUEST_INTERVAL=0.05    # 요청 간격 (초)
RESULT_FILE="bucket4j_rate_limit_test_result.log"

# 함수: 컬러 출력
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 함수: 타임스탬프 생성
timestamp() {
  date +"%Y-%m-%d %H:%M:%S"
}

# 시작 메시지
echo -e "${GREEN}===== Bucket4j Rate Limit 테스트 시작 ($(timestamp)) =====${NC}" | tee $RESULT_FILE
echo -e "서버 URL: ${BASE_URL}" | tee -a $RESULT_FILE
echo -e "테스트 엔드포인트: ${TEST_ENDPOINT}" | tee -a $RESULT_FILE
echo -e "요청 수: ${REQUESTS_PER_TEST}" | tee -a $RESULT_FILE
echo -e "Bucket4j 설정: 익명 사용자 30 요청/분, 인증 사용자 60 요청/분" | tee -a $RESULT_FILE
echo "" | tee -a $RESULT_FILE

# 함수: 익명 사용자로 API 요청 테스트
test_anonymous_rate_limit() {
  local user_agent=$1
  local requests=$2
  local success_count=0
  local rate_limited_count=0
  
  echo -e "${YELLOW}===== 익명 사용자 테스트 시작 (User-Agent: $user_agent) =====${NC}" | tee -a $RESULT_FILE
  
  for ((i=1; i<=$requests; i++)); do
    echo -e "요청 $i 전송 중..." | tee -a $RESULT_FILE
    
    # 응답 저장할 임시 파일
    response_headers=$(mktemp)
    response_body=$(mktemp)
    
    # API 요청 및 응답 정보 저장 (응답 내용도 저장)
    http_code=$(curl -s -w "%{http_code}" \
                -D "${response_headers}" \
                -o "${response_body}" \
                -H "User-Agent: $user_agent" \
                -v \
                -H "Content-Type: application/json" \
                "${BASE_URL}${TEST_ENDPOINT}" 2>&1)
    
    # 결과 분석
    if [ "$http_code" == "200" ]; then
      # 모든 중요 헤더 추출
      remaining=$(grep -i "X-Rate-Limit-Remaining" "${response_headers}" | cut -d' ' -f2 | tr -d '\r' || echo "N/A")
      debug_client_id=$(grep -i "X-Debug-ClientId" "${response_headers}" | cut -d' ' -f2 | tr -d '\r' || echo "N/A")
      
      echo -e "${GREEN}성공 (200 OK)${NC}" | tee -a $RESULT_FILE
      echo -e "${BLUE}남은 요청 수: ${remaining:-N/A}${NC}" | tee -a $RESULT_FILE
      echo -e "${BLUE}클라이언트 ID: ${debug_client_id:-N/A}${NC}" | tee -a $RESULT_FILE
      
      # 모든 응답 헤더 기록 (디버깅 목적)
      echo -e "${BLUE}응답 헤더:${NC}" | tee -a $RESULT_FILE
      cat "${response_headers}" | tee -a $RESULT_FILE
      
      ((success_count++))
    elif [ "$http_code" == "429" ]; then
      retry_after=$(grep -i "X-Rate-Limit-Retry-After-Seconds" "${response_headers}" | cut -d' ' -f2 | tr -d '\r' || echo "N/A")
      
      echo -e "${RED}Rate Limit 초과 (429 Too Many Requests)${NC}" | tee -a $RESULT_FILE
      echo -e "${RED}재시도 가능 시간: ${retry_after:-N/A}초 후${NC}" | tee -a $RESULT_FILE
      
      # 응답 내용 표시
      echo -e "${RED}응답 내용:${NC}" | tee -a $RESULT_FILE
      cat "${response_body}" | tee -a $RESULT_FILE
      
      ((rate_limited_count++))
      
      # 이미 Rate Limit이 확인되면 몇 개만 더 테스트하고 중단
      if [ $rate_limited_count -ge 3 ]; then
        echo -e "${YELLOW}Rate Limit 확인됨. 테스트 조기 종료${NC}" | tee -a $RESULT_FILE
        rm -f "${response_headers}" "${response_body}"
        break
      fi
    else
      echo -e "기타 응답: $http_code" | tee -a $RESULT_FILE
    fi
    
    rm -f "${response_headers}" "${response_body}"
    sleep $REQUEST_INTERVAL
  done
  
  echo -e "\n${YELLOW}요약: 성공 $success_count, Rate Limited $rate_limited_count${NC}" | tee -a $RESULT_FILE
  
  # Rate Limit 동작 확인
  if [ $rate_limited_count -gt 0 ]; then
    echo -e "${GREEN}✓ Bucket4j Rate Limit 정상 작동 확인${NC}" | tee -a $RESULT_FILE
  else
    echo -e "${RED}✗ Rate Limit 미동작 또는 제한에 도달하지 않음${NC}" | tee -a $RESULT_FILE
  fi
  
  echo "" | tee -a $RESULT_FILE
}

# 함수: JWT 토큰으로 인증된 사용자로 API 요청 테스트
test_authenticated_rate_limit() {
  local jwt_token=$1
  local user_agent=$2
  local requests=$3
  local success_count=0
  local rate_limited_count=0
  
  echo -e "${YELLOW}===== 인증된 사용자 테스트 시작 (User-Agent: $user_agent) =====${NC}" | tee -a $RESULT_FILE
  
  for ((i=1; i<=$requests; i++)); do
    echo -e "요청 $i 전송 중..." | tee -a $RESULT_FILE
    
    # API 요청 및 응답 정보 저장 (JWT 토큰 포함)
    response_headers=$(mktemp)
    http_code=$(curl -s -o /dev/null -w "%{http_code}" \
                -D "${response_headers}" \
                -H "Content-Type: application/json" \
                -H "User-Agent: $user_agent" \
                -H "Authorization: Bearer ${jwt_token}" \
                "${BASE_URL}${TEST_ENDPOINT}")
    
    # 결과 분석
    if [ "$http_code" == "200" ]; then
      # 남은 요청 수 추출
      remaining=$(grep -i "X-Rate-Limit-Remaining" "${response_headers}" | cut -d' ' -f2 | tr -d '\r' || echo "N/A")
      echo -e "${GREEN}성공 (200 OK) - 남은 요청 수: ${remaining:-N/A}${NC}" | tee -a $RESULT_FILE
      ((success_count++))
    elif [ "$http_code" == "429" ]; then
      echo -e "${RED}Rate Limit 초과 (429 Too Many Requests)${NC}" | tee -a $RESULT_FILE
      ((rate_limited_count++))
      
      # 이미 Rate Limit이 확인되면 몇 개만 더 테스트하고 중단
      if [ $rate_limited_count -ge 5 ]; then
        echo -e "${YELLOW}Rate Limit 확인됨. 테스트 조기 종료${NC}" | tee -a $RESULT_FILE
        rm -f "${response_headers}"
        break
      fi
    else
      echo -e "기타 응답: $http_code" | tee -a $RESULT_FILE
    fi
    
    rm -f "${response_headers}"
    sleep $REQUEST_INTERVAL
  done
  
  echo -e "\n${YELLOW}요약: 성공 $success_count, Rate Limited $rate_limited_count${NC}" | tee -a $RESULT_FILE
  
  # Rate Limit 동작 확인
  if [ $rate_limited_count -gt 0 ]; then
    echo -e "${GREEN}✓ Bucket4j Rate Limit 정상 작동 확인${NC}" | tee -a $RESULT_FILE
  else
    echo -e "${RED}✗ Rate Limit 미동작 또는 제한에 도달하지 않음${NC}" | tee -a $RESULT_FILE
  fi
  
  echo "" | tee -a $RESULT_FILE
}

# JWT 토큰 획득 함수 (로그인 테스트)
get_jwt_token() {
  local username=$1
  local password=$2
  local user_agent=$3
  
  echo -e "${YELLOW}로그인 요청 중... (사용자: $username)${NC}" | tee -a $RESULT_FILE
  
  # 로그인 요청으로 JWT 토큰 획득
  login_response=$(curl -s -X POST \
                  -H "Content-Type: application/json" \
                  -H "User-Agent: $user_agent" \
                  -d "{\"username\":\"${username}\",\"password\":\"${password}\"}" \
                  "${BASE_URL}/api/v1/users/login")
  
  # JSON 응답에서 토큰 추출 (jq가 없으면 간단한 문자열 처리로 대체)
  if command -v jq &> /dev/null; then
    token=$(echo "$login_response" | jq -r '.accessToken')
  else
    # 간단한 문자열 처리 (jq가 없는 경우)
    token=$(echo "$login_response" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
  fi
  
  if [ -z "$token" ] || [ "$token" == "null" ]; then
    echo -e "${RED}로그인 실패 또는 토큰을 찾을 수 없음${NC}" | tee -a $RESULT_FILE
    return 1
  fi
  
  echo -e "${GREEN}JWT 토큰 획득 성공${NC}" | tee -a $RESULT_FILE
  echo "$token"
}

# 서로 다른 클라이언트를 시뮬레이션하기 위한 User-Agent 생성
generate_user_agent() {
  echo "Mozilla/5.0 TestAgent-$1-$(date +%s%N | sha256sum | head -c 8)"
}

# 1. 익명 사용자 테스트 (Bucket4j 익명 사용자 설정: 30개/분)
echo -e "${GREEN}===== 익명 사용자 Rate Limit 테스트 (30개/분 제한) =====${NC}" | tee -a $RESULT_FILE

# 첫 번째 클라이언트로 테스트 - 35개 요청 (제한 초과)
USER_AGENT1=$(generate_user_agent "client1")
test_anonymous_rate_limit "$USER_AGENT1" $REQUESTS_PER_TEST

# 테스트 결과 요약
echo -e "${GREEN}===== 테스트 완료 ($(timestamp)) =====${NC}" | tee -a $RESULT_FILE
echo -e "결과는 $RESULT_FILE 파일에서 확인할 수 있습니다." 