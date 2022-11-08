package com.reactive.demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.reactive.demo.Utils.functionM;
import static com.reactive.demo.Utils.functionN;

public class AsynchronousDemo2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        triggerA();

        //处理其他任务，完全不用管理任务A。
       Thread.sleep(3000);
    }

    public static void triggerA() {
        long start = System.currentTimeMillis();
        System.out.println("编排务A线程ID为:"+Thread.currentThread().getId());

        CompletableFuture<Integer> taskBFuture = CompletableFuture.supplyAsync(()->{
            System.out.println("执行任务B的当前线程ID:"+Thread.currentThread().getId());
            return taskB();
        });

        CompletableFuture<Integer> taskCFuture = CompletableFuture.supplyAsync(()->{
            System.out.println("执行任务C的当前线程ID:"+Thread.currentThread().getId());
            return taskC();
        });

        //回调执行任务D
        taskBFuture.thenCombineAsync(taskCFuture,(m,n)->{
            try {
                System.out.println("执行任务C的当前线程ID:"+Thread.currentThread().getId());
                Thread.sleep(1000);
                System.out.println("耗时："+(System.currentTimeMillis()-start));
                System.out.println("taskA计算结果："+(m+n));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });

    }

    public static int taskB() {
        return functionM();
    }

    public static int taskC() {
        return functionN();
    }
}
