package org.seokkalae.musicjan.bot.command.common;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.seokkalae.musicjan.bot.command.ICommand;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.StringJoiner;

@Component
public class Help implements ICommand {
    private final List<ICommand> commands;

    public Help(List<ICommand> commands) {
        this.commands = commands;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Список всех команд";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StringBuilder responseBuilder = new StringBuilder("```");
        for (var command : commands) {
            responseBuilder.append(String.format("/%s - %s", command.getName(), command.getDescription()));
            if (command.getOptions() != null) {
                for (var option : command.getOptions()) {
                    responseBuilder.append(String.format("\n\t %s (%s) - %s",
                            option.getName(),
                            option.isRequired() ? "обязательный" : "необязательный",
                            option.getDescription()));
                }
            }
            responseBuilder.append("\n\n");
        }
        responseBuilder.append("```");

        event.reply(responseBuilder.toString()).queue();
    }
}
