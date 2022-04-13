package cn.LTCraft.core.hook.MM.drop;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.EntityUtils;
import cn.LTCraft.core.utils.MathUtils;
import cn.LTCraft.core.utils.Utils;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IIntangibleDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;

import java.util.Map;

/**
 * 伪随机掉落
 * Created by Angel、 on 2022/4/13 20:27
 * 每随机失败一次增加1%的掉落几率，防止玩家多次未掉落
 */
public class PseudorandomDrop extends Drop implements IIntangibleDrop {
    private String itemName;
    private double probability;
    public PseudorandomDrop(String line, MythicLineConfig config) {
        super(line, config);
        String conf = line.substring("PseudorandomDrop ".length());
        String[] split = conf.split("%");
        itemName = split[0];
        probability = Double.parseDouble(split[1]);
    }
    @Override
    public void giveDrop(AbstractPlayer abstractPlayer, DropMetadata dropMetadata) {
        Map<String, Integer> counter = PlayerConfig.getCounter(BukkitAdapter.adapt(abstractPlayer));
        AbstractEntity abstractEntity = dropMetadata.getCaster().getEntity();
        ActiveMob mythicMob = EntityUtils.getMythicMob(abstractEntity);
        if (mythicMob == null){
            Bukkit.getLogger().warning("错误，无法获取怪物！");
            return;
        }
        int count = counter.get(mythicMob.getType().getInternalName());
        double probability = this.probability + count * 0.01;
        if (MathUtils.ifAdopt(probability)) {
            counter.remove(mythicMob.getType().getInternalName());
            AbstractLocation location = abstractEntity.getLocation();
            World world = BukkitAdapter.adapt(abstractPlayer.getWorld());
            Item item = world.dropItem(new Location(world,
                    location.getX(),
                    location.getY(),
                    location.getZ()), new ClutterItem(itemName).generate());
            Temp.protectItem(BukkitAdapter.adapt(abstractPlayer), item);
        }else {
            counter.put(mythicMob.getType().getInternalName(), count + 1);
        }
    }
}
