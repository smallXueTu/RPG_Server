import cn.LTCraft.core.Main;

import java.util.HashMap;

/**
 * Created by Angel、 on 2022/5/14 2:17
 */
public class test {
    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder("11");
        test(stringBuilder);
        System.out.println(stringBuilder);
    }
    public static void test(StringBuilder stringBuilder){
        stringBuilder = new StringBuilder("22");
    }
}
