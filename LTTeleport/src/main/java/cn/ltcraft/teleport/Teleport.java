/**
 * Created with IntelliJ IDEA.
 * projectName: RPG
 * fileName: Teleport.java
 * packageName: cn.ltcraft.Teleport
 * date: 2020-07-15 12:22
 *
 * @Auther: Angel、
 */
package cn.ltcraft.teleport;

import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.utils.PlayerUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * @Auther: Angel、
 * @Date: 2020/07/15/12:22
 * @Description: 传送插件主类
 * 小插件
 */

public class Teleport extends JavaPlugin implements Listener {
    private final HashMap<String, HashMap<String, Request>> requests = new HashMap<String, HashMap<String, Request>>();
    private final HashMap<String, HashMap<String, Home>> playerHomes = new HashMap<String, HashMap<String, Home>>();
    private final HashMap<String, Location> deathLocation = new HashMap<String, Location>();
    private YamlConfiguration warps;
    private final File playersData = new File(getDataFolder()+File.separator+"players");
    HashMap<String, Warp> tmpWarps = new HashMap<String, Warp>();
    HashMap<String, Location> lastLocation = new HashMap<String, Location>();
    private static Teleport instance = null;

    public static Teleport getInstance() {
        return instance;
    }

    /**
     * 插件加载
     */
    @Override
    public void onLoad() {
        super.onLoad();
        File WarpsF = new File(getDataFolder(), "warps.yml");
        if (!playersData.exists())playersData.mkdirs();
        warps = YamlConfiguration.loadConfiguration(WarpsF);
    }

    /**
     * 插件卸载
     */
    @Override
    public void onDisable() {
        super.onDisable();
        save();
    }

