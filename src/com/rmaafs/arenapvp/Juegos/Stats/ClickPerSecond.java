package com.rmaafs.arenapvp.Juegos.Stats;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClickPerSecond implements Listener {

    public static HashMap<Player, Integer> cooldown = new HashMap<>();
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR) {
            Player p = e.getPlayer();
            if (!cooldown.containsKey(p)) {
                cooldown.put(p, 1);
            } else {
                cooldown.put(p, cooldown.get(p) + 1);
            }
        }
    }
}
