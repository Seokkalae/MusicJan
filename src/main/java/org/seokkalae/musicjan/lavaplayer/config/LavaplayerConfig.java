package org.seokkalae.musicjan.lavaplayer.config;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LavaplayerConfig {
    @Bean
    public AudioPlayerManager audioPlayerManager() {
        var defaultAudioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(defaultAudioPlayerManager);
        return defaultAudioPlayerManager;
    }
}
