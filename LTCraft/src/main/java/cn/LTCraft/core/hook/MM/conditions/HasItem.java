package cn.LTCraft.core.hook.MM.conditions;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.utils.ItemUtils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import org.bukkit.entity.Player;

public class HasItem extends SkillCondition implements IEntityCondition {
    private ClutterItem[] clutterItems;
    public HasItem(String line, MythicLineConfig config) {
        super(line);
        String stringItem = config.getString(new String[]{"item", "i"}, "AIR");
        String[] items = stringItem.split(",");
        clutterItems = new ClutterItem[items.length];
        for (int i = 0; i < items.length; i++) {
            clutterItems[i] = ClutterItem.spawnClutterItem(items[i]);
        }
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        if (abstractEntity == null)return false;
        if (abstractEntity.getBukkitEntity() instanceof Player) {
            Player player = (Player) abstractEntity.getBukkitEntity();
            return ItemUtils.sufficientItem(clutterItems, player);
        }
        return false;
    }
}