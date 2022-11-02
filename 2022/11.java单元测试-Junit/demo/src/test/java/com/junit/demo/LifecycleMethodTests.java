package com.junit.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LifecycleMethodTests {


    @BeforeAll
    static void initAll() {
        println("init all test...");
    }

    @BeforeEach
    void init() {
        println("init each test...");
    }

    @Test
    void test_1() {
        println("execute test 1");
    }
    @Test
    void test_2() {
        println("execute test 2");
    }

    @AfterEach
    void tearDown() {
        println("tear down each test...");
    }

    @AfterAll
    static void tearDownAll() {
        println("tear down all test...");
    }


    static void println(String message){
        System.out.println(message);
    }
}
