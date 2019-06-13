package com.herry.rssreader.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Channel {
    @Id(autoincrement = true)
    private Long id;

    @Index(unique = true)
    private String key;

    private String title;

    private String url;

    private Long time;

    private String siteUrl;

    private String desc;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getSiteUrl() {
        return this.siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Generated(hash = 576978851)
    public Channel(Long id, String key, String title, String url, Long time,
            String siteUrl, String desc) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.url = url;
        this.time = time;
        this.siteUrl = siteUrl;
        this.desc = desc;
    }

    @Generated(hash = 459652974)
    public Channel() {
    }
}
