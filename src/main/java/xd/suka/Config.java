package xd.suka;

import xd.suka.map.IDMap;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @author Liycxc
 * Date: 2024/7/19 下午2:49
 */
public class Config {
    private static final String CONFIG_FILE = "config.properties";
    public static double PRICE_MAX = 600;
    public static double PRICE_MIN = 40;
    public static int OFF_MIN = 0;
    public static int OFF_MAX = 60;
    public static String[] KEYS = new String[]{"TAITO", "入梦时刻", "夜刀神十香"};
    public static String[] BLACK_KEYS = new String[]{"展示"};
    public static String[] CategoryFilter = new String[]{IDMap.shou_ban};

    public static String getCookie() {
        try {
            File cookie_file = new File("cookie.txt");
            return Files.readString(cookie_file.toPath());
        } catch (Exception exception) {
            return null;
        }
    }
    public static boolean onlyOne = false;

    // 加载配置
    public static void loadConfig() {
        try (FileReader fis = new FileReader(CONFIG_FILE)) {
            Properties properties = new Properties();
            properties.load(fis);

            String priceMax = properties.getProperty("PRICE_MAX");
            String priceMin = properties.getProperty("PRICE_MIN");
            String offMax = properties.getProperty("OFF_MAX");
            String offMin = properties.getProperty("OFF_MIN");
            String[] keys = properties.getProperty("KEYS").split(",");
            String[] blackKeys = properties.getProperty("BLACK_KEYS").split(",");
            String[] categoryFilter = properties.getProperty("CATEGORY_FILTER").split(",");
            boolean onlyOne1 = Boolean.parseBoolean(properties.getProperty("ONLY_ONE"));

            PRICE_MAX = Double.parseDouble(priceMax);
            PRICE_MIN = Double.parseDouble(priceMin);
            OFF_MAX = Integer.parseInt(offMax);
            OFF_MIN = Integer.parseInt(offMin);
            System.arraycopy(keys, 0, KEYS, 0, keys.length);
            System.arraycopy(blackKeys, 0, BLACK_KEYS, 0, blackKeys.length);
            System.arraycopy(categoryFilter, 0, CategoryFilter, 0, categoryFilter.length);
            onlyOne = onlyOne1;
        } catch (IOException e) {
            System.out.println("配置文件加载失败: " + e.getMessage());
            saveConfig();
        }
    }

    // 保存配置
    public static void saveConfig() {
        try (FileWriter fos = new FileWriter(CONFIG_FILE)) {
            Properties properties = new Properties();

            properties.setProperty("PRICE_MAX", String.valueOf(PRICE_MAX));
            properties.setProperty("PRICE_MIN", String.valueOf(PRICE_MIN));
            properties.setProperty("OFF_MAX", String.valueOf(OFF_MAX));
            properties.setProperty("OFF_MIN", String.valueOf(OFF_MIN));
            properties.setProperty("KEYS", String.join(",", KEYS));
            properties.setProperty("BLACK_KEYS", String.join(",", BLACK_KEYS));
            properties.setProperty("CATEGORY_FILTER", String.join(",", CategoryFilter));
            properties.setProperty("ONLY_ONE", String.valueOf(onlyOne));

            properties.store(fos, null);
        } catch (IOException e) {
            System.out.println("配置文件保存失败: " + e.getMessage());
        }
    }
}
