package hokumei.sys.matchfriends.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import hokumei.sys.matchfriends.model.UserTeam;
import hokumei.sys.matchfriends.model.vo.UserVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 17685
* @description 针对表【user_team(用户-队伍关系表)】的数据库操作Mapper
* @createDate 2025-01-28 15:14:05
* @Entity generator.model.UserTeam
*/
public interface UserTeamMapper extends BaseMapper<UserTeam> {

	@Select(value = "SELECT distinct team.user_id,user.* FROM team LEFT JOIN user_team t ON team.id = t.team_id  \n" +
			"left join user on user.id = t.user_id WHERE team.name = #{name}")
	public List<UserVO> getAllTeamMembers(@Param(value="name") String name);
}




