package com.rmaafs.arenapvp;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class MapaMeetup {

    String name;
    Location corner1, corner2;
    List<Location> spawns = new ArrayList<>();
    
    int totalspawn = 0;

    public List<Block> bloques = new ArrayList<>();
    public boolean lava = false;
    public boolean puesto = false;

    public int maxY = 0;

    public MapaMeetup(String s, Location c1, Location c2, List<Location> locs) {
        name = s;
        corner1 = c1;
        corner2 = c2;
        spawns = locs;
        if (corner1.getBlockY() > corner2.getBlockY()) {
            maxY = corner2.getBlockY();
        } else {
            maxY = corner1.getBlockY();
        }
    }
    
    public Location getLoc(){
        if (totalspawn >= spawns.size()){
            totalspawn = 0;
        }
        return spawns.get(totalspawn++);
    }
    
    public Location getLoc(int i){
        return spawns.get(i);
    }

    public void regen(Kit k) {
        if (k.regen && puesto) {
            if (lava) {
                if (corner1 == null || corner2 == null) {
                    Bukkit.getConsoleSender().sendMessage("§4ArenaPvP++ >> §cError to regen Meetup map " + name + " of kit " + k.kitName + ". Corner1 and Corner2 not set.");
                } else {
                    Location loc1 = corner1.clone();
                    Location loc2 = corner2.clone();
                    if (loc1.getBlockY() > loc2.getBlockY()) {
                        loc1.setY(maxY);
                    } else {
                        loc2.setY(maxY);
                    }
                    Cuboid cubo = new Cuboid(loc1, loc2);
                    List<Block> li = cubo.getBlocks();
                    for (Block b : li) {
                        if (b.getType().equals(Material.STATIONARY_LAVA) || b.getType().equals(Material.LAVA)) {
                            for (ItemStack it : k.deleteBlocks) {
                                if (Material.getMaterial(326).getData().getName().equals(it.getType().getData().getName())) {
                                    b.setType(Material.AIR);
                                }
                            }
                        } else if (b.getType().equals(Material.STATIONARY_WATER) || b.getType().equals(Material.WATER)) {
                            for (ItemStack it : k.deleteBlocks) {
                                if (Material.getMaterial(327).getData().getName().equals(it.getType().getData().getName())) {
                                    b.setType(Material.AIR);
                                }
                            }
                        } else if (b.getType().equals(Material.FIRE)) {
                            for (ItemStack it : k.deleteBlocks) {
                                if (Material.getMaterial(259).getData().getName().equals(it.getType().getData().getName())) {
                                    b.setType(Material.AIR);
                                }
                            }
                        } else {
                            for (ItemStack it : k.deleteBlocks) {
                                if (Material.getMaterial(b.getTypeId()).getData().getName().equals(it.getType().getData().getName())) {
                                    b.setType(Material.AIR);
                                }
                            }
                        }
                    }
                }
            } else {
                for (Block b : bloques) {
                    b.setType(Material.AIR);
                }
            }
            bloques.clear();
            lava = false;
            puesto = false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }
}
