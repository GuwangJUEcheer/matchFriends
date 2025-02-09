package hokumei.sys.matchfriends.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import hokumei.sys.matchfriends.common.BaseResponse;
import hokumei.sys.matchfriends.common.ErrorCode;
import hokumei.sys.matchfriends.common.ResultUtils;
import hokumei.sys.matchfriends.constant.enums.TeamStatusEnum;
import hokumei.sys.matchfriends.domain.TeamAddRequest;
import hokumei.sys.matchfriends.domain.TeamJoinRequest;
import hokumei.sys.matchfriends.domain.TeamQuitRequest;
import hokumei.sys.matchfriends.domain.TeamUpdateRequest;
import hokumei.sys.matchfriends.exception.BusinessException;
import hokumei.sys.matchfriends.model.Team;
import hokumei.sys.matchfriends.model.User;
import hokumei.sys.matchfriends.model.UserTeam;
import hokumei.sys.matchfriends.model.dto.TeamQuery;
import hokumei.sys.matchfriends.model.vo.TeamUserVO;
import hokumei.sys.matchfriends.service.TeamService;
import hokumei.sys.matchfriends.service.UserService;
import hokumei.sys.matchfriends.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team,teamAddRequest);
        User loginUser = userService.getLoginUser(request);
        long teamId = teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 更新队伍信息
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) throws Exception {
        User loginUser = userService.getLoginUser(request);
        boolean update = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取我创建的队伍
     */
    @GetMapping("/hasJoined")
    public BaseResponse<List<Team>> getJoinedTeam(HttpServletRequest request) throws Exception {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "找不到对应用户");
        }
        List<Team> hasJoinedTeam = teamService.getAllJoinedTeam(loginUser.getId());
        return ResultUtils.success(hasJoinedTeam);
    }

    /**
     * 删除队伍
     */
    @GetMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteTeam(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean remove = teamService.removeById(id);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍删除失败");
        }
        return ResultUtils.success(true);
    }


    /**
     * 根据 ID 获取队伍详情
     */
    @GetMapping("/get/{teamId}")
    public BaseResponse<Team> getTeamById(@PathVariable("teamId") Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍不存在");
        }
        return ResultUtils.success(team);
    }

    /**
     * 全量查询
     * @param teamQuery 封装请求参数类
     * @return 返回列表
     */
    @PostMapping("/list")
    public BaseResponse<List<TeamUserVO>> getListAll(@RequestBody TeamQuery teamQuery,HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {

        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //只允许管理员进行访问
        boolean isAdmin = userService.isAdmin(request);
        int status = teamQuery.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(teamStatusEnum == null){
            teamStatusEnum = TeamStatusEnum.PUBLIC;
        }
        if(!isAdmin && !teamStatusEnum.equals(TeamStatusEnum.PUBLIC)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        List<TeamUserVO> teamList = teamService.listTeam(teamQuery);
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).toList();
        try {
            User loginUser = userService.getLoginUser(request);
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("user_id", loginUser.getId());
            if(!CollectionUtils.isEmpty(teamIdList)){
                userTeamQueryWrapper.in("team_id", teamIdList);
            }
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);

            // 已加入的队伍 id 集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream()
                    .map(UserTeam::getTeamId)
                    .collect(Collectors.toSet());

            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        //查询队伍Id ->队伍人数
        // 在查询加入队伍的用户信息（人数）
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        if(!CollectionUtils.isEmpty(teamIdList)){
            userTeamJoinQueryWrapper.in("team_id", teamIdList);
        }
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        Map<Long, List<UserTeam>> longListMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team->team.setMemberNum(longListMap.getOrDefault(team.getId(),new ArrayList<>()).size()));
        return ResultUtils.success(teamList);
    }


    /**
     * 全量查询
     * @return 返回全部队伍
     */
    @GetMapping("/all")
    public BaseResponse<List<Team>> getListAll(HttpServletRequest request) throws Exception {
        User loginUser  = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        List<Team> teamList = teamService.getAllTeam();
        return ResultUtils.success(teamList);
    }


    /**
     * 分页查询
     * @param teamQuery 封装请求参数类
     * @return 返回列表
     */
    @PostMapping("/list/page")
    public BaseResponse<List<Team>> getListByPage(@RequestBody TeamQuery teamQuery) {
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());

        /**
         * 接受两个参数  team 目标对象 teamQuery 源对象
         */
        try{
            BeanUtils.copyProperties(team,teamQuery);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //自动根据字段来查 一个不好地方 不支持模糊查询
        QueryWrapper<Team> queryWrapper= new QueryWrapper<Team>();
        Page<Team> teamList = teamService.page(page,queryWrapper);
        return ResultUtils.success(teamList.getRecords());
    }
    /**
     * 用户加入队伍
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest,HttpServletRequest request) throws Exception {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请事先登录");
        }

        boolean joined = teamService.joinTeam(teamJoinRequest,loginUser);
        if (!joined) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 用户退出队伍
     */
    @PostMapping("/leave")
    public BaseResponse<Boolean> leaveTeam(@RequestBody TeamQuitRequest teamQuitRequest,HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean left = teamService.removeUserFromTeam(teamQuitRequest, loginUser);
        if (!left) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 用户已经创建的队伍
     */
    @GetMapping("/hasCreated")
    public BaseResponse<List<Team>> leaveTeam(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("user_id",loginUser.getId());
        List<Team> teamList = teamService.list(queryWrapper);
        return ResultUtils.success(teamList);
    }
}
