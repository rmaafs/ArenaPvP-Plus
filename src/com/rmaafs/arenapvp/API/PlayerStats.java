package com.rmaafs.arenapvp.API;

import static com.rmaafs.arenapvp.Extra.kits;
import static com.rmaafs.arenapvp.Extra.playerConfig;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerStats {

    public static int getElo(Player player, String kit) {
        if (kits.containsKey(kit)) {
            return playerConfig.get(player).getElo(kits.get(kit));
        }
        msg("§4§lArenaPvP API >> §cKit " + kit + " no exist. getElo");
        return 1400;
    }

    public static String getRank(Player player, String kit) {
        if (kits.containsKey(kit)) {
            return playerConfig.get(player).getRank(kits.get(kit));
        }
        msg("§4§lArenaPvP API >> §cKit " + kit + " no exist. getRank");
        return "ERROR";
    }

    public static int getPlayed(Player player, String kit) {
        if (kits.containsKey(kit)) {
            return playerConfig.get(player).getPlayed(kits.get(kit));
        }
        msg("§4§lArenaPvP API >> §cKit " + kit + " no exist. getPlayed");
        return 0;
    }

    public static int getWins(Player player, String kit) {
        if (kits.containsKey(kit)) {
            return playerConfig.get(player).getWins(kits.get(kit));
        }
        msg("§4§lArenaPvP API >> §cKit " + kit + " no exist. getWins");
        return 0;
    }

    public static int getKillsMeetup(Player player, String kit) {
        if (kits.containsKey(kit)) {
            return playerConfig.get(player).getKillsMeetup(kits.get(kit));
        }
        msg("§4§lArenaPvP API >> §cKit " + kit + " no exist. getKillsMeetup");
        return 0;
    }

    public static int getPlayedMeetup(Player player, String kit) {
        if (kits.containsKey(kit)) {
            return playerConfig.get(player).getPlayedMeetup(kits.get(kit));
        }
        msg("§4§lArenaPvP API >> §cKit " + kit + " no exist. getPlayedMeetup");
        return 0;
    }

    public static int getWinsMeetup(Player player, String kit) {
        if (kits.containsKey(kit)) {
            return playerConfig.get(player).getWinsMeetup(kits.get(kit));
        }
        msg("§4§lArenaPvP API >> §cKit " + kit + " no exist. getWinsMeetup");
        return 0;
    }

    public static int getRankeds(Player player) {
        return playerConfig.get(player).getRankeds();
    }

    public static int getUnRankeds(Player player) {
        return playerConfig.get(player).getUnRankeds();
    }

    public static void msg(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }
}
