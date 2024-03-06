package org.seokkalae.musicjan.bot.command.music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.seokkalae.musicjan.bot.command.ICommand;
import org.seokkalae.musicjan.bot.service.MusicService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Play implements ICommand {
    private final MusicService musicService;

    public Play(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Добавить трек в очередь";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "track", "Ссылка или название трека", true));
        return options;
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
        if (!selfVoiceState.inAudioChannel()) {
            event.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());
        } else if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("Я уже в другом голосовом канале: " + selfVoiceState.getChannel().getName())
                    .queue();
            return;
        }
        var track = event.getOption("track").getAsString();
        musicService.play(event, track);
    }
}
