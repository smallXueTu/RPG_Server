package cn.ltcraft.item.items.gemsstones;

import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.enums.ItemQuality;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.item.objs.StoneInstallResult;
import cn.ltcraft.item.utils.Utils;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Angel、 on 2022/6/2 12:31
 */
public class EternalCrystal extends GemsStone implements ConsumeGemstones {
    private static final List<String> list = Arrays.asList("无品质", "未知", "入门", "初级", "普通", "中级");
    private static final List<String> seniorList = Arrays.asList("无品质", "未知", "入门", "初级", "普通", "中级", "高级", "稀有", "史诗", "传说");
    public EternalCrystal(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public StoneInstallResult installOn(ItemStack itemStack, int installCount) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (!itemMeta.isUnbreakable()) {
                LTItem ltItems = Utils.getLTItems(itemStack);
                if (ltItems instanceof ConfigurableLTItem) {
                    ItemQuality quality = ltItems.getQuality();
                    String name = getConfig().getString("名字");
                    List<String> list = name.equals("永恒水晶")?EternalCrystal.list:EternalCrystal.seniorList;
                    if (list.contains(quality.getName())) {
                        itemMeta.setUnbreakable(true);
                        itemStack.setItemMeta(itemMeta);
                        return new StoneInstallResult(itemStack);
                    }
                }
            }
        }
        return StoneInstallResult.fail;
    }
}
