package cn.LTCraft.core.utils;

import cn.LTCraft.core.callback.DragonDataBaseCallback;
import eos.moe.dragoncore.api.SlotAPI;
import eos.moe.dragoncore.database.IDataBase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DragonCoreUtil {
    public final static Set<UUID> saving = new HashSet();

    public static void setItemStack(final Player player, String slotIdentity, final ItemStack itemStack, DragonDataBaseCallback dragonDataBaseCallback) {
        setItemStack(player, slotIdentity, itemStack, dragonDataBaseCallback, false);
    }
    public static void setItemStack(final Player player, String slotIdentity, final ItemStack itemStack, DragonDataBaseCallback dragonDataBaseCallback, boolean lock) {
        if (saving.contains(player.getUniqueId())) return;
        if (lock)saving.add(player.getUniqueId());
        SlotAPI.setSlotItem(player, slotIdentity, itemStack, true, new IDataBase.Callback<ItemStack>() {
            public void onResult(ItemStack p0) {
                if (lock)saving.remove(player.getUniqueId());
                if (dragonDataBaseCallback != null)dragonDataBaseCallback.complete(null);
            }

            public void onFail() {
                player.sendMessage("§a物品储存失败，已将物品返回至背包中");
                PlayerUtils.securityAddItem(player, itemStack);
                if (lock)saving.remove(player.getUniqueId());
            }
        });
    }
    public static void getItemStack(final Player player, String slotIdentity, DragonDataBaseCallback dragonDataBaseCallback) {
        getItemStack(player, slotIdentity, dragonDataBaseCallback, false);
    }
    public static void getItemStack(final Player player, String slotIdentity, DragonDataBaseCallback dragonDataBaseCallback, boolean lock) {
        if (saving.contains(player.getUniqueId())) return;
        if (lock)saving.add(player.getUniqueId());
        SlotAPI.getSlotItem(player, slotIdentity, new IDataBase.Callback<ItemStack>() {
            public void onResult(ItemStack itemStack) {
                if (lock)saving.remove(player.getUniqueId());
                if (dragonDataBaseCallback != null)dragonDataBaseCallback.complete(itemStack);
            }

            public void onFail() {
                player.sendMessage("§c[错误] §6无法读取槽位数据");
                if (lock)saving.remove(player.getUniqueId());
            }
        });
    }
}
