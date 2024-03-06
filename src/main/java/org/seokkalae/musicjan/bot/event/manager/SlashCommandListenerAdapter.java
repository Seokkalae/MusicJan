package org.seokkalae.musicjan.bot.event.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.seokkalae.musicjan.bot.command.ICommand;
import org.seokkalae.musicjan.lavaplayer.GuildMusicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class SlashCommandListenerAdapter extends ListenerAdapter {
    private final static Logger log = LoggerFactory.getLogger(SlashCommandListenerAdapter.class);
    private final AudioPlayerManager audioPlayerManager;
    private final ApplicationContext context;
    private Map<Long, GuildMusicManager> guildMusicManagers = new HashMap<>();

    private final List<ICommand> commands;
    private final Map<String, ICommand> commandMap;

    public SlashCommandListenerAdapter(
            AudioPlayerManager audioPlayerManager,
            ApplicationContext context,
            List<ICommand> commands
    ) {
        this.audioPlayerManager = audioPlayerManager;
        this.context = context;
        this.commands = commands;
        commandMap = commands.stream()
                .collect(Collectors.toMap(
                        ICommand::getName,
                        Function.identity()
                ));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        var command = commandMap.get(event.getName());
        if (command != null) {
            log.info("execute {}", command.getName());
            command.execute(event);
        } else {
            log.error("command {} not recognize", event.getName());
            event.reply("Команда не найдена").queue();
        }
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
        for (var command : commands) {
            if (command.getOptions() == null) {
                event.getGuild()
                        .upsertCommand(command.getName(), command.getDescription())
                        .queue();
            } else {
                event.getGuild()
                        .upsertCommand(command.getName(), command.getDescription())
                        .addOptions(command.getOptions())
                        .queue();
            }
        }
    }
}