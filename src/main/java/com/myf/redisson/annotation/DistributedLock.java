package com.myf.redisson.annotation;

import com.myf.redisson.constants.LockModel;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    /**
     * 锁的模式:默认自动模式,当参数只有一个使用 REENTRANT 参数多个使用MULTIPLE
     *
     * @return 锁模式
     */
    LockModel lockModel() default LockModel.AUTO;

    /**
     * 增加key的space前缀
     */
    String keySpace() default "";

    /**
     * 如果keys有多个AUTO模式使用联锁
     *
     * @return keys
     */
    String[] keys() default {};

    /**
     * 租赁时间，默认为-1，即为无限续租，只有在不设置租赁时间的情况下看门狗机制才会生效
     * @return 租赁时间
     */
    long leaseTime() default -1;

    /**
     * 等待时间，默认为-1，即为一直等待
     * @return 等待时间
     */
    long waitTime() default -1;
}
