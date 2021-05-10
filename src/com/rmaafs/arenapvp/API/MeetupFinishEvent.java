package com.rmaafs.arenapvp.API;

import java.util.ArrayList;
import java.util.List;

import com.rmaafs.arenapvp.Juegos.Meetup.GameMeetup;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MeetupFinishEvent extends Event{

    String owner, title, kitName, mapName;
    List<Player> players, spectators;
    Player winner;
    Location corner1, corner2;

    public MeetupFinishEvent(GameMeetup game, Player w){
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        owner = game.owner;
        title = game.title;
        kitName = game.kit.getKitName();
        mapName = game.mapa.getName();
        players.addAll(game.players);
        spectators.addAll(game.espectadores);
        corner1 = game.mapa.getCorner1();
        corner2 = game.mapa.getCorner2();
        winner = w;
    }

    public String getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public String getKitName() {
        return kitName;
    }

    public String getMapName() {
        return mapName;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getSpectators() {
        return spectators;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public Player getWinner() {
        return winner;
    }
    
    
    
    
    
    private static final HandlerList HANDLERS = new HandlerList();
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
