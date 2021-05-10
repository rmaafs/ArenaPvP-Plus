package com.rmaafs.arenapvp.Party;

import com.rmaafs.arenapvp.Kit;

public class PreDuelGame {

    Kit kit;
    Party p1, p2;
    
    public PreDuelGame(Party pp1, Party pp2){
        p1 = pp1;
        p2 = pp2;
    }

    public Kit getKit() {
        return kit;
    }

    public Party getParty1() {
        return p1;
    }

    public Party getParty2() {
        return p2;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }
}
