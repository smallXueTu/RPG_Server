package eos.moe.dragoncore.listener;

import cn.LTCraft.core.utils.PlayerUtils;
import eos.moe.dragoncore.DragonCore;
import eos.moe.dragoncore.api.SlotAPI;
import eos.moe.dragoncore.api.gui.event.CustomPacketEvent;
import eos.moe.dragoncore.config.Config;
import eos.moe.dragoncore.dataBase.IDataBase.Callback;
import eos.moe.dragoncore.network.PacketSender;
import eos.moe.dragoncore.util.ItemUtil;
import eos.moe.dragoncore.util.ScriptUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class SlotListener implements Listener {
    private DragonCore plugin;
    public final Set<UUID> saving = new HashSet();
    private static SlotListener instance = null;

    public static SlotListener getInstance() {
        return instance;
    }

    public SlotListener(DragonCore plugin) {
        instance = this;
        this.plugin = plugin;
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onSlotClickEvent(CustomPacketEvent e) {
        final Player player = e.getPlayer();
        if (e.getIdentifier().equals("DragonCore_ClickSlot")) {
            if (!e.isCancelled()) {
                if (e.getData().size() == 2) {
                    if (!this.saving.contains(player.getUniqueId())) {
                        final String slotIdentity = (String)e.getData().get(0);
                        try {
                            final int mouseButton = Integer.parseInt((String)e.getData().get(1));
                            if (Config.getSlotConfig().contains(slotIdentity + ".limit")) {
                                this.saving.add(player.getUniqueId());
                                SlotAPI.getSlotItem(player, slotIdentity, new Callback<ItemStack>() {
                                    public void onResult(ItemStack itemStack) {
                                        SlotListener.this.saving.remove(player.getUniqueId());
                                        SlotListener.this.handleSlotClick(player, slotIdentity, mouseButton, itemStack);
                                    }

                                    public void onFail() {
                                        player.sendMessage("§c[错误] §6无法读取槽位数据");
                                        SlotListener.this.saving.remove(player.getUniqueId());
                                    }
                                });
                            }
                        } catch (NumberFormatException var6) {

                        }
                    }
                }
            }
        }
    }

    public void handleSlotClick(Player player, String slotIdentity, int mouseButton, ItemStack slotItem) {
        ItemStack handItem = player.getItemOnCursor();
        if (slotItem == null) {
            slotItem = new ItemStack(Material.AIR);
        }

        if (handItem == null) {
            handItem = new ItemStack(Material.AIR);
        }

        if (mouseButton == 2 && player.hasPermission("core.slot.clone") && slotItem.getType() != Material.AIR && handItem.getType() == Material.AIR) {
            player.setItemOnCursor(slotItem.clone());
        } else if (handItem.getType() == Material.AIR || checkItemStackCanPutInSlot(player, slotIdentity, handItem)) {
            int amount;
            if (mouseButton == 0) {
                if (slotItem.getType() != Material.AIR && handItem.getType() != Material.AIR) {
                    if (slotItem.isSimilar(handItem)) {
                        amount = slotItem.getMaxStackSize() - slotItem.getAmount();
                        int addAmount = Math.min(amount, handItem.getAmount());
                        if (addAmount > 0) {
                            this.setItemStack(player, slotIdentity, setAmount(slotItem, slotItem.getAmount() + addAmount));
                            this.setCursorItem(player, setAmount(handItem, handItem.getAmount() - addAmount));
                        }
                    } else {
                        this.setItemStack(player, slotIdentity, handItem);
                        this.setCursorItem(player, slotItem);
                    }
                } else if (handItem.getType() != Material.AIR || slotItem.getType() != Material.AIR) {
                    this.setItemStack(player, slotIdentity, handItem);
                    this.setCursorItem(player, slotItem);
                }
            }

            if (mouseButton == 1) {
                if (slotItem.getType() == Material.AIR && handItem.getType() != Material.AIR) {
                    this.setItemStack(player, slotIdentity, setAmount(handItem.clone(), 1));
                    this.setCursorItem(player, setAmount(handItem, handItem.getAmount() - 1));
                }

                if (slotItem.getType() != Material.AIR && handItem.getType() == Material.AIR) {
                    if (slotItem.getAmount() == 1) {
                        this.setItemStack(player, slotIdentity, handItem);
                        this.setCursorItem(player, slotItem);
                    } else {
                        amount = slotItem.getAmount() / 2;
                        this.setItemStack(player, slotIdentity, setAmount(slotItem, slotItem.getAmount() - amount));
                        this.setCursorItem(player, setAmount(slotItem.clone(), amount));
                    }
                }

                if (slotItem.getType() != Material.AIR && handItem.getType() != Material.AIR && slotItem.getAmount() < slotItem.getMaxStackSize()) {
                    if (slotItem.isSimilar(handItem)) {
                        this.setItemStack(player, slotIdentity, setAmount(slotItem, slotItem.getAmount() + 1));
                        this.setCursorItem(player, setAmount(handItem, handItem.getAmount() - 1));
                    } else {
                        this.setItemStack(player, slotIdentity, handItem);
                        this.setCursorItem(player, slotItem);
                    }
                }
            }

        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onSlotRequireEvent(CustomPacketEvent e) {
        if (e.getIdentifier().equals("DragonCore_RetrieveSlot")) {
            if (!e.isCancelled()) {
                if (e.getData().size() == 1) {
                    String slotIdentity = (String)e.getData().get(0);
                    if (Config.getSlotConfig().contains(slotIdentity + ".limit")) {
                        ItemStack itemStack = SlotAPI.getCacheSlotItem(e.getPlayer(), slotIdentity);
                        if (itemStack == null) {
                            itemStack = new ItemStack(Material.AIR);
                        }

                        PacketSender.putClientSlotItem(e.getPlayer(), slotIdentity, itemStack);
                    }
                }
            }
        }
    }

    public void setItemStack(final Player player, String slotIdentity, final ItemStack itemStack) {
        this.saving.add(player.getUniqueId());
        SlotAPI.setSlotItem(player, slotIdentity, itemStack, true, new Callback<ItemStack>() {
            public void onResult(ItemStack p0) {
                SlotListener.this.saving.remove(player.getUniqueId());
            }

            public void onFail() {
                player.sendMessage("§a物品储存失败，已将物品返回至背包中");
                PlayerUtils.securityAddItem(player, itemStack);
                SlotListener.this.saving.remove(player.getUniqueId());
            }
        });
    }

    public void setCursorItem(Player player, ItemStack itemStack) {
        if (itemStack != null && itemStack.getAmount() > 0) {
            player.setItemOnCursor(itemStack);
        } else {
            player.setItemOnCursor(new ItemStack(Material.AIR));
        }

    }

    public static ItemStack setAmount(ItemStack itemStack, int amount) {
        itemStack.setAmount(amount);
        return itemStack;
    }

    public static boolean checkItemStackCanPutInSlot(Player player, String slotIdentity, ItemStack itemStack) {
        YamlConfiguration yaml = Config.getSlotConfig();
        if (!yaml.contains(slotIdentity + ".limit")) {
            return false;
        } else {
            List<String> list = yaml.getStringList(slotIdentity + ".limit");
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
                String s = (String)var5.next();
                if (s.split("\\|").length > 1) {
                    try {
                        String[] split = s.split("\\|", 2);
                        String script = yaml.getString("Script." + split[0]);
                        if (script == null) {
                            player.sendMessage("§c[错误] 背包配置Limit存在未知判断类型" + split[0] + "，你无法将物品放入此槽内");
                            return false;
                        }

                        boolean result = ScriptUtil.execute(script, player, itemStack, slotIdentity, split[1]);
                        if (!result) {
                            return false;
                        }
                    } catch (Exception var10) {
                        Bukkit.getConsoleSender().sendMessage("§c[错误] 处理限制行[" + s + "]出现了错误");
                        player.sendMessage("§c[错误] 背包配置Limit处理出现异常报错,你无法将物品放入此槽内");
                        var10.printStackTrace();
                        return false;
                    }
                } else {
                    List<String> lore = ItemUtil.getLore(itemStack);
                    boolean result = lore.contains(s.replace("&", "§"));
                    if (!result) {
                        player.sendMessage("§c§l该槽位要求物品存在词条: " + Arrays.toString(list.toArray()));
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
