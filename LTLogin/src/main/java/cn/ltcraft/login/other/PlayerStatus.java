package cn.ltcraft.login.other;

/**
 * 玩家状态，记录一个玩家的状态
 * 一个玩家登陆游戏
 * 先去数据库查询，状态是WAITING
 * 如果没查询到，状态是REGISTER
 * 如果查询到了则是LOGIN
 * 如果登陆过了或者注册过了 则 NORMAL
 */
public enum PlayerStatus {
    LOGIN,
    REGISTER,
    WAITING,
    NORMAL,
    //TODO 游客，封禁
}