    /**
     * 保存配置文件
     */
    public void save(){
        try {
            warps = new YamlConfiguration();
            for (Map.Entry<String, Warp> stringWarpEntry : tmpWarps.entrySet()) {
                warps.set(stringWarpEntry.getKey(), stringWarpEntry.getValue().tpMap());
            }
            if (warps.getKeys(false).size() > 0) {
                warps.save(new File(getDataFolder(), "warps.yml"));
            }else {
                getLogger().warning("地标加载失败，跳过保存！");
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                File dataFile = new File(playersData, player.getName().toLowerCase()+".yml");
                YamlConfiguration data = new YamlConfiguration();
                data.set("homes", new HashMap<>());
                for (Map.Entry<String, Home> home : playerHomes.get(player.getName()).entrySet()) {
                    data.set("homes." + home.getKey(), home.getValue().tpMap());
                }
                data.save(dataFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载配置文件
     */
    public void load(){
        Set<String> set = warps.getKeys(false);
        for (String key : set) {
            World world = getServer().getWorld(warps.getString(key + ".world"));
            if (world == null) {
                getLogger().warning(warps.getString(key + ".world") + "=======NULL");
                continue;
            }
            tmpWarps.put(key, new Warp(
                    Double.parseDouble(warps.getString(key + ".x")),
                    Double.parseDouble(warps.getString(key + ".y")),
                    Double.parseDouble(warps.getString(key + ".z")),
                    world
                    )
            );
        }
        if (tmpWarps.size() <= 0){
            System.out.println(set);
            for (int i = 0; i < 50; i++) {
                getLogger().warning("地标加载失败！");
            }
        }
    }

    /**
     * 插件启用
     */
    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().runTaskTimer(this, () -> requests.forEach((player, request) -> request.keySet().removeIf(s -> System.currentTimeMillis() / 1000 - request.get(s).getTime() > 30)), 20, 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::load, 0);
    }

    public YamlConfiguration getWarps() {
        return warps;
    }

    public HashMap<String, HashMap<String, Home>> getPlayerHomes() {
        return playerHomes;
    }

    public HashMap<String, Warp> getTmpWarps() {
        return tmpWarps;
    }

    /**
     * 玩家加入游戏
     * @param event 事件对象
     */
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        requests.put(player.getName(), new HashMap<String, Request>());
        playerHomes.put(player.getName(), new HashMap<String, Home>());
        lastLocation.put(player.getName(), null);
        File dataFile = new File(playersData, player.getName().toLowerCase()+".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        loadHomes(data.getConfigurationSection("homes"), player);
        if (playerHomes.get(player.getName()).size() <= 0){
            playerHomes.get(player.getName()).put("mainline", new Home(Bukkit.getWorld("world").getSpawnLocation()));
        }
    }

    /**
     * 玩家退出游戏
     * @param event 事件对象
     */
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        requests.remove(player.getName());
        lastLocation.remove(player.getName());
        playerHomes.get(player.getName());
        try {
            Iterator<String> iterator = playerHomes.get(player.getName()).keySet().iterator();
            File dataFile = new File(playersData, player.getName().toLowerCase()+".yml");
            YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
            data.set("homes", new HashMap<>());
            while (iterator.hasNext()){
                String key = iterator.next();
                Home home = playerHomes.get(player.getName()).get(key);
                data.set("homes."+key, home.tpMap());
            }
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerHomes.remove(player.getName());
        deathLocation.remove(player.getName());
    }

    /**
     * 加载家
     * @param homes 所有家
     * @param player 玩家对象
     */
    public void loadHomes(ConfigurationSection homes, Player player){
        if (homes == null)return;
        Map<String, Object> map = homes.getValues(false);
        for (String key : map.keySet()) {
            MemorySection homeInfo = (MemorySection) map.get(key);
            World world = getServer().getWorld(homeInfo.getString("world"));
            if (world == null) continue;
            Home home = new Home(Double.parseDouble(homeInfo.getString("x")), Double.parseDouble(homeInfo.getString("y")), Double.parseDouble(homeInfo.getString("z")), world);
            playerHomes.get(player.getName()).put(key, home);
        }
    }

    /**
     * 玩家重生
     * @param event 时间对象
     */
    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event){
        World world = getServer().getWorld("world");
        event.setRespawnLocation(world.getSpawnLocation());
    }
    /**
     * 玩家死亡
     * @param event 事件对象
     */
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        Player player = event.getEntity();
        if (Game.rpgWorlds.contains(player.getWorld().getName()))return;
        deathLocation.put(player.getName(), player.getLocation());
        TextComponent message = new TextComponent("§e你有新的死亡记录，你可以点击：");
        TextComponent back = new TextComponent("[这里]");
        back.setColor(ChatColor.GREEN);
        back.setBold(true);
        back.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/back"));
        back.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§a点击回到死亡点")).create()));
        message.addExtra(back);
        message.addExtra(new TextComponent("§e或者输入/back 返回死亡点"));
        player.spigot().sendMessage(message);
    }
    /**
     * TAB选取
     * @param sender 选取着
     * @param command 命令
     * @param alias 标签
     * @param args 参数
     * @return 所有选取值
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (command.getName()) {
            case "warp":
            case "delwarp":
                return tmpWarps.keySet().stream().filter(s -> s.startsWith(args.length <= 1?"":args[1])).collect(Collectors.toList());
            case "home":
            case "delhome":
                return playerHomes.get(sender.getName()).keySet().stream().filter(s -> s.startsWith(args.length <= 1?"":args[1])).collect(Collectors.toList());
        }
        return null;
    }
    /**
     * 输入命令
     * @param sender 发送者
     * @param command 命令
     * @param label 标签
     * @param args 参数
     * @return .
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player sp;
        if (sender instanceof Player) {
            sp = (Player) sender;
        }else {
            sp = null;
        }
        Player player;
        TextComponent message;
        TextComponent accept;
        Warp warp;
        Home home;
        World world;
        if (
            sp != null && !sp.hasPermission("LTCraft.vip") &&
            (command.getName().equals("w") ||
            command.getName().equals("warp") ||
            command.getName().equals("back") ||
            command.getName().equals("bt") ||
            command.getName().equals("home"))
        ){
            if (Cooling.isCooling(sp, "传送命令")) {
                return true;
            }
            Cooling.cooling(sp, "传送命令", 5, "随机命令剩余冷却时间：%s%S");
        }
        switch (command.getName()){
            case "tpa":
                if (args.length==0){
                    sender.sendMessage("§c用法/tpa 目标玩家ID");
                    return true;
                }
                player = getServer().getPlayer(args[0]);
                if (player==null){
                    sender.sendMessage("对方不在线！");
                    return true;
                }
                if (player.getName().equals(sender.getName())){
                    sender.sendMessage("§c你不能tpa自己！");
                    return true;
                }
                if (this.requests.get(player.getName()).containsKey(sender.getName().toLowerCase())){
                    sender.sendMessage("§c你们两个已经存在一个请求了请等待处理或者到期！");
                    return true;
                }
                message = new TextComponent("§e玩家"+sender.getName()+"想往你这瞧瞧，你可以在30秒内做出决定：");
                accept = new TextComponent("[点我接受]");
                accept.setColor(ChatColor.GREEN);
                accept.setBold(true);
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getName()));
                accept.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§a点击接受这个tpa")).create()));
                message.addExtra(accept);
                message.addExtra(new TextComponent("§e或者输入/tpaccept 来接受这个请求。"));
                requests.get(player.getName()).put(sender.getName().toLowerCase(), new Request(player, sp));
                player.spigot().sendMessage(message);
                sender.sendMessage("§a发送请求成功！");
            break;
            case "w":
                if (sp == null){
                    sender.sendMessage("§c你不是一个玩家！");
                    return true;
                }
                if (args.length==0){
                    sender.sendMessage("§c用法/w 世界名字。");
                    return true;
                }
                world = Bukkit.getWorld(args[0]);
                if (world == null){
                    world = Bukkit.getWorld(Game.worldNames.get(args[0]));
                    if (world == null) {
                        sender.sendMessage("§c世界" + args[0] + "不存在！");
                        return true;
                    }
                }
                sp.teleport(world.getSpawnLocation());
                sender.sendMessage("§a传送成功！");
                break;
            case "tpaccept":
                if (requests.get(sender.getName()).size()<=0){
                    sender.sendMessage("§c还木有人请求过你呢！");
                    return true;
                }
                HashMap<String, Request> requests = this.requests.get(sender.getName());
                Request request;
                if (args.length<=0){
                    request = requests.entrySet().iterator().next().getValue();//第一个
                    requests.remove(requests.entrySet().iterator().next().getKey());
                }else{
                    if (!requests.containsKey(args[0].toLowerCase())){
                        sender.sendMessage("§c"+args[0]+"没有想你发起请求或者请求已经过期了！");
                        return true;
                    }
                    request = requests.get(args[0].toLowerCase());
                    requests.remove(args[0].toLowerCase());
                }
                if (Game.rpgWorlds.contains(request.getPlayer().getWorld().getName()) && !PlayerUtils.hasBQTag(request.getTransmitters(), "解开t3第二关谜团")){
                    request.getTransmitters().sendMessage("§c你需要完成主线副本才能传送其他副本的玩家！");
                    sender.sendMessage("§c接受失败");
                    return true;
                }
                request.getTransmitters().teleport(request.getPlayer(), PlayerTeleportEvent.TeleportCause.COMMAND);
                sender.sendMessage("§a接受成功");
                request.getPlayer().sendMessage("§a"+sender.getName()+"接受了你的请求~");
            break;
            case "tpahere":
                if (args.length==0){
                    sender.sendMessage("§c用法/tpahere 目标玩家ID");
                    return true;
                }
                player = getServer().getPlayer(args[0]);
                if (player==null){
                    sender.sendMessage("§c对方不在线！");
                    return true;
                }
                if (player.getName().equals(sender.getName())){
                    sender.sendMessage("§c你不能tpahere自己！");
                    return true;
                }
                if (this.requests.get(player.getName()).containsKey(sender.getName().toLowerCase())){
                    sender.sendMessage("§c你们两个已经存在一个请求了请等待处理或者到期！");
                    return true;
                }
                message = new TextComponent("§e玩家"+sender.getName()+"邀请你过去喝茶，你可以在30秒内做出决定：");
                accept = new TextComponent("[点我接受]");
                accept.setColor(ChatColor.GREEN);
                accept.setBold(true);
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getName()));
                accept.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("§a点击接受这个tpahere")).create()));
                message.addExtra(accept);
                message.addExtra(new TextComponent("§e或者输入/tpaccept 来接受这个请求。"));
                this.requests.get(player.getName()).put(sender.getName().toLowerCase(), new Request(sp, player));
                player.spigot().sendMessage(message);
                sender.sendMessage("§a发送请求成功！");
            break;
            case "setwarp":
                if (args.length==0){
                    sender.sendMessage("§c用法/setwarp 地标名称");
                    return true;
                }
                warp = new Warp(sp.getLocation());
                tmpWarps.put(args[0], warp);
                sender.sendMessage("§a地标设置成功！");
            break;
            case "delwarp":
                if (args.length==0){
                    sender.sendMessage("§c用法/delwarp 地标名称");
                    return true;
                }
                warp = new Warp(sp.getLocation());
                tmpWarps.put(args[0], warp);
                if (!tmpWarps.containsKey(args[0])){
                    sender.sendMessage("§c地标"+args[0]+"不存在！");
                    return true;
                }
                tmpWarps.remove(args[0]);
                sender.sendMessage("§a地标删除成功！");
            break;
            case "warp":
                if (args.length==0){
                    sender.sendMessage("§c用法/warp 地标名称");
                    sender.sendMessage("§e所有地标：");
                    if (tmpWarps.size() <= 0)return true;
                    Iterator<String> warps = tmpWarps.keySet().iterator();
                    StringBuilder warpsM = new StringBuilder();
                    while (warps.hasNext()){
                        warpsM.append(warps.next()).append(", ");
                    }
                    sender.sendMessage(warpsM.substring(0, warpsM.length()-2));
                    return true;
                }
                if (!tmpWarps.containsKey(args[0])){
                    sender.sendMessage("§c地标"+args[0]+"不存在！");
                    return true;
                }
                warp = tmpWarps.get(args[0]);
                sp.teleport(warp.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
                    sp.playSound(sp.getLocation(), "entity.endermen.teleport", 1, 1)
                , 10);
                sender.sendMessage("§a成功传送到地标"+args[0]+"！");
            break;
            case "warps":
                sender.sendMessage("§e所有地标：");
                if (tmpWarps.size() <= 0)return true;
                Iterator<String> warps = tmpWarps.keySet().iterator();
                StringBuilder warpsM = new StringBuilder();
                while (warps.hasNext()){
                    warpsM.append(warps.next()).append(", ");
                }
                sender.sendMessage(warpsM.substring(0, warpsM.length()-2));
            break;
            case "sethome":
                if (args.length==0){
                    sender.sendMessage("§c用法/sethome 家名字");
                    return true;
                }
                if (args[0].equalsIgnoreCase("mainline")){
                    sender.sendMessage("§c你不能用这个名子来命名你的家！");
                    return true;
                }
                if (sp.getWorld().getName().startsWith("t")){
                    sender.sendMessage("§c你不能在主线副本安置家！");
                    return true;
                }
                int max = getHomeMaxCount(sp);
                if (playerHomes.get(sender.getName()).size() >= max){
                    sender.sendMessage("§a你最大只能设置" + max + "个家！");
                    return true;
                }
                home = new Home(sp.getLocation());
                playerHomes.get(sender.getName()).put(args[0], home);
                sender.sendMessage("§a设置家成功！");
            break;
            case "delhome":
                if (args.length==0){
                    sender.sendMessage("§c用法/delhome 家名字");
                    return true;
                }
                if (args[0].equalsIgnoreCase("mainline")){
                    sender.sendMessage("§c你不能删除这个家！");
                    return true;
                }
                if (!playerHomes.get(sender.getName()).containsKey(args[0])){
                    sender.sendMessage("§c家"+args[0]+"不存在！");
                    return true;
                }
                playerHomes.get(sender.getName()).remove(args[0]);
                sender.sendMessage("§a删除家成功！");
            break;
            case "home":
                if (args.length==0){
                    sender.sendMessage("§c用法/home 家名字");
                    return true;
                }
                if (!playerHomes.get(sender.getName()).containsKey(args[0])){
                    sender.sendMessage("§c家"+args[0]+"不存在！");
                    return true;
                }
                home = playerHomes.get(sender.getName()).get(args[0]);
                sp.teleport(home.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                sender.sendMessage("§a成功传送到家！");
            break;
            case "homes":
                sender.sendMessage("§e所有家：");
                if (playerHomes.get(sender.getName()).size() <= 0)return true;
                Iterator<String> homes = playerHomes.get(sender.getName()).keySet().iterator();
                StringBuilder homesM = new StringBuilder();
                while (homes.hasNext()){
                    homesM.append(homes.next()).append(", ");
                }
                sender.sendMessage(homesM.substring(0, homesM.length()-2));
            break;
            case "back":
                if (!deathLocation.containsKey(sender.getName())){
                    sender.sendMessage("§c没有死亡记录！");
                    return true;
                }
                sp.teleport(deathLocation.get(sender.getName()), PlayerTeleportEvent.TeleportCause.COMMAND);
                sender.sendMessage("§a传送成功！");
            break;
            case "bt":
                if (!lastLocation.containsKey(sender.getName()) || lastLocation.get(sender.getName()) == null){
                    sender.sendMessage("§c无记录！");
                    return true;
                }
                sp.teleport(lastLocation.get(sender.getName()), PlayerTeleportEvent.TeleportCause.COMMAND);
                sender.sendMessage("§a传送成功！");
            break;
            case "setspawn":
                world = sp.getWorld();
                world.setSpawnLocation(sp.getLocation());
                sender.sendMessage("§a设置成功！");
            break;
        }
        return true;
    }

    /**
     * 玩家传送
     * @param event .
     */
    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onTeleportEvent(PlayerTeleportEvent event){
        if (!Game.rpgWorlds.contains(event.getFrom().getWorld().getName()) && !(event.getFrom().getWorld() == event.getTo().getWorld() && event.getTo().distance(event.getFrom()) < 5) &&
                (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN)){
            lastLocation.put(event.getPlayer().getName(), event.getFrom());
            event.getPlayer().sendMessage("§e输入/bt传送到上次传送前的坐标.");
        }
    }
    public int getHomeMaxCount(Player player){
        if (player.hasPermission("LTCraft.svip"))
            return 7;
        else if (player.hasPermission("LTCraft.vip"))
            return 5;
        return 3;
    }
}
