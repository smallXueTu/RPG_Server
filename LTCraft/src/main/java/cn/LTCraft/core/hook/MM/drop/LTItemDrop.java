package cn.LTCraft.core.hook.MM.drop;

import cn.LTCraft.core.entityClass.ClutterItem;
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IItemDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;

public class LTItemDrop extends Drop implements IItemDrop {
    private final ClutterItem item;
    public LTItemDrop(String line, MythicLineConfig config) {
        super(line, config);
        String itemStr = line.substring("ltitem ".length());
        String[] s = itemStr.split(" ");
        item = ClutterItem.spawnClutterItem(s[0], ClutterItem.ItemSource.LTCraft);
    }

    public AbstractItemStack getDrop(DropMetadata metadata) {
        return BukkitAdapter.adapt(item.generate((int) getAmount()));
    }
}