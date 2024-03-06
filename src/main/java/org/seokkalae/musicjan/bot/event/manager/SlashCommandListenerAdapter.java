package org.seokkalae.musicjan.bot.event.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.seokkalae.musicjan.lavaplayer.GuildMusicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class SlashCommandListenerAdapter extends ListenerAdapter {
    private final static Logger log = LoggerFactory.getLogger(SlashCommandListenerAdapter.class);
    private final AudioPlayerManager audioPlayerManager;
    private final ApplicationContext context;
    private Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();

    public SlashCommandListenerAdapter(
            AudioPlayerManager audioPlayerManager,
            ApplicationContext context
    ) {
        this.audioPlayerManager = audioPlayerManager;
        this.context = context;
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        log.info("{}", this.audioPlayerManager);
    }
    private GuildMusicManager getPlayerService() {
        return context.getBean(GuildMusicManager.class);
    }

    private GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            var musicManager = getPlayerService();
            guild.getAudioManager().setSendingHandler(musicManager.getAudioPlayerSendHandler());
            return musicManager;
        });
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        log.info("{} {}", event.getName(), event.getCommandString());
        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();
        if(!memberVoiceState.inAudioChannel()) {
            event.reply("Нужно находиться в голосовом канале").queue();
            return;
        }
        var self = event.getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();
        if(!selfVoiceState.inAudioChannel()) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("Я уже в другом голосовом канале: " + selfVoiceState.getChannel().getName())
                    .queue();
            return;
        }
        var value = event.getOption("value").getAsString();
        var guildAudioManager = getGuildMusicManager(event.getGuild());

        audioPlayerManager.loadItemOrdered(guildAudioManager, value, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                guildAudioManager.getTrackScheduler().queue(audioTrack);
                event.reply("Добавлен " + audioTrack.getInfo().author + " - " + audioTrack.getInfo().title).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                for (var audioTrack : audioPlaylist.getTracks())
                    guildAudioManager.getTrackScheduler().queue(audioTrack);
                event.reply("Добавлен плейлист " + audioPlaylist.getName()).queue();
            }

            @Override
            public void noMatches() {
                event.reply("Не найдено").queue();;
            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.reply("Ошибка загрузки").queue();
            }
        });

    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        updateCommands(event);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        updateCommands(event);
    }

    private void updateCommands(GenericGuildEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(
                Commands.slash("test", "testing command")
                        .addOption(OptionType.STRING, "value", "test value")
        );
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
