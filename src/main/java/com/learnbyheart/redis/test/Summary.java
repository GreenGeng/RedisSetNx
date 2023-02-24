package com.learnbyheart.redis.test;

public class Summary {
    /**
     * 总结
     *  setnx key value （set if key not exists）
     *  如果key 不存在就把key的值设置成value
     *  存在就不设置
     *
     *  redisson原理
     *  指定一个key作为标记 存入redis  指定一个唯一用户标识作为value
     *  当key不存在时候 就设置客户获得锁
     *  设置一个过期时间 防止死锁
     *  处理完要清除key释放锁
     *  watchdog机制很好的解决来过期时间的问题 预防死锁 可以给锁续期
     *
     * 线程一获取锁 加锁成功 就有一个watchdog 每隔10s看一下是否要续期
     * 线程二就会自旋去获取锁
     *
     *
     * @Todo后期看下watchdog怎么实现的
     *
     *
     *
     */
}
