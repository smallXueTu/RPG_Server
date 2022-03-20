package cn.LTCraft.core.dataBase.mappers;

import cn.LTCraft.core.dataBase.bean.PlayerInfo;
import org.apache.ibatis.annotations.*;

public interface PlayerMapper {
    /**
     * 查询
     * @param name 玩家ID 转换为小写
     * @return PlayerInfo
     */
    @Select("select * from user where name = #{name}")
    @Results({
            @Result(property = "registerDate", column = "register_date"),
            @Result(property = "lastPlayTime", column = "last_play_time")
    })
    PlayerInfo selectByName(String name);

    /**
     * 更新
     * @param playerInfo 要更新的信息
     * @return 如果成功
     */
    @Update("update user set name = #{name}, password = #{password}, email = #{email}, qq = #{qq}, ip = #{ip}, gold = #{gold}, cumulative = #{cumulative}, last_play_time = #{lastPlayTime} where id = #{id}")
    boolean update(PlayerInfo playerInfo);

    /**
     * 插入新的用户
     * @param playerInfo 新的用户 只有用户名和密码
     * @return 如果成功
     */
    @Insert("insert into user(name, password) values(#{name}, #{password})")
    boolean insert(PlayerInfo playerInfo);
}
