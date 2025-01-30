package hokumei.sys.matchfriends;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("hokumei.sys.matchfriends.mapper")
@EnableScheduling
//@EnableAspectJAutoProxy
public class UserManageSysApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManageSysApplication.class, args);
	}
}
