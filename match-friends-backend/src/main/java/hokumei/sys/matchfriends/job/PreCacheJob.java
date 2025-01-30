package hokumei.sys.matchfriends.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hokumei.sys.matchfriends.model.User;
import hokumei.sys.matchfriends.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务定义类
 * @author GU WANGJUE
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    //配置中心 重点用户
    private List<Long> userList = Arrays.asList(1L,2L,3L,4L,5L,6L,7L,8L,9L);
    @Scheduled(cron = "0 0 13 * * ?")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("match friends:user:lock");

        try{
            /**
             * waitTime 等待时间
             * releaseTime 释放时间
             */
            //只有一个线程可以拿到锁
            if(lock.tryLock(0,30000,TimeUnit.SECONDS)){
                for(Long userId : userList){
                    String userRedisKey = String.format("match friends:user:recommend:%s",userId);
                    //进行分页查询
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    Page<User> userListPerPage = userService.page(new Page<>(1,20),wrapper);
                    redisTemplate.opsForValue().set(userRedisKey,userListPerPage,3000, TimeUnit.SECONDS);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
