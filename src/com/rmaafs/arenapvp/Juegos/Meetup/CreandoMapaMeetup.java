package com.rmaafs.arenapvp.Juegos.Meetup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.rmaafs.arenapvp.Extra;
import static com.rmaafs.arenapvp.Extra.BURP;
import static com.rmaafs.arenapvp.Extra.LEVEL_UP;
import static com.rmaafs.arenapvp.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.Extra.clang;
import static com.rmaafs.arenapvp.Extra.mapMeetupLibres;
import com.rmaafs.arenapvp.Kit;
import static com.rmaafs.arenapvp.Main.extraLang;
import static com.rmaafs.arenapvp.Main.hotbars;
import static com.rmaafs.arenapvp.Main.meetupControl;
import static com.rmaafs.arenapvp.Main.plugin;
import com.rmaafs.arenapvp.MapaMeetup;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreandoMapaMeetup {

    public enum Accion {

        CORNER1, CORNER2, SPAWNS, NAME
    };

    Player p;
    Kit kit;
    public Accion accion;

    String mapName;
    Location corner1, corner2;
    List<Location> spawns = new ArrayList<>();

    private String corner, spawn, name, created, nameexist, spawnremoved;

    public CreandoMapaMeetup(Player pp, Kit k) {
        p = pp;
        kit = k;
        accion = Accion.CORNER1;
        setConfig();
    }

    public void setConfig() {
        corner = Extra.tc(clang.getString("creating.meetup.corner"));
        spawn = Extra.tc(clang.getString("creating.meetup.spawn"));
        spawnremoved = Extra.tc(clang.getString("creating.meetup.spawnremoved"));
        name = Extra.tc(clang.getString("creating.meetup.name"));
        created = Extra.tc(clang.getString("creating.meetup.created"));
        nameexist = Extra.tc(clang.getString("creating.meetup.nameexist"));

        paso();
    }

    private void paso() {
        Extra.sonido(p, ORB_PICKUP);
        switch (accion) {
            case CORNER1:
                p.sendMessage(corner.replaceAll("<number>", "1"));
                Extra.limpiarP(p);
                p.getInventory().setItem(0, new ItemStack(35, 1, (short) 14));
                break;
            case CORNER2:
                p.sendMessage(corner.replaceAll("<number>", "2"));
                break;
            case SPAWNS:
                p.sendMessage(spawn);
                Extra.limpiarP(p);
                p.getInventory().setItem(0, new ItemStack(35, 5, (short) 14));
                p.setGameMode(GameMode.SURVIVAL);
                p.setAllowFlight(true);
                p.setFlying(true);
                p.setLevel(0);
                break;
            case NAME:
                if (spawns.isEmpty()) {
                    accion = Accion.SPAWNS;
                    paso();
                } else {
                    Extra.limpiarP(p);
                    p.sendMessage(name);
                }
                break;
        }
    }

    public void putCorner(Location loc, boolean uno) {
        if (uno) {
            corner1 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            accion = Accion.CORNER2;
        } else {
            corner2 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            accion = Accion.SPAWNS;
        }
        paso();
    }

    public void putSpawn(Location loc) {
        p.getInventory().setItem(0, new ItemStack(35, 5, (short) 14));
        spawns.add(loc);
        p.setLevel(spawns.size());
        Extra.sonido(p, ORB_PICKUP);
    }

    public void breakBlock(Location loc) {
        if (spawns.contains(loc)) {
            spawns.remove(loc);
            p.sendMessage(spawnremoved);
            p.setLevel(spawns.size());
            Extra.sonido(p, BURP);
        }
    }

    public void saveSpawns() {
        for (Location l : spawns) {
            l.getBlock().setType(Material.AIR);
        }
        accion = Accion.NAME;
        paso();
    }

    public void createMap(String s) {
        if (!mapMeetupLibres.containsKey(kit)) {
            List<MapaMeetup> lista = new ArrayList<>();
            mapMeetupLibres.put(kit, lista);
        }
        for (MapaMeetup m : mapMeetupLibres.get(kit)) {
            if (m.getName().toLowerCase().equals(s.toLowerCase())) {
                p.sendMessage(nameexist);
                paso();
                return;
            }
        }
        MapaMeetup m;
        m = new MapaMeetup(s, corner1, corner2, spawns);
        mapMeetupLibres.get(kit).add(m);
        crearFile(s);
        p.sendMessage(created.replaceAll("<kit>", kit.getKitName()).replaceAll("<map>", m.getName()));
        if (meetupControl.creandoMapaMeetup.containsKey(p)) {
            meetupControl.creandoMapaMeetup.remove(p);
        }
        Extra.limpiarP(p);
        hotbars.setMain(p);
        extraLang.teleportSpawn(p);
        Extra.sonido(p, LEVEL_UP);
    }

    public void crearFile(String s) {
        File f = new File(plugin.getDataFolder() + File.separator + "meetupmaps");
        if (!f.exists()) {
            f.mkdir();
        }
        File elkit = new File(plugin.getDataFolder() + File.separator + "meetupmaps" + File.separator + kit.kitName + ".yml");
        try {
            if (!elkit.exists()) {
                elkit.createNewFile();
            }
            FileConfiguration ckit = YamlConfiguration.loadConfiguration(elkit);
            ckit.set(s + ".w", corner1.getWorld().getName());
            ckit.set(s + ".c1.x", corner1.getBlockX());
            ckit.set(s + ".c1.y", corner1.getBlockY());
            ckit.set(s + ".c1.z", corner1.getBlockZ());

            ckit.set(s + ".c2.x", corner2.getBlockX());
            ckit.set(s + ".c2.y", corner2.getBlockY());
            ckit.set(s + ".c2.z", corner2.getBlockZ());

            int i = 0;
            for (Location spawn1 : spawns) {
                ckit.set(s + ".spawn." + i + ".x", spawn1.getX());
                ckit.set(s + ".spawn." + i + ".y", spawn1.getY() + 1);
                ckit.set(s + ".spawn." + i + ".z", spawn1.getZ());
                i++;
            }

            Extra.guardar(elkit, ckit);
        } catch (IOException ex) {
            Logger.getLogger(CreandoMapaMeetup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
