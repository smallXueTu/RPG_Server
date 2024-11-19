package cn.LTCraft.core.hook.MM.conditions;

import cn.hutool.core.util.ObjectUtil;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.enums.ItemQuality;
import cn.ltcraft.item.items.BaseWeapon;
import cn.ltcraft.item.utils.Utils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * 手持装备质量
 */
public class HandQuality extends SkillCondition implements IEntityCondition {
    private ItemQuality quality;
    public HandQuality(String line, MythicLineConfig config) {
        super(line);
        quality = ItemQuality.ofQuality(config.getString(new String[]{"q", "quality"}));
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        if (abstractEntity == null || quality == null)return false;
        if (abstractEntity.getBukkitEntity() instanceof Player) {
            Player player = (Player) abstractEntity.getBukkitEntity();
            LTItem ltItem = Utils.getLTItems(player.getItemInHand());
            if (ObjectUtil.isNotEmpty(ltItem) && ltItem instanceof BaseWeapon){
                if (ltItem.getQuality().ordinal() >= quality.ordinal()) {
                    return true;
                }
            }
        }
        return false;
    }
}