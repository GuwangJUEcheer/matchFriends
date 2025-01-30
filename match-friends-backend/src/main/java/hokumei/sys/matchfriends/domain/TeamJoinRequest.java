package hokumei.sys.matchfriends.domain;

import lombok.Data;

/**
 * 用户加入队伍Bean
 */
@Data
public class TeamJoinRequest {

	private Long teamId;

	private String password;
}
