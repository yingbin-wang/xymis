package com.cn.wti.util.app.version;

import java.io.Serializable;

/**
 * Created by wyb on 2017/9/2.
 */

public class UpdataInfo {
    private String version;
    private String url;
    private String description;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}