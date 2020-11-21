package com.example;

public class YoutubeTag {
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    String text;
    String telegramId;

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }
}
