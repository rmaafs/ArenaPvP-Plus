package com.rmaafs.arenapvp.API;

import java.util.*;

import com.rmaafs.arenapvp.Party.DuelGame;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyDuelStartEvent extends Event {

    private final Player owner1;
    private final Player owner2;
    private final Set<UUID> players1;
    private final Set<UUID> players2;
    private final Set<UUID> spectators;
    private final String kitName;
    private final String mapName;
    private final Location spawn1;
    private final Location spawn2;
    private final Location corner1;
    private final Location corner2;
    
    public PartyDuelStartEvent(DuelGame game){
        players1 = new HashSet<>();
        players2 = new HashSet<>();
        spectators = new HashSet<>();
        owner1 = game.p1.owner;
        owner2 = game.p2.owner;
        players1.addAll(game.players1);
        players2.addAll(game.players2);
        spectators.addAll(game.spectators);
        kitName = game.kit.getKitName();
        mapName = game.map.getName();
        spawn1 = game.map.getSpawn1();
        spawn2 = game.map.getSpawn2();
        corner1 = game.map.getCorner1();
        corner2 = game.map.getCorner2();
    }

    public Player getOwner1() {
        return owner1;
    }

    public Player getOwner2() {
        return owner2;
    }

    public Set<UUID> getPlayers1() {
        return players1;
    }

    public Set<UUID> getPlayers2() {
        return players2;
    }

    public Set<UUID> getSpectators() {
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
