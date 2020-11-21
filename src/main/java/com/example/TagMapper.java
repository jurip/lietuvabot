package com.example;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagMapper implements RowMapper<YoutubeTag> {
    public YoutubeTag mapRow(ResultSet resultSet, int i) throws SQLException {

        YoutubeTag tag = new YoutubeTag();
        tag.setText(resultSet.getString("text"));
        tag.setTelegramId(resultSet.getString("telegram_id"));
        return tag;
    }


}
