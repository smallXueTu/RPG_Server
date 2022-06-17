package cn.LTCraft.core.hook.MM.drop;

import cn.LTCraft.core.listener.MMListener;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.MathUtils;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.compatibility.CompatibilityManager;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IIntangibleDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCaster;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Angel、 on 2022/6/17 17:50
 */
public class ParticipateInSAPExp extends Drop implements IIntangibleDrop {
    private final int exp;
    private final double probability;
    public ParticipateInSAPExp(String line, MythicLineConfig config) {
        super(line, config);
        String conf = line.substring("ParticipateInSAPExp ".length());
        String[] split = conf.split("%");
        exp = Integer.parseInt(split[0]);
        probability = split.length > 1 ? Double.parseDouble(split[1]) : 0.01;
    }

    @Override
    public void giveDrop(AbstractPlayer abstractPlayer, DropMetadata dropMetadata) {
        SkillCaster caster = dropMetadata.getCaster();
        AbstractEntity entity = caster.getEntity();
        Entity bukkitEntity = entity.getBukkitEntity();
        Map<String, List<MMListener.PlayerDamage>> stringListMap = MMListener.damages.get(bukkitEntity.getEntityId());
        if (stringListMap != null){
            Location location = bukkitEntity.getLocation();
            ArrayList<Map.Entry<String, List<MMListener.PlayerDamage>>> list = new ArrayList<>(stringListMap.entrySet());
            list.sort(Comparator.comparingInt(o -> o.getValue().size()));
            for (Map.Entry<String, List<MMListener.PlayerDamage>> stringListEntry : list) {
                String key = stringListEntry.getKey();
                Player playerExact = Bukkit.getPlayerExact(key);
                if (playerExact != null && playerExact.getWorld() == bukkitEntity.getWorld() && playerExact.getLocation().distance(location) < 10){
                    if (!MathUtils.ifAdopt(probability))continue;
                    SkillAPI.getPlayerAccountData(playerExact).getActiveData().giveExp(exp, ExpSource.MOB);
                }
            }
        }
    }
}
