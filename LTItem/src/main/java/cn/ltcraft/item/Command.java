package cn.ltcraft.item;


import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.items.Armor;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.item.items.Material;
import cn.ltcraft.item.items.MeleeWeapon;
import cn.ltcraft.item.objs.ItemObjs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Command implements CommandExecutor {
    private final LTItemSystem plugin;

    public Command() {
        plugin = LTItemSystem.getInstance();
        plugin.getCommand("lti").setExecutor(this);
        plugin.getCommand("i").setExecutor(this);
    }

    public static void init() {
        new Command();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String cmd, String[] args) {
        if (args.length <= 0) {
            //TODO command help
            return true;
        }
        switch (args[0]) {
            case "":
                break;
            default:
                if (!sender.isOp()) return true;
                switch (args[0]) {
                    case "give":
                    case "g":
                        if (args.length < 3) {
                            sender.sendMessage("§c/lti give 玩家 类型 名字 数量");
                            sender.sendMessage("§c或");
                            sender.sendMessage("§c/lti give 玩家 名字 数量");
                            return true;
                        }
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player == null) {
                            sender.sendMessage("§c玩家不在线！");
                            return true;
                        }
                        ItemTypes itemTypes = ItemTypes.byName(args[2]);
                        String name;
                        int number = 1;
                        if (itemTypes == null) {
                            name = args[2];
                            if (args.length > 3)
                                number = Integer.parseInt(args[3]);
                        } else {
                            name = args[3];
                            if (args.length > 4)
                                number = Integer.parseInt(args[4]);
                        }
                        LTItem ltItem = plugin.getItem(itemTypes, name);
                        if (ltItem == null) {
                            player.sendMessage("§c找不到" + name);
                            return true;
                        }
                        ItemStack itemStack;
                        if (number > 1)
                            itemStack = ltItem.generate(number);
                        else
                            itemStack = ltItem.getItemStack();
                        player.getInventory().addItem(itemStack);
                        sender.sendMessage("§c成功赐予玩家" + name + "×" + itemStack.getAmount());
                        player.sendMessage("§c收到" + name + "×" + itemStack.getAmount());
                        break;
                    case "add":
                    case "a":
                        if (args.length < 4) {
                            sender.sendMessage("§c/lti add 类型 名字 ID");
                            return true;
                        }
                        File file;
                        YamlConfiguration yamlConfiguration = new YamlConfiguration();
                        try {
                            switch (args[1]) {
                                case "材料":
                                    file = new File(plugin.getDataFolder() + File.separator + "items" + File.separator + "材料" + File.separator + args[2] + ".yml");
                                    yamlConfiguration.set("模型", args[3]);
                                    yamlConfiguration.set("名字", args[2]);
                                    yamlConfiguration.set("品质", "未知");
                                    yamlConfiguration.set("绑定", false);
                                    yamlConfiguration.set("不可叠加", false);
                                    yamlConfiguration.set("右键动作", "无");
                                    yamlConfiguration.set("左键动作", "无");
                                    yamlConfiguration.set("捡起动作", "无");
                                    yamlConfiguration.set("显示名字", "§6" + args[2]);
                                    yamlConfiguration.set("说明", new ArrayList<>());
                                    yamlConfiguration.save(file);
                                    yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                                    ItemObjs.materialMap.put(args[2], new Material(yamlConfiguration));
                                    break;
                                case "近战":
                                    file = new File(plugin.getDataFolder() + File.separator + "items" + File.separator + "近战" + File.separator + args[2] + ".yml");
                                    yamlConfiguration.set("模型", args[3]);
                                    yamlConfiguration.set("名字", args[2]);
                                    yamlConfiguration.set("显示名字", "§6" + args[2]);
                                    yamlConfiguration.set("右键动作", "无");
                                    yamlConfiguration.set("左键动作", "无");
                                    yamlConfiguration.set("说明", new ArrayList<>());
                                    yamlConfiguration.set("无限耐久", true);
                                    yamlConfiguration.set("绑定", false);
                                    yamlConfiguration.set("宝石槽位", 3);
                                    yamlConfiguration.set("品质", "未知");
                                    yamlConfiguration.set("PVP", PVP);
                                    yamlConfiguration.set("PVE", PVE);
                                    yamlConfiguration.save(file);
                                    yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                                    ItemObjs.meleeWeaponMap.put(args[2], new MeleeWeapon(yamlConfiguration));
                                    break;
                                case "宝石":
                                    file = new File(plugin.getDataFolder() + File.separator + "items" + File.separator + "宝石" + File.separator + args[2] + ".yml");
                                    yamlConfiguration.set("模型", args[3]);
                                    yamlConfiguration.set("名字", args[2]);
                                    yamlConfiguration.set("品质", "未知");
                                    yamlConfiguration.set("显示名字", "§6" + args[2]);
                                    yamlConfiguration.set("说明", new ArrayList<>());
                                    Map<String, Object> pvp = new HashMap<>(PVP);
                                    pvp.put("伤害", 0);
                                    yamlConfiguration.set("PVP", pvp);
                                    Map<String, Object> pve = new HashMap<>(PVE);
                                    pve.put("伤害", 0);
                                    yamlConfiguration.set("PVE", pve);
                                    yamlConfiguration.save(file);
                                    yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                                    ItemObjs.gemstoneMap.put(args[2], new GemsStone(yamlConfiguration));
                                    break;
                                case "盔甲":
                                    file = new File(plugin.getDataFolder() + File.separator + "items" + File.separator + "盔甲" + File.separator + args[2] + ".yml");
                                    yamlConfiguration.set("模型", args[3]);
                                    yamlConfiguration.set("名字", args[2]);
                                    yamlConfiguration.set("显示名字", "§6" + args[2]);
                                    yamlConfiguration.set("右键动作", "无");
                                    yamlConfiguration.set("左键动作", "无");
                                    yamlConfiguration.set("说明", new ArrayList<>());
                                    yamlConfiguration.set("无限耐久", true);
                                    yamlConfiguration.set("绑定", false);
                                    yamlConfiguration.set("宝石槽位", 3);
                                    yamlConfiguration.set("品质", "未知");
                                    yamlConfiguration.set("护甲", 0);
                                    yamlConfiguration.set("生命值", 0);
                                    yamlConfiguration.set("PVE攻击技能", "");
                                    yamlConfiguration.set("PVP攻击技能", "");
                                    yamlConfiguration.set("药水效果", "");
                                    yamlConfiguration.set("幸运", 0);
                                    yamlConfiguration.set("韧性", 0);
                                    yamlConfiguration.set("闪避", 0);
                                    yamlConfiguration.set("反伤", 0);
                                    yamlConfiguration.save(file);
                                    yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                                    ItemObjs.armorMap.put(args[2], new Armor(yamlConfiguration));
                                    break;
                                default:
                                    return true;
                            }
                            sender.sendMessage("§a添加成功！");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "reload":
                    case "r":
                        ItemObjs.meleeWeaponMap.clear();
                        ItemObjs.materialMap.clear();
                        ItemObjs.armorMap.clear();
                        ItemObjs.gemstoneMap.clear();
                        plugin.loadItems();
                        sender.sendMessage("§a重载成功！");
                        break;
                }
        }
        return true;
    }

    private static final Map<String, Object> PVE = new HashMap<String, Object>() {
        {
            put("伤害", 10);
            put("吸血", 0);
            put("燃烧", 0);
            put("雷击", 0);
            put("群回", 0);
            put("击飞", 0);
            put("击退", 0);
            put("穿甲", 0);
            put("攻击恢复", 0);
            put("自身药水", "");
            put("攻击技能", "");
            put("对方药水", "");
        }
    };
    private static final Map<String, Object> PVP = new HashMap<String, Object>() {
        {
            put("伤害", 5);
            put("吸血", 0);
            put("燃烧", 0);
            put("雷击", 0);
            put("击飞", 0);
            put("击退", 0);
            put("穿甲", 0);
            put("攻击恢复", 0);
            put("自身药水", "");
            put("对方药水", "");
            put("攻击技能", "");
            put("击杀信息", "");
        }
    };
}
