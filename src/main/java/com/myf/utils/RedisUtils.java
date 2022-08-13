package com.myf.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * @author myf
 */
@Component
public class RedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // Key（键），简单的key-value操作

    /**
     * 实现命令：TTL key，以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)。
     *
     * @param key 键
     * @return 值
     */
    public Long ttl(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    /**
     * 实现命令：expire 设置过期时间，单位秒
     *
     * @param key 键
     */
    public void expire(String key, long timeout) {
        stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 是否存在key
     * @param key 键
     */
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 实现命令：INCR key，增加key一次
     *
     * @param key 键
     * @return 值
     */
    public Long incr(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 实现命令：KEYS pattern，查找所有符合给定模式 pattern的 key
     */
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    /**
     * 实现命令：DEL key，删除一个key
     *
     * @param key 键
     */
    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    // String（字符串）

    /**
     * 实现命令：SET key value，设置一个key-value（将字符串值 value关联到 key）
     *
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 实现命令：SET key value EX seconds，设置key-value和超时时间（秒）
     *
     * @param key 键
     * @param value 值
     * @param timeout （以秒为单位）
     */
    public void set(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 实现命令：GET key，返回 key所关联的字符串值。
     *
     * @param key 键
     * @return value
     */
    public String get(String key) {
        return (String) stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 批量查询，对应mget
     *
     * @param keys 键
     * @return 值
     */
    public List<String> mget(List<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量查询，管道pipeline
     *
     * @param keys 键
     * @return 值
     */
    public List<Object> batchGet(List<String> keys) {

        List<Object> result = stringRedisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection src = (StringRedisConnection) connection;

                for (String k : keys) {
                    src.get(k);
                }
                return null;
            }
        });

        return result;
    }


    // Hash（哈希表）

    /**
     * 实现命令：HSET key field value，将哈希表 key中的域 field的值设为 value
     *
     * @param key 键
     * @param field field
     * @param value value
     */
    public void hset(String key, String field, Object value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 实现命令：HGET key field，返回哈希表 key中给定域 field的值
     *
     * @param key 键
     * @param field field
     * @return 值
     */
    public String hget(String key, String field) {
        return (String) stringRedisTemplate.opsForHash().get(key, field);
    }

    /**
     * 实现命令：HDEL key field [field ...]，删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     *
     * @param key 键
     * @param fields fields
     */
    public void hdel(String key, Object... fields) {
        stringRedisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 实现命令：HGETALL key，返回哈希表 key中，所有的域和值。
     *
     * @param key 键
     * @return map
     */
    public Map<Object, Object> hgetall(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    // List（列表）

    /**
     * 实现命令：LPUSH key value，将一个值 value插入到列表 key的表头
     *
     * @param key 键
     * @param value 值
     * @return 执行 LPUSH命令后，列表的长度。
     */
    public Long lpush(String key, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 实现命令：LPOP key，移除并返回列表 key的头元素。
     *
     * @param key 键
     * @return 列表key的头元素。
     */
    public String lpop(String key) {
        return (String) stringRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * 实现命令：RPUSH key value，将一个值 value插入到列表 key的表尾(最右边)。
     *
     * @param key 键
     * @param value 值
     * @return 执行 LPUSH命令后，列表的长度。
     */
    public long rpush(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 实现命令：LINDEX key index
     *
     * @return index对应的值
     */
    public String lindex(String key, Integer index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    /**
     * 实现命令：LTRIM key start end 修剪list
     * 清空list：ltrim key 1 0（ltrim key start end 中的start要比end大即可，数值且都为正数。）
     */
    public void ltrim(String key, Long start, Long end) {
        stringRedisTemplate.opsForList().trim(key, start, end);
    }


    /**
     * 实现命令：LLEN key 获取list长度
     */
    public Long llen(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    /**
     * 如果key不存在那么放入value
     *
     * @param key 键
     * @param value 值
     * @param timeout 时间
     * @return boolean
     * @return void
     * @author yabin.zhang
     * @date 2021/11/30 14:04
     */
    public Boolean setnx(String key, String value, long timeout) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 向set中添加元素
     * @param key set的key
     * @param value set的value
     */
    public void sAdd(String key, String value){
        stringRedisTemplate.opsForSet().add(key, value);
    }

    /**
     * set中所有数据
     * @param key set的key
     * @return set中所有数据
     */
    public Set<String> sMembers (String key){
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 删除并返回set中的一个元素
     * @param key set的key
     * @return set中的一个元素
     */
    public String sPop(String key){
        return stringRedisTemplate.opsForSet().pop(key);
    }

    /**
     * 获取key的大小
     * @param key set的key
     * @return key的大小
     */
    public Long sCard(String key){
        return stringRedisTemplate.opsForSet().size(key);
    }
}
