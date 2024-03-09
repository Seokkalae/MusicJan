package org.seokkalae.musicjan.lavaplayer.config;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LavaplayerConfig {
    @Bean
    public AudioPlayerManager audioPlayerManager(
            @Value("${youtube.email}") String youtubeEmail,
            @Value("${youtube.password}") String youtubePassword
    ) {
        var defaultAudioPlayerManager = new DefaultAudioPlayerManager();
        defaultAudioPlayerManager
                .registerSourceManager(new YoutubeAudioSourceManager(
                        true,
                        youtubeEmail,
                        youtubePassword)
                );
        AudioSourceManagers.registerRemoteSources(defaultAudioPlayerManager);
        return defaultAudioPlayerManager;
    }
}
