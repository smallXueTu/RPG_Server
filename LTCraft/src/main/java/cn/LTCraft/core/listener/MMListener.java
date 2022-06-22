package cn.LTCraft.core.listener;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.hook.MM.conditions.*;
import cn.LTCraft.core.hook.MM.mechanics.singletonSkill.*;
import cn.LTCraft.core.hook.MM.mechanics.*;
import cn.LTCraft.core.hook.MM.drop.*;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.EntityUtils;
import cn.LTCraft.core.utils.ProxyUtils;
import io.lumine.xikage.mythicmobs.api.bukkit.events.*;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.minecraft.server.v1_12_R1.AttributeInstance;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MMListener implements Listener {
    @EventHandler
    public void onMythicConditionLoad(MythicConditionLoadEvent event){
        if (event.getConditionName().equalsIgnoreCase("WeaponDamage")){
            event.register(new WeaponDamage(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("HasObjective")){
            event.register(new HasObjective(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("HasItem")){
            event.register(new HasItem(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("HasBQTag")){
            event.register(new HasBQTag(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("IsSilence")){
            event.register(new IsSilence(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("LTWearing")){
            event.register(new LTWearingCondition(event.getConfig().getLine(), event.getConfig()));
        }else if (event.getConditionName().equalsIgnoreCase("MainLinePositionDistance")){
            event.register(new MainLinePositionDistance(event.getConfig().getLine(), event.getConfig()));
        }
    }
    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("modifySlotItem")){
            event.register(new ModifySlotItem(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("DropCheckS")){
            event.register(new DropCheckS(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("targetCommand")){
            event.register(new TargetCommand(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("roundSummon")){
            event.register(new RoundSummon(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("particleDoor")){
            event.register(new ParticleDoor(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("airDoor")){
            event.register(new AirDoor(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("sendAction")){
            event.register(new SendAction(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("execBQEvent")){
            event.register(new ExecBQEvent(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("addBQTag")){
            event.register(new AddBQTag(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("startAnimation")){
            event.register(new StartAnimation(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("disarm")){
            event.register(new Disarm(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("dropGoldCoins")){
            event.register(new DropGoldCoins(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("summonTheEnemy")){
            event.register(new SummonForMob(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("escort")){
            event.register(new Escort(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("callVigilant")){
            event.register(new CallForHelp(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getMechanicName().equalsIgnoreCase("randomLightning")){
            event.register(new RandomLightning(event.getContainer().getConfigLine(), event.getConfig()));
        }
    }

    /**
     * TODO: 重写MythicMobs掉落系统，保护带掉落物仅能被击杀者捡起
     * {@link cn.LTCraft.core.other.Temp#protectItem(org.bukkit.entity.Player, org.bukkit.entity.Item)}
     * @param event 事件
     */
    @EventHandler
    public void onMythicDropLoad(MythicDropLoadEvent event) {
        if (event.getDropName().equalsIgnoreCase("LTItem")){
            event.register(new LTItemDrop(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getDropName().equalsIgnoreCase("GoldCoinsDrop")){
            event.register(new GoldCoinsDrop(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getDropName().equalsIgnoreCase("GoldCoins")){
            event.register(new GoldCoins(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getDropName().equalsIgnoreCase("PseudorandomDrop")){
            PseudorandomDrop inheritProxy = ProxyUtils.getInheritProxy(PseudorandomDrop.class, new Class[]{String.class, MythicLineConfig.class}, new Object[]{event.getContainer().getConfigLine(), event.getConfig()});
            event.register(inheritProxy);
        }else if (event.getDropName().equalsIgnoreCase("ParticipateInDrop")){
            ParticipateInDrop inheritProxy = ProxyUtils.getInheritProxy(ParticipateInDrop.class, new Class[]{String.class, MythicLineConfig.class}, new Object[]{event.getContainer().getConfigLine(), event.getConfig()});
            event.register(inheritProxy);
        }else if (event.getDropName().equalsIgnoreCase("ParticipateInSAPExp")){
            event.register(new ParticipateInSAPExp(event.getContainer().getConfigLine(), event.getConfig()));
        }else if (event.getDropName().equalsIgnoreCase("ParticipateInGoldCoinsDrop")){
            event.register(new ParticipateInGoldCoinsDrop(event.getContainer().getConfigLine(), event.getConfig()));
        }
    }
    public static final Map<Integer, Map<String, List<PlayerDamage>>> damages = new HashMap<>();
    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event){
        ActiveMob mob = event.getMob();
        MythicConfig config = mob.getType().getConfig();
        if (config.getBoolean("团队")) {
            damages.remove(event.getEntity().getEntityId());
        }
    }
    @EventHandler(
        priority = EventPriority.HIGHEST
    )
    public void onDamageByEntity(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            MythicConfig mythicMobConfig = EntityUtils.getMythicMobConfig(entity);
            if (mythicMobConfig != null) {
                if (mythicMobConfig.getBoolean("团队")) {
                    Map<String, List<PlayerDamage>> stringListMap = damages.computeIfAbsent(entity.getEntityId(), k -> new HashMap<>());
                    List<PlayerDamage> list = stringListMap.computeIfAbsent(player.getName(), k -> new ArrayList<>());
                    list.add(new PlayerDamage(GlobalRefresh.getTick(), event.getDamage()));
                }
            }
        }else {
            MythicConfig mythicMobConfig = EntityUtils.getMythicMobConfig(damager);
            if (mythicMobConfig != null){
                if (mythicMobConfig.getBoolean("团队")) {
                    if (entity instanceof Player){
                        AttributeInstance attributeInstance = ((CraftPlayer) entity).getHandle().getAttributeInstance(GenericAttributes.c);
                        double value = attributeInstance.getValue();
                        attributeInstance.setValue(1);
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> attributeInstance.setValue(value), 0);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onSpawn(EntitySpawnEvent event){
        Entity entity = event.getEntity();
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            MythicConfig mythicMobConfig = EntityUtils.getMythicMobConfig(entity);
            if (mythicMobConfig != null) {
                if (mythicMobConfig.getBoolean("团队")) {
                    double aDouble = mythicMobConfig.getDouble("攻击范围", 0d);
                    if (aDouble > 0.5) {
                        net.minecraft.server.v1_12_R1.Entity handle = ((CraftEntity) entity).getHandle();
                        handle.width = (float) ((float) aDouble / 1.5);
                    }
                }
            }
        }, 0);
    }
    public static class PlayerDamage{
        private final long tick;
        private final double damage;

        public PlayerDamage(long tick, double damage) {
            this.tick = tick;
            this.damage = damage;
        }
    }
}
