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
import org.openjdk.jol.vm.VM;

import java.util.HashMap;
import java.util.Map;

/**
 * 伪随机掉落
 * Created by Angel、 on 2022/4/13 20:27
 * 每随机失败一次增加一倍的掉落几率，防止玩家多次未掉落
 */
public class PseudorandomDrop extends Drop implements IIntangibleDrop {
    private final String itemName;
    private final double probability;
    private final Map<String, Double> playerProbability = new HashMap<>();
    public PseudorandomDrop(String line, MythicLineConfig config) {
        super(line, config);
        String conf = line.substring("PseudorandomDrop ".length());
        String[] split = conf.split("%");
        itemName = split[0];
        probability = split.length > 1? Double.parseDouble(split[1]):0.01;
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
        int count = counter.getOrDefault(mythicMob.getType().getInternalName(), 0);
        if (!playerProbability.containsKey(abstractPlayer.getName())){
            playerProbability.put(abstractPlayer.getName(), probability);
        }
        double probability = playerProbability.get(abstractPlayer.getName()) + count * playerProbability.get(abstractPlayer.getName());
        if (MathUtils.ifAdopt(probability)) {
            counter.remove(mythicMob.getType().getInternalName());
            AbstractLocation location = abstractEntity.getLocation();
            World world = BukkitAdapter.adapt(abstractPlayer.getWorld());
            Item item = world.dropItem(new Location(world,
                    location.getX(),
                    location.getY(),
                    location.getZ()), ClutterItem.spawnClutterItem(itemName).generate());
            Temp.protectItem(BukkitAdapter.adapt(abstractPlayer), item);
            /*
            如果玩家成功触发了
            那么玩家下次触发的几率将会下降至：原本几率降低 $probability 百分比
             */
            playerProbability.put(abstractPlayer.getName(), this.probability / (2d - probability));
        }else {
            counter.put(mythicMob.getType().getInternalName(), count + 1);//计数一次 玩家每次触发失败增加一定的百分比防止玩家多次未掉落
        }
    }
}
