package com.rmaafs.arenapvp.API;

import java.util.ArrayList;
import java.util.List;

import com.rmaafs.arenapvp.Party.Party;
import com.rmaafs.arenapvp.Kit;
import com.rmaafs.arenapvp.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyEventFFADeathEvent extends Event{

    private final Player owner;
    private final Player death;
    private final String kitName;
    private final String mapName;
    private final List<Player> players;
    private final List<Player> spectators;
    private final Location spawn1;
    private final Location spawn2;
    private Location corner1;
    private Location corner2;
    
    public PartyEventFFADeathEvent(Party party, Kit kit, Map map, List<Player> spec, Player d){
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        owner = party.owner;
        death = d;
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

    public Player getDeath() {
        return death;
    }
    
    private static final HandlerList HANDLERS = new HandlerList();
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
