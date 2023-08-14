package com.rmaafs.arenapvp.entity;

import java.util.ArrayList;
import java.util.List;

import com.rmaafs.arenapvp.manager.kit.Kit;
import com.rmaafs.arenapvp.util.world.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class Map {

    public String name;
    public Location corner1;
    public Location corner2;
    public Location spawn1;
    public Location spawn2;
    public List<Block> blocks = new ArrayList<>();
    public boolean lava = false;
    public boolean poss = false;
    public int maxY = 0;

    public Map(String s, Location c1, Location c2, Location s1, Location s2) {
        name = s;
        corner1 = c1;
        corner2 = c2;
        spawn1 = s1;
        spawn2 = s2;
        maxY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        removeAll();
    }

    public Map(String s, Location s1, Location s2) {
        name = s;
        spawn1 = s1;
        spawn2 = s2;
    }

    public void regen(Kit k) {
        if (k.regen && poss) {
            if (lava) {
                if (corner1 == null || corner2 == null) {
                    Bukkit.getConsoleSender().sendMessage("§4ArenaPvP++ >> §cError to regen Map " + name + " of kit " + k.kitName + ". Corner1 and Corner2 not set.");
                } else {
                    regenUtil(k, corner1, corner2, maxY);
                }
            } else {
                for (Block b : blocks) {
                    b.setType(Material.AIR);
                }
            }
            blocks.clear();
            lava = false;
            poss = false;
        }
    }

    static void regenUtil(Kit k, Location corner1, Location corner2, int maxY) {
        Location loc1 = corner1.clone();
        Location loc2 = corner2.clone();
        if (loc1.getBlockY() > loc2.getBlockY()) {
            loc1.setY(maxY);
        } else {
            loc2.setY(maxY);
        }
        Cuboid cuboid = new Cuboid(loc1, loc2);
        List<Block> li = cuboid.getBlocks();

        for (Block b : li) {
            Material blockType = b.getType();
            byte blockData = b.getData();

            boolean shouldRemove = false;
            ItemStack blockItemStack = new ItemStack(blockType, 1, blockData);

            if (blockType == Material.WATER || blockType == Material.STATIONARY_WATER ||
                    blockType == Material.LAVA || blockType == Material.STATIONARY_LAVA) {

                shouldRemove = true;

            } else {
                for (ItemStack it : k.deleteBlocks) {
                    if (it.getType() == blockType && it.getData().getData() == blockData) {
                        shouldRemove = true;
                        break;
                    }
                }
            }

            if (shouldRemove) {
                b.setType(Material.AIR);
            }
        }
    }

    public void removeAll() {
        if (corner1 != null && corner2 != null) {
            Cuboid cuboid = new Cuboid(corner1, corner2);
            List<Block> li = cuboid.getBlocks();
            li.forEach(b -> {
                if ((b.getType() == Material.getMaterial(1)
                        && b.getData() == 0)
                        || b.getType() == Material.getMaterial(4)
                        || b.getType() == Material.getMaterial(5)
                        || b.getType() == Material.getMaterial(49)
                        || b.getType() == Material.getMaterial(8)
                        || b.getType() == Material.getMaterial(9)
                        || b.getType() == Material.getMaterial(10)
                        || b.getType() == Material.getMaterial(11)
                ) {
                    b.setType(Material.AIR);
                }
            });
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
