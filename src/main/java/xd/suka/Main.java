package xd.suka;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import xd.suka.map.InsideMap;
import xd.suka.map.ItemMap;
import xd.suka.map.PayloadMap;
import xd.suka.map.ResponseMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Liycxc
 * Date: 2024/7/19 下午2:30
 */
public class Main {
    public static String MALL_URL = "https://mall.bilibili.com/mall-magic-c/internet/c2c/v2/list";
    public static String nextId = "null";
    public static Gson gson = new Gson();

    public static void main(String[] args) throws InterruptedException, IOException {
        while (true) {
            PayloadMap payloadMap = new PayloadMap();
            payloadMap.priceFilters = new String[]{(int) Config.PRICE_MIN * 100 + "-" + (int) Config.PRICE_MAX * 100};
            payloadMap.discountFilters = new String[]{Config.OFF_MIN + "-" + Config.OFF_MAX};
            payloadMap.nextId = nextId;
            payloadMap.categoryFilter = new Main().convertToCSV(Config.CategoryFilter);

            System.out.println(gson.toJson(payloadMap));

            String result = sendPost(gson.toJson(payloadMap));
            ResponseMap responseMap = gson.fromJson(result, ResponseMap.class);

            System.out.println(result);

            if (responseMap.code != 0) {
                System.out.println("请求失败，错误码：" + responseMap.code);
                Thread.sleep(15000);
                continue;
            }

            InsideMap insideMap = responseMap.data;
            nextId = insideMap.nextId;

            for (ItemMap item : insideMap.data) {
                if (containsList(item.c2cItemsName, Config.KEYS, true) && !containsList(item.c2cItemsName, Config.BLACK_KEYS, false)) {
                    if (Config.onlyOne && item.totalItemsCount > 1) {
                        break;
                    }
                    printAndWrite(item);
                    break;
                }
            }

            Thread.sleep(1000);
        }
    }

    public static boolean containsList(String str, String[] list, boolean defaultRet)  {
        if (list.length == 0) {
            return defaultRet;
        }

        for (String s : list) {
            if (str.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private static void printAndWrite(ItemMap item) throws IOException {
        File csvFile = new File("data.csv");
        if (!csvFile.exists()) {
            if (!csvFile.createNewFile()) {
                System.out.println("Failed to create file: " + csvFile.getAbsolutePath());
                return;
            }
        }

        double price = Double.parseDouble(item.showPrice);
        double marketPrice = Double.parseDouble(item.showMarketPrice);
        String discount_rate = String.format("%.2f", (price / marketPrice) * 10) + "折";

        String[] data = {item.c2cItemsName, item.showPrice, item.showMarketPrice ,discount_rate, "https:" + item.detailDtoList.get(0).img ,"https://mall.bilibili.com/neul-next/index.html?page=magic-market_detail&noTitleBar=1&itemsId=" + item.c2cItemsId + "&from=market_index"};
        System.out.println(Arrays.toString(data));

        try{
            if (csvFile.length() == 0) {
                FileWriter fw = new FileWriter(csvFile);
                fw.write("商品名称,售价,市场价,折扣率,商品图片,商品链接");
                fw.write("\r\n");
                fw.close();
            }

            FileOutputStream fos = new FileOutputStream(csvFile,true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);

            osw.write(new Main().convertToCSV(data));
            osw.write("\r\n");

            osw.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(", "));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    private static String sendPost(String data) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(MALL_URL);

        httpPost.setHeader("authority", "mall.bilibili.com");
        httpPost.setHeader("accept", "application/json, text/plain, */*");
        httpPost.setHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        httpPost.setHeader("content-type", "application/json");
        httpPost.setHeader("cookie", Config.getCookie());
        httpPost.setHeader("origin", "https://mall.bilibili.com");
        httpPost.setHeader("referer", "https://mall.bilibili.com/neul-next/index.html?page=magic-market_index");
        httpPost.setHeader("sec-ch-ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Microsoft Edge\";v=\"120\"");
        httpPost.setHeader("sec-ch-ua-mobile", "?0");
        httpPost.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpPost.setHeader("sec-fetch-dest", "empty");
        httpPost.setHeader("sec-fetch-mode", "cors");
        httpPost.setHeader("sec-fetch-site", "same-origin");
        httpPost.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0");

        try {
            httpPost.setEntity(new StringEntity(data));

            HttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
            return result;
        } catch (IOException exception) {
            System.err.println("Error sending POST request: " + exception.getMessage());
        }

        return null;
    }
}