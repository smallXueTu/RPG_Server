package cn.LTCraft.core.entityClass;

import cn.LTCraft.core.Config;
import cn.LTCraft.core.utils.ItemUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Exchange {
    public static Config config = null;

    /**
     * 获取兑换物品需要的条件
     * @param itemName 物品名
     * @return 条件
     */
    public static ClutterItem[] getNeedItems(String itemName){
        if (config.getItemsYaml().contains(itemName)) {
            List<String> list = config.getItemsYaml().getStringList(itemName);
            ClutterItem[] items = new ClutterItem[list.size()];
            for (int i = 0; i < list.size(); i++) {
                items[i] = ClutterItem.spawnClutterItem(list.get(i));
            }
            return items;
        }
        return null;
    }

    /**
     * 是否可以兑换
     * @param itemName 要兑换物品
     * @param player 玩家
     * @return 是否可以
     */
    public static boolean canExchange(String itemName, Player player){
        ClutterItem[] items = getNeedItems(itemName);
        if (items==null)return false;
        return ItemUtils.sufficientItem(items, player);
    }

    /**
     * 开始兑换
     * @param itemName 物品名字
     * @param player 玩家
     * 这个方法将不在判断玩家是否拥有足够的项目，请先调用 {@link Exchange#canExchange(String, Player)}
     */
    public static void exchange(String itemName, Player player) {
        ClutterItem[] clutterItems = getNeedItems(itemName);
        if (clutterItems==null)return;
        ItemStack[] contents = player.getInventory().getContents();
        ItemUtils.removeItems(contents, clutterItems, player);
        player.getInventory().setContents(contents);
        PlayerUtils.securityAddItem(player, ClutterItem.spawnClutterItem(itemName).generate());
    }
}
