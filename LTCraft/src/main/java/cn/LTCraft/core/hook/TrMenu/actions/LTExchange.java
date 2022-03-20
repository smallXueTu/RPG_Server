package cn.LTCraft.core.hook.TrMenu.actions;

import cn.LTCraft.core.entityClass.Exchange;
import me.arasple.mc.trmenu.api.action.ActionHandle;
import me.arasple.mc.trmenu.api.action.base.ActionBase;
import me.arasple.mc.trmenu.api.action.base.ActionContents;
import me.arasple.mc.trmenu.module.display.MenuSession;
import me.arasple.mc.trmenu.taboolib.common.platform.ProxyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LTExchange extends ActionBase {
    public LTExchange(@NotNull ActionHandle handle) {
        super(handle);
    }

    @Override
    public void onExecute(@NotNull ActionContents contents, @NotNull ProxyPlayer player, @NotNull ProxyPlayer placeholderPlayer) {
        String exchange = String.valueOf(contents);
        Player bukkitPlayer = Bukkit.getPlayerExact(player.getName());
        if (Exchange.canExchange(exchange, bukkitPlayer)) {
            Exchange.exchange(exchange, bukkitPlayer);
            bukkitPlayer.sendMessage("§a兑换成功！");
        }else {
            bukkitPlayer.sendMessage("§c兑换失败，你的物品不足！");
        }
        MenuSession.Companion.getSession(bukkitPlayer).playerItemSlots();
    }
}
