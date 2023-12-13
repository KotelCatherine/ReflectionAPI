package ru.geekbrains.threading;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


public class MyTest {
    @BeforeEach
    void beforeEachFirstTest() {
        System.out.println("beforeEach start");
    }

    @AfterEach
    void afterEachFirstTest() {
        System.out.println("afterEach start");
    }

    @Test(order = 1)
     void firstTest() {
        System.out.println("firstTest запущен");
    }

    @Test(order = -1)
    void secondTest() {
        System.out.println("secondTest запущен");
    }

    @Skip
    @Test
    void thirdTest() {
        System.out.println("thirdTest запущен");
    }


}
