package com.rmaafs.arenapvp.Party;

import java.util.*;

import com.rmaafs.arenapvp.API.PartyEventFFADeathEvent;
import com.rmaafs.arenapvp.API.PartyEventFFAFinishEvent;
import com.rmaafs.arenapvp.API.PartyEventFFAStartEvent;
import com.rmaafs.arenapvp.util.Extra;
import com.rmaafs.arenapvp.manager.kit.Kit;

import static com.rmaafs.arenapvp.ArenaPvP.extraLang;
import static com.rmaafs.arenapvp.ArenaPvP.hotbars;
import static com.rmaafs.arenapvp.ArenaPvP.partyControl;
import static com.rmaafs.arenapvp.ArenaPvP.plugin;
import static com.rmaafs.arenapvp.util.Extra.*;

import com.rmaafs.arenapvp.entity.Map;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
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
    }

    Tipo tipo;
    public Kit kit;
    public Party party;
    public Map map;

    public Set<UUID> players = new HashSet<>();
    public Set<UUID> espectadores = new HashSet<>();
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
                map = Extra.getMap(k);
                if (tipo == Tipo.FFA) {
                    partyControl.startingsEvents.add(this);
                    preTeleportar();
                }
            } else {
                for (UUID id : players) {
                    Player p = Bukkit.getPlayer(id);
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
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (noTienePreSpawns) {
                Extra.cleanPlayer(p);
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
                    preLocs.put(p, map.getSpawn1());
                } else {
                    preLocs.put(p, map.getSpawn2());
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
                        .replaceAll("<map>", map.getName())
                        .replaceAll("<time>", Extra.secToMin(time))
                        .replaceAll("<players>", "" + players.size()));
            } else {
                msg(s);
            }
        }
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
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
        Bukkit.getPluginManager().callEvent(new PartyEventFFAStartEvent(party.owner, players, kit.getKitName(), map));
    }

    public void leave(Player p, boolean online) {
        if (players.contains(p.getUniqueId())) {
            msg(playerdeathdisconnect.replaceAll("<player>", p.getName()));
            players.remove(p.getUniqueId());

            if (players.size() == 1) {
                partyControl.startingsEvents.remove(this);
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
        } else if (espectadores.contains(p.getUniqueId())) {
            espectadores.remove(p.getUniqueId());
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
        if (players.contains(p.getUniqueId())) {
            p.setHealth(20);
            int mykills = 0;
            if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player && e.getEntity().getKiller() != p) {
                Player k = e.getEntity().getKiller();
                msg(playerkilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()));
                p.sendMessage(youkilled.replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getHealt(k.getHealth())));
                Extra.sonido(k, ORB_PICKUP);
            } else {
                msg(playerdeath.replaceAll("<player>", p.getName()));
                p.sendMessage(youdeath.replaceAll("<kills>", "" + mykills));
            }

            Extra.sonido(p, VILLAGER_NO);

            players.remove(p.getUniqueId());

            espectadores.add(p.getUniqueId());

            if (players.size() == 1) {
                Extra.cleanPlayer(p);
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
            Bukkit.getPluginManager().callEvent(new PartyEventFFADeathEvent(party, kit, map, espectadores, p));
        }
    }

    private void ganador() {
        daño = false;
        final Player p = Bukkit.getPlayer(players.iterator().next());
        int maxtime = kit.maxTime;
        for (String s : winner) {
            if (s.contains("<")) {
                msg(s.replaceAll("<player>", p.getName())
                        .replaceAll("<players>", "" + party.players.size())
                        .replaceAll("<time>", Extra.secToMin(maxtime - time))
                        .replaceAll("<map>", map.getName())
                        .replaceAll("<kit>", kit.getKitName())
                        .replaceAll("<owner>", party.owner.getName()));
            } else {
                msg(s);
            }
        }
        Extra.cleanPlayer(p);
        p.setMaximumNoDamageTicks(20);
        p.spigot().setCollidesWithEntities(true);
        for (UUID id: espectadores) {
            Player o = Bukkit.getPlayer(id);
            mostrarPlayer(o);
            Extra.setScore(o, Score.TipoScore.PARTYMAIN);
        }
        Extra.setScore(p, Score.TipoScore.PARTYMAIN);
        for (ItemStack i : drops) {
            i.setTypeId(0);
        }
        Bukkit.getPluginManager().callEvent(new PartyEventFFAFinishEvent(party, kit, map, espectadores, p));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            partyControl.partysEvents.remove(party);
            for (UUID id : party.players) {
                Player o = Bukkit.getPlayer(id);
                hotbars.esperandoEscojaHotbar.remove(o);
                if (o.isOnline()) {
                    extraLang.teleportSpawn(o);
                    if (o == party.owner) {
                        party.setHotbar(o);
                    } else {
                        hotbars.setLeave(o);
                    }
                }
            }
            terminarMapa(map, kit);
        }, 20L);
    }

    private void ponerSpec(final Player p) {
        Extra.cleanPlayer(p);
        if (extraLang.usespectatormode) {
            p.setGameMode(GameMode.valueOf("SPECTATOR"));
        } else {
            ocultarPlayer(p);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setGameMode(GameMode.CREATIVE);
            p.spigot().setCollidesWithEntities(false);
        }
        espectadores.add(p.getUniqueId());
        hotbars.setLeave(p);
    }

    private void ocultarPlayer(Player p) {
        for (UUID id : players) {
            Player o = Bukkit.getPlayer(id);
            if (o != p) {
                o.hidePlayer(p);
            }
        }
        for (UUID id: espectadores) {
            Player o = Bukkit.getPlayer(id);
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
                if (a.getShooter() instanceof Player && a.getShooter() != e.getEntity()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            final Player t = (Player) e.getEntity();
                            final Player dam = (Player) a.getShooter();
                            String s = extraLang.viewheal.replaceAll("<player>", t.getName()).replaceAll("<heal>", "" + Extra.getHealt(t.getHealth()));
                            dam.sendMessage(s);
                            msgSpec(s);
                        }
                    }, 1L);
                }
            }
        }
    }

    public boolean place(Block b) {
        map.poss = true;
        map.blocks.add(b);
        if (b.getLocation().getBlockY() > map.maxY) {
            map.maxY = b.getLocation().getBlockY();
        }
        if (b.getType().equals(Material.FIRE) && kit.deleteBlocks.contains(new ItemStack(Material.getMaterial(259)))) {
            return false;
        }
        for (ItemStack it : kit.deleteBlocks) {
            if (b.getType().name().equals(it.getData().getItemType().name())) {
                return false;
            }
        }
        return true;
    }

    public boolean romper(Block b) {
        if (b.getType().equals(Material.FIRE) && kit.deleteBlocks.contains(new ItemStack(Material.getMaterial(259)))) {
            return false;
        }
        for (ItemStack it : kit.deleteBlocks) {
            if (b.getType().name().equals(it.getData().getItemType().name())) {
                return false;
            }
        }
        return true;
    }

    public void setLava(int y) {
        map.lava = true;
        map.poss = true;
        if (y > map.maxY) {
            map.maxY = y;
        }
    }

    public void removerSec() {
        if (daño) {
            time--;
        }
    }

    public void msg(String s) {
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            p.sendMessage(s);
        }
        for (UUID id: espectadores) {
            Player o = Bukkit.getPlayer(id);
            o.sendMessage(s);
        }
    }

    public void msgSpec(String s) {
        for (UUID id: espectadores) {
            Player o = Bukkit.getPlayer(id);
            o.sendMessage(s);
        }
    }

    public void sonido(String s) {
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            Extra.sonido(p, s);
        }
        for (UUID id: espectadores) {
            Player o = Bukkit.getPlayer(id);
            Extra.sonido(o, s);
        }
    }
}
