package org.seokkalae.musicjan.bot.command.music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.seokkalae.musicjan.bot.command.ICommand;
import org.seokkalae.musicjan.bot.service.MusicService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Skip implements ICommand {
    private final MusicService musicService;

    public Skip(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Пропустить трек";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        var member = event.getMember();
        var memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("Нужно находиться в голосовом канале").queue();
            return;
        }

        var self = event.getGuild().getSelfMember();
        var selfVoiceState = self.getVoiceState();

        if(!selfVoiceState.inAudioChannel()) {
            event.reply("Я не в голосовом канале").queue();
            return;
        }

        if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("Я в другом голосовом канале").queue();
            return;
        }

        musicService.skip(event);
    }
}
