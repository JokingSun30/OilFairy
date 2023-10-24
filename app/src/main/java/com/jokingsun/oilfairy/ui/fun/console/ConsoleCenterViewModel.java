package com.jokingsun.oilfairy.ui.fun.console;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.common.constant.OilCommonSign;
import com.jokingsun.oilfairy.data.local.livedata.BaseLiveDataModel;
import com.jokingsun.oilfairy.data.remote.ApiHelper;
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

    public ConsoleCenterViewModel(ApiHelper apiHelper) {
        super(apiHelper);
        executorService = Executors.newFixedThreadPool(2);
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
                    for (int i = 1; i < rowElements.size(); i++) {
                        Elements priceElements = rowElements.get(i).select("td");

                        ResOilPriceInfo oilPriceInfo = new ResOilPriceInfo();
                        oilPriceInfo.setOilCommonCode(String.valueOf(i));

                        for (int s = 1; s < priceElements.size(); s++) {
                            String price = priceElements.get(s).text();
                            if (s == 1) {
                                //本週
                                oilPriceInfo.setCurrentPrice(price);
                            } else {
                                //下週
                                oilPriceInfo.setNextWeekPrice(price);
                            }
                        }

                        oilPriceInfoList.add(oilPriceInfo);
                    }
                }

                priceSrcJson.postValue(new Gson().toJson(oilPriceInfoList));

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
                        Log.d("爬蟲結果好市多：", "內容：" + columnCostco.get(i).text().substring(0,4));
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("爬蟲測試：", "錯誤！" + e.getMessage());
            }
        });
    }

}
