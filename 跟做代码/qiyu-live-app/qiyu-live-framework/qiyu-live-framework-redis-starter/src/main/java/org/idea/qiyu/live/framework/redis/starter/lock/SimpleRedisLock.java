package org.idea.qiyu.live.framework.redis.starter.lock;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁setnx实现，适用于大多数情况，解决了误删锁问题和原子性问题
 * 但还存在以下问题：
 *      重入问题
 *      不可重试
 *      超时释放
 *      主从一致性
 * 如需解决这些极低概率问题，请使用Redission
 * 
 *  <!--hutool-->
 *  <dependency>
 *      <groupId>cn.hutool</groupId>
 *      <artifactId>hutool-all</artifactId>
 *      <version>5.7.17</version>
 *  </dependency>
 *  
 *  public interface ILock {
 *      /**
 *      * 尝试获取锁
 *      * @param timeoutSec 锁持有的超时时间（秒），过期后自动释放
 *      * @return true代表获取锁成功；false代表获取锁失败
 *      * /
 *      boolean tryLock(Long timeoutSec);
 *      /**
 *      * 释放锁
 *      * /
 *      void unlock();
 *  }
 *  
 *  unlock.lua:（新建在resources目录下）
 *  -- 这里的 KEYS[1] 就是锁的key，这里的ARGV[1] 就是当前线程标示
 *  -- 获取锁中的标示，判断是否与当前线程标示一致
 *  if (redis.call('GET', KEYS[1]) == ARGV[1]) then
 *    -- 一致，则删除锁
 *    return redis.call('DEL', KEYS[1])
 *  end
 *  -- 不一致，则直接返回
 */
public class SimpleRedisLock implements ILock{
    public static final String KEY_PREFIX = "lock:";
    /**
     * 使用public static final的UUID作为JVM的区分，同一个JVM获取到的SimpleRedisLock实例ID_PREFIX相同
     * 用于作为锁的value的前缀，避免不同JVM下threadId相同的情况下锁被别的线程误删
     */
    public static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";
    private String name;
    private StringRedisTemplate stringRedisTemplate;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    //初始化lua脚本，避免重复初始化
    public static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public boolean tryLock(Long timeoutSec) {
        // 获取锁
        /**
         * UUID用于区分JVM，threadId用于区分同一个JVM内的不同线程
         * UUID保证不同JVM内相同userId和相同ThreadId的线程拿到锁
         * threadId保证同一个JVM内相同userId的线程拿到锁
         */
        String threadID = ID_PREFIX + Thread.currentThread().getId();
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + name, threadID, timeoutSec, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(success);
    }

    /**
     * 使用lua脚本保证比锁删锁的原子性
     */
    @Override
    public void unlock() {
        //调用lua脚本
        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(KEY_PREFIX + name),
                ID_PREFIX + Thread.currentThread().getId()
        );
    }

    /**
     * 下面方法还存在比锁和释放锁之间的原子性问题
     * 所以采用lua脚本实现原子性操作，因为调用lua脚本只需要一行代码
     */
    /*@Override
    public void unlock() {
        //获取线程标识
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        //获取锁中的标识（value）
        String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
        //判断标识是否一致
        if (threadId.equals(id)) {
            //释放锁
            stringRedisTemplate.delete(KEY_PREFIX + name);
        }
    }*/
}
