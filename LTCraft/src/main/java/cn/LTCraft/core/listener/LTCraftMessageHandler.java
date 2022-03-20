package cn.LTCraft.core.listener;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.task.ClientCheckTask;
import cn.LTCraft.core.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;


public class LTCraftMessageHandler implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        int cid = (int)bytes[0];
        if (cid==1){
            String temp = new String(bytes);
            temp = temp.substring(1);
            if (temp.length()<=0)return;
            int len = Integer.parseInt(Utils.stringToAscii(temp.substring(0, 1)));
            temp = temp.substring(1);
            String result = temp.substring(0, len);
            ClientCheckTask task = null;
            try {
                task = Main.getInstance().getTack(Integer.parseInt(result));
            }catch (NumberFormatException ignored){
            }
            if (task == null){
                String[] results = result.split(":");
                switch (results[0]){
                    case "clickFrequently":
                        if (GlobalRefresh.warnings.contains(player.getName())){
                            player.kickPlayer("§c频繁点击！");
                        }else {
                            player.sendMessage("§c§l[警告]你的点击速度过快，疑似连点器，请控制你的点击频率！");
                            GlobalRefresh.warnings.add(player.getName());
                        }
                        break;
                }
            }else{
                temp = temp.substring(len);
                if (!temp.equals("true")) {
                    System.out.println("玩家" + player.getName() + " " + task.getTag() + " 返回" + temp);
                }
                task.setComplete(temp.equals("true"));
            }
        }
    }
}
