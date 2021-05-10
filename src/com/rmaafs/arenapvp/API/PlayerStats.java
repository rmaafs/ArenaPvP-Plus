package com.rmaafs.arenapvp.API;

import static com.rmaafs.arenapvp.Extra.kits;
import static com.rmaafs.arenapvp.Extra.playerConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerStats {

    public static int getElo(Player p, String k) {
        if (kits.containsKey(k)) {
            return playerConfig.get(p).getElo(kits.get(k));
        }
        msg("§4§lArenaPvP API >> §cKit " + k + " no exist. getElo");
        return 1400;
    }
    
    public static String getRank(Player p, String k) {
        if (kits.containsKey(k)) {
            return playerConfig.get(p).getRank(kits.get(k));
        }
        msg("§4§lArenaPvP API >> §cKit " + k + " no exist. getRank");
        return "ERROR";
    }

    public static int getPlayed(Player p, String k) {
        if (kits.containsKey(k)) {
            return playerConfig.get(p).getPlayed(kits.get(k));
        }
        msg("§4§lArenaPvP API >> §cKit " + k + " no exist. getPlayed");
        return 0;
    }
    
    public static int getWins(Player p, String k) {
        if (kits.containsKey(k)) {
            return playerConfig.get(p).getWins(kits.get(k));
        }
        msg("§4§lArenaPvP API >> §cKit " + k + " no exist. getWins");
        return 0;
    }
    
    public static int getKillsMeetup(Player p, String k) {
        if (kits.containsKey(k)) {
            return playerConfig.get(p).getKillsMeetup(kits.get(k));
        }
        msg("§4§lArenaPvP API >> §cKit " + k + " no exist. getKillsMeetup");
        return 0;
    }
    
    public static int getPlayedMeetup(Player p, String k) {
        if (kits.containsKey(k)) {
            return playerConfig.get(p).getPlayedMeetup(kits.get(k));
        }
        msg("§4§lArenaPvP API >> §cKit " + k + " no exist. getPlayedMeetup");
        return 0;
    }
    
    public static int getWinsMeetup(Player p, String k) {
        if (kits.containsKey(k)) {
            return playerConfig.get(p).getWinsMeetup(kits.get(k));
        }
        msg("§4§lArenaPvP API >> §cKit " + k + " no exist. getWinsMeetup");
        return 0;
    }
    
    public static int getRankeds(Player p){
        return playerConfig.get(p).getRankeds();
    }
    
    public static int getUnRankeds(Player p){
        return playerConfig.get(p).getUnRankeds();
    }

    public static void msg(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }
}
