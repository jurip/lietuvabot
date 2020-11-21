package com.example;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatMapper implements RowMapper<TelegramChat> {
    public TelegramChat mapRow(ResultSet resultSet, int i) throws SQLException {

        TelegramChat chat = new TelegramChat();
        chat.setText(resultSet.getString("id"));
        chat.setContent(resultSet.getString("content"));
        return chat;
    }


}
