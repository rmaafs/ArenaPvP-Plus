package com.rmaafs.arenapvp.API;

import java.util.*;

import com.rmaafs.arenapvp.Party.Party;
import com.rmaafs.arenapvp.manager.kit.Kit;
import com.rmaafs.arenapvp.entity.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyEventFFAFinishEvent extends Event {

    private final Player owner;
    private final Player winner;
    private final String kitName;
    private final String mapName;
    private final Set<UUID>  players;
    private final  Set<UUID> spectators;
    private final Location spawn1;
    private final Location spawn2;
    private Location corner1;
    private Location corner2;
    
    public PartyEventFFAFinishEvent(Party party, Kit kit, Map map,  Set<UUID> spec, Player w){
        players = new HashSet<>();
        spectators = new HashSet<>();
        owner = party.owner;
        winner = w;
        kitName = kit.getKitName();
        mapName = map.getName();
        players.addAll(party.players);
        spectators.addAll(spec);
        spawn1 = map.getSpawn1();
        spawn2 = map.getSpawn2();
        if (kit.isRegen()){
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

    public  Set<UUID> getPlayers() {
        return players;
    }

    public  Set<UUID> getSpectators() {
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
