package com.rmaafs.arenapvp.manager.config;

import java.util.List;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static com.rmaafs.arenapvp.util.Extra.clang;
import static com.rmaafs.arenapvp.util.Extra.cscoreboards;
import static com.rmaafs.arenapvp.util.Extra.cspawns;

import com.rmaafs.arenapvp.util.Extra;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Lang {

    public String noMapsAvailable, playerOffline;
    public String playerPlayingOne, youPlayingOne;
    public String spawnSet, spawnHotbarSet, goldenname;
    public String playerInParty, youAreInParty;

    public String scoreMainTitle, scoreRankedTitle, scoreUnRankedTitle, scoreDuelTitle,
            scoreMeetupWaitingTitle, scoreMeetupTitle, scorePartyWaitTitle, scorePartyEventTitle,
            scorePartyDuelTitle, viewheal, wordilimited, alreadygift, gived, yougived,
            playernotintheworld;
    public List<String> scoreMain, scoreRanked, scoreUnRanked, scoreDuel,
            scoreMeetupWaiting, scoreMeetup, scorePartyWait, scorePartyEvent,
            scorePartyDuel;

    public Lang() {
        Config();

        playerOffline = Extra.tc(clang.getString("playeroffline"));
        noMapsAvailable = Extra.tc(clang.getString("nomapsavailable"));

        playerPlayingOne = Extra.tc(clang.getString("playing.player.one"));
        youPlayingOne = Extra.tc(clang.getString("playing.you.one"));

        playerInParty = Extra.tc(clang.getString("playing.player.party"));
        youAreInParty = Extra.tc(clang.getString("playing.you.party"));

        spawnSet = Extra.tc(clang.getString("spawnset"));
        spawnHotbarSet = Extra.tc(clang.getString("spawnhotbarset"));

        goldenname = Extra.tc(clang.getString("headname"));
        wordilimited = Extra.tc(clang.getString("wordilimited"));

        try {
            scoreMainTitle = Extra.tc(cscoreboards.getString("main.title"));
        } catch (Exception e) {

        }

        scoreMainTitle = Extra.tc(cscoreboards.getString("main.title"));
        scoreMain = Extra.tCC(cscoreboards.getStringList("main.lines"));
        scoreRankedTitle = Extra.tc(cscoreboards.getString("ranked.title"));
        scoreRanked = Extra.tCC(cscoreboards.getStringList("ranked.lines"));
        scoreUnRankedTitle = Extra.tc(cscoreboards.getString("unranked.title"));
        scoreUnRanked = Extra.tCC(cscoreboards.getStringList("unranked.lines"));
        scoreDuelTitle = Extra.tc(cscoreboards.getString("duel.title"));
        scoreDuel = Extra.tCC(cscoreboards.getStringList("duel.lines"));
        scoreMeetupWaitingTitle = Extra.tc(cscoreboards.getString("meetupwaiting.title"));
        scoreMeetupWaiting = Extra.tCC(cscoreboards.getStringList("meetupwaiting.lines"));
        scoreMeetupTitle = Extra.tc(cscoreboards.getString("meetup.title"));
        scoreMeetup = Extra.tCC(cscoreboards.getStringList("meetup.lines"));
        scorePartyWaitTitle = Extra.tc(cscoreboards.getString("partywait.title"));
        scorePartyWait = Extra.tCC(cscoreboards.getStringList("partywait.lines"));
        scorePartyEventTitle = Extra.tc(cscoreboards.getString("partyevent.title"));
        scorePartyEvent = Extra.tCC(cscoreboards.getStringList("partyevent.lines"));
        scorePartyDuelTitle = Extra.tc(cscoreboards.getString("partyduel.title"));
        scorePartyDuel = Extra.tCC(cscoreboards.getStringList("partyduel.lines"));

        viewheal = Extra.tc(clang.getString("viewheal"));
        alreadygift = Extra.tc(clang.getString("gift.already"));
        gived = Extra.tc(clang.getString("gift.gived"));
        yougived = Extra.tc(clang.getString("gift.yougived"));
        
        playernotintheworld = Extra.tc(clang.getString("playernotintheworld"));
    }

    public boolean chooserandommaps, adventureMode, usespectatormode, damageindicators, sql,
            showhealwitharrow, usePH;
    public String commandsavehotbar, damageindicatorscolor;
    public int defaultElo, defaultRankeds, defaultUnRankeds, giftrankeds;
    public List<String> worlds;

    public void Config() {
        adventureMode = cconfig.getBoolean("adventureonspawn");
        usespectatormode = cconfig.getBoolean("usespectatormode");

        chooserandommaps = cconfig.getBoolean("chooserandommaps");

        damageindicators = cconfig.getBoolean("damageindicators");
        showhealwitharrow = cconfig.getBoolean("showhealwitharrow");

        commandsavehotbar = cconfig.getString("commandsavehotbar");
        damageindicatorscolor = cconfig.getString("damageindicatorscolor");

        defaultElo = cconfig.getInt("defaultelo");
        defaultRankeds = cconfig.getInt("defaultrankeds");
        defaultUnRankeds = cconfig.getInt("defaultunrankeds");
        giftrankeds = cconfig.getInt("giftrankeds");

        sql = cconfig.getBoolean("mysql.use");
        
        worlds = cconfig.getStringList("worlds");
        
        usePH = cconfig.getBoolean("useplaceholderapi");

        fancyConfig();
        setLocations();

        Extra.rangoDefault = Extra.setRank(defaultElo);
    }

    public boolean duelEffectTeleport, duelEffectDeathBlindness, duelEffectDeathSlow, duelEffectDeathInvi;
    public int pretimematch;

    public void fancyConfig() {
        pretimematch = cconfig.getInt("fancy.pretimematch");

        duelEffectTeleport = cconfig.getBoolean("fancy.duel.efectteleport");
        duelEffectDeathBlindness = cconfig.getBoolean("fancy.duel.ondeath.blindness");
        duelEffectDeathSlow = cconfig.getBoolean("fancy.duel.ondeath.slow");
        duelEffectDeathInvi = cconfig.getBoolean("fancy.duel.ondeath.invisibility");
    }

    public Location spawn, spawnHotbar;

    public void setLocations() {
        spawn = setLocation("spawn.");
        spawnHotbar = setLocation("hotbar.");
    }

    private Location setLocation(String path) {
//        Location loc = new Location(Bukkit.getWorld(cspawns.getString(path + "w")), 1, 1, 1);
//        loc.setX(cspawns.getDouble(path + "x"));
//        loc.setY(cspawns.getDouble(path + "y"));
//        loc.setZ(cspawns.getDouble(path + "z"));
//        loc.setYaw(cspawns.getFloat(path + "ya"));
//        loc.setPitch(cspawns.getFloat(path + "p"));

        if (cspawns.contains(path + "w")) {
            return new Location(Bukkit.getWorld(cspawns.getString(path + "w")),
                    cspawns.getDouble(path + "x"),
                    cspawns.getDouble(path + "y"),
                    cspawns.getDouble(path + "z"),
                    cspawns.getInt(path + "ya"),
                    cspawns.getInt(path + "p"));
        } else {
            Bukkit.getConsoleSender().sendMessage("§4ArenaPvp >> §cPlease put /apvp setspawn and /apvp sethotbarspawn");
            return new Location(Bukkit.getWorld("world"),
                    0.0,
                    80,
                    0,
                    0,
                    0);
        }
    }

    public void teleportSpawn(Player p) {
        try {
            p.teleport(spawn);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§4ArenaPvp >> §cPlease put /apvp setspawn");
        }
        if (adventureMode) {
            p.setGameMode(GameMode.ADVENTURE);
        } else {
            p.setGameMode(GameMode.SURVIVAL);
        }
    }

    public void teleportSpawnHotbar(Player p) {
        try {
            p.teleport(spawnHotbar);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§4ArenaPvp >> §cPlease put /apvp sethotbarspawn");
        }
    }
}
