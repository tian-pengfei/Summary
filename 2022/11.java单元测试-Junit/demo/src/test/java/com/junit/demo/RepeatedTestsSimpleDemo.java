package com.junit.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class RepeatedTestsSimpleDemo {

    @BeforeEach
    void beforeEach() {
        System.out.println("init each test...");
    }

    @RepeatedTest(3)
    void repeatedTest() {
        System.out.println("test...");
    }

    @AfterEach
    void tearDown() {
        System.out.println("tear down each test...");
    }
}
