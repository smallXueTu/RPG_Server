package cn.LTCraft.core.listener;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.Main;
import cn.LTCraft.core.commands.LTGCommand;
import cn.LTCraft.core.commands.PrefixCommand;
import cn.LTCraft.core.dataBase.bean.PlayerInfo;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.entityClass.TeleportGate;
import cn.LTCraft.core.game.Game;
import cn.LTCraft.core.game.SmeltingFurnace;
import cn.LTCraft.core.game.TargetOnlyMobsManager;
import cn.LTCraft.core.game.TeleportGateManager;
import cn.LTCraft.core.game.more.FakeBlock;
import cn.LTCraft.core.game.more.FlashingBlock;
import cn.LTCraft.core.game.more.SmeltingFurnaceDrawing;
import cn.LTCraft.core.game.skills.shields.Shield;
import cn.LTCraft.core.hook.MM.mechanics.singletonSkill.AirDoor;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.task.BQObjectiveCheck;
import cn.LTCraft.core.task.ClientCheckTask;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.task.PlayerClass;
import cn.LTCraft.core.utils.*;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.utils.Utils;
import cn.ltcraft.love.Love;
import cn.ltcraft.teleport.Home;
import cn.ltcraft.teleport.Teleport;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerAccountChangeEvent;
import com.sucy.skill.api.event.PlayerLevelUpEvent;
import io.lumine.utils.config.file.YamlConfiguration;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.*;

public class PlayerListener  implements Listener {
    public static Map<String, String[]> signEdit = new HashMap<>();

