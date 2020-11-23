package com.example;

import java.util.List;

public interface PersonDAO {
    List<YoutubeChannel> getAllChannelsForChat(String telegramId);
    List<YoutubeTag> getAllTagsForChat(String telegramId);

	List<Video> getVideo(String text, String telegramId);
	List<TelegramChat> getAllChats();
    int save(String id, String telegramId);
    int addChannel(String id, String telegramId, String name);

    int deleteChannel(String part, String telegramId);

    int deleteChat(String id);


    int addTag(String part, String telegramId);

    int deleteTag(String part, String telegramId);

    int addChat(String chatId, String type, String name);
}