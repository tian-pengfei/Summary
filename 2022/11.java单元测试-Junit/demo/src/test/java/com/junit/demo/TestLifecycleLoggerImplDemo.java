package com.junit.demo;

import org.junit.jupiter.api.RepeatedTest;

public class TestLifecycleLoggerImplDemo implements TestLifecycleLogger{

    @RepeatedTest(5)
    void repeatedTest() {
        System.out.println("test...");
    }
}
