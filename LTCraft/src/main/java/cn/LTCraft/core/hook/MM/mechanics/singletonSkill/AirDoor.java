package cn.LTCraft.core.hook.MM.mechanics.singletonSkill;

import cn.LTCraft.core.utils.WorldUtils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class AirDoor extends SkillMechanic implements ITargetedEntitySkill {
    private static final List<AirDoor> airDoors = new ArrayList<>();
    private AbstractEntity entity = null;
    private boolean forward = true;//监听前进还是后退
    private final WorldUtils.COORDINATE checkDirection;//X还是Z还是Z
    private double location = 0;
    private double distance = 10;
    private String demand = "";
    private String message = "";
    private String fail = "";
    private String success = "";
    public AirDoor(String skill, MythicLineConfig mlc){
        super(skill, mlc);
        location = mlc.getInteger(new String[]{"location", "l"}, 0);
        distance = mlc.getDouble(new String[]{"distance", "di"}, 10);
        forward = mlc.getBoolean(new String[]{"forward", "f"}, true);
        checkDirection = WorldUtils.COORDINATE.valueOf(mlc.getString(new String[]{"direction"}, "X"));
        demand = mlc.getString(new String[]{"demand", "d"}, "");
        message = mlc.getString(new String[]{"message", "msg", "mess", "m"}, "§c你还没有权限通过这个门！");
        fail = mlc.getString(new String[]{"fail"}, "弹开");
        success = mlc.getString(new String[]{"success", "s"}, "");
    }
    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        entity = abstractEntity;
        synchronized (airDoors) {
            airDoors.add(this);
        }
        return false;
    }

    public AbstractEntity getEntity() {
        return entity;
    }

    public Entity getBukkitEntity() {
        return entity.getBukkitEntity();
    }

    public static List<AirDoor> getAirDoors() {
        return airDoors;
    }

    public boolean isForward() {
        return forward;
    }

    public WorldUtils.COORDINATE getCheckDirection() {
        return checkDirection;
    }

    public double getLocation() {
        return location;
    }

    public void setLocation(double location) {
        this.location = location;
    }

    public String getDemand() {
        return demand;
    }

    public String getMessage() {
        return message;
    }

    public String getFail() {
        return fail;
    }

    public String getSuccess() {
        return success;
    }

    public double getDistance() {
        return distance;
    }
}
