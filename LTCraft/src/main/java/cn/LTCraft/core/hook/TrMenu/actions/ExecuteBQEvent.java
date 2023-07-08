package cn.LTCraft.core.hook.TrMenu.actions;

import cn.LTCraft.core.entityClass.Exchange;
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
 * Created by Angel、 on 2023/7/2 22:51
 */
public class ExecuteBQEvent extends ActionBase {
    public ExecuteBQEvent(@NotNull ActionHandle handle) {
        super(handle);
    }

    @Override
    public void onExecute(@NotNull ActionContents contents, @NotNull ProxyPlayer player, @NotNull ProxyPlayer placeholderPlayer) {
        String event = String.valueOf(contents);
        Player bukkitPlayer = Bukkit.getPlayerExact(player.getName());
        PlayerUtils.execBQEvent(bukkitPlayer, event);
        MenuSession.Companion.getSession(bukkitPlayer).playerItemSlots();
    }
}