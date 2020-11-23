package com.example;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChannelMapper implements RowMapper<YoutubeChannel> {
    public YoutubeChannel mapRow(ResultSet resultSet, int i) throws SQLException {

        YoutubeChannel person = new YoutubeChannel();
        person.setId(resultSet.getString("id"));
        person.setTelegramId(resultSet.getString("telegram_id"));
        person.setName(resultSet.getString("name"));
        return person;
    }


}
