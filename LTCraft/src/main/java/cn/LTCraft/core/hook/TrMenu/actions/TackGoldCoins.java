package cn.LTCraft.core.hook.TrMenu.actions;

import cn.LTCraft.core.entityClass.PlayerConfig;
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
 * Created by Angel、 on 2022/3/21 23:41
 * 消耗金币
 */
public class TackGoldCoins extends ActionBase {
    public TackGoldCoins(@NotNull ActionHandle handle) {
        super(handle);
    }

    @Override
    public void onExecute(@NotNull ActionContents contents, @NotNull ProxyPlayer player, @NotNull ProxyPlayer placeholderPlayer) {
        double number = Double.parseDouble(String.valueOf(contents));
        Player bukkitPlayer = Bukkit.getPlayerExact(player.getName());
        PlayerConfig.getPlayerConfig(bukkitPlayer).getPlayerInfo().reduceGold(number);
    }
}
