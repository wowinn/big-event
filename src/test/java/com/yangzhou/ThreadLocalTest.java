package com.yangzhou;

import org.junit.jupiter.api.Test;

public class ThreadLocalTest {
    @Test
    public void testThreadLocalSetAndGet() {
        //提供一个ThreadLocal对象
        ThreadLocal tl = new ThreadLocal();

        //开启两个线程
        new Thread(()-> {
            tl.set("111");
            System.out.println(Thread.currentThread().getName() + ": "+ tl.get());
            System.out.println(Thread.currentThread().getName() + ": "+ tl.get());
            System.out.println(Thread.currentThread().getName() + ": "+ tl.get());
        }, "blue").start();
        new Thread(()-> {
            tl.set("2222");
            System.out.println(Thread.currentThread().getName() + ": "+ tl.get());
            System.out.println(Thread.currentThread().getName() + ": "+ tl.get());
            System.out.println(Thread.currentThread().getName() + ": "+ tl.get());
        }, "red").start();
    }
}
