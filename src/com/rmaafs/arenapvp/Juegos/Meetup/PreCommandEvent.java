package com.rmaafs.arenapvp.Juegos.Meetup;

import static com.rmaafs.arenapvp.ArenaPvP.meetupControl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PreCommandEvent implements Listener {

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/joinmeetupevent")) {
            int slot = Integer.valueOf(e.getMessage().replaceAll("/joinmeetupevent ", ""));
            meetupControl.clickItem(e.getPlayer(), slot, false);
            e.setCancelled(true);
        }
    }
}
