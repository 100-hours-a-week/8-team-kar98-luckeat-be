package com.luckeat.luckeatbackend.category.manual;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryManualTest {

    @Test
    @DisplayName("간단한 테스트 케이스")
    void simpleTest() {
        assertThat(1 + 1).isEqualTo(2);
    }
    
    @Test
    @DisplayName("문자열 테스트")
    void stringTest() {
        String category = "한식";
        assertThat(category).isEqualTo("한식");
    }
} 