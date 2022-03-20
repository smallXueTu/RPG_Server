package cn.LTCraft.core.hook.MM.mechanics.singletonSkill;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticleDoor extends SkillMechanic implements ITargetedEntitySkill {
    private float yaw = 0;
    private int quantity = 0;
    private int radius = 20;
    private int yQuantity = 0;
    private double interval = 0;
    private double yInterval = 0;
    private boolean xChange = true;
    private Effect effect = Effect.FLAME;
    public ParticleDoor(String skill, MythicLineConfig mlc){
        super(skill, mlc);
        yaw = mlc.getFloat(new String[]{"yaw", "y"}, 0);
        quantity = mlc.getInteger(new String[]{"quantity", "q"}, 10);
        interval = mlc.getDouble(new String[]{"interval", "i"}, 0.1);
        yInterval = mlc.getDouble(new String[]{"yInterval", "yi"}, 0.3);
        yQuantity = mlc.getInteger(new String[]{"yQuantity", "yq"}, 4);
        radius = mlc.getInteger(new String[]{"radius", "r"}, 20);
        effect = Effect.valueOf(mlc.getString(new String[]{"effect", "e"}, "FLAME").toUpperCase());
        if (yaw == 0 || yaw == 180)xChange = true;
    }
    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (quantity == 0 || yQuantity == 0)return false;
        double y;
        Location location = abstractEntity.getBukkitEntity().getLocation();
        if (location.getWorld().getNearbyEntities(location, radius, radius, radius).stream().noneMatch(e -> e instanceof Player))return false;
        Location tempLocation;
        for (int i = 1; i <= yQuantity; i++) {
            y = i * yInterval;
            for (int j = 1; j <= quantity / 2; j++) {
                tempLocation = xChange?location.clone().add(0 - interval * j, y, 0):location.clone().add(0, y, 0 - interval * j);
                location.getWorld().spigot().playEffect(tempLocation, effect, 0, 0, 0f, 0f, 0f, 0, 1, radius);
            }
            if (quantity % 2 != 0){
                tempLocation = location.clone().add(0, y, 0);
                location.getWorld().spigot().playEffect(tempLocation, effect, 0, 0, 0f, 0f, 0f, 0, 1, radius);
            }
            for (int j = 1; j <= quantity / 2; j++) {
                tempLocation = xChange?location.clone().add(interval * j, y, 0):location.clone().add(0, y, interval * j);
                location.getWorld().spigot().playEffect(tempLocation, effect, 0, 0, 0f, 0f, 0f, 0, 1, radius);
            }
        }
        return false;
    }
}
