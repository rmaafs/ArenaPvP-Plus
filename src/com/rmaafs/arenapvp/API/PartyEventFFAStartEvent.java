package com.rmaafs.arenapvp.API;

import java.util.ArrayList;
import java.util.List;
import com.rmaafs.arenapvp.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyEventFFAStartEvent extends Event{

    Player owner;
    String kitName, mapName;
    List<Player> players, spectators;
    Location spawn1, spawn2, corner1, corner2;
    
    public PartyEventFFAStartEvent(Player ow, List<Player> pla, String kit, Map map){
        players = new ArrayList<>();
        owner = ow;
        kitName = kit;
        mapName = map.getName();
        players.addAll(pla);
        spectators = new ArrayList<>();
        spawn1 = map.getSpawn1();
        spawn2 = map.getSpawn2();
        if (map.getCorner1() != null){
            corner1 = map.getCorner1();
            corner2 = map.getCorner2();
        }
    }

    public Player getOwner() {
        return owner;
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

    public static HandlerList getHANDLERS() {
        return HANDLERS;
    }
    
    
    
    private static final HandlerList HANDLERS = new HandlerList();
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
