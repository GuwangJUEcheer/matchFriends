package hokumei.sys.matchfriends.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hokumei.sys.matchfriends.common.ErrorCode;
import hokumei.sys.matchfriends.constant.UserConstant;
import hokumei.sys.matchfriends.constant.enums.TeamStatusEnum;
import hokumei.sys.matchfriends.domain.TeamJoinRequest;
import hokumei.sys.matchfriends.domain.TeamQuitRequest;
import hokumei.sys.matchfriends.domain.TeamUpdateRequest;
import hokumei.sys.matchfriends.exception.BusinessException;
import hokumei.sys.matchfriends.mapper.TeamMapper;
import hokumei.sys.matchfriends.mapper.UserTeamMapper;
import hokumei.sys.matchfriends.model.Team;
import hokumei.sys.matchfriends.model.User;
import hokumei.sys.matchfriends.model.UserTeam;
import hokumei.sys.matchfriends.model.dto.TeamQuery;
import hokumei.sys.matchfriends.model.vo.TeamUserVO;
import hokumei.sys.matchfriends.model.vo.UserVO;
import hokumei.sys.matchfriends.service.TeamService;
import hokumei.sys.matchfriends.service.UserService;
import hokumei.sys.matchfriends.service.UserTeamService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 17685
* &#064;description  针对表【team(队伍表)】的数据库操作Service实现
* &#064;createDate  2025-01-28 15:15:07
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

	@Resource
	private UserTeamMapper userTeamMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addTeam(Team team, User user) {
        //1 请求参数是否为空
        if(team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //2 是否登录
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        final long userId = user.getId();
        //3 校验信息
        // 队伍人数必须在 `> 1 且 <= 20` 的范围内。
        int teamNumber = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(teamNumber < 1 || teamNumber > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }

        //4 队伍名称
        String name = Optional.ofNullable(team.getName()).orElse("");
        if(StringUtils.isBlank(name)|| name.length()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称不满足要求");
        }

        //5 描述 长度不能超过 `512`。
        String description = Optional.ofNullable(team.getDescription()).orElse("");
        if(StringUtils.isBlank(description)|| description.length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"描述不满足要求");
        }

        //6 状态校验
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if(statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"状态异常");
        }

        String passWord = Optional.ofNullable(team.getPassword()).orElse("");
        if(TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(passWord) || passWord.length()>32)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置错误");
        }

        //7 超时时间要在当前时间之后
        Date expireTime = Optional.ofNullable(team.getExpireTime()).orElse(new Date());
        if(new Date().before(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"时间设置错误");
        }
        //todo 这里有并发问题 有bug要改
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        long count = this.count(queryWrapper);
        if(count >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超过最大组队数");
        }

        //8 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if(!result || teamId == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍创建失败");
        }

        //9 插入到用户信息表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setCreateTime(new Date());
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍关联失败");
        }
        return teamId;
    }

	@SneakyThrows
	@Override
	public List<TeamUserVO> listTeam(TeamQuery teamQuery) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }

            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }

            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }

            // 补充 maxNum 判断逻辑
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("max_num", maxNum);
            }

            // 补充 userId 判断逻辑
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("user_id", userId);
            }

            // 补充 status 判断逻辑
            Integer status = teamQuery.getStatus();
            if (status != null && status >= 0) { // 假设 status >= 0 为有效值
                queryWrapper.eq("status", status);
            }
        }
        //过期时间大于现在时间
        // 不展示已过期的队伍
       // expireTime IS NULL OR expireTime > NOW()
        queryWrapper.and(qw -> qw.lt("expireTime", new Date()).or().isNull("expireTime"));

        List<Team> list = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for(Team teamItem : list){
              TeamUserVO teamUserVO = new TeamUserVO();
              BeanUtils.copyProperties(teamItem,teamUserVO);
              Long userId = teamItem.getUserId();
              UserVO userVO = new UserVO();
              User user = userService.getById(userId);
              if(user!=null){
                  BeanUtils.copyProperties(user,userVO);
                  teamUserVO.setCreateUser(userVO);
              }
              String name = teamItem.getName();
              if (StringUtils.isNotBlank(name)) {
                  //查找什么用户加入了这个队伍 使用SQL
                  List<UserVO> teamMembers = userTeamMapper.getAllTeamMembers(name);
                  teamUserVO.setUserVOList(teamMembers);
              }
              teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
	}

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) throws IllegalAccessException, InvocationTargetException {
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = teamUpdateRequest.getId();
        if(id<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if(!Objects.equals(teamUpdateRequest.getUserId(), loginUser.getId()) && //
         loginUser.getRoleid() != UserConstant.ADMIN_ROLE){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        int status = teamUpdateRequest.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(teamStatusEnum.equals(TeamStatusEnum.SECRET) && StringUtils.isBlank(teamUpdateRequest.getPassword())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"Password是必须项");
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser) {
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入 5 个队伍");
        }

        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍 ID 无效");
        }

        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }

        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已过期");
        }

        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(teamStatusEnum.equals(TeamStatusEnum.PRIVATE)){
            throw new BusinessException(ErrorCode.NULL_ERROR, "禁止加入");
        }
        if(teamStatusEnum.equals(TeamStatusEnum.SECRET)){
            String password = teamJoinRequest.getPassword();
            if(StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        if(team.getMaxNum() >=5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多加入五个队伍");
        }

        // 检查当前队伍的加入人数
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", teamId);
        userTeamQueryWrapper.eq("user_id", userId).ge("expire_time", new Date());
        long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (teamHasJoinNum >0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "不能重复加入");
        }

        // 添加记录
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        userTeamService.save(userTeam);
        return false;
    }

    /**
     * 用户退出队伍
     * @param request 退出队伍请求bean
     * @param loginUser 用户
     * @return 退队结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeUserFromTeam(TeamQuitRequest request, User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未登录");
        }

        long teamId = request.getTeamId();
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍ID无效");
        }

        // 判断队伍是否存在且未过期
        Team team = this.getById(teamId);
        if (team == null || (team.getExpireTime() != null && team.getExpireTime().before(new Date()))) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在或已过期");
        }

        long userId = loginUser.getId();

        // 查询用户是否在该队伍中
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamId);
        List<UserTeam> userTeamList = Optional.ofNullable(userTeamService.list(queryWrapper))
                .orElse(Collections.emptyList());

        if (userTeamList.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍信息异常");
        }

        // 判断用户是否在队伍中
        Optional<UserTeam> userTeamOptional = userTeamList.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();

        if (!userTeamOptional.isPresent()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户未加入该队伍");
        }

        long count = userTeamList.size();

        // 如果队伍只有一个人，则解散队伍
        if (count == 1) {
            this.removeById(teamId); // 解散队伍
            return userTeamService.removeById(userTeamOptional.get().getId()); // 删除中间表记录
        }

        long userTeamId = userTeamOptional.get().getId();

        // 如果用户是队长
        if (team.getUserId().equals(userId)) {
            // 过滤掉当前用户
            List<UserTeam> remainingMembers = userTeamList.stream()
                    .filter(item -> !item.getUserId().equals(userId))
                    .collect(Collectors.toList());

            // 按加入时间升序排序（最早加入的排在前）
            remainingMembers.sort(Comparator.comparing(UserTeam::getJoinTime));

            // 选出新的队长（最早加入的成员）
            long nextTeamLeaderId = remainingMembers.get(0).getUserId();
            team.setUserId(nextTeamLeaderId);
            this.updateById(team);
        }

        // 普通成员退出
        return userTeamService.removeById(userTeamId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Long id, User loginUser) {
        Team team = this.getById(id);

        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有相关队伍");
        }

        long teamId = team.getId();
        // 只有队长才可以解散队伍
        if (!Objects.equals(team.getUserId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无访问权限");
        }

        // 移除队伍中加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);

        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }

        // 删除队伍
        return this.removeById(teamId);
    }

}




