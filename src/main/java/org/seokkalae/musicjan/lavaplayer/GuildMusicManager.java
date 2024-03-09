package org.seokkalae.musicjan.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class GuildMusicManager {
    private static final Logger log = LoggerFactory.getLogger(GuildMusicManager.class);
    private final TrackScheduler trackScheduler;
    private final AudioPlayer audioPlayer;

    private final AudioPlayerSendHandler audioPlayerSendHandler;

    public GuildMusicManager(
            AudioPlayerManager audioPlayerManager,
            Guild guild
    ) {
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(audioPlayer);
        this.audioPlayerSendHandler = new AudioPlayerSendHandler(audioPlayer, guild);
        log.info("{}",this.audioPlayer);
        log.info("{}",this.trackScheduler);
        log.info("{}",this.audioPlayerSendHandler);
    }

    public final TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public final AudioPlayerSendHandler getAudioPlayerSendHandler() {
        return audioPlayerSendHandler;
    }
}
