package com.bestyou.timeline3;

/**
 * 存储数据bena类
 */

public class TimeData {

    private String posttime;
    private String title;

    public TimeData(String posttime,String title) {
        this.title = title;
        this.posttime = posttime;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
