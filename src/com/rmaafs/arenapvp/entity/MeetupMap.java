package com.rmaafs.arenapvp.entity;

import java.util.ArrayList;
import java.util.List;

import com.rmaafs.arenapvp.manager.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MeetupMap {

    private String name;
    private  Location corner1;
    private Location corner2;
    private  List<Location> spawns = new ArrayList<>();
    private  int totalSpawns = 0;
    public List<Block> blocks = new ArrayList<>();
    public boolean lava = false;
    public boolean set = false;
    public int maxY = 0;

    public MeetupMap(String s, Location c1, Location c2, List<Location> locs) {
        name = s;
        corner1 = c1;
        corner2 = c2;
        spawns = locs;
        maxY = Math.min(corner1.getBlockY(), corner2.getBlockY());
    }
    
    public Location getLoc(){
        if (totalSpawns >= spawns.size()){
            totalSpawns = 0;
        }
        return spawns.get(totalSpawns++);
    }
    
    public Location getLoc(int i){
        return spawns.get(i);
    }

    public void regen(Kit k) {
        if (k.regen && set) {
            if (lava) {
                if (corner1 == null || corner2 == null) {
                    Bukkit.getConsoleSender().sendMessage("§4ArenaPvP++ >> §cError to regen Meetup map " + name + " of kit " + k.kitName + ". Corner1 and Corner2 not set.");
                } else {
                    Map.regenUtil(k, corner1, corner2, maxY);
                }
            } else {
                for (Block b : blocks) {
                    b.setType(Material.AIR);
                }
            }
            blocks.clear();
            lava = false;
            set = false;
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
