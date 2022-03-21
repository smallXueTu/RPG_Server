/**
 * Created with IntelliJ IDEA.
 * projectName: RPG
 * fileName: PlayerInfo.java
 * date: 2020-07-14 20:48
 *
 * @Auther: Angel、
 */
package cn.LTCraft.core.dataBase.bean;

import cn.LTCraft.core.dataBase.mappers.PlayerMapper;
import cn.LTCraft.core.dataBase.SQLQueue;
import cn.LTCraft.core.Main;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * @Auther: Angel、
 * @Date: 2020/07/14/20:48
 * @Description:
 */

public class PlayerInfo {
    /**
     * ID
     */
    private int id;
    /**
     * 玩家ID
     */
    private String name;
    /**
     * 玩家密码
     */
    private String password;
    /**
     * 玩家邮箱
     */
    private String email;
    /**
     * 玩家QQ号
     */
    private Long qq;
    /**
     * 玩家上次登录IP
     */
    private String ip;
    /**
     * 累计金额
     */
    private double cumulative;
    /**
     * 累计金额
     */
    private double gold;
    /**
     * 注册时间
     */
    private Date registerDate;
    /**
     * 上次游戏时间
     */
    private Date lastPlayTime;
    /**
     * 确认密码 该成员函数仅仅在玩家注册中会有值
     */
    private String confirmPassword = null;
    public PlayerInfo(){

    }
    public PlayerInfo(String name){
        this.name = name;
    }

    /**
     * 获取玩家的QQ
     * @return 玩家的QQ号
     */
    public Long getQQ() {
        return qq;
    }

    /**
     * 获取玩家的密码
     * @return 玩家的密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 获取玩家的邮箱
     * @return 玩家的邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 获取玩家的IP
     * @return 玩家的IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 这个玩家的名字
     * @return 玩家的名字
     */
    public String getName() {
        return name;
    }

    /**
     * 获取累计
     * @return 累计
     */
    public double getCumulative() {
        return cumulative;
    }

    /**
     * 获取金币
     * @return 金币余额
     */
    public double getGold() {
        return gold;
    }

    /**
     * 获取上次游玩时间
     * @return 游玩时间
     */
    public Date getLastPlayTime() {
        return lastPlayTime;
    }

    /**
     * 获取确认密码
     * @return 如果玩家两次密码一样注册成功
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * 设置上次游玩时间
     * @param lastPlayTime 上次游玩时间
     */
    public void setLastPlayTime(Date lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }
    /**
     * 设置累计
     * @param cumulative 累计
     */
    public void setCumulative(double cumulative) {
        this.cumulative = cumulative;
    }

    /**
     * 设置金币余额
     * @param gold 金币余额
     */
    public void setGold(double gold) {
        this.gold = gold;
    }

    /**
     * 赐予玩家金币
     * @param balance 金币
     */
    public void addGold(double balance){
        gold += balance;
        gold = Math.max(0, gold);
    }

    /**
     * 扣除玩家金币
     * @param balance 金币
     */
    public void reduceGold(double balance){
        gold -= balance;
        gold = Math.max(0, gold);
    }

    /**
     * 如果玩家上次回到上一步，修改这个的值
     * @param confirmPassword 玩家第一步输入的密码
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * 玩家修改密码
     * 修改后会自动提交至MySQL
     * @param password 玩家密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 修改玩家的邮箱
     * 修改后会自动提交至MySQL
     * @param email 邮箱 这里没做格式验证，格式验证在玩家键入时已经做了
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 如果玩家登陆成功，会默认设置玩家的IP
     * 修改后会自动提交至MySQL
     * @param ip 玩家的ip 请满足x.x.x.x格式
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 修改玩家绑定QQ
     * 修改后会自动提交至MySQL
     * @param qq QQ号
     */
    public void setQQ(Long qq) {
        this.qq = qq;
    }

    /**
     * 提交修改
     * 在副线程处理 无法同步该修改是否修改成功的返回值
     * 大多数情况下修改都会成功，除非副线程数据库链接中断或者数据类型不正确
     * TODO: 当执行完成后回调到 加入结果到队列 让插件判断插入是否成功
     */
    public void commitChanges(){
        SqlSession sqlSession = Main.getInstance().getSQLServer().getSqlSessionFactory().openSession();
        PlayerMapper mapper = sqlSession.getMapper(PlayerMapper.class);
        Main.getInstance().getSQLManage().getQueue().add(new SQLQueue(sqlSession, () -> mapper.update(this)));
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", qq=" + qq +
                ", ip='" + ip + '\'' +
                ", cumulative=" + cumulative +
                ", gold=" + gold +
                ", registerDate='" + registerDate + '\'' +
                ", lastPlayTime='" + lastPlayTime + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
