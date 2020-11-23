package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class PersonDAOImpl implements PersonDAO {

	JdbcTemplate jdbcTemplate;

	private final String SQL_FIND_VIDEO = "select * from lietuva_videos where name = ? and telegram_id = ?";

	@Autowired
	public PersonDAOImpl(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}


	public List<YoutubeChannel> getAllChannelsForChat(String telegramId){
		return jdbcTemplate.query("select * from lietuva_channels where telegram_id=?",new Object[]{ telegramId}, new ChannelMapper());
	}
	@Override
	public List<YoutubeTag> getAllTagsForChat(String telegramId) {
		return jdbcTemplate.query("select * from lietuva_tags where telegram_id=?",new Object[]{telegramId}, new TagMapper());

	}


	public List<TelegramChat> getAllChats(){
		return jdbcTemplate.query("select * from lietuva_chats",new Object[]{}, new ChatMapper());
	}
	public List<Video> getVideo(String id, String telegramId) {



		return jdbcTemplate.query(SQL_FIND_VIDEO, new Object[] { id, telegramId }, new VideoMapper());


	}

	@Override
	public int save(String id, String telegramId) {

		return jdbcTemplate.update("INSERT INTO lietuva_videos(name, telegram_id) VALUES(?, ?)", id, telegramId);

	}

	@Override
	public int addChannel(String id, String telegramId, String name) {

		return jdbcTemplate.update("INSERT INTO lietuva_channels(id, telegram_id, name) VALUES(?, ?, ?)", id, telegramId, name);

	}

	@Override
	public int addChat(String id, String type, String name) {

		return jdbcTemplate.update("INSERT INTO lietuva_chats(id, content, name) VALUES(?, ?, ?)", id, type, name);

	}

	@Override
	public int deleteChannel(String id, String telegramId) {
		return jdbcTemplate.update("DELETE FROM lietuva_channels WHERE id=? AND telegram_id=?", id, telegramId);

	}
	@Override
	public int deleteChat(String id) {
		return jdbcTemplate.update("DELETE FROM lietuva_chats WHERE id=?", id, id);

	}



	@Override
	public int addTag(String part, String telegramId) {
		return jdbcTemplate.update("INSERT INTO lietuva_tags(text, telegram_id) VALUES(?, ?)", part, telegramId);
	}

	@Override
	public int deleteTag(String part, String telegramId) {
		return jdbcTemplate.update("DELETE FROM lietuva_tags WHERE text=? AND telegram_id=?", part, telegramId);

	}


}