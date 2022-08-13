package com.myf.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author zgx
 */
@SpringBootTest
public class LockModelTests {

    @Autowired
    RedissonClient redissonClient;

    /**
     * 可重入锁
     */
    @Test
    void reentrantLock(){
        // 工作时间15s,注意看Redis中的ddl会在过去1/3时间后续约。
        new Thread(()->{
            RLock lock = redissonClient.getLock("testLock");
            lock.lock();
            doSomething(true, lock, 15L, Thread.currentThread().getName());
        }).start();
        // 租赁时间10s,设置了租赁时间，看门狗失效，工作时间15s,释放锁会报IllegalMonitorStateException
        new Thread(()->{
            RLock lock = redissonClient.getLock("testLock");
            lock.lock(5, TimeUnit.SECONDS);
            doSomething(true, lock, 20L, Thread.currentThread().getName());
        }).start();
        // 等待时间45S,未设置租赁时间，看门狗生效
        new Thread(()->{
            RLock lock = redissonClient.getLock("testLock");
            boolean lockFlag = false;
            try {
                lockFlag = lock.tryLock(45L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            doSomething(lockFlag, lock, 15L, Thread.currentThread().getName());
        }).start();
        // 等待时间50S,租赁时间5S,工作时间10S,释放锁会报IllegalMonitorStateException
        new Thread(()->{
            RLock lock = redissonClient.getLock("testLock");
            boolean lockFlag = false;
            try {
                lockFlag = lock.tryLock(50L,5L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            doSomething(lockFlag, lock, 10L, Thread.currentThread().getName());
        }).start();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    /**
     * 公平锁
     * @throws InterruptedException
     */
    @Test
    void fairLock() {
        // 租赁时间5s,设置了租赁时间，看门狗失效，工作时间50s,释放锁会报IllegalMonitorStateException
        new Thread(()->{
            RLock lock = redissonClient.getFairLock("testLock");
            lock.lock(5, TimeUnit.SECONDS);
            doSomething(true, lock, 50L, Thread.currentThread().getName());
        }).start();
        // 工作时间15s,注意看Redis中的ddl会在过去1/3时间后续约。
        new Thread(()->{
            RLock lock = redissonClient.getFairLock("testLock");
            sleepThread(1);
            lock.lock();
            doSomething(true, lock, 15L, Thread.currentThread().getName());
        }).start();
        // 等待时间45S,未设置租赁时间，看门狗生效
        new Thread(()->{
            RLock lock = redissonClient.getFairLock("testLock");
            boolean lockFlag = false;
            try {
                sleepThread(2);
                lockFlag = lock.tryLock(100L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            doSomething(lockFlag, lock, 15L, Thread.currentThread().getName());
        }).start();
        // 等待时间50S,租赁时间5S,工作时间10S,释放锁会报IllegalMonitorStateException
        new Thread(()->{
            RLock lock = redissonClient.getFairLock("testLock");
            boolean lockFlag = false;
            try {
                sleepThread(3);
                lockFlag = lock.tryLock(50L,5L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            doSomething(lockFlag, lock, 10L, Thread.currentThread().getName());
        }).start();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    @Test
    void multiLockTest(){
        RLock lock = redissonClient.getLock("testLock");
        RLock lock2 = redissonClient.getLock("testLock2");
        RLock lock3 = redissonClient.getLock("testLock3");
        RedissonMultiLock multiLock = new RedissonMultiLock(lock, lock2, lock3);
        // 同时加锁：testLock testLock2 testLock3
        // 所有的锁都上锁成功才算成功。
        try {
            boolean tryLock = multiLock.tryLock(1, TimeUnit.SECONDS);
            doSomething(tryLock, multiLock, 10, Thread.currentThread().getName());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void redLockTest(){
        new Thread(()->{
            RLock lock = redissonClient.getLock("testLock");
            lock.lock();
            doSomething(true, lock, 100, Thread.currentThread().getName());
        }).start();
        sleepThread(1);
        RLock lock = redissonClient.getLock("testLock");
        RLock lock2 = redissonClient.getLock("testLock2");
        RLock lock3 = redissonClient.getLock("testLock3");
        RedissonRedLock redLock = new RedissonRedLock(lock, lock2, lock3);
        boolean flag = redLock.tryLock();
        doSomething(flag, redLock, 10, Thread.currentThread().getName());
    }

    @Test
    void readWriteLockTest(){
        new Thread(()->{
            RLock writeLock = redissonClient.getReadWriteLock("testLock").writeLock();
            writeLock.lock();
            doSomething(true,writeLock,10,Thread.currentThread().getName());
        }).start();
        sleepThread(1);
        new Thread(()->{
            RLock readLock = redissonClient.getReadWriteLock("testLock").readLock();
            readLock.lock();
            doSomething(true,readLock,10,Thread.currentThread().getName());
        }).start();
        new Thread(()->{
            RLock readLock = redissonClient.getReadWriteLock("testLock").readLock();
            readLock.lock();
            doSomething(true,readLock,10,Thread.currentThread().getName());
        }).start();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    @Test
    void semaphoreLockTest(){
        RSemaphore testLock = redissonClient.getSemaphore("testLock");
        int permits = testLock.availablePermits();
        testLock.addPermits(2-permits);
        new Thread(()->{
            RSemaphore semaphore = redissonClient.getSemaphore("testLock");
            try {
                semaphore.acquire();
                System.out.println("获取到一个许可"+Thread.currentThread().getName());
                sleepThread(10);
                semaphore.release();
                System.out.println("释放一个许可"+Thread.currentThread().getName());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(()->{
            RSemaphore semaphore = redissonClient.getSemaphore("testLock");
            try {
                semaphore.acquire();
                System.out.println("获取到一个许可"+Thread.currentThread().getName());
                sleepThread(10);
                semaphore.release();
                System.out.println("释放一个许可"+Thread.currentThread().getName());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(()->{
            RSemaphore semaphore = redissonClient.getSemaphore("testLock");
            try {
                semaphore.acquire();
                System.out.println("获取到一个许可"+Thread.currentThread().getName());
                sleepThread(10);
                semaphore.release();
                System.out.println("释放一个许可"+Thread.currentThread().getName());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    @Test
    void permitExpirableSemaphoreLockTest() {
        RPermitExpirableSemaphore expirableSemaphore = redissonClient.getPermitExpirableSemaphore("PermitExpirableSemaphore");
        int permits = expirableSemaphore.availablePermits();
        expirableSemaphore.addPermits(2-permits);
        // 获取一个信号，有效期只有2秒钟。
        try {
            String permitId = expirableSemaphore.acquire(2, TimeUnit.SECONDS);
            System.out.println(permitId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            sleepThread(5);
            String permitId = expirableSemaphore.acquire();
            System.out.println(permitId);
            expirableSemaphore.release(permitId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    @Test
    void countDownLatchTest(){
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("CountDownLatch");
        countDownLatch.trySetCount(1);
        new Thread(()->{
            RCountDownLatch downLatch = redissonClient.getCountDownLatch("CountDownLatch");
            sleepThread(10);
            downLatch.countDown();
        }).start();
        try {
            countDownLatch.await();
            System.out.println("主线程休眠结束");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleepThread(long sleepTime){
        try {
            TimeUnit.SECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void doSomething(boolean lock,RLock rLock,long workTime,String threadName) {
        if(lock){
            System.out.printf("线程：%s，获取到了锁%n",threadName);
            try{
                try {
                    TimeUnit.SECONDS.sleep(workTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }finally {
                rLock.unlock();
                System.out.printf("线程：%s，释放了锁%n",threadName);
            }
        }else {
            System.out.printf("线程：%s，加锁失败%n",threadName);
        }
    }
}
