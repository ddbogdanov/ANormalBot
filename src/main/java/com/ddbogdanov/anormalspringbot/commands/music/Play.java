package com.ddbogdanov.anormalspringbot.commands.music;

import com.ddbogdanov.anormalspringbot.audioplayer.AudioPlayerSendHandler;
import com.ddbogdanov.anormalspringbot.commands.common.CommandInterface;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Play extends Command implements CommandInterface<Play> {

    private EventWaiter waiter;

    @Autowired
    public Play(EventWaiter waiter) {
        super.name = "!play, !p";
        super.help = "Search for a resource on YouTube and play it";
        super.aliases = new String[]{"play", "p"};
        super.cooldown = 2;
        this.waiter = waiter;
    }

    @Override
    public void setWaiter(EventWaiter waiter) {
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        String searchCriteria = event.getArgs();
        Member member = event.getMessage().getMember();
        TextChannel channel = event.getTextChannel();

        if(!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
            channel.sendMessage("I do not have permissions to join a voice channel!").queue();
            return;
        }

        VoiceChannel vChannel = event.getMember().getVoiceState().getChannel();

        if(vChannel == null) {
            channel.sendMessage("You're not in a voice channel!").queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.openAudioConnection(vChannel);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();

        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));

        player.setVolume(150);

        playerManager.loadItem("ytsearch:" + searchCriteria, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Playing something idk").queue();
                player.playTrack(track);
            }
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if(firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Playing: " + firstTrack.getInfo().title).queue();
                player.playTrack(firstTrack);
            }
            @Override
            public void noMatches() {
                channel.sendMessage("No matches!").queue();
            }
            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Load failed!").queue();
            }
        });
    }
}
