package com.rmaafs.arenapvp.versions;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class v1_8_R3 implements Packets {

    @Override
    public int verPing(Player player){
        //return ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().ping;
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return entityPlayer.getClass().getField("ping").getInt(entityPlayer);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
