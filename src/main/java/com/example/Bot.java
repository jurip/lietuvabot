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
    private static final String CHAT_ID = "-1001227644468";
    private static final String YOUTUBE_KEY = "AIzaSyAi12Y4EVYT59-ZUcrYvuNxEAQfCHT6X_w";

    @BeanInject
    private PersonDAO botDAO;

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

        if(message.startsWith("/add ")){
            String[] parts = message.split(" ");
            botDAO.addChannel(parts[1], parts[2], parts[3]);
            return "added";
        }
        if(message.startsWith("/delete ")){
            String[] parts = message.split(" ");
            botDAO.deleteChannel(parts[1], parts[2]);
            return "deleted";
        }


        if(message.startsWith("/listChannels")){
            String[] parts = message.split(" ");
            List<YoutubeChannel> chs = botDAO.getAllChannelsForChat(parts[1]);
            for ( YoutubeChannel ch:
                 chs ) {
                result = result + ch.getId()+" - "+ ch.getName()+", ";
            }
            return result;
        }


        if(message.startsWith("/addTag ")){
            String[] parts = message.split(" ");
            botDAO.addTag(parts[1], parts[2]);
            return "tag added";
        }
        if(message.startsWith("/deleteTag ")){
            String[] parts = message.split(" ");
            botDAO.deleteTag(parts[1], parts[2]);
            return "tag deleted";
        }


        if(message.startsWith("/listTags")){
            result = "";
            String[] parts = message.split(" ");
            List<YoutubeTag> tags = botDAO.getAllTags(parts[1]);
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

        return result;
    }

    private void sendVideos(List<TelegramChat> chats) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        for (TelegramChat chat:
             chats) {

            if(chat.content.equals("channels")){
                List<YoutubeChannel> channels = botDAO.getAllChannelsForChat(chat.getText());

                for (YoutubeChannel channel:
                        channels) {
                    final String uri = "https://youtube.googleapis.com/youtube/v3/search?" +
                            "part=id&channelId="
                            + channel.getId()
                            +
                            "&order=date&maxResults=40" +
                            "&key="+YOUTUBE_KEY;
                    log.info("Sending: {}",uri);
                    YoutubeResponce resp = restTemplate.getForObject(uri, YoutubeResponce.class);
                    log.info("Recieved from youtube from channels {}", StringUtils.arrayToDelimitedString(resp.items, ","));
                    Object[] items = resp.items;
                    for (Object i:
                            items) {
                        Map item = (Map) i;
                        String id = (String) ((Map)item.get("id")).get("videoId");
                        boolean isNotSentBefore = botDAO.getVideo(id, chat.getText()).isEmpty();
                        log.info("Video {} is not sent before: {}", id, isNotSentBefore);
                        if(id != null && !id.isEmpty() && isNotSentBefore)
                            sendVideoToTelegram(chat.getText(), id);
                    }



                }


            }


            if(chat.content.equals("tags")){

                List<YoutubeTag> tags = botDAO.getAllTags(chat.getText());


                for (YoutubeTag tag :
                        tags) {

                    final String uri = "https://www.googleapis.com/youtube/v3/search" +
                            "?part=id&maxResults=20&q=" +
                            tag.getText()
                            + "&type=video&key=" + YOUTUBE_KEY;
                    log.info("Request: {}", uri);
                    YoutubeResponce resp = restTemplate.getForObject(uri, YoutubeResponce.class);
                    log.info("Recieved from youtube from youtube tags {}", StringUtils.arrayToDelimitedString(resp.items, ","));

                    Object[] items = resp.items;

                    for (Object i :
                            items) {
                        Map item = (Map) i;
                        String id = (String) ((Map) item.get("id")).get("videoId");
                        log.info("Video id: {}", id);
                        if (id != null && !id.isEmpty() && botDAO.getVideo(id, chat.getText()).isEmpty())
                            log.info("sendVideoToTelegram {}", id);
                        sendVideoToTelegram(chat.getText(), id);
                        //    sendVideoToTelegram("-1001414463954",id);
                        // sendVideoToTelegram("-1001481826976",id);
                    }


                }


            }





        }
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
