package com.rmaafs.arenapvp.MapControl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.rmaafs.arenapvp.Extra;
import static com.rmaafs.arenapvp.Extra.LEVEL_UP;
import static com.rmaafs.arenapvp.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.Extra.clang;
import static com.rmaafs.arenapvp.Extra.mapLibres;
import static com.rmaafs.arenapvp.Extra.mapOcupadas;
import com.rmaafs.arenapvp.Kit;
import static com.rmaafs.arenapvp.Main.extraLang;
import static com.rmaafs.arenapvp.Main.hotbars;
import static com.rmaafs.arenapvp.Main.plugin;
import static com.rmaafs.arenapvp.MapControl.CrearMapaEvent.creandoMapa;
import com.rmaafs.arenapvp.Mapa;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CrearMapa {

    enum Accion {

        CORNER1, CORNER2, SPAWN1, SPAWN2, NAME
    };

    Kit kit;
    Player p;
    Accion accion;

    Location corner1, corner2, spawn1, spawn2;

    String corner, spawn, name, created, nameexist;

    public CrearMapa(Player pl, Kit k) {
        p = pl;
        kit = k;
        setConfig();
    }

    public void setConfig() {
        corner = Extra.tc(clang.getString("creating.map.corner"));
        spawn = Extra.tc(clang.getString("creating.map.spawn"));
        name = Extra.tc(clang.getString("creating.map.name"));
        created = Extra.tc(clang.getString("creating.map.created"));
        nameexist = Extra.tc(clang.getString("creating.map.nameexist"));

        if (kit.regen) {
            accion = Accion.CORNER1;
        } else {
            accion = Accion.SPAWN1;
        }

        paso();
    }

    public void paso() {
        Extra.sonido(p, ORB_PICKUP);
        switch (accion) {
            case CORNER1:
                p.sendMessage(corner.replaceAll("<number>", "1"));
                Extra.limpiarP(p);
                p.getInventory().addItem(new ItemStack(35, 1, (short) 14));
                break;
            case CORNER2:
                p.sendMessage(corner.replaceAll("<number>", "2"));
                break;
            case SPAWN1:
                p.sendMessage(spawn.replaceAll("<number>", "1"));
                Extra.limpiarP(p);
                break;
            case SPAWN2:
                p.sendMessage(spawn.replaceAll("<number>", "2"));
                break;
            case NAME:
                p.sendMessage(name);
                break;
        }
    }

    public void putCorner(Location loc, boolean uno) {
        if (uno) {
            corner1 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            accion = Accion.CORNER2;
        } else {
            corner2 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            accion = Accion.SPAWN1;
        }
        paso();
    }

    public void putSpawn(Location loc, boolean uno) {
        if (uno) {
            spawn1 = loc;
            accion = Accion.SPAWN2;
        } else {
            spawn2 = loc;
            accion = Accion.NAME;
        }
        paso();
    }

    public void createMap(String s) {
        if (!mapLibres.containsKey(kit)) {
            List<Mapa> lista = new ArrayList<>();
            List<Mapa> lista2 = new ArrayList<>();
            mapLibres.put(kit, lista);
            mapOcupadas.put(kit, lista2);
        }
        for (Mapa m : mapLibres.get(kit)) {
            if (m.getName().toLowerCase().equals(s.toLowerCase())) {
                p.sendMessage(nameexist);
                paso();
                return;
            }
        }
        Mapa m;
        if (kit.regen) {
            m = new Mapa(s, corner1, corner2, spawn1, spawn2);
        } else {
            m = new Mapa(s, spawn1, spawn2);
        }
        mapLibres.get(kit).add(m);
        crearFile(s);
        p.sendMessage(created.replaceAll("<kit>", kit.getKitName()).replaceAll("<map>", m.getName()));
        if (creandoMapa.containsKey(p)) {
            creandoMapa.remove(p);
        }
        Extra.limpiarP(p);
        hotbars.setMain(p);
        extraLang.teleportSpawn(p);
        Extra.sonido(p, LEVEL_UP);
    }

    public void crearFile(String s) {
        File f = new File(plugin.getDataFolder() + File.separator + "maps");
        if (!f.exists()){
            f.mkdir();
        }
        File elkit = new File(plugin.getDataFolder() + File.separator + "maps" + File.separator + kit.kitName + ".yml");
        try {
            if (!elkit.exists()) {
                elkit.createNewFile();
            }
            FileConfiguration ckit = YamlConfiguration.loadConfiguration(elkit);
            ckit.set(s + ".w", spawn1.getWorld().getName());
            if (kit.regen){
                ckit.set(s + ".c1.x", corner1.getX());
                ckit.set(s + ".c1.y", corner1.getY());
                ckit.set(s + ".c1.z", corner1.getZ());
                
                ckit.set(s + ".c2.x", corner2.getX());
                ckit.set(s + ".c2.y", corner2.getY());
                ckit.set(s + ".c2.z", corner2.getZ());
            }
            ckit.set(s + ".s1.x", spawn1.getX());
            ckit.set(s + ".s1.y", spawn1.getY());
            ckit.set(s + ".s1.z", spawn1.getZ());
            ckit.set(s + ".s1.ya", spawn1.getYaw());
            ckit.set(s + ".s1.p", spawn1.getPitch());
            
            ckit.set(s + ".s2.x", spawn2.getX());
            ckit.set(s + ".s2.y", spawn2.getY());
            ckit.set(s + ".s2.z", spawn2.getZ());
            ckit.set(s + ".s2.ya", spawn2.getYaw());
            ckit.set(s + ".s2.p", spawn2.getPitch());
            
            Extra.guardar(elkit, ckit);
        } catch (IOException ex) {
            Logger.getLogger(CrearMapa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
