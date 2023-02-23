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
//        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "gy", 10, TimeUnit.SECONDS);
//        if(!result)return  "err";

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
//            stringRedisTemplate.delete(lockKey);
        }

        return "end";

    }
}
