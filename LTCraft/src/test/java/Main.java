import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Angel、 on 2022/6/22 22:14
 */
public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        logger.error("${java:vm}");
    }
}
