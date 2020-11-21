package com.example;

public class YoutubeChannel {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String telegramChannelId;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelegramChannelId() {
        return telegramChannelId;
    }

    public void setTelegramChannelId(String telegramChannelId) {
        this.telegramChannelId = telegramChannelId;
    }
}
