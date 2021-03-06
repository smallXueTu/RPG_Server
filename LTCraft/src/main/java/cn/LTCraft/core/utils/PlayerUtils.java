package cn.LTCraft.core.utils;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.task.PlayerClass;
import cn.LTCraft.core.game.skills.BaseSkill;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.ltcraft.love.Love;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.I18n;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.NoChargeException;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import net.minecraft.server.v1_12_R1.*;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.ConditionID;
import pl.betoncraft.betonquest.ObjectNotFoundException;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class PlayerUtils {
    /**
     * ????????????BQ??????
     * @param playerName ????????????
     * @param tag ??????
     * @return ????????????
     */
    public boolean hasBQTag(String playerName, String tag){
        pl.betoncraft.betonquest.database.PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(playerName));
        return playerData.hasTag(tag);
    }
    /**
     * ??????????????????
     * @param player ??????
     * @return ????????????
     */
    public static int getClassLevel(Player player){
        int level = 0;
        PlayerData data = SkillAPI.getPlayerData(player);
        if (data != null && data.getMainClass() != null){
            level = data.getMainClass().getLevel();
        }
        return level;
    }

    /**
     * ??????????????????
     * @param player ??????
     * @return ?????? {@link PlayerClass}
     */
    public static PlayerClass getClass(Player player){
        PlayerData data = SkillAPI.getPlayerData(player);
        if (data != null && data.getMainClass() != null){
            return PlayerClass.byName(data.getMainClass().getData().getName());
        }
        return PlayerClass.NONE;
    }

    /**
     * ??????????????????
     * @param player ??????
     * @return ?????? id
     */
    public static int getAccount(Player player){
        PlayerData data = SkillAPI.getPlayerData(player);
        if (data != null && data.getMainClass() != null){
            return SkillAPI.getPlayerAccountData(player).getActiveId();
        }
        return 0;
    }

    /**
     * ???????????????????????????BQ??????
     * @param player ??????
     * @param tag ??????
     * @return ?????????????????????BQ??????
     */
    public static boolean hasBQTag(Player player, String tag){
        return BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player)).hasTag(tag);
    }

    /**
     * ????????????bq??????
     * @param player ??????
     * @param condition ??????
     * @return ????????????
     */
    public static boolean satisfyBQCondition(Player player, String condition){
        try {
            String id = PlayerConverter.getID(player);
            ConditionID conditionID = new ConditionID(null, condition);
            return BetonQuest.condition(id, conditionID);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ????????????MM??????
     * @param player ??????
     * @param condition ??????
     * @return ????????????
     */
    public static boolean satisfyMMCondition(Player player, String condition){
        SkillCondition skillCondition = SkillCondition.getCondition(condition);
        return skillCondition.evaluateEntity(BukkitAdapter.adapt(player));
    }

    /**
     * ??????BQ??????
     * @param player ??????
     * @param tag ??????
     */
    public static void addBQTag(Player player, String tag){
        BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player)).addTag(tag);
    }

    /**
     * ??????BQ??????
     * @param player ??????
     * @param tag ??????
     */
    public static void removeBQTag(Player player, String tag){
        BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player)).removeTag(tag);
    }

    /**
     * ??????MM??????
     * @param player ??????
     * @param skillName ????????????
     */
    public static void castMMSkill(Player player, String skillName){
        List<Entity> targets = new ArrayList<Entity>();
        targets.add(player);
        MythicMobs.inst().getAPIHelper().castSkill(player, skillName, player, player.getLocation(), targets, null, 1.0F);
    }

    /**
     * ??????MM??????
     * @param player ??????
     * @param skillName ????????????
     * @param targets ??????
     */
    public static void castMMSkill(Player player, Entity trigger, String skillName, List<Entity> targets){
        MythicMobs.inst().getAPIHelper().castSkill(player, skillName, trigger, player.getLocation(), targets, null, 1.0F);
    }
    public static void castSkill(Player player, String skillName, List<Entity> targets, EntityDamageEvent event){
        if (Temp.silence.containsKey(player))return;//??????
        String[] split = skillName.split("\\|");
        if (split.length >= 2){
            String type = split[0];
            if (type.equals("MM") || type.equals("MMSkill")){
                EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;
                castMMSkill(player, player.equals(event1.getEntity())?event1.getDamager():player, split[1], targets);
            }
            if (type.equals("class")){
                castClassSkill(player, split[1], targets.get(0));
            }
            if (split.length >= 3){
                switch (split[2]){
                    case "??????":
                        event.setCancelled(true);
                    break;
                }
            }
            if (split.length >= 4){
                int c = Integer.parseInt(split[3]);
                Cooling.cooling(player, split[1], c, split[1] + "?????????%s%S");
            }
        }
    }

    /**
     * ????????????
     * @param player ??????
     * @param skillName  ????????????
     * @param target ????????????
     */
    public static void castClassSkill(Player player, String skillName, Entity target){
        if (Temp.silence.containsKey(player))return;//??????
        PlayerClass clazz = PlayerUtils.getClass(player);
        int level = PlayerUtils.getClassLevel(player);
        if (Cooling.isCooling(player, "ClassSkill")) {
            return;
        }
        MythicConfig classAtt = PlayerConfig.getPlayerConfig(player).getClassAttConfig();
        Set<String> skills = classAtt.getKeys("skills");
        if (skills==null || !skills.contains(skillName)){
            player.sendMessage("??c???????????????" + skillName + "?????????");
            return;
        }

        if (BaseSkill.getSkill(skillName) != null){
            BaseSkill skill = BaseSkill.getSkillObj(skillName, player, classAtt.getInteger("skills." + skillName + ".level"), classAtt.getInteger("skills." + skillName + ".awakenLevel"), classAtt.getInteger("skills." + skillName + ".awakenLevel") > 0);
            if (skill!=null){
                skill.cast(target);
                if (Game.getClassSkill(PlayerClass.byName(clazz.getName())).equals(skillName)) {
                    Cooling.cooling(player, "ClassSkill", 50 - Math.min(level, 60) / 3, "???????????????????????????????????????%s%S");
                }
            }
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     * @param player ??????
     * @param stacks ??????
     */
    public static void securityAddItem(Player player, ItemStack ...stacks){
        List<ItemStack> left = new ArrayList<>();
        Inventory inventory = player.getInventory();
        for (ItemStack stack : stacks) {
            left.addAll(inventory.addItem(stack).values());
        }
        for (ItemStack itemStack : left) {
            Item entityItem = player.getWorld().dropItem(player.getLocation(), itemStack);
            Temp.playerDropItem.put(entityItem, player.getName());
            Temp.discardOnly.add(entityItem);
        }
    }

    /**
     * ????????????????????????????????????
     * @param player ??????
     * @param stacks ??????
     */
    public static void dropItem(Player player, ItemStack ...stacks) {
        dropItem(player, player.getLocation(), stacks);
    }
    public static void dropItem(Player player, Location location, ItemStack ...stacks){
        for (ItemStack itemStack : stacks) {
            Item entityItem = location.getWorld().dropItem(location, itemStack);
            Temp.playerDropItem.put(entityItem, player.getName());
            Temp.discardOnly.add(entityItem);
        }
    }

    /**
     * ?????????????????????????????????????????????
     * @param player ??????
     * @param stacks ??????
     */
    public static void dropItemFloat(Player player, ItemStack ...stacks) {
        dropItemFloat(player, player.getLocation(), stacks);
    }
    public static void dropItemFloat(Player player, Location location, ItemStack ...stacks){
        for (ItemStack itemStack : stacks) {
            if (itemStack.getType() == Material.AIR)continue;
            CraftItem craftItem = (CraftItem) location.getWorld().dropItem(location, itemStack);
            Temp.playerDropItem.put(craftItem, player.getName());
            Temp.discardOnly.add(craftItem);
            craftItem.setMomentum(new Vector(0, 0, 0));
            craftItem.setGravity(false);
        }
    }

    /**
     * ???????????? ESS?????????
     * @param player ??????
     * @param kitNames ??????????????? ???,??????
     */
    public static void giveKits(Player player, String kitNames) {
        String[] kitList = kitNames.split(",");
        Essentials essentials = Main.getInstance().getEssentials();
        User user = new User(player, essentials);
        for (String kitName : kitList) {
            try {
                if (kitName.isEmpty())continue;
                Kit kit = new Kit(kitName, essentials);
                kit.checkPerms(user);
                kit.checkDelay(user);
                kit.checkAffordable(user);
                kit.setTime(user);
                kit.expandItems(user);
                kit.chargeUser(user);
            } catch (Exception ignore) {

            }
        }
    }

    /**
     * ???????????? ???????????????????????????
     * @param player ??????
     * @param message ??????
     */
    public static void sendActionMessage(Player player, String message){
        PlayerConnection conn = ((CraftPlayer)player).getHandle().playerConnection;
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), ChatMessageType.GAME_INFO);
        conn.sendPacket(packet);
    }
    public static void sendActionMessage(Collection<? extends Player> players, String message){
        for (Player player : players) {
            PlayerConnection conn = ((CraftPlayer)player).getHandle().playerConnection;
            PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), ChatMessageType.GAME_INFO);
            conn.sendPacket(packet);
        }
    }
    public static void sendActionMessage(String message){
        sendActionMessage(Bukkit.getOnlinePlayers(), message);
    }
    public static void setGroup(Player player, String group, String world){
        GroupManager groupManager = Main.getInstance().getGroupManager();
        OverloadedWorldHolder worldData = groupManager.getWorldsHolder().getWorldData(world);
        Group groupObj = worldData.getGroup(group);
        if (groupObj != null) {
            org.anjocaido.groupmanager.data.User user = worldData.getUser(player.getName());
            user.setGroup(groupObj);
        }
    }
    public static void setGroup(Player player, String group){
        setGroup(player, group, "world");
        setGroup(player, group, "zy");
    }

    /**
     * ????????????ID????????????
     * @param name ID
     * @return ????????????
     */
    public static boolean isLegitimateName(String name){
        return Pattern.matches("^\\w{3,}$", name);
    }

    public static Scoreboard scoreboard = null;
    public static void updatePlayerDisplayName(Player player){
        if (scoreboard == null)return;
        int classLevel = getClassLevel(player);
        Love.Sex sex = Love.getSex(player);
        String sexStr = sex == Love.Sex.NONE?"":"??d" + sex.getName();
        String s = "??eLV." + classLevel + sexStr + "??e";
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }
        team.setPrefix(s);
        team.addEntry(player.getName());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setScoreboard(team.getScoreboard());
        }
    }

    /**
     * ????????????????????????????????????
     * @param player ??????
     * @param command ??????
     */
    public static void sudoExec(Player player, String command){
        boolean op = player.isOp();
        if (!op)player.setOp(true);
        Bukkit.dispatchCommand(player, command);
        if (!op)player.setOp(false);
    }
}
