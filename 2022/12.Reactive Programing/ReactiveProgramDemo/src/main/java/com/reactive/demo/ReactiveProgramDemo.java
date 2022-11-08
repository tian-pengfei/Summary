package com.reactive.demo;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.reactive.demo.Utils.functionM;
import static com.reactive.demo.Utils.functionN;

public class ReactiveProgramDemo {

    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();

        Mono<Integer> monoB =
                Mono.fromCallable(ReactiveProgramDemo::taskB).publishOn(Schedulers.boundedElastic());

        Mono<Integer> monoC =
                Mono.fromCallable(ReactiveProgramDemo::taskC).publishOn(Schedulers.boundedElastic());


        Mono.zip(monoB,monoC).subscribeOn(Schedulers.boundedElastic()).subscribe(nums->{
            System.out.println("计算结果为："+taskD(nums.getT1(),nums.getT2()));
            System.out.println("tasA执行用时："+(System.currentTimeMillis()-start));
        });

        Thread.sleep(5000);
    }

    public static int taskB() {
        System.out.println("执行任务B...,当前线程ID"+Thread.currentThread().getId());
        return functionM();
    }

    public static int taskC() {
        System.out.println("执行任务C...,当前线程ID"+Thread.currentThread().getId());
        return functionN();
    }
    public static int taskD(int n1,int n2){
        System.out.println("执行任务D...,当前线程ID"+Thread.currentThread().getId());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return n1+n2;
    }
}
