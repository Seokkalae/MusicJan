package org.seokkalae.musicjan.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final static Logger log = LoggerFactory.getLogger(TrackScheduler.class);

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.player.addListener(this);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        player.startTrack(queue.poll(), false);
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            log.info("added to queue. current length {}", queue.size());
        }
    }

    public String skip() {
        var title = player.getPlayingTrack().getInfo().title;
        player.stopTrack();
        return title;
    }

    public int getQueueSize() {
        return queue.size();
    }
}