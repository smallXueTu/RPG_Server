package cn.LTCraft.core;


import cn.LTCraft.core.game.SpawnManager;
import cn.LTCraft.core.game.TeleportGateManager;
import cn.LTCraft.core.hook.BQ.event.*;
import cn.LTCraft.core.hook.BQ.objective.*;
import cn.LTCraft.core.hook.BQ.condition.*;
import cn.LTCraft.core.commands.CommandLoader;
import cn.LTCraft.core.dataBase.SQLManage;
import cn.LTCraft.core.dataBase.SQLServer;
import cn.LTCraft.core.hook.TrMenu.actions.TackGoldCoins;
import cn.LTCraft.core.listener.*;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.hook.papi.LTExpansion;
import cn.LTCraft.core.task.ClientCheckTask;
import cn.LTCraft.core.task.GarbageClear;
import cn.LTCraft.core.hook.TrMenu.actions.GetClutterItem;
import cn.LTCraft.core.hook.TrMenu.actions.LTExchange;
import cn.LTCraft.core.hook.TrMenu.actions.OpenCoreGui;
import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.utils.TrMenuUtils;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.earth2me.essentials.Essentials;
import me.arasple.mc.trmenu.TrMenu;
import me.arasple.mc.trmenu.api.action.ActionHandle;
import me.arasple.mc.trmenu.module.internal.script.js.JavaScriptAgent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.betoncraft.betonquest.BetonQuest;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 核心的主类，目前大部分功能都是图方便临时实现的
 * 内部逻辑可能不太尽人意。
 * 当然有重写它们的想法，但现在还没必要那么做，由于经济压力和时间精力关系，为了更快上线游戏。
 * 等游戏上线稳定了再考虑重构它们...
 * 2022年4月19日13:35:22
 * TODO above
 */
public class Main extends JavaPlugin {
    private SQLServer SQLServer;
    private SQLManage SQLManage;
    private final LinkedHashMap<Integer, ClientCheckTask> tacks = new LinkedHashMap<>();
    private static Main instance;
    public static int id = 0;
    private Chat chat;
    private Config config;
    Essentials essentials;
    private LTCraftMessageHandler messageHandler;
    private Economy economy;
    private GroupManager groupManager;
    private static ProtocolManager protocolManager = null;
    private static List<PacketAdapter> adapters;

