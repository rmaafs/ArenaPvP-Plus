package com.rmaafs.arenapvp.MapControl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.rmaafs.arenapvp.Extra;
import com.rmaafs.arenapvp.Kit;

import static com.rmaafs.arenapvp.Extra.*;
import static com.rmaafs.arenapvp.Main.extraLang;
import static com.rmaafs.arenapvp.Main.hotbars;
import static com.rmaafs.arenapvp.Main.plugin;
import static com.rmaafs.arenapvp.MapControl.CrearMapaEvent.creandoMapa;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import com.rmaafs.arenapvp.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateMap {

    enum Action {CORNER1, CORNER2, SPAWN1, SPAWN2, NAME};

    private final Kit kit;
    private final Player p;
    public Action action;
    public Location corner1;
    public Location corner2;
    public Location spawn1;
    public Location spawn2;
    public String corner;
    public String spawn;
    public String name;
    public String created;
    public String nameExist;

    public CreateMap(Player p, Kit kit) {
        this.p = p;
        this.kit = kit;
        setConfig();
    }

    public void setConfig() {
        corner = tc(clang.getString("creating.map.corner"));
        spawn = tc(clang.getString("creating.map.spawn"));
        name = tc(clang.getString("creating.map.name"));
        created = tc(clang.getString("creating.map.created"));
        nameExist = tc(clang.getString("creating.map.nameexist"));

        if (kit.regen) {
            action = Action.CORNER1;
        } else {
            action = Action.SPAWN1;
        }
        paso();
    }

    public void paso() {
        Extra.sonido(p, ORB_PICKUP);
        switch (action) {
            case CORNER1:
                p.sendMessage(corner.replaceAll("<number>", "1"));
                cleanPlayer(p);
                p.getInventory().addItem(new ItemStack(Material.getMaterial(35), 1, (byte) 14));
                break;
            case CORNER2:
                p.sendMessage(corner.replaceAll("<number>", "2"));
                break;
            case SPAWN1:
                p.sendMessage(spawn.replaceAll("<number>", "1"));
                cleanPlayer(p);
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
            action = Action.CORNER2;
        } else {
            corner2 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            action = Action.SPAWN1;
        }
        paso();
    }

    public void putSpawn(Location loc, boolean uno) {
        if (uno) {
            spawn1 = loc;
            action = Action.SPAWN2;
        } else {
            spawn2 = loc;
            action = Action.NAME;
        }
        paso();
    }

    public void createMap(String s) {
        if (!mapLibres.containsKey(kit)) {
            mapLibres.put(kit, new ArrayList<>());
            mapOcupadas.put(kit, new ArrayList<>());
        }
        for (Map m : mapLibres.get(kit)) {
            if (m.getName().toLowerCase().equals(s.toLowerCase())) {
                p.sendMessage(nameExist);
                paso();
                return;
            }
        }
        Map m;
        if (kit.regen) {
            m = new Map(s, corner1, corner2, spawn1, spawn2);
        } else {
            m = new Map(s, spawn1, spawn2);
        }
        mapLibres.get(kit).add(m);
        createFiles(s);
        p.sendMessage(created.replaceAll("<kit>", kit.getKitName()).replaceAll("<map>", m.getName()));
        creandoMapa.remove(p);
        cleanPlayer(p);
        hotbars.setMain(p);
        extraLang.teleportSpawn(p);
        Extra.sonido(p, LEVEL_UP);
    }

    public void createFiles(String s) {
        File f = new File(plugin.getDataFolder() + File.separator + "maps");
        if (!f.exists()){
            f.mkdir();
        }
        File kit = new File(plugin.getDataFolder() + File.separator + "maps" + File.separator + this.kit.kitName + ".yml");
        try {
            if (!kit.exists()) {
                kit.createNewFile();
            }
            FileConfiguration fcKit = YamlConfiguration.loadConfiguration(kit);
            fcKit.set(s + ".w", spawn1.getWorld().getName());
            if (this.kit.regen){
                fcKit.set(s + ".c1.x", corner1.getX());
                fcKit.set(s + ".c1.y", corner1.getY());
                fcKit.set(s + ".c1.z", corner1.getZ());
                
                fcKit.set(s + ".c2.x", corner2.getX());
                fcKit.set(s + ".c2.y", corner2.getY());
                fcKit.set(s + ".c2.z", corner2.getZ());
            }
            fcKit.set(s + ".s1.x", spawn1.getX());
            fcKit.set(s + ".s1.y", spawn1.getY());
            fcKit.set(s + ".s1.z", spawn1.getZ());
            fcKit.set(s + ".s1.ya", spawn1.getYaw());
            fcKit.set(s + ".s1.p", spawn1.getPitch());
            
            fcKit.set(s + ".s2.x", spawn2.getX());
            fcKit.set(s + ".s2.y", spawn2.getY());
            fcKit.set(s + ".s2.z", spawn2.getZ());
            fcKit.set(s + ".s2.ya", spawn2.getYaw());
            fcKit.set(s + ".s2.p", spawn2.getPitch());
            guardar(kit, fcKit);
        } catch (IOException ex) {
            getLogger(CreateMap.class.getName()).log(SEVERE, null, ex);
        }
    }
}
