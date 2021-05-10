package com.rmaafs.arenapvp.API;

import com.rmaafs.arenapvp.Mapa;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelFinishEvent extends Event {
    Player winner, loser;
    String kitName, mapName;
    boolean regen;
    Location spawn1, spawn2, corner1, corner2;
    int bestof, winsPlayer1, winsPlayer2;

    public DuelFinishEvent(Player p1, Player p2, String kn, Mapa m, int b, int w1, int w2) {
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
        bestof = b;
        winsPlayer1 = w1;
        winsPlayer2 = w2;
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

    public int getBestof() {
        return bestof;
    }

    public int getWinsPlayer1() {
        return winsPlayer1;
    }

    public int getWinsPlayer2() {
        return winsPlayer2;
    }
    
    
    
    private static final HandlerList HANDLERS = new HandlerList();
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
