package com.junit.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestInstanceDemo {
    private final Calculator calculator = new Calculator();

    @BeforeAll
    void  setUp(){
        System.out.println("start to test....");
    }

    @Test
    void addition() {
        System.out.println(this);
        assertEquals(2, calculator.add(1, 1));
    }

    @Test
    void division () {
        System.out.println(this);
        assertEquals(2, calculator.divide(4, 2));
    }
}
