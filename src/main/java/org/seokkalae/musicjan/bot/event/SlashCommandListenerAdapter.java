package org.seokkalae.musicjan.bot.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.seokkalae.musicjan.bot.command.ICommand;
import org.seokkalae.musicjan.dao.ServerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class SlashCommandListenerAdapter extends ListenerAdapter {
    private final static Logger log = LoggerFactory.getLogger(SlashCommandListenerAdapter.class);

    private final List<ICommand> commands;
    private final Map<String, ICommand> commandMap;

    private final ServerDao serverDao;

    public SlashCommandListenerAdapter(
            List<ICommand> commands, ServerDao serverDao
    ) {
        this.commands = commands;
        commandMap = commands.stream()
                .collect(Collectors.toMap(
                        ICommand::getName,
                        Function.identity()
                ));
        this.serverDao = serverDao;
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
        isAllowedServer(event, false);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        isAllowedServer(event, true);
    }

    private void isAllowedServer(GenericGuildEvent event, Boolean saveServerToDb) {
        var guild = event.getGuild();
        serverDao.serverIsAllow(guild.getId())
                .subscribe(result -> {
                    if (result) {
                        log.info("server {} allowed", guild.getName());
                        updateCommands(event);
                    } else {
                        log.info("detect not allowed server {}. save to db", guild.getName());
                        if (saveServerToDb) {
                            serverDao.saveServer(guild.getId(), guild.getName());
                        }
                        var defaultChannel = guild.getDefaultChannel();
                        if (defaultChannel != null) {
                            defaultChannel.asTextChannel()
                                    .sendMessage("Вы не можешь пригласить меня на этот сервер \uD83D\uDE22"
                                            + "\n Обратитесь к тому, что дал Вам эту ссылку").queue();
                        }
                    }
                });
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
