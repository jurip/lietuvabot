package com.example;

public class YoutubeChannel implements  Pair {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String telegramId;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    @Override
    public String getChatId() {
        return telegramId;
    }

    @Override
    public String getText() {
        return id;
    }

}
