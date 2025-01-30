package hokumei.sys.matchfriends.service;

import hokumei.sys.matchfriends.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    //将key作为String
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test() {
        //获得列表对象
        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setPlanetcode("00");

        //增
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("testKey1","testValue1");
        valueOperations.set("testKey2","testValue2");
        valueOperations.set("testKey3",user);


        List<String> list = new ArrayList<>();
        list.add("testValue1");
        list.add("testValue2");
        ListOperations listOperations = redisTemplate.opsForList();

        //查
        Object testValue1 = valueOperations.get("testKey1");
        Object testValue2= valueOperations.get("testKey2");
        Object testValue3 = valueOperations.get("testKey3");
        Assertions.assertTrue(((String)testValue1).equals("testValue1"));
        Assertions.assertTrue(((String)testValue2).equals("testValue2"));
    }
}
