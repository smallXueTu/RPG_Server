package cn.ltcraft.item;

import cn.LTCraft.core.dataBase.bean.PlayerInfo;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.game.more.FloatText;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.*;
import cn.ltcraft.item.base.interfaces.Attribute;
import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.objs.PlayerAttribute;
import cn.ltcraft.item.base.subAttrbute.PotionAttribute;
import cn.ltcraft.item.utils.TriggerAction;
import cn.ltcraft.item.utils.Utils;
import com.earth2me.essentials.api.Economy;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import com.sucy.skill.api.event.PlayerAccountChangeEvent;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.io.MythicConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AllListener implements Listener {
    @EventHandler
    public void onChangedMainHand(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        PlayerAttribute.getPlayerAttribute(player).onChangeHand(event.getNewSlot());
    }
    @EventHandler
    public void onAccountChange(PlayerAccountChangeEvent event){
        final Player player = event.getAccountData().getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(LTItemSystem.getInstance(), () -> {
            PlayerAttribute.getPlayerAttribute(player).reset();
        }, 10);
    }
    @EventHandler
    public void onClassChange(PlayerClassChangeEvent event){
        final Player player = event.getPlayerData().getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(LTItemSystem.getInstance(), () -> {
            PlayerAttribute.getPlayerAttribute(player).reset();
        }, 10);
    }
    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        PlayerAttribute.getPlayerAttribute(player);
    }
    @EventHandler
    public void onQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        PlayerAttribute.getPlayerAttributeMap().remove(player.getName());
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        PlayerAttribute.getPlayerAttribute(event.getPlayer()).reset();
    }
    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.LOWEST
    )
    public void onDamageByEntityLOWEST(EntityDamageByEntityEvent event){
        //删除原版护甲
        Entity damager = event.getDamager();
        if (damager instanceof Player || MythicMobs.inst().getMobManager().isActiveMob(damager.getUniqueId())) {
            if (event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) != 0)
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
            if (event.getDamage(EntityDamageEvent.DamageModifier.HARD_HAT) != 0)
                event.setDamage(EntityDamageEvent.DamageModifier.HARD_HAT, 0);
        }
        Entity entity = event.getEntity();
        if (damager instanceof Arrow){
            ProjectileSource shooter = ((Arrow) damager).getShooter();
            if (shooter instanceof Player){
                damager = ((Player) shooter);
            }
        }
        if (damager instanceof Player){
            Player damagerPlayer = ((Player) damager).getPlayer();
            ItemStack itemInHand = damagerPlayer.getItemInHand();
            Material type = itemInHand.getType();
            if (type == Material.BOW){
                LTItem ltItems = Utils.getLTItems(itemInHand);
                if (ltItems != null && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                    return;
                }
            }
            double originalDamage = ((CraftPlayer) damagerPlayer).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
            EntityPlayer handle = ((CraftPlayer) damagerPlayer).getHandle();
            if (entity instanceof LivingEntity) {
                originalDamage += EnchantmentManager.a(handle.getItemInMainHand(), ((EntityLiving) ((CraftEntity) entity).getHandle()).getMonsterType());
            }
            PlayerAttribute damagerAttribute = PlayerAttribute.getPlayerAttribute(damagerPlayer);
            //设置伤害
            double p = Math.min(event.getDamage() / originalDamage, 1.2);
            double damage = damagerAttribute.getDamage(entity).getValue();
            if (damage > 1){
                double v = damage * p;
                if (MathUtils.ifAdopt(damagerAttribute.getCriticalRate(entity))){
                    v = damagerAttribute.getCritical(entity).getValue(v);
                }
                event.setDamage(v);
            }
            NBTTagCompound nbt = ItemUtils.getNBT(itemInHand);
            LTItem ltItem = Utils.getLTItems(nbt);
            if (ltItem != null && ltItem.binding()) {
                if (!ItemUtils.canUse(nbt, damagerPlayer)){
                    return;
                }
            }
            if (ltItem instanceof ConfigurableLTItem){
                ConfigurableLTItem configurable = (ConfigurableLTItem) ltItem;
                Utils.action(damagerPlayer, configurable, "攻击动作", event);
            }
        }
        if (event.getEntity() instanceof Player){
            Player entityPlayer = ((Player) event.getEntity()).getPlayer();
            PlayerAttribute entityAttribute = PlayerAttribute.getPlayerAttribute(entityPlayer);
            /*
             * Miss
             */
            if (MathUtils.ifAdopt(entityAttribute.getDodge())){
                EntityUtils.missAttack(entity);
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onDamageByEntityHIGHEST(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof Arrow){
            ProjectileSource shooter = ((Arrow) damager).getShooter();
            if (shooter instanceof Player){
                damager = ((Player) shooter);
            }
        }
        Player damagerPlayer = null;
        PlayerAttribute playerAttribute = null;
        if (damager instanceof Player){
            damagerPlayer = ((Player) damager).getPlayer();
            ItemStack itemInHand = damagerPlayer.getItemInHand();
            Material type = itemInHand.getType();
            if (type == Material.BOW){
                LTItem ltItems = Utils.getLTItems(itemInHand);
                if (ltItems != null && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                    onEntityDamage(event);
                    return;
                }
            }
            playerAttribute = PlayerAttribute.getPlayerAttribute(damagerPlayer);
            //设置燃烧
            entity.setFireTicks((int) playerAttribute.getBurning(entity) * 20);
            //召唤闪电
            if (MathUtils.ifAdopt(playerAttribute.getLightning(entity))) entity.getWorld().strikeLightning(entity.getLocation());
            //击退和击飞
            EntityUtils.repel(entity, damagerPlayer.getLocation(), playerAttribute.getRepel(entity), playerAttribute.getStrikeFly(entity));
            //自身药水效果
            for (Map.Entry<PotionEffectType, PotionAttribute> potionEffectTypePotionAttributeEntry : playerAttribute.getSelfPotion(entity).entrySet()) {
                if (MathUtils.ifAdopt(potionEffectTypePotionAttributeEntry.getValue().getProbability()))damagerPlayer.addPotionEffect(potionEffectTypePotionAttributeEntry.getValue());
            }
            //对方药水效果
            if (entity instanceof LivingEntity) {
                for (Map.Entry<PotionEffectType, PotionAttribute> potionEffectTypePotionAttributeEntry : playerAttribute.getEntityPotion(entity).entrySet()) {
                    if (MathUtils.ifAdopt(potionEffectTypePotionAttributeEntry.getValue().getProbability()))
                        ((LivingEntity) entity).addPotionEffect(potionEffectTypePotionAttributeEntry.getValue());
                }
            }
        }
        //受伤者是玩家
        MythicConfig mythicConfig;
        if (event.getEntity() instanceof Player){
            Player entityPlayer = ((Player) event.getEntity()).getPlayer();
            PlayerAttribute entityAttribute = PlayerAttribute.getPlayerAttribute(entityPlayer);
            //护甲
            int armorValue = entityAttribute.getArmorValue();
            //计算攻击者的穿甲
            if (damagerPlayer != null){
                armorValue -= armorValue * playerAttribute.getNailPiercing(damagerPlayer);
            }
            if (Temp.armorBreaking.containsKey(entity)){
                armorValue -= armorValue * Temp.armorBreaking.get(entity).value;
            }
            //计算伤害
            event.setDamage(event.getDamage() - event.getDamage() * MathUtils.getInjuryFreePercentage(armorValue));
            event.setDamage(event.getDamage() - event.getDamage() * entityAttribute.getInjuryFree(entityPlayer));
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                if (entityAttribute.getBackInjury() > 0) {
                    double damage = event.getDamage() * entityAttribute.getBackInjury();
                    ((CraftLivingEntity)damager).getHandle().damageEntity(DamageSource.MAGIC, (float) damage);
                }
            }
            Entity finalDamager = damager;
            entityAttribute.getInjuredSkill(damager).forEach((skill, probability) -> {
                if (MathUtils.ifAdopt(probability / 100)){
                    List<Entity> targets = new ArrayList<>();
                    targets.add(finalDamager);
                    PlayerUtils.castSkill(entityPlayer, skill, targets, event);
                }
            });
        }else if ((mythicConfig = EntityUtils.getMythicMobConfig(entity)) != null){//MythicMobs支持护甲
            int armorValue = mythicConfig.getInteger("护甲", 0);
            if (armorValue > 0) {
                //计算攻击者的穿甲
                if (damagerPlayer != null) {
                    armorValue -= armorValue * playerAttribute.getNailPiercing(damagerPlayer);
                }
                if (Temp.armorBreaking.containsKey(entity)){
                    armorValue -= armorValue * Temp.armorBreaking.get(entity).value;
                }
                //计算伤害
                event.setDamage(event.getDamage() - event.getDamage() * MathUtils.getInjuryFreePercentage(armorValue));
            }
        }
        if (damager instanceof Player){
            ItemStack itemInHand = damagerPlayer.getItemInHand();
            Material type = itemInHand.getType();
            if (type == Material.BOW){
                LTItem ltItems = Utils.getLTItems(itemInHand);
                if (ltItems != null && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                    onEntityDamage(event);
                    return;
                }
            }
            //最终伤害
            double damage = event.getDamage();
            //吸血和恢复生命值
            double recoveryHealth = 0;
            if (entity instanceof Player) {
                recoveryHealth += playerAttribute.getAttackRecovery(Attribute.Type.PVP) + damage * playerAttribute.getSuckingBlood(Attribute.Type.PVP);
            }else {
                ActiveMob mythicMob = EntityUtils.getMythicMob(entity);
                if (mythicMob != null) {
                    MythicConfig config = mythicMob.getType().getConfig();
                    if (config.getBoolean("团队", false)) {
                        EntityUtils.rangeRecovery(damagerPlayer.getLocation(), 5, playerAttribute.getGroupGyrus());
                        recoveryHealth += playerAttribute.getAttackRecovery(Attribute.Type.PVE);
                    }else {
                        recoveryHealth += playerAttribute.getAttackRecovery(Attribute.Type.PVE) + damage * playerAttribute.getSuckingBlood(Attribute.Type.PVE);
                    }
                }
            }
            if (recoveryHealth >= 1) ((CraftPlayer) damagerPlayer).getHandle().heal((float) recoveryHealth);
            Player finalDamagerPlayer = damagerPlayer;
            playerAttribute.getAttackSkill(entity).forEach((skill, probability) -> {
                if (MathUtils.ifAdopt(probability / 100)){
                    List<Entity> targets = new ArrayList<>();
                    targets.add(entity);
                    PlayerUtils.castSkill(finalDamagerPlayer, skill, targets, event);
                }
            });
            TriggerAction.onAttack(damagerPlayer, event);
        }
        onEntityDamage(event);
    }
    public void onEntityDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();
        if (entity instanceof Player){
            Player player = (Player) entity;
            TriggerAction.onDamage(player, event);
        }
        if (entity instanceof LivingEntity && event.getDamage() >= 1){
            if (((CraftEntity) entity).getHandle().isInvisible())return;
            new FloatText(entity.getLocation().add(0, entity.getHeight(), 0), "§l§c-" + cn.LTCraft.core.utils.Utils.formatNumber(event.getFinalDamage()), 2 * 20, new Vector(0, 0.03, 0));
        }
    }
    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onRegainHealth(EntityRegainHealthEvent event){
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && event.getAmount() >= 1){
            if (entity.getType() == EntityType.ARMOR_STAND)return;
            new FloatText(entity.getLocation().add(0, entity.getHeight(), 0), "§l§a+"  + cn.LTCraft.core.utils.Utils.formatNumber(event.getAmount()), 2 * 20, new Vector(0, 0.03, 0));
        }
    }
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event){
        ItemStack item = event.getInventory().getItem(0);
        ItemStack result = event.getResult();
        if (Utils.getLTItems(result) != null) {
            if (item != null && result != null && item.getTypeId() != 0 && result.getTypeId() != 0 && item.hasItemMeta() && result.hasItemMeta() && item.getItemMeta().getDisplayName() != null) {
                if (!item.getItemMeta().getDisplayName().replace("§", "").equals(result.getItemMeta().getDisplayName())) {
                    event.setResult(new ItemStack(Material.AIR));
                    event.getView().getPlayer().sendMessage("§c特殊物品不支持改名！");
                }
                ItemMeta itemMeta = result.getItemMeta();
                itemMeta.setDisplayName(item.getItemMeta().getDisplayName());
                result.setItemMeta(itemMeta);
                event.setResult(result);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        String click = "";
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            click = "右键动作";
        else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            click = "左键动作";
        if (click.isEmpty())return;
        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();
        NBTTagCompound nbt = ItemUtils.getNBT(inHand);
        LTItem ltItem = Utils.getLTItems(nbt);
        if (ltItem != null && ltItem.binding()) {
            if (!ItemUtils.canUse(nbt, player)){
                return;
            }
        }
        if (ltItem instanceof ConfigurableLTItem){
            ConfigurableLTItem configurable = (ConfigurableLTItem) ltItem;
            Utils.action(player, configurable, click);
        }
    }
    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onDropItemEvent(PlayerDropItemEvent event){
        ItemStack itemStack = event.getItemDrop().getItemStack();
        NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
        LTItem ltItem = Utils.getLTItems(nbt);
        if (ltItem instanceof ConfigurableLTItem && ((ConfigurableLTItem)ltItem).getConfig().getBoolean("不可叠加")){
            event.getItemDrop().setItemStack(ltItem.generate(itemStack.getAmount()));
        }
    }
    @EventHandler
    public void onPickupItemEvent(PlayerPickupItemEvent event){
        ItemStack itemStack = event.getItem().getItemStack();
        Player player = event.getPlayer();
        NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
        LTItem ltItem = Utils.getLTItems(nbt);
        if (ltItem != null && ltItem.binding()) {
            if (!ItemUtils.canUse(nbt, player)){
                return;
            }
        }
        if (ltItem instanceof ConfigurableLTItem) {
            ConfigurableLTItem configurableLTItem = (ConfigurableLTItem) ltItem;
            String action = configurableLTItem.getConfig().getString("捡起动作", "无");
            String[] split = action.split("&");
            for (String s : split) {
                if (!s.equals("无")){
                    String[] split1 = s.split(":");
                    int number;
                    switch(split1[0]){
                        case "增加金币":
                            number = Integer.parseInt(split1[1]);
                            PlayerInfo playerInfo = PlayerConfig.getPlayerConfig(player).getPlayerInfo();
                            playerInfo.addGold(number * itemStack.getAmount());
                            player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1, 1);
                        break;
                        case "增加橙币":
                            number = Integer.parseInt(split1[1]);
                            Main.getInstance().getEconomy().withdrawPlayer(player, number * itemStack.getAmount());
                            player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1, 1);
                        break;
                    }
                    if (split1.length > 2){
                        switch (split1[2]){
                            case "取消杀死":
                                event.getItem().remove();
                                event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCombustEvent(EntityCombustEvent event){
        if (event.getEntity() instanceof Player) {
            TriggerAction.onCombust(((Player) event.getEntity()), event);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        ItemStack itemStack = event.getItemInHand();
        NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
        LTItem ltItem = Utils.getLTItems(nbt);
        if (ltItem instanceof cn.ltcraft.item.items.Material){
            cn.ltcraft.item.items.Material material = (cn.ltcraft.item.items.Material) ltItem;
            if (material.getConfig().contains("放置注入属性")){//todo
                Block block = event.getBlockPlaced();
                if (block.getState() instanceof CraftBlockEntityState){
//                    ConfigurationSection section = material.getConfig().getConfigurationSection("放置注入属性");
//                    CraftBlockEntityState<?> blockState = (CraftBlockEntityState<?>) block.getState();
//                    NBTTagCompound snapshotNBT = blockState.getSnapshotNBT();
//                    for (String key : section.getKeys(false)) {
//                        snapshotNBT.setString(key, section.getString(key));
//                    }
////                    try {
////                        Method getTileEntity = CraftBlockEntityState.class.getDeclaredMethod("getTileEntity");
////                        getTileEntity.setAccessible(true);
////                        TileEntity tileEntity = ((TileEntity) getTileEntity.invoke(blockState));
////                        tileEntity.save(snapshotNBT);
////                    }catch (Exception exception){
////                        exception.printStackTrace();
////                    }
//                    Location location = block.getLocation();
//                    Bukkit.getScheduler().runTaskLater(LTItemSystem.getInstance(), () -> {
//                        TileEntity tileEntity = ((CraftWorld) location.getWorld()).getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
//                        tileEntity.load(snapshotNBT);
//                    }, 10L);
                }
            }
        }
    }
}
