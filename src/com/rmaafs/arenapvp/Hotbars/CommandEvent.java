package com.rmaafs.arenapvp.Hotbars;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static com.rmaafs.arenapvp.ArenaPvP.*;

public class CommandEvent implements Listener {

    @EventHandler
    public void Command(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String cmd = e.getMessage();

        if (cmd.equalsIgnoreCase(extraLang.commandsavehotbar) && hotbars.editingSlotHotbar.containsKey(p)){
            e.setCancelled(true);
            hotbars.guardarHotbar(p);
        }
    }
}
