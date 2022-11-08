package com.reactive.demo;

import com.ea.async.Async;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static com.reactive.demo.Utils.functionM;
import static com.reactive.demo.Utils.functionN;

public class AyTest {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        int m = functionM();
        int n = functionN();
        //Complex calculations
        Thread.sleep(1000);
        System.out.println("计算结果："+(m+n));
        System.out.println("耗时："+(System.currentTimeMillis()-start));

    }
}
