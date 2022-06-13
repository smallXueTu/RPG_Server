package cn.ltcraft.login.listener;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.dataBase.SQLQueue;
import cn.LTCraft.core.dataBase.bean.PlayerInfo;
import cn.LTCraft.core.dataBase.mappers.PlayerMapper;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.utils.DateUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.LTCraft.core.utils.Utils;
import cn.ltcraft.login.Login;
import cn.ltcraft.login.other.PlayerStatus;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
    private final Login plugin;

    /**
     * 构造函数
     * @param plugin 插件
     */
    public PlayerListener(Login plugin){
        this.plugin = plugin;
    }

    /**
     * 玩家加入游戏
     * @param event 请不要主动调用此方法
     */
    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event){
        Player player = event.getPlayer();
        if (Login.playerStatus.containsKey(player.getName())){
            if (Login.playerStatus.get(player.getName())==PlayerStatus.NORMAL){
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§c相同ID的玩家已经登录！");
            }
        }else{
            Login.playerStatus.put(player.getName(), PlayerStatus.WAITING);//设置玩家为等待状态
            final int[] counter = {0};
            BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    counter[0]++;
                    PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
                    if (playerConfig == null){
                        Bukkit.getScheduler().cancelTask(getTaskId());
                        return;
                    }
                    if (playerConfig.getPlayerInfo() != null) {
                        PlayerInfo playerInfo = playerConfig.getPlayerInfo();
                        if (playerInfo.getPassword() == null) {//没记录
                            plugin.setPlayerStatus(player, PlayerStatus.REGISTER);
                            Login.forceSendMessage(player, "§l§e感谢你在千万服务器选择LTCraft。");
                            Login.forceSendMessage(player, "§l§e这个账户还没有注册，请直接发送密码来注册。");
                        } else {//有记录
                            plugin.setPlayerStatus(player, PlayerStatus.LOGIN);
                            Login.forceSendMessage(player, "§l§e欢迎回来，请输入密码~");
                        }
                        Bukkit.getScheduler().cancelTask(getTaskId());
                    }else {
                        if (counter[0] >= 20){
                            player.kickPlayer("§c查询超时，请联系管理员！");
                            Bukkit.getScheduler().cancelTask(getTaskId());
                        }
                    }
                }
            };
            bukkitRunnable.runTaskTimer(Login.getInstance(), 1, 1);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Login.getInstance(), () -> {
            Login.getInventoryPacketAdapter().sendBlankInventoryPacket(player);
        }, 1);
    }

    /**
     * 玩家退出游戏
     * @param event 请不要主动调用此方法
     */
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Login.playerStatus.remove(player.getName());
        Login.allowReceiveChat.remove(player.getName());
        Login.errorCount.remove(player.getName());
        if (Login.playerStatus.get(player.getName()) == PlayerStatus.NORMAL) {
            PlayerUtils.sendActionMessage("§e" + player.getName() + "退出了游戏。");
        }
    }

    /**
     * 背包点击事件
     * @param event 背包点击事件
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getWhoClicked() instanceof Player) {
            cancelled(event, (Player) event.getWhoClicked());
        }
    }
    /**
     * 玩家输入命令
     * @param event 不要主动调用此事件
     */
    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
        check(event.getPlayer(), event.getMessage(), event);
    }

    /**
     * 玩家聊天
     * @param event 不要主动调用此事件
     */
    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onPlayerChatEvent(AsyncPlayerChatEvent event){
        check(event.getPlayer(), event.getMessage(), event);
    }

    /**
     * 玩家输入聊天内容 包括命令
     * 关于玩家状态 {@link PlayerStatus}
     * @param player 执行的玩家
     * @param message 执行的内容
     * @param event 事件
     */
    public void check(Player player, String message, Cancellable event){
        if (!Login.playerStatus.containsKey(player.getName())){//这是不可能发生的事
            player.kickPlayer("§c无状态信息。");
            return;
        }
        PlayerStatus playerStatus = Login.playerStatus.get(player.getName());
        PlayerInfo playerInfo = PlayerConfig.getPlayerConfig(player).getPlayerInfo();
        switch (playerStatus){
            case LOGIN:
                event.setCancelled(true);
                if (message.equals(playerInfo.getPassword())){
                    Login.forceSendMessage(player, "§l§a恭喜你，登录成功啦~");
                    plugin.setPlayerStatus(player, PlayerStatus.NORMAL);
                    player.updateInventory();
                    playerInfo.setIp(player.getAddress().toString().substring(1).split(":")[0]);
                    playerInfo.commitChanges();
                    PlayerInfo.VIP vipStatus = playerInfo.getVipStatus();
                    if (vipStatus.getLevel().getGrade() > 0 && !player.isOp()){
                        if (vipStatus.getExpirationTime().getTime() < System.currentTimeMillis()){
                            player.sendMessage("§l§e你的" + vipStatus.getLevel().toString() + "§e已到达期限，从现在起你无法再享受" + vipStatus.getLevel().toString() + "§e带来的权益！");
                            playerInfo.setVipStatus(new PlayerInfo.VIP(null));
                            PlayerUtils.setGroup(player, "default");
                        }else {
                            player.sendMessage("§l§e尊贵的" + vipStatus.getLevel().toString() + "§e，你的" + vipStatus.getLevel().toString() + "§e截止到" + DateUtils.simpleDateFormat.format(vipStatus.getExpirationTime()));
                            PlayerUtils.setGroup(player, vipStatus.getLevel().toStringClean().toLowerCase());
                        }
                    }
                    PlayerUtils.sendActionMessage("§e" + player.getName() + "加入了游戏。");
                }else{
                    Login.forceSendMessage(player, "§l§c抱歉，密码不对哦~");
                    if(message.startsWith("/")){
                        Login.forceSendMessage(player, "§l§c注意，你输入的密码可能为命令，在此服务器你应该§d直接输入密码§c来的登录！");
                    }
                    if (Login.errorCount.containsKey(player.getName())){
                        if (Login.errorCount.get(player.getName()) >= 5){
                            Bukkit.getScheduler().runTask(Login.getInstance(), () ->
                                    player.kickPlayer("§c多次密码错误！"));
                            event.setCancelled(true);
                            return;
                        }
                        Login.errorCount.put(player.getName(), Login.errorCount.get(player.getName()) + 1);
                    }else {
                        Login.errorCount.put(player.getName(), 1);
                    }
                }
            break;
            case REGISTER:
                if(message.startsWith("/")){
                    Login.forceSendMessage(player, "§l§c注意，你输入的可能为命令，在此服务器你应该§d直接输入密码§c来的注册！");
                }
                if (playerInfo.getConfirmPassword()==null) {//玩家在第一步
                    String check = Utils.checkPassword(message);
                    if (!check.equals("true")){
                        Login.forceSendMessage(player, "§l§c"+check);
                    }else {
                        playerInfo.setConfirmPassword(message);
                        Login.forceSendMessage(player, "§l§e请再输入一遍密码来确认~输入§dtop§e返回上一步");
                    }
                }else{//再次验证玩家密码..
                    if (message.equals("top")){//回到上一步
                        Login.forceSendMessage(player, "§l§e请直接发送密码来注册。");
                        playerInfo.setConfirmPassword(null);
                    }else {
                        if (message.equals(playerInfo.getConfirmPassword())) {
                            SqlSession sqlSession = Main.getInstance().getSQLServer().getSqlSessionFactory().openSession();
                            PlayerMapper mapper = sqlSession.getMapper(PlayerMapper.class);
                            playerInfo.setPassword(playerInfo.getConfirmPassword());
                            Main.getInstance().getSQLManage().getQueue().add(new SQLQueue(sqlSession, () -> mapper.insert(playerInfo),
                                sqlQueue -> {
                                    if (sqlQueue.getStatus()== SQLQueue.STATUS.DONE){
                                        Login.forceSendMessage(player, "§l§a恭喜你！注册成功了~");
                                        Login.forceSendMessage(player, "§l§a现在。你可以开始游戏啦~");
                                        plugin.setPlayerStatus(player, PlayerStatus.NORMAL);
                                        player.updateInventory();
                                    }else{
                                        Login.forceSendMessage(player, "§l§c哎呀，注册失败，清联系管理员。");
                                    }
                                })
                            );//插入到队列
                        } else {
                            Login.forceSendMessage(player, "§l§c两次密码不符合，请重新输入：");
                            playerInfo.setConfirmPassword(null);
                        }
                    }
                }
                event.setCancelled(true);
            break;
            case WAITING:
                event.setCancelled(true);
            break;
            case NORMAL:
                if (message.trim().equals(playerInfo.getPassword())){
                    event.setCancelled(true);
                    Login.forceSendMessage(player, "§l§c你差点泄露密码！");
                }
            break;
        }
    }

    /**
     * @param event 玩家钓鱼
     */
    @EventHandler
    public void onPlayerEvent(PlayerFishEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家消费物品
     */
    @EventHandler
    public void onPlayerEvent(PlayerItemConsumeEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家修补物品
     */
    @EventHandler
    public void onPlayerEvent(PlayerItemMendEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家手持更新
     */
    @EventHandler
    public void onPlayerEvent(PlayerItemHeldEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家尝试捡起物品
     */
    @EventHandler
    public void onPlayerEvent(PlayerPickupItemEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家伤害物品
     */
    @EventHandler
    public void onPlayerEvent(PlayerItemDamageEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家使用桶
     */
    @EventHandler
    public void onPlayerEvent(PlayerBucketEmptyEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家使用桶
     */
    @EventHandler
    public void onPlayerEvent(PlayerBucketFillEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家编辑书
     */
    @EventHandler
    public void onPlayerEvent(PlayerEditBookEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家点丢弃物品
     */
    @EventHandler
    public void onPlayerEvent(PlayerDropItemEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家点击
     */
    @EventHandler
    public void onPlayerEvent(PlayerInteractEvent event){
        cancelled(event, event.getPlayer());
    }

    /**
     * @param event 玩家移动
     */
    @EventHandler
    public void onPlayerEvent(PlayerMoveEvent event){
        if (
                event.getFrom().getX()!=event.getTo().getX() ||
                event.getFrom().getY()<event.getTo().getY() ||
                event.getFrom().getZ()!=event.getTo().getZ()
        )
         cancelled(event, event.getPlayer());
    }

    /**
     * 玩家破坏方块
     * @param event 不要主动调用此事件
     */
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event){
        if (event.getPlayer()!=null) {
          cancelled(event, event.getPlayer());
        }
    }

    /**
     * 玩家放置方块
     * @param event 不要主动调用此事件
     */
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event){
        if (event.getPlayer()!=null) {
            cancelled(event, event.getPlayer());
        }
    }

    /**
     * 实体的可取消事件
     * @param event 不要主动调用此事件
     */
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event){
        if(
                event instanceof EntityDamageByEntityEvent//实体受伤
        ){
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
            if (e.getDamager() instanceof Player)
                cancelled(event, (Player) e.getDamager());
            if (e.getEntity() instanceof Player)
                cancelled(event, (Player) e.getEntity());
        }else{
            if (event.getEntity() instanceof Player)
                cancelled(event, (Player) event.getEntity());
        }
    }

    /**
     * 取消事件 如果满足 不是NORMAL 取消事件
     * @see PlayerStatus
     * @param event 可取消事件
     * @param player 事件的玩家
     */
    public void cancelled(Cancellable event, Player player){
        if (event.isCancelled())return;
        if (!Login.playerStatus.containsKey(player.getName()) || Login.playerStatus.get(player.getName())!= PlayerStatus.NORMAL){
            event.setCancelled(true);//取消移动
        }
    }
}
