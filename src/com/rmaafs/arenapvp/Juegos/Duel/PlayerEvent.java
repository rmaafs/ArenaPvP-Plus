package com.rmaafs.arenapvp.Juegos.Duel;

import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static com.rmaafs.arenapvp.util.Extra.clang;
import static com.rmaafs.arenapvp.util.Extra.jugandoUno;
import static com.rmaafs.arenapvp.util.Extra.playerConfig;
import static com.rmaafs.arenapvp.ArenaPvP.*;

import com.rmaafs.arenapvp.manager.config.PlayerConfig;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerEvent implements Listener {

    boolean teleportOnJoin;
    String youcantcraft;

    public PlayerEvent() {
        teleportOnJoin = cconfig.getBoolean("teleportonjoin");
        youcantcraft = Extra.tc(clang.getString("playing.youcantcraft"));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage("");
        Player p = e.getEntity();
        if (jugandoUno.containsKey(p)) {
            e.getDrops().clear();
            jugandoUno.get(p).finish(p);
        } else if (meetupControl.meetupsPlaying.containsKey(p)) {
            meetupControl.meetupsPlaying.get(p).morir(p, e);
        } else if (partyControl.partys.containsKey(p) && partyControl.partysEvents.containsKey(partyControl.partys.get(p))) {
            partyControl.partysEvents.get(partyControl.partys.get(p)).morir(p, e);
        } else if (partyControl.partys.containsKey(p) && partyControl.partysDuel.containsKey(partyControl.partys.get(p))) {
            partyControl.partysDuel.get(partyControl.partys.get(p)).morir(p, e);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        
        
        if (e.getDamager() instanceof Player){
            
            Player p = (Player) e.getDamager();
            if (specControl.mirando.containsKey(p)) {
                ((Player) e.getEntity()).spigot().getCollidesWithEntities();
                e.setCancelled(true);
                return;
            }
        }
        
        if (e.getEntity() instanceof Player) {
            
            Player p = (Player) e.getEntity();
            ((Player) e.getEntity()).spigot().getCollidesWithEntities();
            if (specControl.mirando.containsKey(p)) {
                e.setCancelled(true);
            } else if (jugandoUno.containsKey(p)) {
                jugandoUno.get(p).dañar(e);
            } else if (meetupControl.meetupsPlaying.containsKey(p)) {
                meetupControl.meetupsPlaying.get(p).hit(e);
            } else if (partyControl.partys.containsKey(p) && partyControl.partysEvents.containsKey(partyControl.partys.get(p))) {
                partyControl.partysEvents.get(partyControl.partys.get(p)).hit(e);
            } else if (partyControl.partys.containsKey(p) && partyControl.partysDuel.containsKey(partyControl.partys.get(p))) {
                partyControl.partysDuel.get(partyControl.partys.get(p)).hit(e);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (specControl.mirando.containsKey(p)) {
            e.setCancelled(true);
        } else if (jugandoUno.containsKey(p)) {
            e.setCancelled(jugandoUno.get(p).place(e.getBlock()));
        } else if (meetupControl.meetupsPlaying.containsKey(p)) {
            e.setCancelled(meetupControl.meetupsPlaying.get(p).place(e.getBlock()));
        } else if (partyControl.partys.containsKey(p) && partyControl.partysEvents.containsKey(partyControl.partys.get(p))) {
            e.setCancelled(partyControl.partysEvents.get(partyControl.partys.get(p)).place(e.getBlock()));
        } else if (partyControl.partys.containsKey(p) && partyControl.partysDuel.containsKey(partyControl.partys.get(p))) {
            e.setCancelled(partyControl.partysDuel.get(partyControl.partys.get(p)).place(e.getBlock()));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = (Player) e.getPlayer();
        if (!p.spigot().getCollidesWithEntities()) {
            e.setCancelled(true);
        } else {
            if (specControl.mirando.containsKey(p)) {
                e.setCancelled(true);
            } else if (jugandoUno.containsKey(p)) {
                e.setCancelled(jugandoUno.get(p).romper(e.getBlock()));
            } else if (meetupControl.meetupsPlaying.containsKey(p)) {
                e.setCancelled(meetupControl.meetupsPlaying.get(p).romper(e.getBlock()));
            } else if (partyControl.partys.containsKey(p) && partyControl.partysEvents.containsKey(partyControl.partys.get(p))) {
                e.setCancelled(partyControl.partysEvents.get(partyControl.partys.get(p)).romper(e.getBlock()));
            } else if (partyControl.partys.containsKey(p) && partyControl.partysDuel.containsKey(partyControl.partys.get(p))) {
                e.setCancelled(partyControl.partysDuel.get(partyControl.partys.get(p)).romper(e.getBlock()));
            }
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        if (e.getBucket().toString().contains("LAVA") || e.getBucket().toString().contains("WATER")) {
            Player p = e.getPlayer();
            if (specControl.mirando.containsKey(p)) {
                e.setCancelled(true);
            } else if (jugandoUno.containsKey(p)) {
                jugandoUno.get(p).setLava(e.getBlockClicked().getLocation().getBlockY());
            } else if (meetupControl.meetupsPlaying.containsKey(p)) {
                meetupControl.meetupsPlaying.get(p).setLava(e.getBlockClicked().getLocation().getBlockY());
            } else if (partyControl.partys.containsKey(p) && partyControl.partysEvents.containsKey(partyControl.partys.get(p))) {
                partyControl.partysEvents.get(partyControl.partys.get(p)).setLava(e.getBlockClicked().getLocation().getBlockY());
            } else if (partyControl.partys.containsKey(p) && partyControl.partysDuel.containsKey(partyControl.partys.get(p))) {
                partyControl.partysDuel.get(partyControl.partys.get(p)).setLava(e.getBlockClicked().getLocation().getBlockY());
            }
        }
    }

    @EventHandler
    public void onRegenerate(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player && e.getRegainReason() == RegainReason.SATIATED) {
            Player p = (Player) e.getEntity();
            if (specControl.mirando.containsKey(p)) {
                e.setCancelled(true);
            }
            try {
                if ((jugandoUno.containsKey(p) && !jugandoUno.get(p).kit.natural)
                        || (meetupControl.meetupsPlaying.containsKey(p) && !meetupControl.meetupsPlaying.get(p).kit.natural)
                        || (partyControl.partys.containsKey(p) && partyControl.partysEvents.containsKey(partyControl.partys.get(p)) && !partyControl.partysEvents.get(partyControl.partys.get(p)).kit.natural)
                        || (partyControl.partys.containsKey(p) && partyControl.partysDuel.containsKey(partyControl.partys.get(p)) && !partyControl.partysDuel.get(partyControl.partys.get(p)).kit.natural)) {
                    e.setCancelled(true);
                }
            } catch (Exception ex) {
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if (hotbars.editingSlotHotbar.containsKey(e.getPlayer()) || specControl.mirando.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        } else {
            if (e.getItem().getType() == Material.GOLDEN_APPLE) {
                ItemStack goldenHead = e.getItem();
                if (goldenHead.getItemMeta().getDisplayName() != null && goldenHead.getItemMeta().getDisplayName().equalsIgnoreCase(extraLang.goldenname)) {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (specControl.mirando.containsKey(p)) {
            e.setCancelled(true);
        }
        if (jugandoUno.containsKey(p) || meetupControl.meetupsPlaying.containsKey(p) || partyControl.partys.containsKey(p)) {
            ItemStack i = e.getItemDrop().getItemStack();
            if (i.equals(new ItemStack(Material.GLASS_BOTTLE)) || i.equals(new ItemStack(Material.BOWL))) {
                e.getItemDrop().remove();
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void craftItem(PrepareItemCraftEvent e) {
        for (HumanEntity he : e.getViewers()) {
            if (he instanceof Player) {
                Player p = (Player) he;
                if (jugandoUno.containsKey(p) || hotbars.editingSlotHotbar.containsKey(p) || meetupControl.meetupsPlaying.containsKey(p) || partyControl.partys.containsKey(p)) {
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                    p.closeInventory();
                    p.sendMessage(youcantcraft);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player p = e.getPlayer();
        if (extraLang.worlds.contains(p.getWorld().getName())) {
            Extra.cleanPlayer(p);
            hotbars.setMain(p);
            playerConfig.put(p, new PlayerConfig(p));
            Extra.setScore(p, Score.TipoScore.MAIN);
            if (teleportOnJoin) {
                extraLang.teleportSpawn(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        e.setQuitMessage("");
        if (playerConfig.containsKey(e.getPlayer())) {
            playerConfig.get(e.getPlayer()).saveStats();
            Extra.sacar(e.getPlayer());
        }
    }
}
