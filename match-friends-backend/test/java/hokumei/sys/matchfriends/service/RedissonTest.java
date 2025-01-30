package hokumei.sys.matchfriends.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void testRedisson() {
        List<String> list = new ArrayList<String>();
        list.add("hahaha");
        list.get(0);
        list.remove(0);

        //List
        RList<Object> redissonList = redissonClient.getList("name-test");
        redissonList.add("aaa");
        redissonList.add("bbb");
        System.out.println(redissonList.get(1));
        //删除下标为1的 redis里面
        redissonList.remove(1);
    }

    @Test
    void testWatchDog() {
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            // 尝试获取锁，只允许一个线程获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 释放当前线程持有的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
