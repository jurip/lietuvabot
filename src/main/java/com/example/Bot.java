package com.example;

import org.apache.camel.BeanInject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.*;

/**
 * This class contains the chat-bot logic: use your fantasy to implement your own Bot.
 */
@Component
public class Bot {
    private static final String YOUTUBE_KEY = "AIzaSyAp4w7RT9B5FpEgDvH7hQ9gsrHcHgpPDdY";

    @BeanInject
    private PersonDAO botDAO;

    RestTemplate restTemplate = new RestTemplate();

    private Logger log = LoggerFactory.getLogger(getClass());


    /**
     * This method processes incoming messages and return responses.
     *
     * @param message a message coming from a human user in a chat
     * @return the rbeply of the bot. Return null if you don't want to answer
     */
    public String process(String message) throws IOException {
        if (message == null) {
            //return null; // skip non-text messages
            message = "";
        }


        String result = "";

        if(message.startsWith("/addChat ")){
            String[] parts = message.split(" ");

            String GET_USER_ID = "https://api.telegram.org/bot1494127847:AAEd4bcPfnWYqVUKRFO63XF-bNfKObOgZIE/" +
                    "sendMessage?chat_id=@"+ parts[1]+"&text=test";
            log.info(GET_USER_ID);
            Map resp = restTemplate.getForObject(GET_USER_ID, Map.class);
            log.info(resp.toString());
            if(resp.get("ok") == "false")
                return resp.get("description").toString();

            Long id = (Long) ((Map)((Map)(resp).get("result")).get("sender_chat")).get("id");

            String type = "channels";
            if(parts.length>=3 && parts[2]!="tags")
                type = "tags";

            return  "added " +botDAO.addChat(String.valueOf(id), type, parts[1])+ "with id "+id;
            //message = "";
        }
        if(message.startsWith("/addChannel ")){
            String[] parts = message.split(" ");
            return "added "+botDAO.addChannel(parts[1], parts[2], parts[3]);

        }
        if(message.startsWith("/deleteChannel ")){
            String[] parts = message.split(" ");
            return "deleted "+botDAO.deleteChannel(parts[1], parts[2]);

        }
        if(message.startsWith("/deleteChat ")){
            String[] parts = message.split(" ");
            return "deleted "+botDAO.deleteChat(parts[1]);

        }

        if(message.startsWith("/listChannels")){
            String[] parts = message.split(" ");
            List<YoutubeChannel> chs = botDAO.getAllChannelsForChat(parts[1]);
            for ( YoutubeChannel ch:
                 chs ) {
                result = result + ch.getText()+" - " + ch.getName()+ ", ";
            }
            return result;
        }


        if(message.startsWith("/addTag ")){
            String[] parts = message.split(" ");
            return "added "+botDAO.addTag(parts[1], parts[2]);
        }
        if(message.startsWith("/deleteTag ")){
            String[] parts = message.split(" ");
            return "deleted "+botDAO.deleteTag(parts[1], parts[2]);
        }

        if(message.startsWith("/listTags")){
            result = "";
            String[] parts = message.split(" ");
            List<YoutubeTag> tags = botDAO.getAllTagsForChat(parts[1]);
            for ( YoutubeTag t:
                    tags ) {
                result = result + t.getText()+", ";
            }
            return result;
        }
        if(message == "") {
            List<TelegramChat> chats = botDAO.getAllChats();
            sendVideos(chats);

        }
        log.info("Received message: {}", message);

        return result + message;
    }

    private void sendVideos(List<TelegramChat> chats) throws IOException {


        for (TelegramChat chat:
             chats) {
            if ((chat.content.equals("channels"))) {
                sendAllChannelsToChat(restTemplate, chat, botDAO.getAllChannelsForChat(chat.getText()));
            } else if((chat.content.equals("tags"))) {
                sendAllTagsToChat(restTemplate, chat, botDAO.getAllTagsForChat(chat.getText()));
            }


        }
    }



    private void sendAllChannelsToChat(RestTemplate restTemplate, TelegramChat chat, List<YoutubeChannel> channels) throws IOException {
        for (Pair channel:
                channels) {
            YoutubeResponce resp = getYoutubeResponce(restTemplate, channel);
            sendVideosFromResponce(chat, resp);

        }
    }
    private void sendAllTagsToChat(RestTemplate restTemplate, TelegramChat chat, List<YoutubeTag> tags) throws IOException {
        for (YoutubeTag tag:
                tags) {
            YoutubeResponce resp = getYoutubeResponce(restTemplate, tag);
            sendVideosFromResponce(chat, resp);

        }
    }

    private void sendVideosFromResponce(TelegramChat chat, YoutubeResponce resp) throws IOException {
        Object[] items = resp.items;
        for (Object i :
                items) {
            sendVideo(chat, (Map) i);
        }
    }


    private void sendVideo(TelegramChat chat, Map i) throws IOException {
        Map item = i;
        String id = (String) ((Map)item.get("id")).get("videoId");
        boolean isNotSentBefore = botDAO.getVideo(id, chat.getText()).isEmpty();
        log.info("Video {} is not sent before: {}", id, isNotSentBefore);
        if(id != null && !id.isEmpty() && isNotSentBefore)
            sendVideoToTelegram(chat.getText(), id);
    }

    private YoutubeResponce getYoutubeResponce(RestTemplate restTemplate, Pair search) {
        final String uri = (search instanceof  YoutubeChannel)?
                    "https://youtube.googleapis.com/youtube/v3/search?part=id&channelId="+search.getText() +"&order=date&maxResults=40&key=" + YOUTUBE_KEY
                    :"https://www.googleapis.com/youtube/v3/search?part=id&maxResults=20&q="+search.getText()+"&type=video&key=" + YOUTUBE_KEY;

        log.info("Sending to telegram {}: {}",search.getChatId(), uri);
        YoutubeResponce resp = restTemplate.getForObject(uri, YoutubeResponce.class);
        log.info("Recieved from youtube: {}", StringUtils.arrayToDelimitedString( resp.items, ","));
        return resp;
    }

    private void sendVideoToTelegram(String chatId, String id) throws IOException {
        HttpClient httpClient2 = HttpClients.createDefault();

        log.info("Chat id {} send video {}", chatId, id);
        HttpUriRequest request2 = RequestBuilder.post(
                "https://api.telegram.org/bot1494127847:AAEd4bcPfnWYqVUKRFO63XF-bNfKObOgZIE/sendMessage")

                .setEntity(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("chat_id", chatId),
                        new BasicNameValuePair("text", "https://www.youtube.com/watch?v=" + id)
                ))).build();
        log.info("Created request: {}", request2.getURI());
        HttpResponse responce = httpClient2.execute(request2);
        log.info("Sent video {} to telegram {} with responce message: {}", id,chatId, responce.toString());

        botDAO.save(id, chatId);
    }

}
