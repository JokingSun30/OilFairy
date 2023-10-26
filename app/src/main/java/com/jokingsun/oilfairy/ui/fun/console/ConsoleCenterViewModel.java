package com.jokingsun.oilfairy.ui.fun.console;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.common.constant.OilCommonSign;
import com.jokingsun.oilfairy.data.local.livedata.BaseLiveDataModel;
import com.jokingsun.oilfairy.data.remote.ApiHelper;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilDetailInfo;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilPriceInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleCenterViewModel extends BaseViewModel<ConsoleCenter, BaseLiveDataModel> {

    private final ExecutorService executorService;
    public final MutableLiveData<String> dieselFuelDelta = new MutableLiveData<>("-");
    public final MutableLiveData<String> gasolineDelta = new MutableLiveData<>("-");
    public final MutableLiveData<String> priceSrcJson = new MutableLiveData<>("");

    private final ArrayList<ResOilDetailInfo> oilDetailInfoList = new ArrayList<>();

    public ConsoleCenterViewModel(ApiHelper apiHelper) {
        super(apiHelper);
        executorService = Executors.newFixedThreadPool(2);
        oilDetailInfoList.add(new ResOilDetailInfo("98"));
        oilDetailInfoList.add(new ResOilDetailInfo("95"));
        oilDetailInfoList.add(new ResOilDetailInfo("92"));
        oilDetailInfoList.add(new ResOilDetailInfo("99"));
    }

    @Override
    protected void setWhenNetWorkRework() {

    }

    @Override
    protected void attachRepository(ConsoleCenter navigator) {

    }

    public void getNextWeekPredict() {
        executorService.submit(() -> {
            try {

                //Connect to the website
                Document doc = Jsoup.connect("https://icard.ai/blog/featured-post/gasprice/").get();

                // 使用选择器定位到特定的 div 元素
                Elements gasoline = doc.select("div.elementor-element-573fc71");
                Elements dieselFuel = doc.select("div.elementor-element-7876d4d");
                Elements updateTime = doc.select("div.elementor-element-8061220");


                if (gasoline != null && gasoline.size() > 0) {
                    gasolineDelta.postValue(gasoline.get(0).text());
                    Log.d("爬蟲結果：", "汽油預測：" + gasoline.get(0).text());
                }

                if (dieselFuel != null && dieselFuel.size() > 0) {
                    dieselFuelDelta.postValue(dieselFuel.get(0).text());
                    Log.d("爬蟲結果：", "柴油預測：" + dieselFuel.get(0).text());
                }

                if (updateTime != null && updateTime.size() > 0) {
                    Log.d("爬蟲結果：", "更新時間：" + updateTime.get(0).text());
                }

                ArrayList<ResOilPriceInfo> oilPriceInfoList = new ArrayList<>();
                Elements rowElements = doc.select("div.elementor-element-c746410").select("tr");

                if (rowElements != null) {
                    //index：1 & 5 -> 中油 92 & 台塑 92
                    //index：2 & 6 -> 中油 95 & 台塑 95
                    //index：3 & 7 -> 中油 98 & 台塑 98
                    //index：4 & 8 -> 中油柴油 & 台塑柴油

                    for (int i = 1; i < rowElements.size(); i++) {
                        Elements priceElements = rowElements.get(i).select("td");

                        // 98:0 95:1 92:2 99:3
                        for (int s = 1; s < priceElements.size(); s++) {
                            String price = priceElements.get(s).text();

                            //第一行資訊：中油 92 第五行：台塑 92
                            if (i == 1 || i == 5) {
                                insertOilRowData(2, s, i == 1, price);
                            }

                            //第二行資訊：中油 95 第六行：台塑 95
                            if (i == 2 || i == 6) {
                                insertOilRowData(1, s, i == 2, price);
                            }

                            //第三行資訊：中油 98 第七行：台塑 98
                            if (i == 3 || i == 7) {
                                insertOilRowData(0, s, i == 3, price);
                            }

                            //第四行資訊：中油柴油 第八行：台塑柴油
                            if (i == 4 || i == 8) {
                                insertOilRowData(3, s, i == 4, price);
                            }

                        }
                    }

                    //計算本周和下周的差值預測
                    ResOilDetailInfo detailInfo = oilDetailInfoList.get(0);

                    float gasNowPrice = Float.parseFloat(detailInfo.getCpcNowPrice());
                    float gasNextPrice = Float.parseFloat(detailInfo.getCpcNextPrice());

                }

                priceSrcJson.postValue(new Gson().toJson(oilDetailInfoList));

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("爬蟲測試：", "錯誤！");
            }
        });

        executorService.submit(() -> {
            try {

                //Connect to the website
                Document doc = Jsoup.connect("https://www.toolskk.com/tw-gas-price").get();

                // 使用选择器定位到特定的 div 元素
                Elements elements = doc.select("div.results-display-area");

                if (elements != null && elements.size() > 0) {
                    Elements innerElements = elements.get(0).select("div.gas-price-box");
                    Elements columnCostco = innerElements.get(1).select("div.column");

                    for (int i = 1; i < columnCostco.size(); i++) {
                        Log.d("爬蟲結果好市多：", "內容：" + columnCostco.get(i).text().substring(0, 4));
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("爬蟲測試：", "錯誤！" + e.getMessage());
            }
        });
    }

    private void insertOilRowData(int qualityCode, int isNow, boolean isCpc, String price) {
        if (isNow == 1) {
            if (isCpc) {
                oilDetailInfoList.get(qualityCode).setCpcNowPrice(price);
            } else {
                oilDetailInfoList.get(qualityCode).setFpgNowPrice(price);
            }

        } else {
            if (isCpc) {
                oilDetailInfoList.get(qualityCode).setFpgNowPrice(price);
            } else {
                oilDetailInfoList.get(qualityCode).setFpgNextPrice(price);
            }
        }
    }

}
