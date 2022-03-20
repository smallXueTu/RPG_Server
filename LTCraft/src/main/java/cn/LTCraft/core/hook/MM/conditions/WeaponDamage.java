package cn.LTCraft.core.hook.MM.conditions;

import cn.ltcraft.item.base.interfaces.Attribute;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.objs.ItemObjs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import io.lumine.xikage.mythicmobs.util.MythicUtil;
import org.bukkit.entity.Player;
public class WeaponDamage extends SkillCondition implements IEntityCondition {
    private String data;
    public WeaponDamage(String line, MythicLineConfig config) {
        super(line);
        data = config.getString(new String[]{"WeaponDamage", "Damage", "d"}, ">0");
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        if (abstractEntity == null)return false;
        if (abstractEntity.getBukkitEntity() instanceof Player) {
            Player player = (Player)abstractEntity.getBukkitEntity();
            LTItem handLTItem = ItemObjs.getHandLTItem(player);
            if (!(handLTItem instanceof Attribute))return false;
            Attribute attribute = (Attribute) handLTItem;
            if (attribute.getDamage(Attribute.Type.PVE).getMin() <= 0)return false;
            return MythicUtil.matchNumber(data, attribute.getDamage(Attribute.Type.PVE).getMin());
        }
        return false;
    }
}
