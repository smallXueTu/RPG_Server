package cn.LTCraft.core.utils;


import cn.LTCraft.core.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_12_R1.*;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Utils {
    private static Random random = new Random();
    private static Gson gson = new Gson();

    public static Random getRandom() {
        return random;
    }

    public static Gson getGson() {
        return gson;
    }

    /**
     * 获取时间戳的秒
     * @return 秒
     */
    public static long getSecondTime(){
        return System.currentTimeMillis() / 1000;
    }
    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }
    /**
     * 验证密码
     * @param password
     * @return
     */
    public static String checkPassword(String password){
        if(isChinese(password)){//判断是否有在中文字符
            return "密码不能含有中文字符!";
        }
        int lo = password.length();
        if(!(lo>=6 && lo<=16)){
            return "密码长度在6-16位!";
        }
        if(
                password.equals("123456") ||
                        password.equals("654321") ||
                        password.equals("111111") ||
                        password.equals("123123") ||
                        password.equals("321321") ||
                        password.equals("123456789") ||
                        password.equals("666666") ||
                        password.equals("1234567") ||
                        password.equals("12345678") ||
                        password.equals("012345") ||
                        password.equals("0123456789") ||
                        password.equals("0123456") ||
                        password.equals("01234567") ||
                        password.equals("012345678") ||
                        password.equals("1234560") ||
                        password.equals("1234567890"))
        {
            return "密码太简单了，不建议这样做！";
        }
        return "true";
    }

    /**
     * 效验一个字符是否为中文
     * @param c 待效验字符
     * @return 是否为中文
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 邮箱格式校验
     * @param email 袋效验的字符串
     * @return 是否合法
     */
    public static boolean  checkEmail(String email) {
        return Pattern.matches("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", email.trim());
    }

    /**
     * 判断字符串中是否包含中文
     * @param strName 待校验字符串
     * @return 是否为中文
     */
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 自定义合成配方
     */
    public static void applyRecipes(){
        CraftingManager.recipes = new RegistryMaterials<MinecraftKey, IRecipe>();
        String dir = Main.getInstance().getDataFolder()+ File.separator + "recipe";
        try {
            Path recipe_path = Paths.get(dir);
            Iterator<Path> iterator =  Files.walk(recipe_path).iterator();
            while (iterator.hasNext()){
                Path path = iterator.next();
                if ("json".equals(FilenameUtils.getExtension(path.toString()))) {
                    Path json_path = recipe_path.relativize(path);
                    String s = FilenameUtils.removeExtension(json_path.toString()).replaceAll("\\\\", "/");
                    BufferedReader bufferedreader = Files.newBufferedReader(path);
                    Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
                    JsonObject jsonObject = ChatDeserializer.a(gson, bufferedreader, JsonObject.class);
                    Class<CraftingManager> cls = CraftingManager.class;
                    IRecipe iRecipe = null;
                    try {
                        Method method = cls.getDeclaredMethod("a", JsonObject.class);
                        method.setAccessible(true);
                        iRecipe = (IRecipe) method.invoke(CraftingManager.class, jsonObject);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (iRecipe != null) {
                        CraftingManager.a(s, iRecipe);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重复一个字符串n次
     * @param seed 字符串
     * @param n 次数
     * @return 重复后的
     */
    private static String repeatedStr(String seed,int n) {
        return String.join("", Collections.nCopies(n, seed));
    }

    /**
     * 获取等级的罗马数字
     * @return 罗马数字
     */
    public static String getLevelStr(int level) {
        String str = "";
        switch (level){
            case 1:
            case 2:
            case 3:
            case 4:
                str += repeatedStr("I", level);
            break;
            case 5:
                str = "V";
            case 6:
            case 7:
            case 8:
            case 9:
                str += repeatedStr("I", level - 5);
            break;
            default:
            return String.valueOf(level);
        }
        return str;
    }
    /**
     * 获取大写数字
     * @return 大写数字
     */
    public static String getNumberCapitalize(int level) {
        String str = "";
        switch (level){
            case 1:
                str = "一";
                break;
            case 2:
                str = "二";
                break;
            case 3:
                str = "三";
                break;
            case 4:
                str = "四";
                break;
            case 5:
                str = "五";
                break;
            case 6:
                str = "六";
                break;
            case 7:
                str = "七";
                break;
            case 8:
                str = "八";
                break;
            case 9:
                str = "九";
                break;
            case 0:
                str = "零";
                break;
            default:
                str = String.valueOf(level);
        }
        return str;
    }

    /**
     * 格式化数字
     * @param number 数字
     * @param length 最大保留长度
     * @return 格式化后的数字
     * TODO: 优化它！
     */
    public static String formatNumber(double number, int length){
        if (number == (int)number)return String.valueOf((int)number);
        String[] n = String.valueOf(number).split("\\.");
        String decimal = n[1].substring(0, Math.min(n[1].length(), length));
        int i = getLast0(decimal);
        if (i != -1) {
            if (decimal.substring(0, i).length() > 0)
                return n[0] + "." + decimal.substring(0, i);
            else
                return n[0];
        }else {
            if (decimal.length() > 0)
                return n[0] + "." + decimal;
            else
                return n[0];
        }
    }
    public static String formatNumber(double number){
        return formatNumber(number, 2);
    }

    /**
     * 获取多于0的开始位置
     * @param number 数字
     * @return 位置
     */
    public static int getLast0(String number){
        if (number.charAt(number.length() - 1) != '0')return -1;
        for (int i = number.length() - 2; i >0 ; i--) {
            if (number.charAt(i) != '0')return i+1;
        }
        return 0;
    }

    /**
     * 随机数组内的一个元素
     * @param arr 数组
     * @return 随机一个元素
     */
    public static <E> E arrayRandom(E[] arr){
        return arr[getRandom().nextInt(arr.length)];
    }

    /**
     * 清除颜色符号 包括§|
     * @param str 要清除的字符串
     * @return 被清除的字符串
     */
    public static String clearColor(String str){
        return str.replaceAll("([§&])[A-Za-z0-9|]" , "");
    }
    public static void main(String[] args) {
//        System.out.println(formatNumber(4.55566));
//        System.out.println(formatNumber(4.555660000000, 10));
//        System.out.println(formatNumber(4.555660000111000, 10));
//        System.out.println(arrayRandom(new Integer[]{111, 222, 333}));
        System.out.println(clearColor("&8PVE伤害:200 &|"));
    }

}
