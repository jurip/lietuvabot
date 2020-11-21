package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class VideoMapper implements RowMapper<Video> {

	public Video mapRow(ResultSet resultSet, int i) throws SQLException {

		Video video = new Video();
		video.setName(resultSet.getString("name"));
		video.setTelegramId(resultSet.getString("telegram_id"));

		return video;
	}
}