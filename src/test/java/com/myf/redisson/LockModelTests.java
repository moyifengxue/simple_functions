package com.myf.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
            lock.lock(10, TimeUnit.SECONDS);
            doSomething(true, lock, 15L, Thread.currentThread().getName());
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
        }
    }
}
