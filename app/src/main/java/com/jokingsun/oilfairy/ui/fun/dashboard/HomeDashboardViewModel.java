package com.jokingsun.oilfairy.ui.fun.dashboard;

import static com.jokingsun.oilfairy.utils.MathUtil.roundToDecimalPlaces;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.common.constant.OilQualityEnum;
import com.jokingsun.oilfairy.data.local.livedata.BaseLiveDataModel;
import com.jokingsun.oilfairy.data.remote.ApiHelper;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilDetailInfo;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilPriceInfo;

import org.checkerframework.checker.units.qual.A;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeDashboardViewModel extends BaseViewModel<HomeDashboard, BaseLiveDataModel> {

    private final ExecutorService executorService;
    private final ArrayList<ResOilDetailInfo> oilDetailInfoList = new ArrayList<>();
    public final MutableLiveData<Float> dieselPriceDelta = new MutableLiveData<>();
    public final MutableLiveData<Float> gasPriceDelta = new MutableLiveData<>();
    public final MutableLiveData<ArrayList<ResOilDetailInfo>> oilDashboardData = new MutableLiveData<>();

    public HomeDashboardViewModel(ApiHelper apiHelper) {
        super(apiHelper);
        executorService = Executors.newFixedThreadPool(2);
        oilDetailInfoList.add(new ResOilDetailInfo(OilQualityEnum.QUALITY_98.getOilQualitySign()));
        oilDetailInfoList.add(new ResOilDetailInfo(OilQualityEnum.QUALITY_95.getOilQualitySign()));
        oilDetailInfoList.add(new ResOilDetailInfo(OilQualityEnum.QUALITY_92.getOilQualitySign()));
        oilDetailInfoList.add(new ResOilDetailInfo(OilQualityEnum.QUALITY_SUPER.getOilQualitySign()));
    }

    @Override
    protected void setWhenNetWorkRework() {

    }

    @Override
    protected void attachRepository(HomeDashboard navigator) {

    }

    public void getNextWeekPredict() {
        executorService.submit(() -> {
            try {
                //取得中油和台塑的爬蟲價格
                getNewCpcAndFpgRemotePrice();
                //取得 costco 爬蟲價格
                getCostcoRemotePrice();

                oilDashboardData.postValue(oilDetailInfoList);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("爬蟲測試：", "錯誤！");
            }
        });
    }

    /**
     * 取得中油和台塑的即時公告價格(本、下週)
     */
    private void getNewCpcAndFpgRemotePrice() {
        //Connect to the website

        try {
            Document doc = Jsoup.connect("https://toolboxtw.com/zh-TW/detector/gasoline_price").get();
            Elements rowElements = doc.select("div.next_week_price_table").select("tbody")
                    .select("tr");

            if (rowElements != null) {
                //index：1 & 5 -> 中油 92 & 台塑 92 //index：2 & 6 -> 中油 95 & 台塑 95
                //index：3 & 7 -> 中油 98 & 台塑 98 //index：4 & 8 -> 中油柴油 & 台塑柴油

                for (int i = 0; i < rowElements.size(); i++) {
                    Elements priceElements = rowElements.get(i).select("td");

                    // 98:0 95:1 92:2 99:3
                    for (int s = 0; s < priceElements.size() - 1; s++) {
                        String srcPrice = priceElements.get(s).text();
                        String price = srcPrice.substring(0, srcPrice.length() - 1);

                        //第一行資訊：中油 92 第五行：台塑 92
                        if (i == 0 || i == 4) {
                            insertOilRowData(2, s == 0, i == 0, price);
                        }

                        //第二行資訊：中油 95 第六行：台塑 95
                        if (i == 1 || i == 5) {
                            insertOilRowData(1, s == 0, i == 1, price);
                        }

                        //第三行資訊：中油 98 第七行：台塑 98
                        if (i == 2 || i == 6) {
                            insertOilRowData(0, s == 0, i == 2, price);
                        }

                        //第四行資訊：中油柴油 第八行：台塑柴油
                        if (i == 3 || i == 7) {
                            insertOilRowData(3, s == 0, i == 3, price);
                        }

                    }
                }
            }

            if (oilDetailInfoList != null) {
                //計算汽油預測差值
                float cpcNowGasPrice = Float.parseFloat(oilDetailInfoList.get(0).getCpcNowPrice());
                float cpcNextGasPrice = Float.parseFloat(oilDetailInfoList.get(0).getCpcNextPrice());
                gasPriceDelta.postValue(cpcNextGasPrice - cpcNowGasPrice);

                //計算柴油預測差值
                float cpcNowDieselPrice = Float.parseFloat(oilDetailInfoList.get(3).getCpcNowPrice());
                float cpcNextDieselPrice = Float.parseFloat(oilDetailInfoList.get(3).getCpcNextPrice());
                dieselPriceDelta.postValue(cpcNextDieselPrice - cpcNowDieselPrice);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("爬蟲測試：", "錯誤！");
        }
    }

    /**
     * 插入爬蟲的每列油價資訊到 oilDetailInfoList(中油和台塑)
     */
    private void insertOilRowData(int qualityCode, boolean isNow, boolean isCpc, String price) {
        if (isNow) {
            if (isCpc) {
                oilDetailInfoList.get(qualityCode).setCpcNowPrice(price);
            } else {
                oilDetailInfoList.get(qualityCode).setFpgNowPrice(price);
            }

        } else {
            if (isCpc) {
                oilDetailInfoList.get(qualityCode).setCpcNextPrice(price);
            } else {
                oilDetailInfoList.get(qualityCode).setFpgNextPrice(price);
            }
        }
    }

    /**
     * 取得 Costco 的即時公告價格(本、下週)
     */
    private void getCostcoRemotePrice() throws IOException {
        //取得 costco 價格
        //Connect to the website
        Document costcoDoc = Jsoup.connect("https://www.toolskk.com/tw-gas-price").get();

        // 使用选择器定位到特定的 div 元素
        Elements elements = costcoDoc.select("div.results-display-area");

        if (elements != null && elements.size() > 0) {
            Elements innerElements = elements.get(0).select("div.gas-price-box");
            Elements columnCostco = innerElements.get(1).select("div.column");

            for (int i = 1; i < columnCostco.size(); i++) {
                //順序依序 98 95 超柴 (costco 沒有 92)
                String nowPrice = columnCostco.get(i).text().substring(0, 4);
                calculateCostcoPrice(i, nowPrice);

                Log.d("爬蟲結果好市多：", "內容：" + columnCostco.get(i).text().substring(0, 4));
            }
        }
    }

    /**
     * 計算 costco 本週 &下週預測價格
     */
    private void calculateCostcoPrice(int index, String nowPrice) {
        if (gasPriceDelta.getValue() == null || dieselPriceDelta.getValue() == null) {
            return;
        }

        boolean isGas = index == 1 || index == 2;
        int q = isGas ? index - 1 : index;
        float delta = isGas ? gasPriceDelta.getValue() : dieselPriceDelta.getValue();
        float result = Float.parseFloat(nowPrice) + delta;
        //本週
        oilDetailInfoList.get(q).setCostcoNowPrice(nowPrice);
        //下週
        String nextPrice = String.valueOf(roundToDecimalPlaces(result, 1));
        oilDetailInfoList.get(q).setCostcoNextPrice(nextPrice);
    }

    public MutableLiveData<ArrayList<ResOilDetailInfo>> getOilDashboardData() {
        return oilDashboardData;
    }

    public MutableLiveData<Float> getDieselPriceDelta() {
        return dieselPriceDelta;
    }

    public MutableLiveData<Float> getGasPriceDelta() {
        return gasPriceDelta;
    }
}
