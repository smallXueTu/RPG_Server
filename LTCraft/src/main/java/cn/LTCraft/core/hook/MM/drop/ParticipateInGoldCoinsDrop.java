package cn.LTCraft.core.hook.MM.drop;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.listener.MMListener;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.MathUtils;
import cn.LTCraft.core.utils.Utils;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IIntangibleDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCaster;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Angel、 on 2022/6/18 1:32
 */
public class ParticipateInGoldCoinsDrop extends Drop implements IIntangibleDrop {
    private final String gold;
    private int radius = 1;
    public ParticipateInGoldCoinsDrop(String line, MythicLineConfig config) {
        super(line, config);
        String conf = line.substring("ParticipateInGoldCoinsDrop ".length());
        String[] split = conf.split(" ")[0].split(":");
        gold = split[0];
        if (split.length >= 2) {
            radius = Integer.parseInt(split[1]);
        }
    }
    @Override
    public void giveDrop(AbstractPlayer abstractPlayer, DropMetadata dropMetadata) {
        SkillCaster caster = dropMetadata.getCaster();
        AbstractEntity entity = caster.getEntity();
        Entity bukkitEntity = entity.getBukkitEntity();
        Map<String, List<MMListener.PlayerDamage>> stringListMap = MMListener.damages.get(bukkitEntity.getEntityId());
        if (stringListMap != null) {
            Location location = bukkitEntity.getLocation();
            ArrayList<Map.Entry<String, List<MMListener.PlayerDamage>>> list = new ArrayList<>(stringListMap.entrySet());
            list.sort(Comparator.comparingInt(o -> o.getValue().size()));
            World world = location.getWorld();
            ItemStack generate = ClutterItem.spawnClutterItem(gold, ClutterItem.ItemSource.LTCraft).generate();
            for (Map.Entry<String, List<MMListener.PlayerDamage>> stringListEntry : list) {
                String key = stringListEntry.getKey();
                Player playerExact = Bukkit.getPlayerExact(key);
                if (playerExact != null && playerExact.getWorld() == bukkitEntity.getWorld() && playerExact.getLocation().distance(location) < 10) {
                    for (int i = 0; i < getAmount(); i++) {
                        double x = Utils.getRandom().nextDouble() * radius;
                        double z = Utils.getRandom().nextDouble() * radius;
                        Item item = world.dropItem(new Location(world,
                                location.getX() + (Utils.getRandom().nextBoolean() ? x : 0 - x),
                                location.getY(),
                                location.getZ() + (Utils.getRandom().nextBoolean() ? z : 0 - z)), generate);
                        Temp.protectItem(playerExact, item);
                    }
                }
            }
        }
    }
}
