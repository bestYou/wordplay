package com.bestyou.wtc1;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class GeoWord extends BmobObject {

    private String userName;
    private BmobGeoPoint gpsAdd;

    public BmobDate getTimeDingwei() {
        return timeDingwei;
    }

    public void setTimeDingwei(BmobDate timeDingwei) {
        this.timeDingwei = timeDingwei;
    }

    private BmobDate timeDingwei;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BmobGeoPoint getGpsAdd() {
        return gpsAdd;
    }
    public void setGpsAdd(BmobGeoPoint gpsAdd) {
        this.gpsAdd = gpsAdd;
    }

}