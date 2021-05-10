package com.rmaafs.arenapvp;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class Mapa {

    String name;
    Location corner1, corner2, spawn1, spawn2;

    public List<Block> bloques = new ArrayList<>();
    public boolean lava = false;
    public boolean puesto = false;

    public int maxY = 0;

    public Mapa(String s, Location c1, Location c2, Location s1, Location s2) {
        name = s;
        corner1 = c1;
        corner2 = c2;
        spawn1 = s1;
        spawn2 = s2;
        if (corner1.getBlockY() > corner2.getBlockY()) {
            maxY = corner2.getBlockY();
        } else {
            maxY = corner1.getBlockY();
        }
        removeAll();
    }

    public Mapa(String s, Location s1, Location s2) {
        name = s;
        spawn1 = s1;
        spawn2 = s2;
    }

    public void regen(Kit k) {
        if (k.regen && puesto) {
            if (lava) {
                if (corner1 == null || corner2 == null) {
                    Bukkit.getConsoleSender().sendMessage("§4ArenaPvP++ >> §cError to regen Map " + name + " of kit " + k.kitName + ". Corner1 and Corner2 not set.");
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
                            if (k.deleteBlocks.contains(new ItemStack(326))) {
                                b.setType(Material.AIR);
                            }
                        } else if (b.getType().equals(Material.STATIONARY_WATER) || b.getType().equals(Material.WATER)) {
                            if (k.deleteBlocks.contains(new ItemStack(327))) {
                                b.setType(Material.AIR);
                            }
                        } else if (b.getType().equals(Material.FIRE)) {
                            if (k.deleteBlocks.contains(new ItemStack(259))) {
                                b.setType(Material.AIR);
                            }
                        } else {
                            if (k.deleteBlocks.contains(new ItemStack(b.getTypeId(), 1, (short) b.getData()))) {
                                b.setType(Material.AIR);
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

    public void removeAll() {
        if (corner1 != null && corner2 != null) {
            Cuboid cubo = new Cuboid(corner1, corner2);
            List<Block> li = cubo.getBlocks();
            for (Block b : li) {
                if ((b.getType() == Material.getMaterial(1) && b.getData() == 0) || b.getType() == Material.getMaterial(4) || b.getType() == Material.getMaterial(5) || b.getType() == Material.getMaterial(49) || b.getType() == Material.getMaterial(8) || b.getType() == Material.getMaterial(9) || b.getType() == Material.getMaterial(10) || b.getType() == Material.getMaterial(11)) {
                    b.setType(Material.AIR);
                }
            }
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

    public Location getSpawn1() {
        return spawn1;
    }

    public void setSpawn1(Location spawn1) {
        this.spawn1 = spawn1;
    }

    public Location getSpawn2() {
        return spawn2;
    }

    public void setSpawn2(Location spawn2) {
        this.spawn2 = spawn2;
    }
}
