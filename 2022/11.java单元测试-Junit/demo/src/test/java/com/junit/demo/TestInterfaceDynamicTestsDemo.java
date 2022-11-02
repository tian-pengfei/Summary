package com.junit.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static com.junit.demo.DynamicTestsDemo.isPalindrome;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public interface TestInterfaceDynamicTestsDemo {
    @TestFactory
    default Stream<DynamicTest> dynamicTestsForPalindromes() {
        return Stream.of("racecar", "radar", "mom", "dad")
                .map(text -> dynamicTest(text, () -> Assertions.assertTrue(isPalindrome(text))));
    }
}
