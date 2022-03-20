package cn.ltcraft.item.items;

import cn.ltcraft.item.base.ItemTypes;
import org.bukkit.configuration.MemoryConfiguration;

public class MeleeWeapon extends BaseWeapon{
    public MeleeWeapon(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public cn.ltcraft.item.base.ItemTypes getType() {
        return ItemTypes.Melee;
    }
}
