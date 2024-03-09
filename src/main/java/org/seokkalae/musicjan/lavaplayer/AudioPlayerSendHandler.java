package org.seokkalae.musicjan.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {
    private final Logger log = LoggerFactory.getLogger(AudioPlayerSendHandler.class);

    private final AudioPlayer player;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private final MutableAudioFrame frame = new MutableAudioFrame();
    private final Guild guild;
    private int time;

    public AudioPlayerSendHandler(
            AudioPlayer player,
            Guild guild
    ) {
        this.player = player;
        this.guild = guild;
        frame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
        var canProvide = player.provide(frame);
        if (!canProvide) {
            time += 20;
            if (time >= 300_000) {
                time = 0;
                guild.getAudioManager().closeAudioConnection();
                log.info("bot disconnected from {}", guild.getName());
            }
        } else {
            time = 0;
        }
        return canProvide;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}