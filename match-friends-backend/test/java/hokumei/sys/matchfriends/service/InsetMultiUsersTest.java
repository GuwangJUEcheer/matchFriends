package hokumei.sys.matchfriends.service;

import hokumei.sys.matchfriends.UserManageSysApplication;
import hokumei.sys.matchfriends.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest(classes = UserManageSysApplication.class)
public class InsetMultiUsersTest {

    @Resource
    private UserService userService;

    //CPU 密集型 内存里面运算 线程数=CPU数目 -1 留一个给主线程
    //IO密集型 写磁盘 网络运输 线程数可以>CPU数目
    private ExecutorService executors = new ThreadPoolExecutor(60,1000,1000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));

//    @Scheduled(fixedRate = 5000)
    @Test
    public  void insertUsers(){
        List<User> userList = new ArrayList<User>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for(int i = 0; i < INSERT_NUM; i++){
            User user = new User();
            user.setUsername("User" + i);
            user.setAvatarurl("https://example.com/avatar/" + i);
            user.setGender(i % 2); // 性别：0 或 1
            user.setPassword("password" + i);
            user.setPhone("1234567890" + i);
            user.setEmail("user" + i + "@example.com");
            user.setIsvalid(1); // 是否有效：1
            user.setCreatetime(new Date());
            user.setUpdatetime(new Date());
            user.setIsdelete(0); // 是否删除：0
            user.setRoleid(i % 3); // 角色：0, 1, 2
            user.setPlanetcode("P" + i);
            user.setTags("tag1,tag2");
            user.setProfile("User " + i + " profile information.");
            userList.add(user);
        }
        userService.saveBatch(userList);
        //other executes
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 使用CompletableFuture进行异步插入
     */
    @Test
    public  void insertUsersByFuture() {
        List<User> userList = new ArrayList<User>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //分1000条进行查询
        int i = 0;
        final int INSERT_NUM = 10000;
        List<CompletableFuture<Void>> futureList = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executors = Executors.newFixedThreadPool(10);
        for (int j = 0; j < 10; j++) {
            while (true) {
                i++;
                User user = new User();
                user.setUsername("User" + i);
                user.setAvatarurl("https://example.com/avatar/" + i);
                user.setGender(i % 2); // 性别：0 或 1
                user.setPassword("password" + i);
                user.setPhone("1234567890" + i);
                user.setEmail("user" + i + "@example.com");
                user.setIsvalid(1); // 是否有效：1
                user.setCreatetime(new Date());
                user.setUpdatetime(new Date());
                user.setIsdelete(0); // 是否删除：0
                user.setRoleid(i % 3); // 角色：0, 1, 2
                user.setPlanetcode("P" + i);
                user.setTags("tag1,tag2");
                user.setProfile("User " + i + " profile information.");
                userList.add(user);
                if (i % 10000 == 0) {
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> {
                        userService.saveBatch(userList,10000);
                    }, executors
            );
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        //other executes
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}

