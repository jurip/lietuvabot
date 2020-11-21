package com.example;

import java.util.List;

public interface PersonDAO {
    List<YoutubeChannel> getAllChannelsForChat(String telegramId);

	List<Video> getVideo(String text, String telegramId);
	List<TelegramChat> getAllChats();
    void save(String id, String telegramId);
    void addChannel(String id, String telegramId, String name);

    void deleteChannel(String part, String telegramId);

    List<YoutubeTag> getAllTags(String telegramId);

    void addTag(String part, String telegramId);

    void deleteTag(String part, String telegramId);
}