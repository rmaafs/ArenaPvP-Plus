package com.rmaafs.arenapvp.API;

import java.util.*;

import com.rmaafs.arenapvp.Party.DuelGame;
import com.rmaafs.arenapvp.Party.Party;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyDuelDeathEvent extends Event {

    private final Player ownerKilled;
    private final Player ownerKiller;
    private final Player playerDeath;
    private final Set<UUID> playersKilled;
    private final Set<UUID> playersKiller;
    private final Set<UUID> spectators;
    private final String kitName;
    private final String mapName;
    private final Location spawnKilled;
    private final Location spawnKiller;
    private final Location corner1;
    private final Location corner2;

    public PartyDuelDeathEvent(DuelGame game, Player d, Party p) {
        playersKilled = new HashSet<>();
        playersKiller = new HashSet<>();
        spectators = new HashSet<>();
        if (game.p1 == p) {
            ownerKilled = game.p1.owner;
            ownerKiller = game.p2.owner;
            playersKilled.addAll(game.players1);
            playersKiller.addAll(game.players2);
            spawnKilled = game.map.getSpawn1();
            spawnKiller = game.map.getSpawn2();
        } else {
            ownerKilled = game.p2.owner;
            ownerKiller = game.p1.owner;
            playersKilled.addAll(game.players2);
            playersKiller.addAll(game.players1);
            spawnKilled = game.map.getSpawn2();
            spawnKiller = game.map.getSpawn1();
        }
        playerDeath = d;

        spectators.addAll(game.spectators);
        kitName = game.kit.getKitName();
        mapName = game.map.getName();

        corner1 = game.map.getCorner1();
        corner2 = game.map.getCorner2();
    }

    public Player getPlayerDeath() {
        return playerDeath;
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

    public Player getOwnerKilled() {
        return ownerKilled;
    }

    public Player getOwnerKiller() {
        return ownerKiller;
    }

    public Set<UUID> getPlayersKilled() {
        return playersKilled;
    }

    public Set<UUID> getPlayersKiller() {
        return playersKiller;
    }

    public Location getSpawnKilled() {
        return spawnKilled;
    }

    public Location getSpawnKiller() {
        return spawnKiller;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
