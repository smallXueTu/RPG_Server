package cn.LTCraft.core.hook.papi;

import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.utils.MathUtils;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.utils.Utils;
import cn.ltcraft.item.objs.PlayerAttribute;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.*;

public class LTExpansion extends PlaceholderExpansion {
    private static String[] colors = {
            "§a","§c","§d","§1","§2","§4","§3","§5","§6",
            "§7","§8","§9","§e","§f"
    };
    private static Map<String, Integer> index = new HashMap<>();
    private static boolean add;
    @Override
    public String getIdentifier() {
        return "lt";
    }

    @Override
    public String getAuthor() {
        return "Angel 、";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        PlayerData data = null;
        switch (params){
            case "class":
                data = SkillAPI.getPlayerData(p);
                if (data == null)return "";
                if (data.getMainClass() == null)return "无职业";
                return data.getMainClass().getData().getName();
            case "class_level":
                data = SkillAPI.getPlayerData(p);
                if (data == null)return "";
                if (data.getMainClass() == null)return "0";
                return String.valueOf(data.getMainClass().getLevel());
            case "class_exp":
                data = SkillAPI.getPlayerData(p);
                if (data == null)return "";
                if (data.getMainClass() == null)return "0";
                return Utils.formatNumber(data.getMainClass().getExp());
            case "class_exp_full":
                data = SkillAPI.getPlayerData(p);
                if (data == null)return "";
                if (data.getMainClass() == null)return "0";
                return Utils.formatNumber(data.getMainClass().getTotalExp());
            case "health":
                return Utils.formatNumber(p.getHealth());
            case "gold_coins":
                return Utils.formatNumber(PlayerConfig.getPlayerConfig(p).getPlayerInfo().getGold()) + "";
            case "max_health":
                return Utils.formatNumber(p.getMaxHealth());
            case "armor":
                return PlayerAttribute.getPlayerAttribute(p).getArmorValue() + "";
            case "injuryFree":
                double injuryFreePercentage = MathUtils.getInjuryFreePercentage(PlayerAttribute.getPlayerAttribute(p).getArmorValue());
                return Utils.formatNumber(injuryFreePercentage * 100);
            case "server_name":
                int index = LTExpansion.index.getOrDefault(p.getName(), 0);
                char[] chars = "LTCraft Server".toCharArray();
                StringBuilder serverName = new StringBuilder("§l");
                for (char aChar : chars) {
                    if (aChar != ' ')
                        serverName.append(colors[index++ % colors.length]).append(aChar);
                    else
                        serverName.append(aChar);
                }
                LTExpansion.index.put(p.getName(), index);
                return serverName.toString();
        }
        if (params.startsWith("hastag")){
            String tag = params.substring("hastag".length() + 1);
            String playerID = PlayerConverter.getID(p);
            pl.betoncraft.betonquest.database.PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
            return String.valueOf(playerData.hasTag("default." + tag));
        }
        if (params.startsWith("player_state")){
            int index = Integer.parseInt(params.substring("player_state".length()));
            if (Temp.playerStates.get(p) == null)return "";
            if (Temp.playerStates.get(p).size() >= index){
                return Temp.playerStates.get(p).get(index - 1).getMessages();
            }
        }
        return "";
    }
}
