package com.rmaafs.arenapvp.Juegos.Duel;

import com.rmaafs.arenapvp.Kit;
import org.bukkit.entity.Player;

public class PreDuelConfig {

    Kit kit;
    Player p, p2;
    int total = 0;
    
    public PreDuelConfig(Player pp, Player pp2){
        p = pp;
        p2 = pp2;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Player getP() {
        return p;
    }

    public void setP(Player p) {
        this.p = p;
    }

    public Player getP2() {
        return p2;
    }

    public void setP2(Player p2) {
        this.p2 = p2;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
    
}
