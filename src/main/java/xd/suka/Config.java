package xd.suka;

import xd.suka.map.IDMap;

import java.io.File;
import java.nio.file.Files;

/**
 * @author Liycxc
 * Date: 2024/7/19 下午2:49
 */
public class Config {
    public static double PRICE_MAX = 1200;
    public static double PRICE_MIN = 350;
    public static int OFF_MAX = 50;
    public static int OFF_MIN = 0;
    public static String[] KEYS = new String[]{"22","33","幻星集", "初音未来", "约会", "东方"};
    public static String[] BLACK_KEYS = new String[]{"棕色相框","中央芭蕾舞团"};
    public static String[] CategoryFilter = new String[]{IDMap.shou_ban,IDMap.mo_xing,IDMap.zhou_bian};
    public static boolean onlyOne = true;

    public static String getCookie() {
        try {
            File cookie_file = new File("cookie.txt");
            return Files.readString(cookie_file.toPath());
        } catch (Exception exception) {
            return null;
        }
    }
}
