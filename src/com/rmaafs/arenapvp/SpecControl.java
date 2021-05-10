package com.rmaafs.arenapvp;

import java.util.HashMap;
import static com.rmaafs.arenapvp.Extra.LEVEL_UP;
import static com.rmaafs.arenapvp.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.Extra.clang;
import static com.rmaafs.arenapvp.Main.extraLang;
import static com.rmaafs.arenapvp.Main.hotbars;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpecControl {

    public HashMap<Player, Player> mirando = new HashMap<>();
    public HashMap<Player, Player> teMiran = new HashMap<>();

    String youspecting;

    public SpecControl() {
        youspecting = Extra.tc(clang.getString("meetup.game.spectatormode"));
    }

    public void teleport(Player p, Location e) {
        if (teMiran.containsKey(p)) {
            Player m = teMiran.get(p);
            m.teleport(e);
            Extra.sonido(p, ORB_PICKUP);
        }
    }

    public void spec(Player p, String o) {
        if (o.equalsIgnoreCase("leave")) {
            if (mirando.containsKey(p)) {
                leave(p, true);
            }
        } else {
            if (!p.getName().equalsIgnoreCase(o)) {
                Player t = Bukkit.getPlayer(o);
                if (Extra.isExist(t, p)) {
                    if (mirando.containsKey(p)) {
                        teMiran.remove(mirando.get(p));
                        mirando.remove(p);
                    } else {
                        if (!Extra.isCheckYouPlaying(p)){
                            return;
                        }
                    }

                    mirando.put(p, t);
                    teMiran.put(t, p);
                    p.teleport(t);
                    ponerSpec(p);
                    p.sendMessage(youspecting);
                    Extra.sonido(p, LEVEL_UP);
                }
            }
        }
    }

    public void leave(Player p, boolean online) {
        if (mirando.containsKey(p)) {
            teMiran.remove(mirando.get(p));
            mirando.remove(p);
        }
        mostrarPlayer(p);
        if (online) {
            hotbars.setMain(p);
            extraLang.teleportSpawn(p);
        }
    }

    private void ponerSpec(final Player p) {
        Extra.limpiarP(p);
        if (extraLang.usespectatormode) {
            p.setGameMode(GameMode.valueOf("SPECTATOR"));
        } else {
            ocultarPlayer(p);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setGameMode(GameMode.CREATIVE);
            p.spigot().setCollidesWithEntities(false);
        }
        hotbars.setLeave(p);
    }

    private void ocultarPlayer(Player p) {
        for (Player o : Bukkit.getServer().getOnlinePlayers()) {
            if (o != p) {
                o.hidePlayer(p);
            }
        }
    }

    private void mostrarPlayer(Player p) {
        p.setMaximumNoDamageTicks(20);
        p.spigot().setCollidesWithEntities(true);
        for (Player o : Bukkit.getServer().getOnlinePlayers()) {
            if (o != p) {
                o.showPlayer(p);
            }
        }
    }
}
