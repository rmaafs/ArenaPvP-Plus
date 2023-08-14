package com.rmaafs.arenapvp.manager.config;

import java.util.HashMap;

import com.rmaafs.arenapvp.Hotbars.SavedHotbars;
import com.rmaafs.arenapvp.manager.data.SQL;
import com.rmaafs.arenapvp.manager.data.Stats;
import com.rmaafs.arenapvp.manager.data.StatsMeetup;
import com.rmaafs.arenapvp.manager.kit.Kit;
import org.bukkit.entity.Player;

public class PlayerConfig {

    private final HashMap<Kit, SavedHotbars> hotbars = new HashMap<>();
    public Stats stats;
    public final StatsMeetup statsMeetup;
    private final Player p;

    public PlayerConfig(Player pp) {
        p = pp;
        stats = new Stats(p);
        statsMeetup = new StatsMeetup(p);
    }
    
    public void removeKit(Kit k){
        hotbars.remove(k);
        stats.removeKit(k);
    }

    public SavedHotbars getHotbars(Kit k) {
        if (!hotbars.containsKey(k)) {
            hotbars.put(k, new SavedHotbars(p, k));
        }
        return hotbars.get(k);
    }

    public void putHotbar(int i, Kit k) {
        p.getInventory().setContents(getHotbars(k).getHotbar(i));
    }

    public void putArmor(Kit k) {
        p.getInventory().setArmorContents(k.armor);
    }

    public void putInv(int i, Kit k) {
        putHotbar(i, k);
        putArmor(k);
    }

    public void saveStats() {
        stats.componer();
        statsMeetup.componer();
        SQL.guardarStats(p, true);
    }
    
    //---------------------

    public Integer getElo(Kit k) {
        return stats.getElo(k);
    }

    public Integer getWins(Kit k) {
        return stats.getWins(k);
    }

    public Integer getPlayed(Kit k) {
        return stats.getPlayed(k);
    }
    
    public void removeElo(Kit k, int i){
        stats.setElo(k, getElo(k) - i);
    }
    
    public void addElo(Kit k, int i){
        stats.setElo(k, getElo(k) + i);
    }
    
    public void addPlayed(Kit k){
        stats.setPlayed(k, getPlayed(k) + 1);
    }
    
    public void addWins(Kit k){
        stats.setWins(k, getWins(k) + 1);
    }
    
    //---------------------
    
    public Integer getKillsMeetup(Kit k) {
        return statsMeetup.getKills(k);
    }

    public Integer getWinsMeetup(Kit k) {
        return statsMeetup.getWins(k);
    }

    public Integer getPlayedMeetup(Kit k) {
        return statsMeetup.getPlayed(k);
    }
    
    public void removeKillsMeetup(Kit k, int i){
        statsMeetup.setKills(k, getKillsMeetup(k) - i);
    }
    
    public void addKillsMeetup(Kit k, int i){
        statsMeetup.setKills(k, getKillsMeetup(k) + i);
    }
    
    public void addPlayedMeetup(Kit k){
        statsMeetup.setPlayed(k, getPlayedMeetup(k) + 1);
    }
    
    public void addWinsMeetup(Kit k){
        statsMeetup.setWins(k, getWinsMeetup(k) + 1);
    }
    
    //---------------------
    
    public String getRank(Kit k) {
        return stats.getRank(k);
    }
    
    public Integer getRankeds(){
        return stats.getRankeds();
    }
    
    public Integer getUnRankeds(){
        return stats.getUnRankeds();
    }
    
    public void removeRanked(){
        stats.setRankeds(getRankeds() - 1);
    }
    
    public void removeUnRanked(){
        stats.setUnRankeds(getUnRankeds() - 1);
    }
}
