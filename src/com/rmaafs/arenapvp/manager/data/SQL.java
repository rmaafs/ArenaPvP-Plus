package com.rmaafs.arenapvp.manager.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.rmaafs.arenapvp.util.Extra.cstats;
import static com.rmaafs.arenapvp.util.Extra.playerConfig;
import static com.rmaafs.arenapvp.ArenaPvP.extraLang;
import static com.rmaafs.arenapvp.ArenaPvP.mysql;

import com.rmaafs.arenapvp.ArenaPvP;
import org.bukkit.entity.Player;

public class SQL {

    public static HashMap<Player, String> preMeetup = new HashMap<>();
    public static List<String> exist = new ArrayList<>();

    public static String getStats(Player p) {
        if (extraLang.sql) {
            try {
                ResultSet rs = mysql.query("SELECT * FROM APVP WHERE UUID='" + p.getUniqueId().toString() + "'");
                if (rs.next()) {
                    if (rs.getString("MEETUP") != null) {
                        preMeetup.put(p, rs.getString("MEETUP"));
                    }
                    if (rs.getString("STATS") != null) {
                        return rs.getString("STATS");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            if (cstats.contains(p.getUniqueId().toString() + ".o")) {
                return cstats.getString(p.getUniqueId().toString() + ".o");
            }
        }
        return "";
    }

    public static String getStatsMeetup(Player p) {
        if (extraLang.sql) {
            if (preMeetup.containsKey(p)) {
                String a = preMeetup.get(p);
                preMeetup.remove(p);
                return a;
            }
            try {
                ResultSet rs = mysql.query("SELECT MEETUP FROM APVP WHERE UUID='" + p.getUniqueId().toString() + "'");
                if (rs.next()) {
                    if (rs.getString("MEETUP") != null) {
                        return rs.getString("MEETUP");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            if (cstats.contains(p.getUniqueId().toString() + ".m")) {
                return cstats.getString(p.getUniqueId().toString() + ".m");
            }
        }
        return "";
    }

    public static boolean isExist(Player p) {
        if (exist.contains(p.getName())) {
            return true;
        }
        try {
            ResultSet rs = mysql.query("SELECT NICK FROM APVP WHERE UUID='" + p.getUniqueId().toString() + "'");
            if (rs.next() && rs.getString("NICK") != null) {
                exist.add(p.getName());
                return rs.getString("NICK") != null;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void guardarStats(Player p, boolean tambienMeetup) {
        if (extraLang.sql) {
            if (isExist(p)) {
                playerConfig.get(p).stats.componer();
                if (tambienMeetup) {
                    playerConfig.get(p).statsMeetup.componer();
                    if (!playerConfig.get(p).stats.statsString.equals("")) {
                        if (!playerConfig.get(p).statsMeetup.statsString.equals("")) {
                            ArenaPvP.mysql.update("UPDATE APVP SET STATS='" + playerConfig.get(p).stats.statsString + "', MEETUP='" + playerConfig.get(p).statsMeetup.statsString + "' WHERE UUID= '" + p.getUniqueId().toString() + "';");
                        } else {
                            ArenaPvP.mysql.update("UPDATE APVP SET STATS='" + playerConfig.get(p).stats.statsString + "' WHERE UUID= '" + p.getUniqueId().toString() + "';");
                        }
                    } else if (!playerConfig.get(p).statsMeetup.statsString.equals("")) {
                        ArenaPvP.mysql.update("UPDATE APVP SET MEETUP='" + playerConfig.get(p).statsMeetup.statsString + "' WHERE UUID= '" + p.getUniqueId().toString() + "';");
                    }
                } else {
                    if (!playerConfig.get(p).stats.statsString.equals("")) {
                        ArenaPvP.mysql.update("UPDATE APVP SET STATS='" + playerConfig.get(p).stats.statsString + "' WHERE UUID= '" + p.getUniqueId().toString() + "';");
                    }
                }
            } else {
                playerConfig.get(p).stats.componer();
                if (tambienMeetup) {
                    playerConfig.get(p).statsMeetup.componer();
                    if (!playerConfig.get(p).stats.statsString.equals("")) {
                        if (!playerConfig.get(p).statsMeetup.statsString.equals("")) {
                            mysql.update("INSERT INTO APVP(UUID, NICK, STATS, MEETUP) VALUES ('" + p.getUniqueId().toString() + "', '" + p.getName() + "', '" + playerConfig.get(p).stats.statsString + "', '" + playerConfig.get(p).statsMeetup.statsString + "');");
                        } else {
                            mysql.update("INSERT INTO APVP(UUID, NICK, STATS) VALUES ('" + p.getUniqueId().toString() + "', '" + p.getName() + "', '" + playerConfig.get(p).stats.statsString + "');");
                        }
                    } else if (!playerConfig.get(p).statsMeetup.statsString.equals("")) {
                        mysql.update("INSERT INTO APVP(NICK, MEETUP) VALUES ('" + p.getName() + "', '" + playerConfig.get(p).statsMeetup.statsString + "');");
                    }
                } else {
                    if (!playerConfig.get(p).stats.statsString.equals("")) {
                        mysql.update("INSERT INTO APVP(UUID, NICK, STATS) VALUES ('" + p.getUniqueId().toString() + "', '" + p.getName() + "', '" + playerConfig.get(p).stats.statsString + "');");
                    }
                }
                if (!exist.contains(p.getName())) {
                    exist.add(p.getName());
                }
            }
        }
    }

    public static void guardarMeetup(Player p) {
        if (extraLang.sql) {
            if (isExist(p)) {
                playerConfig.get(p).statsMeetup.componer();
                ArenaPvP.mysql.update("UPDATE APVP SET MEETUP='" + playerConfig.get(p).statsMeetup.statsString + "' WHERE UUID= '" + p.getUniqueId().toString() + "';");
            } else {
                playerConfig.get(p).statsMeetup.componer();
                mysql.update("INSERT INTO APVP(NICK, MEETUP) VALUES ('" + p.getName() + "', '" + playerConfig.get(p).statsMeetup.statsString + "');");
                if (!exist.contains(p.getName())) {
                    exist.add(p.getName());
                }
            }
        }
    }
}
