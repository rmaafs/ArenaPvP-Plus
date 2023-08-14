package com.rmaafs.arenapvp.manager.rank;

public class Rangos {

    String name;
    int elo;

    public Rangos(String n, int e){
        name = n;
        elo = e;
    }

    public String getName() {
        return name;
    }

    public int getElo() {
        return elo;
    }
    
    
}
