package cn.LTCraft.core.listener;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.commands.LTGCommand;
import cn.LTCraft.core.entityClass.TeleportGate;
import cn.LTCraft.core.game.TeleportGateManager;
import cn.LTCraft.core.hook.MM.mechanics.singletonSkill.AirDoor;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.task.PlayerClass;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.task.ClientCheckTask;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.utils.EntityUtils;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.ItemUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.ltcraft.teleport.Home;
import cn.ltcraft.teleport.Teleport;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerAccountChangeEvent;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerListener  implements Listener {
    public static Map<String, String[]> signEdit = new HashMap<>();

    /**
     * 玩家职业更新时间
     * @param event .
     */
    @EventHandler
    public void onAccountChange(PlayerAccountChangeEvent event){
        final Player player = event.getAccountData().getPlayer();
        PlayerConfig.getPlayerConfig(player).updateClassAttConfig();
    }
    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e){
        final Player player = e.getPlayer();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->{
            if (player.getHealth() > 0)player.setHealth(player.getMaxHealth());
            String s = (char)2 + "" + Main.id;
            player.sendPluginMessage(Main.getInstance(), "LTCraft", s.getBytes());
            Main.getInstance().addTack(Main.id, new ClientCheckTask(Main.id, "check", player));
            Main.id++;
        }, 20);
        if (!player.hasPlayedBefore()){
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
                player.teleport(player.getWorld().getSpawnLocation())
            , 1);
        }
        Temp.onPlayerJoin(player);
        new PlayerConfig(player);
    }

    /**
     * 玩家重生
     * @param event .
     */
    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event){
        final Player player = event.getPlayer();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
            player.setHealth(player.getMaxHealth())
        , 20);
        boolean sign = true;
        if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();
            if (entityDamageByEntityEvent.getDamager() instanceof Player)sign = false;
        }
        World world = player.getWorld();
        if (sign) {
            try {
                Home home = Teleport.getInstance().getPlayerHomes().get(player.getName()).get("mainline");
                if (home != null && home.getWorld().equals(player.getWorld())) {
                    event.setRespawnLocation(home.getLocation());
                }else if (Game.rpgWorlds.contains(world.getName())){

                    event.setRespawnLocation(player.getWorld().getSpawnLocation());
                }else {
                    sign = false;
                }
            } catch (Exception e) {
                sign = false;
            }
        }
        if (!sign){
            world = Bukkit.getWorld("world");
            event.setRespawnLocation(world.getSpawnLocation());
        }
    }

    /**
     * 玩家操作盔甲架
     * @param event .
     */
    @EventHandler
    public void onArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event){
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (!player.hasPermission("LTCraft.interact.armorStand."+entity.getCustomName()) &&
                !player.hasPermission("LTCraft.interact.armorStand.*") &&
                !player.hasPermission("LTCraft.interact.*")){
            event.setCancelled(true);
        }
    }
    /**
     * 阻止玩家无权限右键物品展示框
     * @param event .
     */
    @EventHandler
    public void onInteractEntityEvent(PlayerInteractEntityEvent event){
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (entity instanceof ItemFrame) {
            if (!player.hasPermission("LTCraft.interact.ItemFrame." + entity.getCustomName()) &&
                    !player.hasPermission("LTCraft.interact.ItemFrame.*") &&
                    !player.hasPermission("LTCraft.interact.*")) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 阻止 具有 LTCraft.antiBurning 权限节点的玩家燃烧
     * @param event .
     */
    @EventHandler
    public void onCombustEvent(EntityCombustEvent event){
        Entity entity = event.getEntity();
        if (entity instanceof Player && entity.hasPermission("LTCraft.antiBurning")){
            event.setCancelled(true);
        }
    }

    /**
     * 扑灭 具有 LTCraft.antiBurning 权限节点玩家的火
     * @param event .
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();
        if (entity instanceof Player && entity.hasPermission("LTCraft.antiBurning") && (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)){
            entity.setFireTicks(0);
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBucketFillEvent(PlayerBucketFillEvent event){
        Player player = event.getPlayer();
        if (player.hasPermission("LTCraft.noConsumptionBucketFill")){
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            event.setCancelled(true);
            Material material = event.getBlockClicked().getType() == Material.LAVA || event.getBlockClicked().getType() == Material.STATIONARY_LAVA?Material.LAVA_BUCKET:Material.WATER_BUCKET;
            if (itemStack.getAmount() == 1)
                player.getInventory().setItemInMainHand(new ItemStack(material));
            else{
                PlayerUtils.securityAddItem(player, new ItemStack(material));
                itemStack.setAmount(itemStack.getAmount() - 1);
            }
        }
    }
    @EventHandler
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event){
        Player player = event.getPlayer();
        if (player.hasPermission("LTCraft.noConsumptionBucketFill") && !player.isOp()){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        World world = player.getWorld();
        switch (action){
            case PHYSICAL:
                if (event.getClickedBlock().getType() == Material.SOIL){
                    event.setCancelled(true);
                }
            break;
            case RIGHT_CLICK_BLOCK:
                Game.onRightClickBlock(event);
            case RIGHT_CLICK_AIR:
                Game.onRightClick(player);
                ItemStack item = event.getItem();
                if (item!=null) {
                    NBTTagCompound nbt = ItemUtils.getNBT(item);
                    if (nbt != null && nbt.hasKey("Potion")){
                        String potions = nbt.getString("Potion");
                        if (potions.contains("strong_strength")){
                            player.sendMessage("§c力量药水在这个服务器是禁用的！");
                            event.setCancelled(true);
                        }
                    }
                }
                break;
            case LEFT_CLICK_BLOCK:
                if (block != null && Game.rpgWorlds.contains(world.getName())) {
                    BlockFace blockFace = event.getBlockFace();
                    Location add = block.getLocation().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
                    Block blockAt = world.getBlockAt(add);
                    if (blockAt.getTypeId() == 51){
                        event.setCancelled(true);
                    }
                }
            break;
        }
        if (event.isCancelled())return;
        if (block != null && block.getState() instanceof Sign){
            Sign blockState = (Sign)block.getState();
            if (signEdit.containsKey(player.getName())){
                blockState.setLine(0, signEdit.get(player.getName())[0]);
                blockState.setLine(1, signEdit.get(player.getName())[1]);
                blockState.setLine(2, signEdit.get(player.getName())[2]);
                blockState.setLine(3, signEdit.get(player.getName())[3]);
                blockState.update();
                signEdit.remove(player.getName());
            }
        }
    }

    /**
     * 玩家传送世界 判断玩家权限
     * @param event .
     */
    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onChangedWorld(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())){
            String toWorld = event.getTo().getWorld().getName();
            if (toWorld.startsWith("t")){
                try {
                    int level = Integer.parseInt(toWorld.substring(1));
                    PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                    if (level > 1 && !playerData.hasTag("default.毕业T" + (level - 1))){
                        event.setCancelled(true);
                        player.sendMessage("§c你需要完成T"+ (level - 1) + "才能前往" + toWorld + "！");
                    }
                }catch (NumberFormatException ignored){

                }
            }else{
                switch (toWorld){
                    case "f1":
                        if (PlayerUtils.getClass(player) == PlayerClass.NONE) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§c你需要选择一门职业才能前往F1！");
                        }
                    break;
                    case "RPG":
                        PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                        if (!playerData.hasTag("default.毕业T3")){
                            event.setCancelled(true);
                            player.sendMessage("§c你需要完成T3才能前往RPG！");
                        }
                    break;
                }
            }
            if (Config.getInstance().getWorldTitleYaml().contains(toWorld.toLowerCase())){
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                    List<String> list = Config.getInstance().getWorldTitleYaml().getStringList(toWorld.toLowerCase());
                    player.sendTitle(list.get(0), list.get(1), 60, 20, 20);
                }, 20);
            }
        }else if (Game.rpgWorlds.contains(player.getWorld().getName())){
            if (
                    event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL ||
                    event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT
            ){
                event.setCancelled(true);
            }
        }
        if (
                event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL ||
                event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL
        ){
            if (((CraftWorld)event.getFrom().getWorld()).getHandle().dimension == 1 || ((CraftWorld)event.getFrom().getWorld()).getHandle().dimension == -1){
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> player.teleport(Bukkit.getWorld("world").getSpawnLocation()));
                event.setTo(Bukkit.getWorld("world").getSpawnLocation());
            }else {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (event.getPlayer().getGameMode().compareTo(GameMode.SURVIVAL) == 0){
            //event.setCancelled(true);
        }
    }
    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerChatEvent(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        com.sucy.skill.api.player.PlayerData data;
        data = SkillAPI.getPlayerData(player);
        String clazz = "无职业";
        String prefix =  Main.getInstance().getChat().getPlayerPrefix(player).replace("&", "§");
        int level = 0;
        if (data != null && data.getMainClass() != null){
                clazz = data.getMainClass().getData().getName();
                level = data.getMainClass().getLevel();
        }
        event.setFormat("§f[§bLv.§e" + level + " §c" + clazz + "§f]" + prefix + "§e" + player.getName() + "§f: §r%2$s");
//        event.setMessage("§f[§bLv.§e" + level + " §c" + clazz + "§f]§e" + player.getName() + "§f: §e" + event.getMessage());
    }
    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e){
        Player player = e.getPlayer();
        GlobalRefresh.getLastObj().remove(player.getName());
        GlobalRefresh.warnings.remove(player.getName());
        Temp.onPlayerQuit(player);
        PlayerConfig.getConfigMap().get(player.getName()).save();
        PlayerConfig.getConfigMap().remove(player.getName());
        LTGCommand.mapList.remove(player);
        LTGCommand.mapGate.remove(player);
    }
    /**
     * 伤害计算
     * @param event
     */
    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onEntityDamageByEntityLow(EntityDamageByEntityEvent event){
        Player damager = null;
        Player entity = null;
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
            if (PlayerUtils.getClass(damager) == PlayerClass.ASSASSIN){
                Temp.addInjured(event.getEntity(), 20);
            }
        }
        if (event.getEntity() instanceof Player) {
            entity = (Player) event.getEntity();
            if (Temp.silence.containsKey(entity)){
                Temp.shield.get(entity).block(event);
            }
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event){
        event.setCancelled(true);
        event.getPlayer().sendMessage("§c服务器禁止F交换物品！");
    }
    private static final List<String> notDrop = Arrays.asList(
        "铁剑", "小型医疗水晶", "锋利的铁剑", "现币", "钢铁之刃",
        "鱼竿", "钢铁之刃", "交易盘", "皮革头盔", "皮革胸甲",
        "皮革护腿", "皮革靴子", "感应器"
    );
    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (!playerData.hasTag("default.毕业T3")){
            for (String s : notDrop) {
                if (ClutterItem.spawnClutterItem(s).isSimilar(itemStack)){
                    event.setCancelled(true);
                    player.sendMessage("§c你现在还不能丢弃这个物品！");
                    return;
                }
            }
        }
        if (Temp.dropCount.containsKey(player) && Temp.dropCount.get(player) >= 120){
            player.sendMessage("§c五分钟内最大丢弃物品数：120。");
            event.setCancelled(true);
            return;
        }
        Temp.playerDropItem.put(event.getItemDrop(), player.getName());
        if (Temp.dropCount.containsKey(player))
            Temp.dropCount.put(player, Temp.dropCount.get(player) + 1);
        else
            Temp.dropCount.put(player, 1);
        if (player.getWorld().getName().equals("t3") && player.getLocation().distance(new Location(player.getWorld(), 258.5, 9, 148.5)) < 20){
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                if (player.isOnline() && Game.demand(player, "custom1")){
                    player.sendTitle("§8[§a恭喜§8]", "§6解开T3第一关谜团！");
                    player.sendMessage("§a你现在可以通过火焰粒子门了！");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 100);
                    playerData.addTag("default.解开t3第一关谜团");
                }
            }, 20);
        }else if (player.getWorld().getName().equals("t3") && player.getLocation().distance(new Location(player.getWorld(), 140.5, 4, 473.5)) < 20){
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                if (player.isOnline() && Game.demand(player, "custom2")){
                    player.sendTitle("§8[§a恭喜§8]", "§6解开T3第二关谜团！");
                    player.sendMessage("§a你现在可以通过火焰粒子门了！");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 100);
                    playerData.addTag("default.解开t3第二关谜团");
                }
            }, 200);
        }
    }

    /**
     * 玩家捡起物品事件
     * 判断是否可以捡起
     * @param event .
     */
    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event){
        Item item = event.getItem();
        if (Temp.playerDropItem.containsKey(item)){
            if (
                    EntityUtils.getItemAge(item) <= 600 && //30s * 20tick
                    !Temp.playerDropItem.get(item).equals(event.getPlayer().getName()) &&
                    Temp.discardOnly.contains(item)
            ){
                event.setCancelled(true);
            }
        }
        ItemStack itemStack = item.getItemStack();
        Player player = event.getPlayer();
        if (ClutterItem.spawnClutterItem("凝固经验块").isSimilar(itemStack)){
            ItemStack inHand = player.getItemInHand();
            ClutterItem result = ClutterItem.spawnClutterItem("知识之瓶");
            if (inHand.getType() == Material.GLASS_BOTTLE && !inHand.hasItemMeta()){
                int amount = itemStack.getAmount();
                if (amount < inHand.getAmount()){
                    inHand.setAmount(inHand.getAmount() - amount);
                    result.setNumber(amount);
                    amount = 0;
                } else {
                    result.setNumber(inHand.getAmount());
                    inHand = new ItemStack(Material.AIR);
                    amount -= result.getNumber();
                }
                itemStack.setAmount(amount);
                if (amount <= 0){
                    item.remove();
                }else {
                    item.setItemStack(itemStack);
                }
                if (inHand.getType() == Material.AIR){
                    player.getInventory().setItemInMainHand(result.generate());
                }else {
                    PlayerUtils.securityAddItem(player, result.generate());
                }
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
            }else {
                if (item.getPickupDelay() != 20)item.setPickupDelay(20);
                player.sendMessage("§c你需要手持玻璃瓶才能捡起凝固经验块！");
            }
            event.setCancelled(true);
        }
    }

    /**
     * 玩家移动事件，判断玩家移动的目标是否可以通过
     * 空气门，判断玩家是否满足指定条件
     * @see AirDoor
     * @param event .
     */
    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event){
        if (event.getTo().getWorld() == event.getFrom().getWorld()){
            Player player = event.getPlayer();
            Location from = event.getFrom();
            Location to = event.getTo();
            if (Game.rpgWorlds.contains(event.getPlayer().getWorld().getName())) {
                boolean sign = false;
                AirDoor airDoor = null;
                for (Iterator<AirDoor> iterator = AirDoor.getAirDoors().iterator(); iterator.hasNext(); ) {
                    airDoor = iterator.next();
                    if (airDoor.getBukkitEntity().isDead()) {
                        iterator.remove();
                        continue;
                    }
                    if (airDoor.getBukkitEntity().getWorld() != player.getWorld() || airDoor.getBukkitEntity().getLocation().distance(player.getLocation()) > airDoor.getDistance())
                        continue;
                    double fLocation = airDoor.isX() ? from.getX() : from.getZ();
                    double tLocation = airDoor.isX() ? to.getX() : to.getZ();
                    if (airDoor.isForward()) {
                        if (fLocation <= airDoor.getLocation() && tLocation > airDoor.getLocation()) {
                            sign = true;
                        }
                    } else {
                        if (fLocation >= airDoor.getLocation() && tLocation < airDoor.getLocation()) {
                            sign = true;
                        }
                    }
                    if (sign) {
                        if (Game.demand(player, airDoor.getDemand())) {
                            Game.execute(player, airDoor.getSuccess());
                        } else {
                            Game.execute(player, airDoor.getFail(), event, airDoor);
                            break;
                        }
                    }
                }
                if (new Location(player.getWorld(), 198, 9, 474).distance(player.getLocation()) < 100) {
                    Block block = player.getWorld().getBlockAt(event.getTo());
                    if (Game.burningBlocks.contains(block.getTypeId())) {
                        sign = false;
                        for (ItemStack armorContent : ((Player) player).getInventory().getArmorContents()) {
                            if (armorContent == null || armorContent.getItemMeta() == null || !armorContent.getItemMeta().getDisplayName().startsWith("§6虚空寒冰"))
                                sign = true;
                        }
                        if (sign) {
                            ((CraftPlayer) player).getHandle().setHealth(0);
                            ((CraftPlayer) player).getHandle().getCombatTracker().trackDamage(DamageSource.BURN, 0f, 0f);
                            ((CraftPlayer) player).getHandle().die(DamageSource.BURN);
                        }
                    }
                }
            }
            /*
            传送门
             */
            if (TeleportGate.blockIDs.contains(to.getBlock().getTypeId())){
                TeleportGateManager.getInstance().onAfter(player);
            }
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event){
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack bow = event.getBow();
            if (
                    ClutterItem.spawnClutterItem("虚焱弓").isSimilar(bow) ||
                            ClutterItem.spawnClutterItem("残破的虚焱弓").isSimilar(bow)
            ){
                if (ClutterItem.spawnClutterItem("虚空之箭").isSimilar(player.getInventory().getItemInOffHand())) {
                    ((CraftEntity) event.getProjectile()).getHandle().setCustomName("虚空之箭");
                }else {
                    PlayerUtils.securityAddItem(player, new ItemStack(Material.ARROW));
                    player.sendMessage("§c你需要将虚空之箭放到副手才能使用这把弓！");
                    event.setCancelled(true);
                }
            }else {
                if (player.getWorld().getName().equals("t3")) {
                    ((CraftEntity) event.getProjectile()).getHandle().motX = 0;
                    ((CraftEntity) event.getProjectile()).getHandle().motY = 0;
                    ((CraftEntity) event.getProjectile()).getHandle().motZ = 0;
                    ((CraftEntity) event.getProjectile()).getHandle().impulse = true;
                    player.sendMessage("§c这个时空因为重力问题普通弓无法射出箭...");
                }
            }
        }
    }

    /**
     * TODO: 玩家格挡后冷却盾牌
     */
    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onEntityDamageByEntityHigh(EntityDamageByEntityEvent event){
        CraftPlayer[] craftPlayers = new CraftPlayer[2];
        if (event.getEntity() instanceof Player) {
            craftPlayers[1] = ((CraftPlayer)event.getEntity());
        }
        for (CraftPlayer craftPlayer : craftPlayers) {
            if (craftPlayer == null)continue;
            ItemStack itemStack = craftPlayer.getInventory().getItemInOffHand();
            if (itemStack == null)continue;
            if (
                    itemStack.getTypeId() == 442
            ){
                craftPlayer.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                PlayerUtils.securityAddItem(craftPlayer, itemStack);
                craftPlayer.sendMessage("§c服务器暂时不支持盾牌！");
            }
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (LTGCommand.mapGate.containsKey(player)) {
            LTGCommand.mapList.get(player).add(block.getLocation());
        }
    }
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        NBTTagCompound nbt = ItemUtils.getNBT(item);
        if (nbt != null && nbt.hasKey("Potion")){
            String potions = nbt.getString("Potion");
            if (potions.contains("strong_strength")){
                player.sendMessage("§c力量药水在这个服务器是禁用的！");
                event.setCancelled(true);
            }
        }
    }
}
