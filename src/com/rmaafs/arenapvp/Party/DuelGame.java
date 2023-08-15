package com.rmaafs.arenapvp.Party;

import java.util.*;

import com.rmaafs.arenapvp.API.PartyDuelDeathEvent;
import com.rmaafs.arenapvp.API.PartyDuelFinishEvent;
import com.rmaafs.arenapvp.API.PartyDuelStartEvent;
import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.CHICKEN_EGG_POP;
import static com.rmaafs.arenapvp.util.Extra.FIREWORK_LARGE_BLAST;
import static com.rmaafs.arenapvp.util.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.util.Extra.VILLAGER_NO;
import static com.rmaafs.arenapvp.util.Extra.clang;
import static com.rmaafs.arenapvp.util.Extra.playerConfig;
import com.rmaafs.arenapvp.manager.kit.Kit;
import static com.rmaafs.arenapvp.ArenaPvP.extraLang;
import static com.rmaafs.arenapvp.ArenaPvP.hotbars;
import static com.rmaafs.arenapvp.ArenaPvP.partyControl;
import static com.rmaafs.arenapvp.ArenaPvP.plugin;
import com.rmaafs.arenapvp.entity.Map;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DuelGame {

    public enum GameType {
        DUEL, RANKED2, RANKED3, RANKED4
    }

    public Party p1, p2;
    public Kit kit;
    public Map map;
    GameType gameType = GameType.DUEL;
    public int pretime = 6, time;

    public Set<UUID> players1 = new HashSet<>();
    public Set<UUID> players2 = new HashSet<>();
    public Set<UUID> spectators = new HashSet<>();
    List<ItemStack> drops = new ArrayList<>();
    boolean damage = false;

    List<String> started, winner;
    public String startingGame;
    public String playerKilled;
    public String youkilled;
    public String playerdeath;
    public String playerdeathdisconnect;
    public String youdeath;

    public DuelGame(Party pp1, Party pp2, Kit k, Map m) {
        p1 = pp1;
        p2 = pp2;
        kit = k;
        map = m;

        time = kit.maxTime;
        players1.addAll(p1.players);
        players2.addAll(p2.players);

        startingGame = Extra.tc(clang.getString("party.game.duel.startinggame"));
        started = Extra.tCC(clang.getStringList("party.game.duel.started"));
        winner = Extra.tCC(clang.getStringList("party.game.duel.winner"));

        playerKilled = Extra.tc(clang.getString("party.game.duel.playerkilled"));
        youkilled = Extra.tc(clang.getString("party.game.youkilled"));
        playerdeath = Extra.tc(clang.getString("party.game.duel.playerdeath"));
        playerdeathdisconnect = Extra.tc(clang.getString("party.game.duel.playerdeathdisconnect"));
        youdeath = Extra.tc(clang.getString("party.game.youdeath"));

        preTeleportar();
    }

    public boolean removePretime() {
        pretime--;
        if (pretime <= 0) {
            return true;
        }
        p1.msg(startingGame.replaceAll("<time>", "" + pretime));
        p2.msg(startingGame.replaceAll("<time>", "" + pretime));
        p1.sonido(CHICKEN_EGG_POP);
        p2.sonido(CHICKEN_EGG_POP);
        if (pretime == 3) {
            preTeleportar();
        }
        return false;
    }

    boolean teleportados = false;

    public void preTeleportar() {
        PotionEffect pot = new PotionEffect(PotionEffectType.BLINDNESS, 30, 1);
        for (UUID id : p1.players) {
            Player p = Bukkit.getPlayer(id);
            if (!teleportados) {
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
            p.teleport(map.getSpawn1());
        }

        for (UUID id : p2.players) {
            Player p = Bukkit.getPlayer(id);
            if (!teleportados) {
                Extra.cleanPlayer(p);
                if (extraLang.duelEffectTeleport) {
                    p.addPotionEffect(pot);
                }
                if (kit.combo) {
                    p.setMaximumNoDamageTicks(1);
                }
                if (!kit.potionList.isEmpty()) {
                    for (PotionEffect pots : kit.potionList) {
                        p.addPotionEffect(pots);
                    }
                }
                hotbars.ponerItemsHotbar(p);
            }
            p.teleport(map.getSpawn2());
        }

        teleportados = true;
    }

    public void start() {
        preTeleportar();
        for (String s : started) {
            if (s.contains("<")) {
                msgAll(s.replaceAll("<kit>", kit.getKitName())
                        .replaceAll("<owner1>", p1.owner.getName())
                        .replaceAll("<owner2>", p2.owner.getName())
                        .replaceAll("<players1>", "" + p1.players.size())
                        .replaceAll("<players2>", "" + p2.players.size())
                        .replaceAll("<map>", map.getName())
                        .replaceAll("<time>", Extra.secToMin(time)));
            } else {
                msgAll(s);
            }
        }

        for (UUID id : p1.players) {
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
            Extra.setScore(p, Score.TipoScore.PARTYDUEL);
        }

        for (UUID id : p2.players) {
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
            Extra.setScore(p, Score.TipoScore.PARTYDUEL);
        }
        damage = true;
        Bukkit.getPluginManager().callEvent(new PartyDuelStartEvent(this));
    }

    public void morir(Player p, PlayerDeathEvent e) {
        if (players1.contains(p.getUniqueId()) || players2.contains(p.getUniqueId())) {
            p.setHealth(20);
            if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player && e.getEntity().getKiller() != p) {
                Player k = e.getEntity().getKiller();
                if (players1.contains(p.getUniqueId())) {
                    p1.msg("§c" + playerKilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getHealt(k.getHealth())));
                    p2.msg("§a" + playerKilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getHealt(k.getHealth())));
                } else {
                    p1.msg("§a" + playerKilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getHealt(k.getHealth())));
                    p2.msg("§c" + playerKilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getHealt(k.getHealth())));
                }

                p.sendMessage(youkilled.replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getHealt(k.getHealth())));
                Extra.sonido(k, ORB_PICKUP);
            } else {
                if (players1.contains(p.getUniqueId())) {
                    p1.msg("§c" + playerdeath.replaceAll("<player>", p.getName()));
                    p2.msg("§a" + playerdeath.replaceAll("<player>", p.getName()));
                } else {
                    p1.msg("§a" + playerdeath.replaceAll("<player>", p.getName()));
                    p2.msg("§c" + playerdeath.replaceAll("<player>", p.getName()));
                }
                p.sendMessage(youdeath);
            }

            Extra.sonido(p, VILLAGER_NO);

            players1.remove(p.getUniqueId());
            players2.remove(p.getUniqueId());

            spectators.add(p.getUniqueId());

            if (players1.isEmpty() || players2.isEmpty()) {
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
            Bukkit.getPluginManager().callEvent(new PartyDuelDeathEvent(this, p, partyControl.partys.get(p)));
        }
    }

    public void leave(Player p, boolean online) {
        if (players1.contains(p.getUniqueId()) || players2.contains(p.getUniqueId())) {
            if (players1.contains(p.getUniqueId())) {
                p1.msg("§c" + playerdeathdisconnect.replaceAll("<player>", p.getName()));
                p2.msg("§a" + playerdeathdisconnect.replaceAll("<player>", p.getName()));
                players1.remove(p.getUniqueId());
            } else {
                p1.msg("§a" + playerdeathdisconnect.replaceAll("<player>", p.getName()));
                p2.msg("§c" + playerdeathdisconnect.replaceAll("<player>", p.getName()));
                players2.remove(p.getUniqueId());
            }

            if (players1.isEmpty() || players2.isEmpty()) {
                partyControl.startingPartyDuel.remove(this);
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
        } else if (spectators.contains(p.getUniqueId())) {
            spectators.remove(p.getUniqueId());
            if (online) {
                mostrarPlayer(p);
                p.setFlying(false);
                extraLang.teleportSpawn(p);
                hotbars.setMain(p);
                p.spigot().setCollidesWithEntities(true);
            }
        }
    }

    private void ganador() {
        damage = false;

        Party w = p1, l = p2;
        if (players1.isEmpty()) {
            w = p2;
            l = p1;
        }

        for (String s : winner) {
            if (s.contains("<")) {
                msgAll(s
                        .replaceAll("<kit>", kit.getKitName())
                        .replaceAll("<owner1>", w.owner.getName())
                        .replaceAll("<owner2>", l.owner.getName())
                        .replaceAll("<players1>", "" + w.players.size())
                        .replaceAll("<players2>", "" + l.players.size())
                        .replaceAll("<map>", map.getName())
                        .replaceAll("<time>", Extra.secToMin(kit.maxTime - time)));
            } else {
                msgAll(s);
            }
        }

        for (UUID id : w.players) {
            Player p = Bukkit.getPlayer(id);
            Extra.cleanPlayer(p);
            p.setMaximumNoDamageTicks(20);
            p.spigot().setCollidesWithEntities(true);
            Extra.setScore(p, Score.TipoScore.PARTYMAIN);
        }
        for (UUID id : l.players) {
            Player p = Bukkit.getPlayer(id);
            Extra.cleanPlayer(p);
            p.setMaximumNoDamageTicks(20);
            p.spigot().setCollidesWithEntities(true);
            Extra.setScore(p, Score.TipoScore.PARTYMAIN);
        }

        for (UUID id : spectators) {
            Player p = Bukkit.getPlayer(id);
            mostrarPlayer(p);
        }
        for (ItemStack i : drops) {
            i.setTypeId(0);
        }
        Bukkit.getPluginManager().callEvent(new PartyDuelFinishEvent(this, w));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                removeParty(p1);
                removeParty(p2);
                Extra.terminarMapa(map, kit);
            }
        }, 20L);
    }

    public void removeParty(Party p) {
        partyControl.partysDuel.remove(p);
        for (UUID id : p.players) {
            Player o = Bukkit.getPlayer(id);
            hotbars.esperandoEscojaHotbar.remove(o);
            if (o.isOnline()) {
                extraLang.teleportSpawn(o);
                if (o == p.owner) {
                    p.setHotbar(o);
                } else {
                    hotbars.setLeave(o);
                }
            }
        }
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
        spectators.add(p.getUniqueId());
        hotbars.setLeave(p);
    }

    private void ocultarPlayer(Player p) {
        for (UUID id : players1) {
            Player o = Bukkit.getPlayer(id);
            if (o != p) {
                o.hidePlayer(p);
            }
        }
        for (UUID id : players2) {
            Player o = Bukkit.getPlayer(id);
            if (o != p) {
                o.hidePlayer(p);
            }
        }
        for (UUID id : spectators) {
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
        if (!damage) {
            e.setCancelled(true);
        } else if (e.getDamager() != null && e.getDamager() instanceof Player) {
            Player k = (Player) e.getDamager();
            Player p = (Player) e.getEntity();
            if (!k.spigot().getCollidesWithEntities()) {
                e.setCancelled(true);
            } else if (players1.contains(p) && players1.contains(k)) {
                e.setCancelled(true);
            } else if (players2.contains(p) && players2.contains(k)) {
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
        if (damage) {
            time--;
        }
    }

    public void msgAll(String s) {
        p1.msg(s);
        p2.msg(s);
    }

    public void msgSpec(String s) {
        for (UUID id : spectators) {
            Player p = Bukkit.getPlayer(id);
            p.sendMessage(s);
        }
    }

    public void sonidoAll(String s) {
        p1.sonido(s);
        p2.sonido(s);
    }
}
