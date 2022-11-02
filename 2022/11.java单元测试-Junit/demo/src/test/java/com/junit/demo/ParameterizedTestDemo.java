package com.junit.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParameterizedTestDemo {

    @BeforeEach
    void beforeEach() {
        System.out.println("init each test...");
    }

    @ParameterizedTest
    @ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
    void palindromes(String candidate) {
        System.out.println("test...");
        assertTrue(StringUtils.isNotBlank(candidate));
    }

    @AfterEach
    void tearDown() {
        System.out.println("tear down each test...");
    }


}
