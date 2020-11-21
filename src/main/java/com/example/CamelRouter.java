package com.example;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;

/**
 * This class is responsible for routing the messages from and to the Telegram chat.
 */
@Component
public class CamelRouter extends RouteBuilder {

    @Autowired
    private Bot bot;

    @Override
    public void configure() throws Exception {

        from("telegram:bots")
        .bean(bot)
              //h  .to("sql:select CAST (COUNT(name) AS text) from sales_product where name='a' group by name?dataSource=myDataSource")
        .to("telegram:bots")
        .to("log:INFO?showHeaders=true");
        from("timer:tick?fixedRate=true&period=6000000")
                .bean(bot)
        //        .setBody().constant("Hello")
        .to("telegram:bots?authorizationToken=1494127847:AAEd4bcPfnWYqVUKRFO63XF-bNfKObOgZIE?chatId=-1001296306988");

    }
}
