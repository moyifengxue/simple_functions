package com.myf.controller;

import com.myf.entity.HelloWord;
import com.myf.redisson.annotation.DistributedLock;
import com.myf.service.IHelloWordService;
import com.myf.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author myf
 * @since 2022-07-27
 */
@RestController
@RequestMapping("/hello-word")
@RequiredArgsConstructor
public class HelloWordController {

    private final IHelloWordService helloWordService;

    private final RedisUtils redisUtils;
    private final RedisTemplate<String, HelloWord> helloWordRedisCache;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/hello")
    @DistributedLock(keys = "hello")
    public String Hello() throws InterruptedException {
        System.out.println(new Date());
        TimeUnit.SECONDS.sleep(10L);
        return helloWordService.getById(1).getName();
    }

    @GetMapping("/hello/{value}")
    @DistributedLock(keys = "#{#apple.getArray()}",keyPrefix = "redisson")
    public String Hello(@PathVariable String value) throws InterruptedException {
        TimeUnit.SECONDS.sleep(10L);
        redisUtils.set("hello",value,100);
        HelloWord word = helloWordService.getById(1);
        redisTemplate.opsForValue().set("helloObj",word, 10, TimeUnit.SECONDS);
        helloWordRedisCache.opsForValue().set("helloHello",word, 10, TimeUnit.SECONDS);
        return "设置成功";
    }

    @GetMapping("/getHello")
    public String getHello(){
        return redisUtils.get("hello");
    }

}
