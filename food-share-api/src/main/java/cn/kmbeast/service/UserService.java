package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.update.UserLoginDTO;
import cn.kmbeast.pojo.dto.update.UserRegisterDTO;
import cn.kmbeast.pojo.dto.update.UserUpdateDTO;
import cn.kmbeast.pojo.dto.query.extend.UserQueryDto;
import cn.kmbeast.pojo.entity.User;
import cn.kmbeast.pojo.vo.CenterVO;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.CurrentUserVO;
import cn.kmbeast.pojo.vo.UnreadCountVO;
import cn.kmbeast.pojo.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 * 提供用户注册、登录、信息管理、权限验证等功能
 */
public interface UserService {

    /**
     * 用户注册
     * @param userRegisterDTO 注册信息
     * @return 操作结果
     */
    Result<String> register(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录
     * @param userLoginDTO 登录信息
     * @return 登录结果（含token）
     */
    Result<Object> login(UserLoginDTO userLoginDTO);

    /**
     * Token验证
     * @return 用户信息
     */
    Result<UserVO> auth();

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    Result<CurrentUserVO> current();

    /**
     * 查询用户列表（管理端）
     * @param userQueryDto 查询参数
     * @return 用户列表
     */
    Result<List<User>> query(UserQueryDto userQueryDto);

    /**
     * 更新用户信息
     * @param userUpdateDTO 更新信息
     * @return 操作结果
     */
    Result<String> update(UserUpdateDTO userUpdateDTO);

    /**
     * 批量删除用户
     * @param ids ID列表
     * @return 操作结果
     */
    Result<String> batchDelete(List<Integer> ids);

    /**
     * 修改密码
     * @param map 密码信息
     * @return 操作结果
     */
    Result<String> updatePwd(Map<String, String> map);

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    Result<UserVO> getById(Integer id);

    /**
     * 管理员新增用户
     * @param userRegisterDTO 用户信息
     * @return 操作结果
     */
    Result<String> insert(UserRegisterDTO userRegisterDTO);

    /**
     * 管理员更新用户信息
     * @param user 用户实体
     * @return 操作结果
     */
    Result<String> backUpdate(User user);

    /**
     * 按天数统计用户增量
     * @param day 天数
     * @return 统计结果
     */
    Result<List<ChartVO>> daysQuery(Integer day);

    /**
     * 获取用户中心数据
     * @return 用户中心信息
     */
    Result<CenterVO> center();

    /**
     * 获取未读消息数量
     * @return 未读消息统计
     */
    Result<UnreadCountVO> getUnreadCount();

}
