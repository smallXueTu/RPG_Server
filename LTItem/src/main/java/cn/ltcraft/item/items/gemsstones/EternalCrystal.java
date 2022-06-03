package cn.ltcraft.item.items.gemsstones;

import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.item.utils.Utils;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Angel、 on 2022/6/2 12:31
 */
public class EternalCrystal extends GemsStone {
    private static final List<String> list = Arrays.asList("无品质", "未知", "入门", "初级", "普通", "中级");
    public EternalCrystal(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public ItemStack installOn(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (!itemMeta.isUnbreakable()) {
                LTItem ltItems = Utils.getLTItems(itemStack);
                if (ltItems instanceof ConfigurableLTItem) {
                    ConfigurableLTItem configurableLTItem = (ConfigurableLTItem) ltItems;
                    String string = configurableLTItem.getConfig().getString("品质", "无品质");
                    if (list.contains(string)) {
                        itemMeta.setUnbreakable(true);
                        itemStack.setItemMeta(itemMeta);
                    }
                }
            }
        }
        return itemStack;
    }
}
