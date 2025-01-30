package hokumei.sys.matchfriends.service;

import hokumei.sys.matchfriends.mapper.UserTeamMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OtherTest {
	@Resource
	UserTeamMapper userTeamMapper;

	@Test
	public void test() {
		System.out.println(userTeamMapper.getAllTeamMembers("team1"));
	}
}
