package com.rmaafs.arenapvp.MapControl;

import java.util.HashMap;

import com.rmaafs.arenapvp.Juegos.Meetup.CreandoMapaMeetup;

import static com.rmaafs.arenapvp.ArenaPvP.meetupControl;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class CrearMapaEvent implements Listener {

    public static HashMap<Player, CreateMap> creandoMapa = new HashMap<>();
    
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if (creandoMapa.containsKey(p)){
            CreateMap ck = creandoMapa.get(p);
            e.setCancelled(true);
            if (ck.action == CreateMap.Action.CORNER1){
                ck.putCorner(e.getBlock().getLocation(), true);
            } else if (ck.action == CreateMap.Action.CORNER2){
                ck.putCorner(e.getBlock().getLocation(), false);
            } else {
                e.setCancelled(false);
            }
        } else if (meetupControl.creandoMapaMeetup.containsKey(p)){
            CreandoMapaMeetup ck = meetupControl.creandoMapaMeetup.get(p);
            e.setCancelled(true);
            if (ck.accion == CreandoMapaMeetup.Accion.CORNER1){
                ck.putCorner(e.getBlock().getLocation(), true);
            } else if (ck.accion == CreandoMapaMeetup.Accion.CORNER2){
                ck.putCorner(e.getBlock().getLocation(), false);
            } else if (ck.accion == CreandoMapaMeetup.Accion.SPAWNS){
                ck.putSpawn(e.getBlock().getLocation());
                e.setCancelled(false);
            } else {
                e.setCancelled(false);
            }
        }
    }
    
    @EventHandler
    public void onChat(PlayerChatEvent e){
        Player p = e.getPlayer();
        if (creandoMapa.containsKey(p)){
            CreateMap ck = creandoMapa.get(p);
            e.setCancelled(true);
            if (ck.action == CreateMap.Action.SPAWN1 && e.getMessage().toLowerCase().equalsIgnoreCase("ready")){
                ck.putSpawn(p.getLocation(), true);
            } else if (ck.action == CreateMap.Action.SPAWN2 && e.getMessage().toLowerCase().equalsIgnoreCase("ready")){
                ck.putSpawn(p.getLocation(), false);
            } else if (ck.action == CreateMap.Action.NAME){
                ck.createMap(ChatColor.stripColor(e.getMessage()));
            } else {
                e.setCancelled(false);
            }
        } else if (meetupControl.creandoMapaMeetup.containsKey(p)){
            CreandoMapaMeetup ck = meetupControl.creandoMapaMeetup.get(p);
            e.setCancelled(true);
            if (ck.accion == CreandoMapaMeetup.Accion.SPAWNS){
                ck.saveSpawns();
            } else if (ck.accion == CreandoMapaMeetup.Accion.NAME){
                ck.createMap(ChatColor.stripColor(e.getMessage()));
            } else {
                e.setCancelled(false);
            }
        } else if (meetupControl.creandoEventoMeetup.containsKey(p)) {
            e.setCancelled(true);
            meetupControl.creandoEventoMeetup.get(p).start(e.getMessage());
        }
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if (meetupControl.creandoMapaMeetup.containsKey(p)){
            CreandoMapaMeetup ck = meetupControl.creandoMapaMeetup.get(p);
            e.setCancelled(true);
            if (ck.accion == CreandoMapaMeetup.Accion.SPAWNS){
                ck.breakBlock(e.getBlock().getLocation());
                e.getBlock().setType(Material.AIR);
                e.setCancelled(true);
            } else {
                e.setCancelled(false);
            }
        }
    }
}
