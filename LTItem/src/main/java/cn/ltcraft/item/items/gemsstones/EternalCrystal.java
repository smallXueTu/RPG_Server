package cn.ltcraft.item.items.gemsstones;

import cn.ltcraft.item.items.GemsStone;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Angel、 on 2022/6/2 12:31
 */
public class EternalCrystal extends GemsStone {
    public EternalCrystal(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public ItemStack installOn(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
