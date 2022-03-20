package cn.LTCraft.core.hook.MM.drop;

import cn.LTCraft.core.entityClass.PlayerConfig;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IIntangibleDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import org.bukkit.entity.Player;

/**
 * Created by Angel、 on 2022/3/20 23:41
 */
public class GoldCoins extends Drop implements IIntangibleDrop {
    public GoldCoins(String line, MythicLineConfig config) {
        super(line, config);

    }
    @Override
    public void giveDrop(AbstractPlayer abstractPlayer, DropMetadata dropMetadata) {
        Player player = BukkitAdapter.adapt(abstractPlayer);
        PlayerConfig playerConfig = PlayerConfig.getPlayerConfig(player);
        playerConfig.getPlayerInfo().addGold((int) getAmount());
    }
}
