package com.rmaafs.arenapvp.manager.data;

import java.util.HashMap;
import java.util.Map;
import static com.rmaafs.arenapvp.util.Extra.cstats;
import static com.rmaafs.arenapvp.util.Extra.kits;

import com.rmaafs.arenapvp.manager.kit.Kit;
import com.rmaafs.arenapvp.util.Extra;
import org.bukkit.entity.Player;

public class StatsMeetup {

    HashMap<Kit, Integer> kills = new HashMap<>();
    HashMap<Kit, Integer> played = new HashMap<>();
    HashMap<Kit, Integer> wins = new HashMap<>();

    Player p;
    String pname;
    String statsString = "";

    public StatsMeetup(Player pp) {
        p = pp;
        pname = p.getName();
        statsString = SQL.getStatsMeetup(p);
        descomponer();
    }
    
    public void removeKit(Kit k){
        if (kills.containsKey(k)){
            kills.remove(k);
        }
        if (played.containsKey(k)){
            played.remove(k);
        }
        if (wins.containsKey(k)){
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
                kills.put(k, Integer.valueOf(l[3]));
            }
        }
    }

    public void componer() {
        String total = "";
        for (Map.Entry<String, Kit> entry : kits.entrySet()) {
            String name = entry.getKey();
            Kit k = entry.getValue();
            if (kits.containsKey(k.kitName) && played.containsKey(k)) {
                total = total + name + ",";
                total = total + getPlayed(k) + ",";
                total = total + getWins(k) + ",";
                total = total + getKills(k) + "@";
            }
        }
        if (!total.equals("")) {
            statsString = total;
            cstats.set(p.getUniqueId().toString() + ".m", total);
            Extra.guardar(Extra.stats, cstats);
        }
    }

    public Integer getKills(Kit k) {
        if (kills.containsKey(k)) {
            return kills.get(k);
        }
        return 0;
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

    public void setKills(Kit k, int i) {
        kills.put(k, i);
    }

    public void setPlayed(Kit k, int i) {
        played.put(k, i);
    }

    public void setWins(Kit k, int i) {
        wins.put(k, i);
    }
}
