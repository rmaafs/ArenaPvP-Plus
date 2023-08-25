package com.rmaafs.arenapvp.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Scoreboard;
public class ScoreBoardRefreshed extends Event{

    private final Scoreboard board;
    private final Player player;

    public ScoreBoardRefreshed(Scoreboard board, Player player) {
        this.board = board;
        this.player = player;
    }
    
    private static final HandlerList HANDLERS = new HandlerList();

    public Scoreboard getBoardRefreshed() {
        return this.board;
    }
    
    public Player getPlayerRefreshed() {
        return this.player;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
