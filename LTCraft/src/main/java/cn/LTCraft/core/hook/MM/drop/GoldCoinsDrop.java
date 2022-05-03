package cn.LTCraft.core.hook.MM.drop;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.entityClass.RandomValue;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.Utils;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IIntangibleDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;

public class GoldCoinsDrop extends Drop implements IIntangibleDrop {
    private final String gold;
    private int radius = 1;
    public GoldCoinsDrop(String line, MythicLineConfig config) {
        super(line, config);
        String conf = line.substring("GoldCoinsDrop ".length());
        String[] split = conf.split(" ")[0].split(":");
        gold = split[0];
        if (split.length >= 2) {
            radius = Integer.parseInt(split[1]);
        }

    }
    @Override
    public void giveDrop(AbstractPlayer abstractPlayer, DropMetadata dropMetadata) {
        Location location = dropMetadata.getCaster().getEntity().getBukkitEntity().getLocation();
        World world = Bukkit.getWorld(abstractPlayer.getWorld().getName());
        for (int i = 0; i < getAmount(); i++) {
            double x = Utils.getRandom().nextDouble() * radius;
            double z = Utils.getRandom().nextDouble() * radius;
            Item item = world.dropItem(new Location(world,
                    location.getX() + (Utils.getRandom().nextBoolean() ? x : 0 - x),
                    location.getY(),
                    location.getZ() + (Utils.getRandom().nextBoolean() ? z : 0 - z)), ClutterItem.spawnClutterItem(gold, ClutterItem.ItemSource.LTCraft).generate());
            Temp.protectItem(BukkitAdapter.adapt(abstractPlayer), item);
        }
    }
}
