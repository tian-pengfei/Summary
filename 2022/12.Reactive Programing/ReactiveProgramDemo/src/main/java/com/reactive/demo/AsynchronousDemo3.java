package com.reactive.demo;

import com.ea.async.Async;

import java.util.concurrent.CompletableFuture;

public class AsynchronousDemo3 {
    static{
        Async.init();
    }
    public static void main(String[] args) throws InterruptedException {

        Async.await(CompletableFuture.allOf(
                CompletableFuture.runAsync(AsynchronousDemo3::executeTaskA),
                CompletableFuture.runAsync(AsynchronousDemo3::executeTaskB),
                CompletableFuture.runAsync(AsynchronousDemo3::executeTaskC)));

        Thread.sleep(3000);
    }

    public static void executeTaskA() {

        int a= Async.await(CompletableFuture.supplyAsync(()->{
            System.out.println("执行前部分任务的线程为："+Thread.currentThread().getId());
            return 1;
        }));

        int b =0;
        //中间阻塞

        b= Async.await(CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2;
        }));

         System.out.println("线程："+Thread.currentThread().getId()+"执行A后半部分任务...");
    }

    public static void executeTaskB() {
        System.out.println("线程："+Thread.currentThread().getId()+"执行B前半部分任务...");

        //中间阻塞
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("线程："+Thread.currentThread().getId()+"执行B后半部分任务...");
    }
    public static void executeTaskC() {
        System.out.println("线程："+Thread.currentThread().getId()+"执行C前半部分任务...");

        //中间阻塞
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("线程："+Thread.currentThread().getId()+"执行C后半部分任务...");
    }
}
