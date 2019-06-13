package com.herry.rssreader.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Article {
    @Id(autoincrement = true)
    private Long id;

    private String title;

    private String link;

    private String description;

    private Boolean read;

    private Boolean trash;

    private String content;

    private Long channelId;

    private Long published;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    @Generated(hash = 742516792)
    public Article() {
    }

    @Generated(hash = 85056863)
    public Article(Long id, String title, String link, String description,
            Boolean read, Boolean trash, String content, Long channelId,
            Long published) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.read = read;
        this.trash = trash;
        this.content = content;
        this.channelId = channelId;
        this.published = published;
    }

    public String getDescription() {
        return description;
    }

    public Long getPublished() {
        return published;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRead() {
        return this.read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public void setPublished(Long published) {
        this.published = published;
    }

    public Boolean getTrash() {
        return this.trash;
    }

    public void setTrash(Boolean trash) {
        this.trash = trash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Article) {
            Article article = (Article) o;
            return article.getTitle().equals(getTitle())
                    && article.getChannelId().equals(getChannelId());
        }
        return false;
    }
}
