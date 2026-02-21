package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.dto.query.extend.InteractionStaticQueryDto;
import cn.kmbeast.pojo.dto.query.extend.UserBehaviorWeightDto;
import cn.kmbeast.pojo.entity.Interaction;
import cn.kmbeast.pojo.vo.InteractionVO;
import cn.kmbeast.pojo.vo.WhoLikedMeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容互动的持久化接口
 */
@Mapper
public interface InteractionMapper {

    void save(Interaction interaction);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    void delByUserInfo(@Param(value = "type") Integer type,
                       @Param(value = "contentId") Integer contentId,
                       @Param(value = "userId") Integer userId);

    List<InteractionVO> query(InteractionQueryDto interactionQueryDto);

    List<InteractionVO> queryDays(InteractionStaticQueryDto interactionStaticQueryDto);

    Integer queryCount(InteractionQueryDto interactionQueryDto);

    Integer queryByGourmetIds(@Param(value = "gourmetIds") List<Integer> gourmetIds,
                           @Param(value = "type") Integer type);

    /**
     * 查询用户的未读点赞数量
     *
     * @param userId 用户ID
     * @param type   互动类型（1=点赞）
     * @return 未读点赞数量
     */
    Integer countUnreadLikes(@Param(value = "userId") Integer userId,
                           @Param(value = "type") Integer type);

    List<WhoLikedMeVO> queryWhoLikedMe(InteractionQueryDto interactionQueryDto);

    Integer countWhoLikedMe(InteractionQueryDto interactionQueryDto);

    /**
     * 查询所有用户的综合行为数据
     * 用于基于用户行为的推荐算法
     * @return 用户行为权重列表
     */
    List<UserBehaviorWeightDto> queryUserBehaviorWeights();

    /**
     * 查询指定用户的综合行为数据
     * @param userId 用户ID
     * @return 用户行为权重列表
     */
    List<UserBehaviorWeightDto> queryUserBehaviorByUserId(@Param(value = "userId") Integer userId);

}
