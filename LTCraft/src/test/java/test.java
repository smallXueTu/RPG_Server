import java.util.HashMap;

/**
 * Created by Angel、 on 2022/5/14 2:17
 */
public class test {
    private static boolean sign = false;
    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        if (sign){
            map.put("test", "test");
        }
        String test = map.computeIfAbsent("test", k -> "test");
        System.out.println(test);
    }
}
