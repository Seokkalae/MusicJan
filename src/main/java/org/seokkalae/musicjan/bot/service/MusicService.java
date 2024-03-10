package org.seokkalae.musicjan.bot.service;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.seokkalae.musicjan.lavaplayer.GuildMusicManager;
import org.seokkalae.musicjan.utils.Converter;
import org.seokkalae.musicjan.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MusicService {
    private final static Logger log = LoggerFactory.getLogger(MusicService.class);
    private final AudioPlayerManager audioPlayerManager;
    private final ApplicationContext context;
    public final Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();

    public MusicService(
            AudioPlayerManager audioPlayerManager,
            ApplicationContext context
    ) {
        this.audioPlayerManager = audioPlayerManager;
        this.context = context;
    }

    private GuildMusicManager getPlayerService(Guild guild) {
        return context.getBean(GuildMusicManager.class, audioPlayerManager, guild);
    }

    private GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            var musicManager = getPlayerService(guild);
            guild.getAudioManager().setSendingHandler(musicManager.getAudioPlayerSendHandler());
            return musicManager;
        });
    }

    private String createTrackSearchType(String track) {
        if (Validator.isValidUrl(track)) {
            return track;
        }
        return "ytsearch:" + track;
    }

    public void play(SlashCommandInteractionEvent event, String track) {
        var guildAudioManager = getGuildMusicManager(event.getGuild());
        var trackSearch = createTrackSearchType(track);
        audioPlayerManager.loadItemOrdered(guildAudioManager, trackSearch, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                guildAudioManager.getTrackScheduler().queue(audioTrack);
                event.deferReply().queue();
                String response = "Трек добавлен: **" + audioTrack.getInfo().title + "**"
                        + " [" + Converter.toFormattedDuration(audioTrack.getDuration()) + "]";
                log.info(response);
                event.getHook().sendMessage(response).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.isSearchResult()) {
                    trackLoaded(audioPlaylist.getTracks().getFirst());
                    return;
                }
                for (var audioTrack : audioPlaylist.getTracks())
                    guildAudioManager.getTrackScheduler().queue(audioTrack);
                event.deferReply().queue();
                long totalDuration = audioPlaylist.getTracks().stream()
                        .mapToLong(AudioTrack::getDuration)
                        .sum();
                String response = "Плейлист добавлен: **" + audioPlaylist.getName() + "**"
                        + " [" + Converter.toFormattedDuration(totalDuration) + "]";
                event.getHook().sendMessage(response).queue();
            }

            @Override
            public void noMatches() {
                event.reply("Трек не найден").queue();

            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.reply("Ошибка загрузки.").queue();
            }
        });
    }

    public void skip(SlashCommandInteractionEvent event) {
        var guildAudioManager = getGuildMusicManager(event.getGuild());
        var skippedTrack = guildAudioManager.getTrackScheduler().skip();
        event.deferReply().queue();
        if (!skippedTrack.isBlank()) {
            String response = event.getMember().getEffectiveName() + " скипнул трек **"
                    + skippedTrack + "**";
            event.getHook().sendMessage(response).queue();
        } else {
            event.getHook().deleteOriginal().queue();
        }
    }

    public void stop(SlashCommandInteractionEvent event) {
        var guildAudioManager = getGuildMusicManager(event.getGuild());
        event.deferReply().queue();
        guildAudioManager.getTrackScheduler().stop();
        var response = event.getMember().getEffectiveName() + " остановил бота";
        event.getHook().sendMessage(response).queue();
    }
}
