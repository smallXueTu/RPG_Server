package cn.LTCraft.core.hook.TrMenu.actions;

import eos.moe.dragoncore.config.Config;
import eos.moe.dragoncore.network.PacketSender;
import me.arasple.mc.trmenu.api.action.ActionHandle;
import me.arasple.mc.trmenu.api.action.base.ActionBase;
import me.arasple.mc.trmenu.api.action.base.ActionContents;
import me.arasple.mc.trmenu.taboolib.common.platform.ProxyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 打开龙核GUI
 */
public class OpenCoreGui extends ActionBase {
    public OpenCoreGui(@NotNull ActionHandle handle) {
        super(handle);
    }

    @Override
    public void onExecute(@NotNull ActionContents contents, @NotNull ProxyPlayer player, @NotNull ProxyPlayer placeholderPlayer) {
        String gui = String.valueOf(contents);
        Player bukkitPlayer = Bukkit.getPlayerExact(player.getName());
        if (!Config.fileMap.containsKey("Gui/" + gui + ".yml")) {
            bukkitPlayer.sendMessage("找不到界面" + gui + "！请报告给管理员！");
        } else {
            PacketSender.sendOpenGui(bukkitPlayer, gui);
        }
    }
}
