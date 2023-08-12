package com.rmaafs.arenapvp.API;

import java.util.ArrayList;
import java.util.List;

import com.rmaafs.arenapvp.Party.DuelGame;
import com.rmaafs.arenapvp.Party.Party;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyDuelFinishEvent extends Event {

    Player ownerWinner, ownerLoser;
    List<Player> playersWinner, playersLoser, spectators;
    String kitName, mapName;
    Location spawnWinner, spawnLoser, corner1, corner2;

    public PartyDuelFinishEvent(DuelGame game, Party p) {
        playersWinner = new ArrayList<>();
        playersLoser = new ArrayList<>();
        spectators = new ArrayList<>();
        if (game.p1 == p) {
            ownerWinner = game.p1.owner;
            ownerLoser = game.p2.owner;
            playersWinner.addAll(game.players1);
            playersLoser.addAll(game.players2);
            spawnWinner = game.map.getSpawn1();
            spawnLoser = game.map.getSpawn2();
        } else {
            ownerWinner = game.p2.owner;
            ownerLoser = game.p1.owner;
            playersWinner.addAll(game.players2);
            playersLoser.addAll(game.players1);
            spawnWinner = game.map.getSpawn2();
            spawnLoser = game.map.getSpawn1();
        }

        spectators.addAll(game.espectadores);
        kitName = game.kit.getKitName();
        mapName = game.map.getName();

        corner1 = game.map.getCorner1();
        corner2 = game.map.getCorner2();
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

    public List<Player> getPlayersWinner() {
        return playersWinner;
    }

    public List<Player> getPlayersLoser() {
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
