package cn.ltcraft.item.items.materials;

import cn.ltcraft.item.base.interfaces.actions.TickItem;
import cn.ltcraft.item.items.Material;
import net.minecraft.server.v1_12_R1.DamageSource;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * 火魔
 * Created by Angel、 on 2022/5/3 21:28
 */
public class Infernal extends Material implements TickItem {
    public Infernal(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void doTick(long tick, Player player, int invIndex) {
        PlayerInventory inventory = player.getInventory();
        if (inventory.getHeldItemSlot() == invIndex || invIndex == inventory.getSize() - 1) {
            ((CraftPlayer) player).getHandle().damageEntity(DamageSource.BURN, 2);
        }
    }
}
