package cn.ltcraft.item.items.gemsstones;

import cn.LTCraft.core.utils.ItemUtils;
import cn.ltcraft.item.base.AICLA;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.enums.ItemQuality;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.item.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

/**
 * 盔甲经验水晶
 * Created by Angel、 on 2024/4/27 21:53
 */
public class ArmorExpCrystal extends GemsStone implements ConsumeGemstones {
    public ArmorExpCrystal(MemoryConfiguration configuration) {
        super(configuration);
    }
}
