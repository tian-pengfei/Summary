package com.reactive.demo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.reactive.demo.Utils.functionM;
import static com.reactive.demo.Utils.functionN;

public class MultithreadingDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        ExecutorService threadpool = Executors.newCachedThreadPool();
        Future<Integer> FutureTaskB = threadpool.submit(MultithreadingDemo::TaskB);
        Future<Integer> FutureTaskC = threadpool.submit(MultithreadingDemo::TaskC);

        int m = FutureTaskB.get();
        int n = FutureTaskC.get();

        int result = TaskD(m, n);
        System.out.println("计算结果：" + result);
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
        threadpool.shutdown();
    }

    public static int TaskB() {
        return functionM();
    }

    public static int TaskC() {
        return functionN();
    }


    public static int TaskD(int m, int n) throws InterruptedException {
        Thread.sleep(1000);
        return m + n;
    }


}
