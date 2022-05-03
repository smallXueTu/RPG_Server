package cn.ltcraft.item.items;

import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.items.materials.Infernal;
import org.bukkit.configuration.MemoryConfiguration;

public class MeleeWeapon extends BaseWeapon{

    public static void initItems(){

    }
    public static MeleeWeapon get(MemoryConfiguration configuration){
        return new MeleeWeapon(configuration);
    }
    public MeleeWeapon(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public cn.ltcraft.item.base.ItemTypes getType() {
        return ItemTypes.Melee;
    }
}
