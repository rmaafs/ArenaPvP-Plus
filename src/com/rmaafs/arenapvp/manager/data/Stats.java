package com.rmaafs.arenapvp.manager.data;

import java.util.HashMap;
import java.util.Map;
import static com.rmaafs.arenapvp.util.Extra.cstats;
import static com.rmaafs.arenapvp.util.Extra.kits;
import static com.rmaafs.arenapvp.ArenaPvP.extraLang;

import com.rmaafs.arenapvp.manager.rank.Rangos;
import com.rmaafs.arenapvp.manager.kit.Kit;
import com.rmaafs.arenapvp.util.Extra;
import org.bukkit.entity.Player;

public class Stats {

    HashMap<Kit, Integer> elo = new HashMap<>();
    HashMap<Kit, Integer> played = new HashMap<>();
    HashMap<Kit, Integer> wins = new HashMap<>();
    HashMap<Kit, Rangos> rango = new HashMap<>();

    int rankeds = 0, unrankeds = 0;

    Player p;
    String pname;
    String statsString = "";

    public Stats(Player pp) {
        p = pp;
        pname = p.getName();
        statsString = SQL.getStats(p);
        descomponer();
        reloadRankedsUnrankeds();
    }

    public void reloadRankedsUnrankeds() {
        if (Extra.regenRankedsUnrankeds && Extra.addPlayerNextDay(p)) {
            if (rankeds < extraLang.defaultRankeds) {
                rankeds = extraLang.defaultRankeds;
            }

            if (unrankeds < extraLang.defaultUnRankeds) {
                unrankeds = extraLang.defaultUnRankeds;
            }
            for (int i = 100; i > 1; i--) {
                if (p.hasPermission("apvp.rankeds." + i)) {
                    if (rankeds < i) {
                        rankeds = i;
                    }
                    break;
                }
            }
            for (int i = 100; i > 1; i--) {
                if (p.hasPermission("apvp.unrankeds." + i)) {
                    if (unrankeds < i) {
                        unrankeds = i;
                    }
                    break;
                }
            }
        }
    }

    public void removeKit(Kit k) {
        if (elo.containsKey(k)) {
            elo.remove(k);
        }
        if (played.containsKey(k)) {
            played.remove(k);
        }
        if (wins.containsKey(k)) {
            wins.remove(k);
        }
        componer();
    }

    //BuildUHC,played,wins,elo@
    public void descomponer() {
        String m[] = statsString.split("@");
        for (String m1 : m) {
            String[] l = m1.split(",");
            if (l.length == 4) {
                Kit k = kits.get(l[0]);
                played.put(k, Integer.valueOf(l[1]));
                wins.put(k, Integer.valueOf(l[2]));
                elo.put(k, Integer.valueOf(l[3]));
                rango.put(k, Extra.setRank(elo.get(k)));
            } else if (l.length == 3) {
                pname = l[0];
                rankeds = Integer.valueOf(l[1]);
                unrankeds = Integer.valueOf(l[2]);
            }
        }
    }

    public void componer() {
        String total = "";
        for (Map.Entry<String, Kit> entry : kits.entrySet()) {
            String name = entry.getKey();
            Kit k = entry.getValue();
            if (kits.containsKey(k.kitName) && elo.containsKey(k)) {
                total = total + name + ",";
                total = total + getPlayed(k) + ",";
                total = total + getWins(k) + ",";
                total = total + getElo(k) + "@";
            }
        }
        if (rankeds != extraLang.defaultRankeds || unrankeds != extraLang.defaultUnRankeds) {
            total = total + pname + "," + rankeds + "," + unrankeds;
        }
        if (!total.equals("")) {
            statsString = total;
            cstats.set(p.getUniqueId().toString() + ".o", total);
            Extra.guardar(Extra.stats, cstats);
        }
    }

    public Integer getElo(Kit k) {
        if (elo.containsKey(k)) {
            return elo.get(k);
        }
        return extraLang.defaultElo;
    }

    public Integer getWins(Kit k) {
        if (wins.containsKey(k)) {
            return wins.get(k);
        }
        return 0;
    }

    public Integer getPlayed(Kit k) {
        if (played.containsKey(k)) {
            return played.get(k);
        }
        return 0;
    }

    public String getRank(Kit k) {
        if (rango.containsKey(k)) {
            return rango.get(k).getName();
        }
        return Extra.rangoDefault.getName();
    }

    public Integer getRankeds() {
        return rankeds;
    }

    public Integer getUnRankeds() {
        return unrankeds;
    }

    public void setElo(Kit k, int i) {
        elo.put(k, i);
        rango.put(k, Extra.setRank(i));
    }

    public void setPlayed(Kit k, int i) {
        played.put(k, i);
    }

    public void setWins(Kit k, int i) {
        wins.put(k, i);
    }

    public void setRankeds(int i) {
        rankeds = i;
    }

    public void setUnRankeds(int i) {
        unrankeds = i;
    }
}
