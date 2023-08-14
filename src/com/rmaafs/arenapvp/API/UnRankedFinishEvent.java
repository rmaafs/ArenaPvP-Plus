package com.rmaafs.arenapvp.API;

import com.rmaafs.arenapvp.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UnRankedFinishEvent extends Event {

    private final Player winner;
    private final Player loser;
    private final String kitName;
    private final String mapName;
    private boolean regen;
    private final Location spawn1;
    private final Location spawn2;
    private Location corner1;
    private Location corner2;

    public UnRankedFinishEvent(Player p1, Player p2, String kn, Map m) {
        winner = p1;
        loser = p2;
        kitName = kn;
        mapName = m.getName();
        spawn1 = m.getSpawn1();
        spawn2 = m.getSpawn2();
        if (m.getCorner1() != null) {
            regen = true;
            corner1 = m.getCorner1();
            corner2 = m.getCorner2();
        }
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLoser() {
        return loser;
    }

    public String getKitName() {
        return kitName;
    }

    public String getMapName() {
        return mapName;
    }

    public boolean isRegen() {
        return regen;
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
