package cn.LTCraft.core.hook.MM.conditions;

import cn.LTCraft.core.utils.GameUtils;
import cn.ltcraft.teleport.Home;
import cn.ltcraft.teleport.Teleport;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityLocationComparisonCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.ILocationCondition;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * 主线位置距离某个坐标的距离
 * Created by Angel、 on 2022/4/18 0:43
 */
public class MainLinePositionDistance extends SkillCondition implements IEntityCondition {
    private final double range;
    private final Location location;
    public MainLinePositionDistance(String line, MythicLineConfig config) {
        super(line);
        range = config.getDouble(new String[]{"range", "r"}, 3);
        location = GameUtils.spawnLocation(config.getString(new String[]{"location", "loc", "l"}, ""));
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        Entity bukkitEntity = abstractEntity.getBukkitEntity();
        if (bukkitEntity instanceof Player){
            Player player = (Player) bukkitEntity;
            Home mainline = Teleport.getInstance().getPlayerHomes().get(player.getName()).get("mainline");
            return mainline.getLocation().distance(location) >= range;
        }
        return false;
    }
}
