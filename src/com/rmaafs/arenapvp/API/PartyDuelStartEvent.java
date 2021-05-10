package com.rmaafs.arenapvp.API;

import java.util.ArrayList;
import java.util.List;

import com.rmaafs.arenapvp.Party.DuelGame;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyDuelStartEvent extends Event {

    Player owner1, owner2;
    List<Player> players1, players2, spectators;
    String kitName, mapName;
    Location spawn1, spawn2, corner1, corner2;
    
    public PartyDuelStartEvent(DuelGame game){
        players1 = new ArrayList<>();
        players2 = new ArrayList<>();
        spectators = new ArrayList<>();
        owner1 = game.p1.owner;
        owner2 = game.p2.owner;
        players1.addAll(game.players1);
        players2.addAll(game.players2);
        spectators.addAll(game.espectadores);
        kitName = game.kit.getKitName();
        mapName = game.mapa.getName();
        spawn1 = game.mapa.getSpawn1();
        spawn2 = game.mapa.getSpawn2();
        corner1 = game.mapa.getCorner1();
        corner2 = game.mapa.getCorner2();
    }

    public Player getOwner1() {
        return owner1;
    }

    public Player getOwner2() {
        return owner2;
    }

    public List<Player> getPlayers1() {
        return players1;
    }

    public List<Player> getPlayers2() {
        return players2;
    }

    public List<Player> getSpectators() {
        return spectators;
    }

    public String getKitName() {
        return kitName;
    }

    public String getMapName() {
        return mapName;
    }

    public Location getSpawn1() {
        return spawn1;
    }

    public Location getSpawn2() {
        return spawn2;
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
