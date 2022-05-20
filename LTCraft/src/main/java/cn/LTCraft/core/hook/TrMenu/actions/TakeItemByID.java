package cn.LTCraft.core.hook.TrMenu.actions;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.utils.ItemUtils;
import me.arasple.mc.trmenu.api.action.ActionHandle;
import me.arasple.mc.trmenu.api.action.base.ActionBase;
import me.arasple.mc.trmenu.api.action.base.ActionContents;
import me.arasple.mc.trmenu.module.display.MenuSession;
import me.arasple.mc.trmenu.taboolib.common.platform.ProxyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Angel、 on 2022/5/20 18:31
 */
public class TakeItemByID extends ActionBase {
    public TakeItemByID(@NotNull ActionHandle handle) {
        super(handle);
    }

    @Override
    public void onExecute(@NotNull ActionContents contents, @NotNull ProxyPlayer player, @NotNull ProxyPlayer placeholderPlayer) {
        String item = String.valueOf(contents);
        Player bukkitPlayer = Bukkit.getPlayerExact(player.getName());
        ClutterItem clutterItem = ClutterItem.spawnClutterItem(item, ClutterItem.ItemSource.Minecraft);
        PlayerInventory inventory = bukkitPlayer.getInventory();
        ItemStack[] invContents = inventory.getContents();
        ItemUtils.removeItem(invContents, clutterItem.generate());
        inventory.setContents(invContents);
        MenuSession.Companion.getSession(bukkitPlayer).playerItemSlots();
    }
}