    /**
     * ????????????????????????
     * @param event .
     */
    @EventHandler
    public void onAccountChange(PlayerAccountChangeEvent event){
        Player player = event.getAccountData().getPlayer();
        PlayerConfig.getPlayerConfig(player).updateClassAttConfig(event.getNewID());
    }
    /**
     * ????????????????????????
     * @param event .
     */
    @EventHandler
    public void onLevelChange(PlayerLevelUpEvent event){
        Player player = event.getPlayerData().getPlayer();
        PlayerUtils.updatePlayerDisplayName(player);
    }
    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e){
        final Player player = e.getPlayer();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->{
            String s = (char)2 + "" + Main.id;
            player.sendPluginMessage(Main.getInstance(), "LTCraft", s.getBytes());
            Main.getInstance().addTack(Main.id, new ClientCheckTask(Main.id, "check", player));
            Main.id++;
        }, 20);
        if (!player.hasPlayedBefore()){
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
                player.teleport(player.getWorld().getSpawnLocation())
            , 1);
            PlayerUtils.giveKits(player, "tools");
        }
        Temp.onPlayerJoin(player);
        new PlayerConfig(player);
        PlayerUtils.updatePlayerDisplayName(player);
        e.setJoinMessage(null);
    }
    @EventHandler
    public void onPreLogin(PlayerPreLoginEvent event){
        if (!cn.LTCraft.core.utils.Utils.checkName(event.getName())){
            event.setKickMessage("???????????????:" + event.getName() + "???????????????????????????_??????");
            event.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }
    /**
     * ????????????
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
     * ?????????????????????
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
     * ??????????????????????????????????????????
     * @param event .
     */
    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST
    )
    public void onInteractEntityEvent(PlayerInteractEntityEvent event){
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (entity instanceof ItemFrame) {
            if (!player.hasPermission("LTCraft.interact.ItemFrame." + entity.getCustomName()) &&
                    !player.hasPermission("LTCraft.interact.ItemFrame.*") &&
                    !player.hasPermission("LTCraft.interact.*")) {
                event.setCancelled(true);
                return;
            }
            ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
            if (itemInMainHand != null && itemInMainHand.hasItemMeta() && itemInMainHand.getItemMeta().hasDisplayName() && itemInMainHand.getItemMeta().getDisplayName().endsWith("??????")) {
                ItemFrame frame = (ItemFrame) entity;
                BlockFace attachedFace = frame.getAttachedFace();
                Location add = entity.getLocation().add(attachedFace.getModX(), attachedFace.getModY(), attachedFace.getModZ());
                Block blockAt = entity.getWorld().getBlockAt(add);
                if (blockAt.getType() == Material.GLASS){
                    FakeBlock[] blocks = SmeltingFurnace.check(blockAt.getLocation(), entity.getLocation());
                    if (blocks.length > 0){
                        player.sendMessage("??c???????????????????????????????????????????????????????????????????????????");
                        new FlashingBlock(player, blocks);
                    }else {
                        if (SmeltingFurnace.getSmeltingFurnaceMap().values().stream().anyMatch(smeltingFurnace -> smeltingFurnace.getLocation().distance(blockAt.getLocation()) < 3)) {
                            return;
                        }
                        LTItem ltItems = Utils.getLTItems(itemInMainHand);
                        String name = ltItems == null? cn.LTCraft.core.utils.Utils.clearColor(itemInMainHand.getItemMeta().getDisplayName()):ltItems.getName();
                        new SmeltingFurnace(player, blockAt.getLocation(), entity, SmeltingFurnaceDrawing.getSmeltingFurnaceDrawing(name));
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBucketFillEvent(PlayerBucketFillEvent event){
        Player player = event.getPlayer();
        if (Game.rpgWorlds.contains(player.getWorld().getName())){
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
        if (Game.rpgWorlds.contains(player.getWorld().getName())){
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
                            player.sendMessage("??c?????????????????????????????????????????????");
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
     * ?????????????????? ??????????????????
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
                    if (level > 1 && !playerData.hasTag("default.??????T" + (level - 1))){
                        event.setCancelled(true);
                        player.sendMessage("??c???????????????T"+ (level - 1) + "????????????T" + level + "???");
                    }
                }catch (NumberFormatException ignored){

                }
            }else{
                switch (toWorld){
                    case "f1":
                        if (PlayerUtils.getClass(player) == PlayerClass.NONE) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("??c???????????????????????????????????????F1???");
                        }
                    break;
                    case "RPG":
                        PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                        if (!playerData.hasTag("default.??????T3")){
                            event.setCancelled(true);
                            player.sendMessage("??c???????????????T3????????????RPG???");
                        }
                    break;
                }
            }
            if (!event.isCancelled()) {
                if (Config.getInstance().getWorldTitleYaml().contains(toWorld.toLowerCase())) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                        List<String> list = Config.getInstance().getWorldTitleYaml().getStringList(toWorld.toLowerCase());
                        player.sendTitle(list.get(0), list.get(1), 60, 20, 20);
                    }, 20);
                }
                if (Game.rpgWorlds.contains(toWorld)){
                    Disguise disguise = DisguiseAPI.getDisguise(player);
                    if (disguise != null)disguise.stopDisguise();
                }
            }
        }else if (Game.rpgWorlds.contains(player.getWorld().getName())){
            if (
                    event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL ||
                    event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT
            ){
                event.setCancelled(true);
                return;
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
        int level = 0;
        if (data != null && data.getMainClass() != null){
                level = data.getMainClass().getLevel();
        }
        PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
        String sex = playerConfig.getConfig().getString("??????", "");
        String prefix = playerConfig.getConfig().getString("prefix", "");
        if (!prefix.isEmpty()){
            prefix = "??3<??r" + prefix + "??3>";
        }
        String love = Love.getLove(player);
        if (!love.equals("")){
            love = "??c?????3" + love;
        }
        if (!sex.equals("")){
            sex = " ??d" + sex;
        }
        PlayerInfo playerInfo = PlayerConfig.getPlayerConfig(player).getPlayerInfo();
        PlayerInfo.VIP vipStatus = playerInfo.getVipStatus();
        String vip = "";
        if (vipStatus.getLevel().getGrade() > 0){
            vip = vipStatus.getLevel().toString() + " ";
        }
        event.setFormat("??f[??bLv.??e" + level + sex + love + "??f]" + vip + prefix + "??e" + player.getName() + "??f: ??r%2$s");
//        event.setMessage("??f[??bLv.??e" + level + " ??c" + clazz + "??f]??e" + player.getName() + "??f: ??e" + event.getMessage());
    }
    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e){
        Player player = e.getPlayer();
        Map<String, List<String>> lastObj = BQObjectiveCheck.getLastObj();
        synchronized (lastObj) {
            lastObj.remove(player.getName());
        }
        GlobalRefresh.warnings.remove(player.getName());
        Temp.onPlayerQuit(player);
        PlayerConfig.getConfigMap().get(player.getName()).save();
        PlayerConfig.getConfigMap().remove(player.getName());
        LTGCommand.mapList.remove(player);
        LTGCommand.mapGate.remove(player);
        PlayerConfig.getCounter().remove(player.getName());
        e.setQuitMessage(null);
    }
    /**
     * ????????????
     * @param event
     */
    @EventHandler(
            priority = EventPriority.HIGH,
            ignoreCancelled = true
    )
    public void onEntityDamageByEntityLow(EntityDamageByEntityEvent event){
        Player damager = null;
        Entity entity = event.getEntity();
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
            if (PlayerUtils.getClass(damager) == PlayerClass.ASSASSIN){
                Temp.addInjured(event.getEntity(), 20);
            }
        }
        if (Temp.shield.containsKey(entity)){
            Shield shield = Temp.shield.get(entity);
            if (shield != null)shield.block(event);
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event){
        event.setCancelled(true);
        event.getPlayer().sendMessage("??c???????????????F???????????????");
    }
    private static final List<String> notDrop = Arrays.asList(
        "??????", "??????????????????", "???????????????", "??????", "????????????",
        "??????", "????????????", "?????????", "????????????", "????????????",
        "????????????", "????????????", "?????????"
    );
    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (!playerData.hasTag("default.??????T3")){
            for (String s : notDrop) {
                if (ClutterItem.spawnClutterItem(s).isSimilar(itemStack)){
                    event.setCancelled(true);
                    player.sendMessage("??c???????????????????????????????????????");
                    return;
                }
            }
        }
        if (Temp.dropCount.containsKey(player) && Temp.dropCount.get(player) >= 120){
            player.sendMessage("??c????????????????????????????????????120???");
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
                    player.sendTitle("??8[??a????????8]", "??6??????T3??????????????????");
                    player.sendMessage("??a??????????????????????????????????????????");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 100);
                    playerData.addTag("default.??????t3???????????????");
                }
            }, 20);
        }else if (player.getWorld().getName().equals("t3") && player.getLocation().distance(new Location(player.getWorld(), 140.5, 4, 473.5)) < 20){
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                if (player.isOnline() && Game.demand(player, "custom2")){
                    player.sendTitle("??8[??a????????8]", "??6??????T3??????????????????");
                    player.sendMessage("??a??????????????????????????????????????????");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 100);
                    playerData.addTag("default.??????t3???????????????");
                }
            }, 200);
        }
    }
    private static Map<String, String> putItInABottle = new HashMap<String, String>(){
        {
            put("???????????????", "????????????");
            put("???????????????", "???????????????");
        }
    };
    /**
     * ????????????????????????
     * ????????????????????????
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
        if (itemStack.hasItemMeta() && putItInABottle.containsKey(cn.LTCraft.core.utils.Utils.clearColor(itemStack.getItemMeta().getDisplayName()))) {
            Set<Map.Entry<String, String>> entries = putItInABottle.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if (ClutterItem.spawnClutterItem(entry.getKey()).isSimilar(itemStack)) {
                    ItemStack inHand = player.getItemInHand();
                    ClutterItem result = ClutterItem.spawnClutterItem(entry.getValue());
                    if (inHand.getType() == Material.GLASS_BOTTLE && !inHand.hasItemMeta()) {
                        int amount = itemStack.getAmount();
                        if (amount < inHand.getAmount()) {
                            inHand.setAmount(inHand.getAmount() - amount);
                            result.setNumber(amount);
                            amount = 0;
                        } else {
                            result.setNumber(inHand.getAmount());
                            inHand = new ItemStack(Material.AIR);
                            amount -= result.getNumber();
                        }
                        itemStack.setAmount(amount);
                        if (amount <= 0) {
                            item.remove();
                        } else {
                            item.setItemStack(itemStack);
                        }
                        if (inHand.getType() == Material.AIR) {
                            player.getInventory().setItemInMainHand(result.generate());
                        } else {
                            PlayerUtils.securityAddItem(player, result.generate());
                        }
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    } else {
                        if (item.getPickupDelay() != 20) item.setPickupDelay(20);
                        player.sendMessage("??c????????????????????????????????????" + entry.getValue() + "???");
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????
     * @see AirDoor
     * @param event .
     */
    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event){
        if (event.getTo().getWorld() == event.getFrom().getWorld()){
            Player player = event.getPlayer();
            Location from = event.getFrom();
            Location to = event.getTo();
            if (Game.rpgWorlds.contains(player.getWorld().getName())) {
                if (Game.mainLineWorlds.contains(player.getWorld().getName())){
                    if (!player.isOp() && player.isFlying()){
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        event.setCancelled(true);
                    }
                }
                boolean sign = false;
                AirDoor airDoor = null;
                synchronized (AirDoor.getAirDoors()) {
                    for (Iterator<AirDoor> iterator = AirDoor.getAirDoors().iterator(); iterator.hasNext(); ) {
                        airDoor = iterator.next();
                        if (airDoor.getBukkitEntity().isDead()) {
                            iterator.remove();
                            continue;
                        }
                        if (airDoor.getBukkitEntity().getWorld() != player.getWorld() || airDoor.getBukkitEntity().getLocation().distance(player.getLocation()) > airDoor.getDistance())
                            continue;
                        double fLocation = airDoor.getCheckDirection() == WorldUtils.COORDINATE.X ? from.getX() : airDoor.getCheckDirection() == WorldUtils.COORDINATE.Z ? from.getZ() : from.getY();
                        double tLocation = airDoor.getCheckDirection() == WorldUtils.COORDINATE.X ? to.getX() : airDoor.getCheckDirection() == WorldUtils.COORDINATE.Z ? to.getZ() : to.getY();
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
                }
                if (player.getWorld().getName().equals("t3") && new Location(player.getWorld(), 198, 9, 474).distance(player.getLocation()) < 100) {
                    Block block = player.getWorld().getBlockAt(event.getTo());
                    if (Game.burningBlocks.contains(block.getTypeId())) {
                        sign = false;
                        for (ItemStack armorContent : ((Player) player).getInventory().getArmorContents()) {
                            if (armorContent == null || armorContent.getItemMeta() == null || !armorContent.getItemMeta().getDisplayName().startsWith("??6????????????"))
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
            ?????????
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
                    ClutterItem.spawnClutterItem("?????????").isSimilar(bow) ||
                            ClutterItem.spawnClutterItem("??????????????????").isSimilar(bow)
            ){
                if (ClutterItem.spawnClutterItem("????????????").isSimilar(player.getInventory().getItemInOffHand())) {
                    ((CraftEntity) event.getProjectile()).getHandle().setCustomName("????????????");
                }else {
                    PlayerUtils.securityAddItem(player, new ItemStack(Material.ARROW));
                    player.sendMessage("??c????????????????????????????????????????????????????????????");
                    event.setCancelled(true);
                }
            }else {
                if (player.getWorld().getName().equals("t3")) {
                    ((CraftEntity) event.getProjectile()).getHandle().motX = 0;
                    ((CraftEntity) event.getProjectile()).getHandle().motY = 0;
                    ((CraftEntity) event.getProjectile()).getHandle().motZ = 0;
                    ((CraftEntity) event.getProjectile()).getHandle().impulse = true;
                    player.sendMessage("??c??????????????????????????????????????????????????????...");
                }
            }
        }
    }
    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onEntityDamageByEntityHigh(EntityDamageByEntityEvent event){
        CraftPlayer[] craftPlayers = new CraftPlayer[2];
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (damager instanceof Player){
            craftPlayers[0] = ((CraftPlayer) damager);
        }
        if (entity instanceof Player) {
            craftPlayers[1] = ((CraftPlayer) entity);
        }
        for (CraftPlayer craftPlayer : craftPlayers) {
            if (craftPlayer == null)continue;
            if (Game.rpgWorlds.contains(craftPlayer.getWorld().getName())) {
                if (craftPlayer.isBlocking()) {
                    if (Cooling.isCooling(craftPlayer, "????????????") && event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) > 0) {
                        event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
                    } else {
                        Cooling.cooling(craftPlayer, "????????????", 10, "?????????????????????????????????%s%S");
                    }
                }
            }
        }
        if (damager instanceof Player){
            ActiveMob mythicMob = EntityUtils.getMythicMob(entity);
            if (TargetOnlyMobsManager.targetOnlyMobs.containsKey(mythicMob) && TargetOnlyMobsManager.targetOnlyMobs.get(mythicMob) != damager){
                damager.sendMessage("??c???????????????????????????????????????????????????" + TargetOnlyMobsManager.targetOnlyMobs.get(mythicMob).getName() + "???????????????????????????");
                event.setCancelled(true);
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
                player.sendMessage("??c?????????????????????????????????????????????");
                event.setCancelled(true);
            }
        }
    }
    /*
     * TODO ??????????????????
     */
    /*
    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event){
        Player player = event.getEntity();
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent lastDamageCause1 = (EntityDamageByEntityEvent) lastDamageCause;
            if (!(lastDamageCause1.getDamager() instanceof Player)) {
                PlayerUtils.sendActionMessage(event.getDeathMessage());
                event.setDeathMessage(null);
            }
        }
    }

     */
    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        Long time = PrefixCommand.settingPlayers.get(player.getName());
        if (time != null){
            if (GlobalRefresh.getTick() - time > 30 * 20){
                PrefixCommand.settingPlayers.remove(player.getName());
            }else {
                event.setCancelled(true);
                String message = event.getMessage();
                if (message.equals("exit")) {
                    PrefixCommand.settingPlayers.remove(player.getName());
                    player.sendMessage("??c???????????????");
                    return;
                }
                if (message.length() > 18){
                    player.sendMessage("??c??????????????????18??????");
                    return;
                }
                Economy economy = Main.getInstance().getEconomy();
                if (!economy.withdrawPlayer(player, 500d).transactionSuccess()){
                    player.sendMessage("??c??????????????????500???");
                    return;
                }
                YamlConfiguration config = PlayerConfig.getPlayerConfig(player.getName());
                config.set("prefix", message.replace("&", "??"));
                PlayerUtils.updatePlayerDisplayName(player);
                PrefixCommand.settingPlayers.remove(player.getName());
                player.sendMessage("??c???????????????");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void censorCommand(PlayerCommandPreprocessEvent event) {
        if (Log4jFixerUtils.match(event.getMessage())) {
            Main.getInstance().getLogger().warning("?????????????????????" + event.getPlayer().getName());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void censorChatAsync(AsyncPlayerChatEvent event) {
        if (Log4jFixerUtils.match(event.getMessage())) {
            Main.getInstance().getLogger().warning("?????????????????????" + event.getPlayer().getName());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onRenameItem(PrepareAnvilEvent event) {
        ItemStack item = event.getResult();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        if (Log4jFixerUtils.match(item.getItemMeta().getDisplayName())) {
            event.setResult(null);
            if (event.getInventory().getViewers().size() > 0)Main.getInstance().getLogger().warning("?????????????????????" + event.getInventory().getViewers().toString());
        }
    }
}
