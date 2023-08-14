package com.rmaafs.arenapvp.Juegos.Duel;

import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static com.rmaafs.arenapvp.util.Extra.clang;
import com.rmaafs.arenapvp.ArenaPvP;
import static com.rmaafs.arenapvp.ArenaPvP.specControl;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldEvent implements Listener {

    boolean antiRain;
    String youarespecting;

    public WorldEvent() {
        antiRain = cconfig.getBoolean("antirain");
        
        youarespecting = Extra.tc(clang.getString("playing.youarespecting"));
    }

    @EventHandler
    public void WeatherChangeEvent(WeatherChangeEvent event) {
        if (antiRain) {
            if (event.toWeatherState()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getEntity();
            if (((Player) arrow.getShooter()) instanceof Player) {
                arrow.remove();
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        specControl.teleport(e.getPlayer(), e.getTo());
        if (ArenaPvP.VERSIONNUMERO != 7 && e.getCause().equals(PlayerTeleportEvent.TeleportCause.valueOf("SPECTATE"))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(youarespecting);
        }
    }
}
