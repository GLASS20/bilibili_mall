package xd.suka;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static ArrayList<String> KEYS = new ArrayList<>();
    public static ArrayList<String> BLACK_KEYS = new ArrayList<>();
    public static ArrayList<String> CategoryFilter = new ArrayList<>();

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

            KEYS.clear();
            BLACK_KEYS.clear();
            CategoryFilter.clear();

            KEYS.addAll(Arrays.asList(keys));
            BLACK_KEYS.addAll(Arrays.asList(blackKeys));
            CategoryFilter.addAll(Arrays.asList(categoryFilter));
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
            properties.setProperty("KEYS", String.join(",", KEYS.toArray(new String[0])));
            properties.setProperty("BLACK_KEYS", String.join(",", BLACK_KEYS.toArray(new String[0])));
            properties.setProperty("CATEGORY_FILTER", String.join(",", CategoryFilter.toArray(new String[0])));
            properties.setProperty("ONLY_ONE", String.valueOf(onlyOne));

            properties.store(fos, null);
        } catch (IOException e) {
            System.out.println("配置文件保存失败: " + e.getMessage());
        }
    }
}
