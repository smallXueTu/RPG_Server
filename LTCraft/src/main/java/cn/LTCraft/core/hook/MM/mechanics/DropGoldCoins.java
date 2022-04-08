package cn.LTCraft.core.hook.MM.mechanics;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.entityClass.RandomValue;
import cn.LTCraft.core.utils.Utils;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.Future;

public class DropGoldCoins  extends SkillMechanic implements ITargetedLocationSkill {
    private final RandomValue count;
    private final int radius;
    private final String drop;
    public DropGoldCoins(String skill, MythicLineConfig mlc){
        super(skill, mlc);
        count = new RandomValue(mlc.getString(new String[]{"count", "c",}, "1"));
        radius = mlc.getInteger(new String[]{"radius", "r",}, 1);
        drop = mlc.getString(new String[]{"drop", "d",}, "金币");
    }
    @Override
    public boolean castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {
        World world = Bukkit.getWorld(abstractLocation.getWorld().getName());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            int count = (int) this.count.getValue();
            for (int i = 0; i < count; i++) {
                double x = Utils.getRandom().nextDouble() * radius;
                double z = Utils.getRandom().nextDouble() * radius;
                world.dropItem(new Location(world,
                        abstractLocation.getX() + (Utils.getRandom().nextBoolean() ? x : 0 - x),
                        abstractLocation.getY(),
                        abstractLocation.getZ() + (Utils.getRandom().nextBoolean() ? z : 0 - z)), new ClutterItem(drop, ClutterItem.ItemSource.LTCraft).generate());
            }
        }, 0);
        return true;
    }
}
