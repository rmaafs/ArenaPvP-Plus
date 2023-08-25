package com.rmaafs.arenapvp.API;

import java.util.*;

import com.rmaafs.arenapvp.Party.DuelGame;
import com.rmaafs.arenapvp.Party.Party;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyDuelFinishEvent extends Event {

    private final Player ownerWinner;
    private final Player ownerLoser;
    private final Set<UUID> playersWinner;
    private final Set<UUID> playersLoser;
    private final Set<UUID> spectators;
    private final String kitName;
    private final String mapName;
    private final Location spawnWinner;
    private final Location spawnLoser;
    private final Location corner1;
    private final Location corner2;

    public PartyDuelFinishEvent(DuelGame game, Party p) {
        playersWinner = new HashSet<>();
        playersLoser = new HashSet<>();
        spectators = new HashSet<>();
        if (game.p1 == p) {
            ownerWinner = game.p1.owner;
            ownerLoser = game.p2.owner;
            playersWinner.addAll(game.players1);
            playersLoser.addAll(game.players2);
            spawnWinner = game.gameMap.getSpawn1();
            spawnLoser = game.gameMap.getSpawn2();
        } else {
            ownerWinner = game.p2.owner;
            ownerLoser = game.p1.owner;
            playersWinner.addAll(game.players2);
            playersLoser.addAll(game.players1);
            spawnWinner = game.gameMap.getSpawn2();
            spawnLoser = game.gameMap.getSpawn1();
        }

        spectators.addAll(game.spectators);
        kitName = game.kit.getKitName();
        mapName = game.gameMap.getName();

        corner1 = game.gameMap.getCorner1();
        corner2 = game.gameMap.getCorner2();
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

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public Player getOwnerWinner() {
        return ownerWinner;
    }

    public Player getOwnerLoser() {
        return ownerLoser;
    }

    public Set<UUID> getPlayersWinner() {
        return playersWinner;
    }

    public Set<UUID> getPlayersLoser() {
        return playersLoser;
    }

    public Location getSpawnWinner() {
        return spawnWinner;
    }

    public Location getSpawnLoser() {
        return spawnLoser;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