    public static Main getInstance(){
        return instance;
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    /**
     * 插件启用
     */
    public void onEnable() {
        super.onEnable();
        instance = this;
        config = Config.getInstance();
        /*
        创建插件配置目录
         */
        this.getDataFolder().mkdirs();
        new File(getDataFolder() + File.separator + "playerData").mkdirs();
        SQLServer = new SQLServer(this);
        SQLManage = new SQLManage(SQLServer);
        GlobalRefresh.init(this);
        getServer().getScheduler().runTaskTimer(this, GlobalRefresh::refresh, 1, 1);
        //hooks
        initVault();
        initBQ();
        initEss();
        initTrMenu();
        initGM();
        initProtocol();

        this.messageHandler = new LTCraftMessageHandler();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "LTCraft");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "LTCraft", this.messageHandler);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new DragonCoreListener(), this);
        Bukkit.getPluginManager().registerEvents(new MMListener(), this);
        Bukkit.getPluginManager().registerEvents(new CitizensListener(), this);
        Bukkit.getPluginManager().registerEvents(new Cooling(), this);

        new CommandLoader();

        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            PlaceholderAPI.registerExpansion(new LTExpansion());
            Game.resetWorld("zy");
            for (String string : new String[]{
                    "丢弃物"
            }) {
                GarbageClear.skips.add(new ClutterItem(string));
            }
            SpawnManager.getInstance().init();
            TeleportGateManager.getInstance().init();
        }, 1);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerConfig.getPlayerConfig(onlinePlayer).save();
            onlinePlayer.kickPlayer("§c服务器关闭！");
        }
        SQLManage.setShutdown();
        SQLServer.setShutdown();
        try {
            SQLManage.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (PacketAdapter adapter : adapters) {
            protocolManager.removePacketListener(adapter);
        }
    }


    /**
     * @return SQL对象
     */
    public SQLServer getSQLServer() {
        return SQLServer;
    }

    /**
     * @return 数据库信息
     */
    public YamlConfiguration getMySQLInfo() {
        return config.getMySQLInfoYaml();
    }

    /**
     * @return 数据库管理对象
     */
    public SQLManage getSQLManage() {
        return SQLManage;
    }

    public LinkedHashMap<Integer, ClientCheckTask> getTacks() {
        return tacks;
    }

    public void addTack(int id, ClientCheckTask tack){
        synchronized (tacks) {
            tacks.put(id, tack);
        }
    }

    public ClientCheckTask getTack(int id){
        return tacks.get(id);
    }

    public Essentials getEssentials() {
        return essentials;
    }

    private boolean initVault() {
        economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        boolean hasNull = false;
        RegisteredServiceProvider<Chat> chatProvider = this.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null && (chat = (Chat)chatProvider.getProvider()) == null) {
            hasNull = true;
        }
        return !hasNull;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Chat getChat() {
        return chat;
    }

    private void initBQ() {
        BetonQuest.getInstance().registerObjectives("pickup", PickupObjective.class);
        BetonQuest.getInstance().registerObjectives("item", ItemObjective.class);
        BetonQuest.getInstance().registerObjectives("tag", TagObjective.class);
        BetonQuest.getInstance().registerObjectives("notag", NoTagObjective.class);
        BetonQuest.getInstance().registerObjectives("conditions", ConditionObjective.class);
        BetonQuest.getInstance().registerObjectives("enchante", EnchantEObjective.class);
        BetonQuest.getInstance().registerObjectives("dropitemrange", DropItemRangeObjective.class);

        BetonQuest.getInstance().registerEvents("saveMainLine", SaveMainLineEvent.class);
        BetonQuest.getInstance().registerEvents("changeClass", ChangeClassEvent.class);
        BetonQuest.getInstance().registerEvents("delay", DelayEventEvent.class);
        BetonQuest.getInstance().registerEvents("setClassLevel", SetClassLevelEvent.class);
        BetonQuest.getInstance().registerEvents("give", GiveEvent.class);
        BetonQuest.getInstance().registerEvents("take", TakeEvent.class);
        BetonQuest.getInstance().registerEvents("castMMSkill", CastMMSkillEvent.class);
        BetonQuest.getInstance().registerEvents("stealGold", StealGoldEvent.class);

        BetonQuest.getInstance().registerConditions("skillapiLevel", SkillAPILevelCondition.class);
        BetonQuest.getInstance().registerConditions("item", ItemCondition.class);
        BetonQuest.getInstance().registerConditions("agold", AdequateGoldCondition.class);
    }
    private void initEss() {
        essentials = ((Essentials)Bukkit.getPluginManager().getPlugin("Essentials"));
    }
    private void initTrMenu(){
        TrMenu instance = TrMenu.INSTANCE;
        ActionHandle actionHandle = instance.getActionHandle();
        actionHandle.register(new LTExchange(actionHandle));
        actionHandle.register(new GetClutterItem(actionHandle));
        actionHandle.register(new OpenCoreGui(actionHandle));
        actionHandle.register(new TackGoldCoins(actionHandle));
        Class<JavaScriptAgent> javaScriptAgentClass = JavaScriptAgent.class;
        try {
            Field bindings = javaScriptAgentClass.getDeclaredField("bindings");
            bindings.setAccessible(true);
            Map<String, Object> map = (Map)bindings.get(null);
            map.put("LTCraftUtils", TrMenuUtils.getInstance());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private void initGM(){
        groupManager = ((GroupManager)Bukkit.getPluginManager().getPlugin("GroupManager"));
    }
    public void initProtocol(){
        protocolManager = ProtocolLibrary.getProtocolManager();
        adapters = new ArrayList<>();
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public void registerPacket(PacketAdapter adapter){
        adapters.add(adapter);
        protocolManager.addPacketListener(adapter);
    }
    public GroupManager getGroupManager() {
        return groupManager;
    }

    public Config getConfigOBJ() {
        return config;
    }
}