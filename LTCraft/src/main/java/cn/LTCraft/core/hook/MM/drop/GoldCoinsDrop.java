package cn.LTCraft.core.hook.MM.drop;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.entityClass.RandomValue;
import cn.LTCraft.core.utils.Utils;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IIntangibleDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class GoldCoinsDrop extends Drop implements IIntangibleDrop {
    private final String gold;
    private int radius = 1;
    public GoldCoinsDrop(String line, MythicLineConfig config) {
        super(line, config);
        String conf = line.substring("GoldCoinsDrop ".length());
        String[] split = conf.split(":");
        gold = split[0];
        if (split.length >= 2) {
            radius = Integer.parseInt(split[1]);
        }

    }
    @Override
    public void giveDrop(AbstractPlayer abstractPlayer, DropMetadata dropMetadata) {
        Location location = abstractPlayer.getBukkitEntity().getLocation();
        World world = Bukkit.getWorld(abstractPlayer.getWorld().getName());
        for (int i = 0; i < getAmount(); i++) {
            double x = Utils.getRandom().nextDouble() * radius;
            double z = Utils.getRandom().nextDouble() * radius;
            world.dropItem(new Location(world,
                    location.getX() + (Utils.getRandom().nextBoolean()?x:0-x),
                    location.getY(),
                    location.getZ() + (Utils.getRandom().nextBoolean()?z:0-z)), new ClutterItem(gold, ClutterItem.ItemSource.LTCraft).generate());
        }
    }
}
