package com.rmaafs.arenapvp.API;

import static com.rmaafs.arenapvp.ArenaPvP.duelControl;

import me.roy.builduhcbot.bot.Bot;
import org.bukkit.entity.Player;

import java.io.IOException;

public class CreateDuel {

    public static void createFakeDuel(Player player, Player bot){
            duelControl.createFakeDuel(player, bot);
    }
}
