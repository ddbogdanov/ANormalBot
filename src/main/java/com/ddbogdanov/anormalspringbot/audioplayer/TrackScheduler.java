package com.ddbogdanov.anormalspringbot.audioplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {

    @Override
    public void onPlayerPause(AudioPlayer player) {

    }
    @Override
    public void onPlayerResume(AudioPlayer player) {

    }
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

    }
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext) {
            //start next track
        }
    }
    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {

    }
    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {

    }

    public void queue(AudioTrack track) {

    }
}
