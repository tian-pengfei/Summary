package com.reactive.demo;

import javafx.util.Pair;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.reactive.demo.Utils.functionM;
import static com.reactive.demo.Utils.functionN;

public class AsynchronousDemo {

    static ExecutorService  threadpool = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // Run a task specified by a Runnable Object asynchronously.
        CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                // Simulate a long-running Job
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                System.out.println("I'll run in a separate thread than the main thread.");
            }
        });
        TimeUnit.SECONDS.sleep(2);
    }

    public static void taskA(){
        long start = System.currentTimeMillis();
        System.out.println("执行taskA");
        CompletableFuture<Pair<Integer,Integer>>executingOperation = triggerOperation();

        executingOperation.whenCompleteAsync((pair, throwable) -> {
            try {
                Thread.sleep(100);
                System.out.println("taskA 执行结果："+pair.getKey()+pair.getValue());
                System.out.println("耗时：" + (System.currentTimeMillis() - start));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Pair<Integer,Integer>>  triggerOperation() {

        CompletableFuture<Integer> executingTaskA = CompletableFuture.supplyAsync(Utils::functionM);

        CompletableFuture<Integer> executingTaskB = CompletableFuture.supplyAsync(Utils::functionN);

        CompletableFuture<Pair<Integer,Integer>>executingTaskBC  =  executingTaskA
                .thenCombine(executingTaskB, Pair::new);

        return executingTaskBC;
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

    public static void taskZ() throws InterruptedException {
        Thread.sleep(4000);
    }

}
