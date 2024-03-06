package org.seokkalae.musicjan.bot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BotConfiguration {
    @Bean
    JDA createBot(
            @Value("${token}") String token,
            List<ListenerAdapter> listenerAdapters
    ) {
        return JDABuilder
                .createDefault(token)
                .addEventListeners(listenerAdapters.toArray())
                .build();
    }
}