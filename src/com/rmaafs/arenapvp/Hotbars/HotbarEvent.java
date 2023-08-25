package com.rmaafs.arenapvp.Hotbars;

import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.playerConfig;
import static com.rmaafs.arenapvp.ArenaPvP.extraLang;
import static com.rmaafs.arenapvp.ArenaPvP.guis;
import static com.rmaafs.arenapvp.ArenaPvP.hotbars;
import static com.rmaafs.arenapvp.ArenaPvP.meetupControl;
import static com.rmaafs.arenapvp.ArenaPvP.partyControl;
import com.rmaafs.arenapvp.manager.config.PlayerConfig;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class HotbarEvent implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (hotbars.editingSlotHotbar.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        } else {
            if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK) || (e.getAction() == Action.LEFT_CLICK_AIR) || (e.getAction() == Action.LEFT_CLICK_BLOCK)) {
                if (e.getItem() != null) {
                    if (((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK))
                            && (e.getPlayer().getItemInHand().getType() == Material.MUSHROOM_SOUP)) {
                        if (e.getPlayer().getHealth() != 20.0D) {
                            e.getPlayer().setHealth(e.getPlayer().getHealth() + 7 > e.getPlayer().getMaxHealth() ? e.getPlayer().getMaxHealth() : e.getPlayer().getHealth() + 7);
                            e.getPlayer().getItemInHand().setType(Material.BOWL);
                        }
                    } else {
                        Player p = e.getPlayer();
                        ItemStack item = e.getItem();
                        if (item.isSimilar(hotbars.itemRanked)) {
                            e.setCancelled(true);
                            p.openInventory(guis.invRanked);
                        } else if (item.isSimilar(hotbars.itemUnRanked)) {
                            e.setCancelled(true);
                            p.openInventory(guis.invUnRanked);
                        } else if (item.isSimilar(hotbars.itemEditHotbar)) {
                            e.setCancelled(true);
                            p.openInventory(guis.invChooseKit);
                        } else if (item.isSimilar(hotbars.itemLeave)) {
                            e.setCancelled(true);
                            hotbars.clickLeave(p);
                        } else if (item.isSimilar(hotbars.itemMeetup)) {
                            e.setCancelled(true);
                            meetupControl.openInvMeetup(p);
                        } else if (item.isSimilar(hotbars.itemParty)) {
                            e.setCancelled(true);
                            partyControl.openInvSelect(p);
                        } else if (item.isSimilar(hotbars.itemExtra1)) {
                            e.setCancelled(true);
                            p.chat(hotbars.commandExtra1);
                        } else if (item.isSimilar(hotbars.itemExtra2)) {
                            e.setCancelled(true);
                            p.chat(hotbars.commandExtra2);
                        } else if (hotbars.esperandoEscojaHotbar.contains(p)) {
                            e.setCancelled(true);
                            hotbars.clickPonerHotbar(p, item.getAmount());
                        } else if (partyControl.partyHash.containsKey(p)) {
                            e.setCancelled(true);
                            partyControl.partyHash.get(p).clickItemHotbar(p, e);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().getInventory().contains(hotbars.itemRanked)
                || hotbars.editingSlotHotbar.containsKey(e.getPlayer()) || e.getItemDrop().getItemStack().equals(hotbars.itemRanked)
                || partyControl.partyHash.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (extraLang.worlds.contains(p.getWorld().getName())) {
            if (Extra.isPlaying(p)) {
                Extra.cleanPlayer(p);
                hotbars.setMain(p);                                     
                playerConfig.put(p, new PlayerConfig(p));
                Extra.setScore(p, Score.TipoScore.MAIN);
                //extraLang.teleportSpawn(p);
            }
            if (!playerConfig.containsKey(p)) {
                playerConfig.put(p, new PlayerConfig(p));
            }
        } else {
            if (playerConfig.containsKey(p)) {
                playerConfig.get(p).saveStats();
                Extra.sacar(p);
            }
        }
    }

}
