package com.junit.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyFirstJUnitJupiterTests {

    private final Calculator calculator = new Calculator();

    @BeforeAll
    static void setUp(){
        System.out.println("start to test....");
    }

    @Test
    void addition() {
        assertEquals(2, calculator.add(1, 1));
    }
}
