package com.rmaafs.arenapvp.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rmaafs.arenapvp.Juegos.Meetup.GameMeetup;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MeetupStartEvent extends Event {

    private final String owner;
    private final String title;
    private final String kitName;
    private final String mapName;
    private final List<Player> players;
    private final List<Player> spectators;
    private final HashMap<Player, Location> spawns;
    private final Location corner1;
    private final Location corner2;
    
    public MeetupStartEvent(GameMeetup game, HashMap<Player, Location> e){
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        owner = game.owner;
        title = game.title;
        kitName = game.kit.getKitName();
        mapName = game.mapa.getName();
        players.addAll(game.players);
        spectators.addAll(game.espectadores);
        spawns = e;
        corner1 = game.mapa.getCorner1();
        corner2 = game.mapa.getCorner2();
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

    public HashMap<Player, Location> getSpawns() {
        return spawns;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }
    
    private static final HandlerList HANDLERS = new HandlerList();
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
