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
import com.jokingsun.oilfairy.utils.StringUtil;

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
    public final MutableLiveData<String> placardInfoData = new MutableLiveData<>();

    private float gasDelta = 0.0f;
    private float dieselDelta = 0.0f;

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

    public void getOilDashboardInfo() {
        executorService.submit(() -> {

            try {

                Document doc = Jsoup.connect("https://toolboxtw.com/zh-TW/detector/gasoline_price").get();

                //取得標題公告日期及時間
                String placardInfo = doc.select("div.price-prediction").select("h2").text();

                if (StringUtil.isNullSafeString(placardInfo)) {
                    boolean isHavePlacard = placardInfo.contains("公告");
                    placardInfoData.postValue(isHavePlacard ? "最新油價調整已公告" : "下週油價預測");
                }

                //取得下周油價變化預測
                Elements deltaElements = doc.select("div.card-body");

                gasDelta = StringUtil.analyticsOilPrice(deltaElements.get(0).text());
                dieselDelta = StringUtil.analyticsOilPrice(deltaElements.get(1).text());
                gasPriceDelta.postValue(gasDelta);
                dieselPriceDelta.postValue(dieselDelta);

                //取得中油和台塑的爬蟲價格
                getNewCpcAndFpgRemotePrice();
                //取得 costco 爬蟲價格
                getCostcoRemotePrice();

                oilDashboardData.postValue(oilDetailInfoList);

            } catch (Exception e) {
                e.printStackTrace();
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
                    String srcPrice = priceElements.get(0).text();
                    String price = srcPrice.substring(0, srcPrice.length() - 1);

                    //第一行資訊：中油 92 第五行：台塑 92
                    if (i == 0 || i == 4) {
                        insertOilRowData(2, i == 0, price);
                    }

                    //第二行資訊：中油 95 第六行：台塑 95
                    if (i == 1 || i == 5) {
                        insertOilRowData(1, i == 1, price);
                    }

                    //第三行資訊：中油 98 第七行：台塑 98
                    if (i == 2 || i == 6) {
                        insertOilRowData(0, i == 2, price);
                    }

                    //第四行資訊：中油柴油 第八行：台塑柴油
                    if (i == 3 || i == 7) {
                        insertOilRowData(3, i == 3, price);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入爬蟲的每列油價資訊到 oilDetailInfoList(中油和台塑)
     */
    private void insertOilRowData(int qualityCode, boolean isCpc, String price) {
        float priceDelta = qualityCode == 3 ? dieselDelta : gasDelta;
        String nextPrice = String.valueOf(roundToDecimalPlaces(Float.parseFloat(price) + priceDelta, 1));

        if (isCpc) {
            oilDetailInfoList.get(qualityCode).setCpcNowPrice(price);
            oilDetailInfoList.get(qualityCode).setCpcNextPrice(nextPrice);

        } else {
            oilDetailInfoList.get(qualityCode).setFpgNowPrice(price);
            oilDetailInfoList.get(qualityCode).setFpgNextPrice(nextPrice);
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
            }
        }
    }

    /**
     * 計算 costco 本週 &下週預測價格
     */
    private void calculateCostcoPrice(int index, String nowPrice) {
        boolean isGas = index == 1 || index == 2;
        int q = isGas ? index - 1 : index;
        float delta = isGas ? gasDelta : dieselDelta;
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
