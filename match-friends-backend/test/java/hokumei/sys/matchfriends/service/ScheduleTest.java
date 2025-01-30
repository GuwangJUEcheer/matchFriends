package hokumei.sys.matchfriends.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootTest
public class ScheduleTest {

    //第一种实现 使用Timer类
    @Test
    public void test1() throws ParseException {
      Timer timer = new Timer();
        // 实现定时任务(hello, timer!) - 具体时间点
        String str = "2025-01-21 16:34:59";
        Date start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
        //TimerTask是一个抽象类
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("test Timer");
            }
        }, start);
    }

    @Test
    public void test2() throws ParseException {
        Timer timer = new Timer();
        // 实现定时任务(hello, timer!) - 具体时间点
        String str = "2025-01-21 16:34:59";
        Date start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
        //TimerTask是一个抽象类
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("test Timer");
            }
        }, start);
    }

}
