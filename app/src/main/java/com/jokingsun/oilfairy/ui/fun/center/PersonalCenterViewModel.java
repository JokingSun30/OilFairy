package com.jokingsun.oilfairy.ui.fun.center;

import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.data.local.livedata.BaseLiveDataModel;
import com.jokingsun.oilfairy.data.remote.ApiHelper;

public class PersonalCenterViewModel extends BaseViewModel<PersonalCenter, BaseLiveDataModel> {

    public PersonalCenterViewModel(ApiHelper apiHelper) {
        super(apiHelper);
    }

    @Override
    protected void setWhenNetWorkRework() {

    }

    @Override
    protected void attachRepository(PersonalCenter navigator) {

    }
}
