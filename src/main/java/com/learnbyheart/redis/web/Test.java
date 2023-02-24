package com.learnbyheart.redis.web;

import com.learnbyheart.redis.config.RedisConf;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class Test {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    @RequestMapping("/test")
    public String test(){
        String lockKey = "lock:102";
        // setIf的缺点是不安全 因为这个时间的设置不准 可能会造成误差 （之前的锁可能超时了 还没释放 锁被另一个线程给获取了 就可能会导致死锁）
        // 设置key 作为锁标记 指定唯一用户标识作为value 确保同一个时间只有一个客户端进程获得锁
        // 设置过期时间 防止因为系统异常导致key没有被删除 出现死锁
//        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "gy", 10, TimeUnit.SECONDS);
//        if(!result)return  "err";


        //使用redisson的好处
        // 里面有一个watchdog机制 可以很好的解决锁续期的问题 能够灵活掌握锁使用的时间
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();//可重入锁 因为会记录当前的线程的Id

        try {
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock+"");

            } else {
                System.out.println("剩余不足");
            }
        }finally {
            lock.unlock();
            //要把key删除 不然一直锁着会死锁
//            stringRedisTemplate.delete(lockKey);
        }

        return "end";

    }
}
