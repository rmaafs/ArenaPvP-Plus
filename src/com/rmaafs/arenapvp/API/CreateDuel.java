package com.rmaafs.arenapvp.API;

import static com.rmaafs.arenapvp.Main.duelControl;
import org.bukkit.entity.Player;

public class CreateDuel {

    public static void createFakeDuel(Player p, Player bot){
        duelControl.createFakeDuel(p, bot);
    }
}
