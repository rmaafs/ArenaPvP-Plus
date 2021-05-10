package com.rmaafs.arenapvp.Party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.rmaafs.arenapvp.API.PartyEventFFADeathEvent;
import com.rmaafs.arenapvp.API.PartyEventFFAFinishEvent;
import com.rmaafs.arenapvp.API.PartyEventFFAStartEvent;
import com.rmaafs.arenapvp.Extra;
import static com.rmaafs.arenapvp.Extra.CHICKEN_EGG_POP;
import static com.rmaafs.arenapvp.Extra.FIREWORK_LARGE_BLAST;
import static com.rmaafs.arenapvp.Extra.NOTE_BASS;
import static com.rmaafs.arenapvp.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.Extra.VILLAGER_NO;
import static com.rmaafs.arenapvp.Extra.clang;
import static com.rmaafs.arenapvp.Extra.playerConfig;
import com.rmaafs.arenapvp.Kit;
import static com.rmaafs.arenapvp.Main.extraLang;
import static com.rmaafs.arenapvp.Main.hotbars;
import static com.rmaafs.arenapvp.Main.partyControl;
import static com.rmaafs.arenapvp.Main.plugin;
import com.rmaafs.arenapvp.Mapa;
import com.rmaafs.arenapvp.Score;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventGame {
    
    public enum Tipo {
        
        FFA, GROUP, REDROVER
    };
    
    Tipo tipo;
    public Kit kit;
    public Party party;
    public Mapa mapa;
    
    public List<Player> players = new ArrayList<>();
    public List<Player> espectadores = new ArrayList<>();
    public int pretime, time;
    boolean daño = false;
    
    HashMap<Player, Location> preLocs = new HashMap<>();
    List<ItemStack> drops = new ArrayList<>();
    
    String staringgame;
    List<String> startedFFA, winner;
    
    String playerkilled, youkilled, playerdeath, playerdeathdisconnect, youdeath;
    
    public EventGame(Tipo t, Player p) {
        tipo = t;
        party = partyControl.partys.get(p);
        
        playerkilled = Extra.tc(clang.getString("party.game.ffa.playerkilled"));
        youkilled = Extra.tc(clang.getString("party.game.youkilled"));
        playerdeath = Extra.tc(clang.getString("party.game.playerdeath"));
        playerdeathdisconnect = Extra.tc(clang.getString("party.game.playerdeathdisconnect"));
        youdeath = Extra.tc(clang.getString("party.game.youdeath"));
        winner = Extra.tCC(clang.getStringList("party.game.ffa.winner"));
    }
    
    public boolean removePretime() {
        pretime--;
        if (pretime <= 0) {
            return true;
        }
        msg(staringgame.replaceAll("<time>", "" + pretime));
        sonido(CHICKEN_EGG_POP);
        if (pretime == 3) {
            preTeleportar();
        }
        return false;
    }
    
    public void preStartGame(Kit k) {
        if ((party.owner.hasPermission("apvp.party.event.ffa." + k.getKitName().toLowerCase())) || (party.owner.hasPermission("apvp.party.event.ffa.*") && party.owner.hasPermission("apvp.party.event.ffa." + k.getKitName().toLowerCase()))) {
            kit = k;
            players.addAll(party.players);
            
            staringgame = Extra.tc(clang.getString("party.game.startinggame"));
            startedFFA = Extra.tCC(clang.getStringList("party.game.ffa.started"));
            time = kit.maxTime;
            
            pretime = 6;
            if (Extra.checkMapAvailables(k)) {
                mapa = Extra.getMap(k);
                if (tipo == Tipo.FFA) {
                    partyControl.startingsEvents.add(this);
                    preTeleportar();
                }
            } else {
                for (Player p : players) {
                    p.sendMessage(extraLang.noMapsAvailable);
                    Extra.sonido(p, NOTE_BASS);
                }
            }
        } else {
            party.owner.sendMessage("§cNo permission.");
        }
    }
    
    public void preTeleportar() {
        boolean noTienePreSpawns = preLocs.isEmpty();
        boolean spawn1 = false;
        
        PotionEffect pot = new PotionEffect(PotionEffectType.BLINDNESS, 30, 1);
        for (Player p : players) {
            if (noTienePreSpawns) {
                Extra.limpiarP(p);
                if (extraLang.duelEffectTeleport) {
                    p.addPotionEffect(pot);
                }
//                if (kit.combo) {
//                    p.setMaximumNoDamageTicks(1);
//                }
//                if (!kit.potionList.isEmpty()) {
//                    for (PotionEffect pots : kit.potionList) {
//                        p.addPotionEffect(pots);
//                    }
//                }
                hotbars.ponerItemsHotbar(p);
            }
            
            if (noTienePreSpawns) {
                if (spawn1) {
                    preLocs.put(p, mapa.getSpawn1());
                } else {
                    preLocs.put(p, mapa.getSpawn2());
                }
                spawn1 = !spawn1;
            }
            p.teleport(preLocs.get(p));
        }
    }
    
    public void start() {
        preTeleportar();
        preLocs.clear();
//        lore.clear();
//        accion = Accion.PLAYING;
//        totalplayers = players.size();
        for (String s : startedFFA) {
            if (s.contains("<")) {
                msg(s.replaceAll("<kit>", kit.getKitName())
                        .replaceAll("<owner>", party.owner.getName())
                        .replaceAll("<map>", mapa.getName())
                        .replaceAll("<time>", Extra.secToMin(time))
                        .replaceAll("<players>", "" + players.size()));
            } else {
                msg(s);
            }
        }
        for (Player p : players) {
            if (hotbars.esperandoEscojaHotbar.contains(p)) {
                playerConfig.get(p).putInv(1, kit);
                hotbars.esperandoEscojaHotbar.remove(p);
            }
            p.setGameMode(GameMode.SURVIVAL);
            Extra.sonido(p, FIREWORK_LARGE_BLAST);
            
            if (kit.combo) {
                p.setMaximumNoDamageTicks(1);
            }
            if (!kit.potionList.isEmpty()) {
                for (PotionEffect pots : kit.potionList) {
                    p.addPotionEffect(pots);
                }
            }
            Extra.setScore(p, Score.TipoScore.PARTYEVENT);
        }
        daño = true;
        Bukkit.getPluginManager().callEvent(new PartyEventFFAStartEvent(party.owner, players, kit.getKitName(), mapa));
    }
    
    public void leave(Player p, boolean online) {
        if (players.contains(p)) {
            msg(playerdeathdisconnect.replaceAll("<player>", p.getName()));
            players.remove(p);
            
            if (players.size() == 1) {
                if (partyControl.startingsEvents.contains(this)) {
                    partyControl.startingsEvents.remove(this);
                }
                ganador();
            } else {
                for (ItemStack i : p.getInventory().getContents()) {
                    if (i != null && i.getTypeId() != 0) {
                        p.getWorld().dropItemNaturally(p.getLocation(), i);
                    }
                }
                for (ItemStack i : p.getInventory().getArmorContents()) {
                    if (i != null && i.getTypeId() != 0) {
                        p.getWorld().dropItemNaturally(p.getLocation(), i);
                    }
                }
            }
            if (online) {
                p.setFlying(false);
                p.spigot().setCollidesWithEntities(true);
            }
        } else if (espectadores.contains(p)) {
            espectadores.remove(p);
            if (online) {
                mostrarPlayer(p);
                p.setFlying(false);
                extraLang.teleportSpawn(p);
                hotbars.setMain(p);
                p.spigot().setCollidesWithEntities(true);
            }
        }
    }
    
    public void morir(Player p, PlayerDeathEvent e) {
        if (players.contains(p)) {
            p.setHealth(20);
            int mykills = 0;
            if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player && e.getEntity().getKiller() != p) {
                Player k = e.getEntity().getKiller();
                msg(playerkilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()));
                p.sendMessage(youkilled.replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getSangre(k.getHealth())));
                Extra.sonido(k, ORB_PICKUP);
            } else {
                msg(playerdeath.replaceAll("<player>", p.getName()));
                p.sendMessage(youdeath.replaceAll("<kills>", "" + mykills));
            }
            
            Extra.sonido(p, VILLAGER_NO);
            
            if (players.contains(p)) {
                players.remove(p);
            }
            
            espectadores.add(p);
            
            if (players.size() == 1) {
                Extra.limpiarP(p);
                p.setGameMode(GameMode.ADVENTURE);
                e.getDrops().clear();
                ganador();
            } else {
                ponerSpec(p);
            }
            if (extraLang.duelEffectDeathBlindness) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
            }
            if (extraLang.duelEffectDeathSlow) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 5));
            }
            drops.addAll(e.getDrops());
            Bukkit.getPluginManager().callEvent(new PartyEventFFADeathEvent(party, kit, mapa, espectadores, p));
        }
    }
    
    private void ganador() {
        daño = false;
        final Player p = players.get(0);
        int maxtime = kit.maxTime;
        for (String s : winner) {
            if (s.contains("<")) {
                msg(s.replaceAll("<player>", p.getName())
                        .replaceAll("<players>", "" + party.players.size())
                        .replaceAll("<time>", Extra.secToMin(maxtime - time))
                        .replaceAll("<map>", mapa.getName())
                        .replaceAll("<kit>", kit.getKitName())
                        .replaceAll("<owner>", party.owner.getName()));
            } else {
                msg(s);
            }
        }
        Extra.limpiarP(p);
        p.setMaximumNoDamageTicks(20);
        p.spigot().setCollidesWithEntities(true);
        for (Player o : espectadores) {
            mostrarPlayer(o);
            Extra.setScore(o, Score.TipoScore.PARTYMAIN);
        }
        Extra.setScore(p, Score.TipoScore.PARTYMAIN);
        for (ItemStack i : drops) {
            i.setTypeId(0);
        }
        Bukkit.getPluginManager().callEvent(new PartyEventFFAFinishEvent(party, kit, mapa, espectadores, p));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                partyControl.partysEvents.remove(party);
                for (Player o : party.players) {
                    if (hotbars.esperandoEscojaHotbar.contains(o)) {
                        hotbars.esperandoEscojaHotbar.remove(o);
                    }
                    if (o.isOnline()) {
                        extraLang.teleportSpawn(o);
                        if (o == party.owner) {
                            party.setHotbar(o);
                        } else {
                            hotbars.setLeave(o);
                        }
                    }
                }
                Extra.terminarMapa(mapa, kit);
            }
        }, 20L);
    }
    
    private void ponerSpec(final Player p) {
        Extra.limpiarP(p);
        if (extraLang.usespectatormode) {
            p.setGameMode(GameMode.valueOf("SPECTATOR"));
        } else {
            ocultarPlayer(p);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setGameMode(GameMode.CREATIVE);
            p.spigot().setCollidesWithEntities(false);
        }
        if (!espectadores.contains(p)) {
            espectadores.add(p);
        }
        hotbars.setLeave(p);
    }
    
    private void ocultarPlayer(Player p) {
        for (Player o : players) {
            if (o != p) {
                o.hidePlayer(p);
            }
        }
        for (Player o : espectadores) {
            if (o != p) {
                o.hidePlayer(p);
            }
        }
    }
    
    private void mostrarPlayer(Player p) {
        p.setMaximumNoDamageTicks(20);
        p.spigot().setCollidesWithEntities(true);
        for (Player o : Bukkit.getServer().getOnlinePlayers()) {
            if (o != p) {
                o.showPlayer(p);
            }
        }
    }
    
    public void hit(final EntityDamageByEntityEvent e) {
        if (!daño || (e.getDamager() != null && e.getDamager() instanceof Player && !((Player) e.getDamager()).spigot().getCollidesWithEntities())) {
            e.setCancelled(true);
        }
        if (extraLang.showhealwitharrow) {
            if (e.getDamager() instanceof Arrow) {
                final Arrow a = (Arrow) e.getDamager();
                if (a.getShooter() instanceof Player && ((Player) a.getShooter()) != ((Player) e.getEntity())) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            final Player t = (Player) e.getEntity();
                            final Player dam = (Player) a.getShooter();
                            String s = extraLang.viewheal.replaceAll("<player>", t.getName()).replaceAll("<heal>", "" + Extra.getSangre(t.getHealth()));
                            dam.sendMessage(s);
                            msgSpec(s);
                        }
                    }, 1L);
                }
            }
        }
    }
    
    public boolean place(Block b) {
        mapa.puesto = true;
        mapa.bloques.add(b);
        if (b.getLocation().getBlockY() > mapa.maxY) {
            mapa.maxY = b.getLocation().getBlockY();
        }
        if (b.getType().equals(Material.FIRE) && kit.deleteBlocks.contains(new ItemStack(259))) {
            return false;
        }
        return !kit.deleteBlocks.contains(new ItemStack(b.getTypeId(), 1, (short) b.getData()));
    }
    
    public boolean romper(Block b) {
        if (b.getType().equals(Material.FIRE) && kit.deleteBlocks.contains(new ItemStack(259))) {
            return false;
        }
        return !kit.deleteBlocks.contains(new ItemStack(b.getTypeId(), 1, (short) b.getData()));
    }
    
    public void setLava(int y) {
        mapa.lava = true;
        mapa.puesto = true;
        if (y > mapa.maxY) {
            mapa.maxY = y;
        }
    }
    
    public void removerSec() {
        if (daño) {
            time--;
        }
    }
    
    public void msg(String s) {
        for (Player p : players) {
            p.sendMessage(s);
        }
        for (Player p : espectadores) {
            p.sendMessage(s);
        }
    }
    
    public void msgSpec(String s) {
        for (Player p : espectadores) {
            p.sendMessage(s);
        }
    }
    
    public void sonido(String s) {
        for (Player p : players) {
            Extra.sonido(p, s);
        }
        for (Player p : espectadores) {
            Extra.sonido(p, s);
        }
    }
}
