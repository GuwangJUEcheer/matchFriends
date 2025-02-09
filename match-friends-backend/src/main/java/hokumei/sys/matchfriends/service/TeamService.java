package hokumei.sys.matchfriends.service;

import com.baomidou.mybatisplus.extension.service.IService;
import hokumei.sys.matchfriends.domain.TeamJoinRequest;
import hokumei.sys.matchfriends.domain.TeamQuitRequest;
import hokumei.sys.matchfriends.domain.TeamUpdateRequest;
import hokumei.sys.matchfriends.model.Team;
import hokumei.sys.matchfriends.model.User;
import hokumei.sys.matchfriends.model.dto.TeamQuery;
import hokumei.sys.matchfriends.model.vo.TeamUserVO;

import java.util.List;


/**
* @author 17685
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2025-01-28 15:15:07
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team 队伍情报
     * @return 队伍Id
     */
  long addTeam(Team team, User loginUser);

    /**
     * 根据列表查询队伍
     */
    List<TeamUserVO> listTeam(TeamQuery teamQuery);

	boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) throws Exception;

	boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser) throws Exception;

	boolean removeUserFromTeam(TeamQuitRequest request, User loginUser);

	boolean deleteTeam(Long id, User loginUser);

	List<Team> getAllTeam();

	List<Team> getAllJoinedTeam(Long userId);

}
