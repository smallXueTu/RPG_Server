package cn.LTCraft.core.hook.TrMenu.actions;

import cn.LTCraft.core.utils.ItemUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import me.arasple.mc.trmenu.api.action.ActionHandle;
import me.arasple.mc.trmenu.api.action.base.ActionBase;
import me.arasple.mc.trmenu.api.action.base.ActionContents;
import me.arasple.mc.trmenu.module.display.MenuSession;
import me.arasple.mc.trmenu.taboolib.common.platform.ProxyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 获取杂物
 */
public class GetClutterItem extends ActionBase {
    public GetClutterItem(@NotNull ActionHandle handle) {
        super(handle);
    }

    @Override
    public void onExecute(@NotNull ActionContents contents, @NotNull ProxyPlayer player, @NotNull ProxyPlayer placeholderPlayer) {
        String item = String.valueOf(contents);
        Player bukkitPlayer = Bukkit.getPlayerExact(player.getName());
        PlayerUtils.securityAddItem(bukkitPlayer, ItemUtils.getItem(item));
        MenuSession.Companion.getSession(bukkitPlayer).playerItemSlots();
    }
}
