package com.rmaafs.arenapvp.Party;

import java.util.ArrayList;
import java.util.List;
import com.rmaafs.arenapvp.API.PartyDuelDeathEvent;
import com.rmaafs.arenapvp.API.PartyDuelFinishEvent;
import com.rmaafs.arenapvp.API.PartyDuelStartEvent;
import com.rmaafs.arenapvp.Extra;
import static com.rmaafs.arenapvp.Extra.CHICKEN_EGG_POP;
import static com.rmaafs.arenapvp.Extra.FIREWORK_LARGE_BLAST;
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

    public enum Tipo {

        DUEL, RANKED2, RANKED3, RANKED4
    };

    public Party p1, p2;
    public Kit kit;
    public Mapa mapa;
    Tipo tipo = Tipo.DUEL;
    public int pretime = 6, time;

    public List<Player> players1 = new ArrayList<>();
    public List<Player> players2 = new ArrayList<>();
    public List<Player> espectadores = new ArrayList<>();
    List<ItemStack> drops = new ArrayList<>();
    boolean daño = false;

    List<String> started, winner;
    String startinggame, playerkilled, youkilled, playerdeath, playerdeathdisconnect,
            youdeath;

    public DuelGame(Party pp1, Party pp2, Kit k, Mapa m) {
        p1 = pp1;
        p2 = pp2;
        kit = k;
        mapa = m;

        time = kit.maxTime;
        players1.addAll(p1.players);
        players2.addAll(p2.players);

        startinggame = Extra.tc(clang.getString("party.game.duel.startinggame"));
        started = Extra.tCC(clang.getStringList("party.game.duel.started"));
        winner = Extra.tCC(clang.getStringList("party.game.duel.winner"));

        playerkilled = Extra.tc(clang.getString("party.game.duel.playerkilled"));
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
        p1.msg(startinggame.replaceAll("<time>", "" + pretime));
        p2.msg(startinggame.replaceAll("<time>", "" + pretime));
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
        for (Player p : p1.players) {
            if (!teleportados) {
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
            p.teleport(mapa.getSpawn1());
        }

        for (Player p : p2.players) {
            if (!teleportados) {
                Extra.limpiarP(p);
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
            p.teleport(mapa.getSpawn2());
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
                        .replaceAll("<map>", mapa.getName())
                        .replaceAll("<time>", Extra.secToMin(time)));
            } else {
                msgAll(s);
            }
        }

        for (Player p : p1.players) {
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

        for (Player p : p2.players) {
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
        daño = true;
        Bukkit.getPluginManager().callEvent(new PartyDuelStartEvent(this));
    }

    public void morir(Player p, PlayerDeathEvent e) {
        if (players1.contains(p) || players2.contains(p)) {
            p.setHealth(20);
            if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player && e.getEntity().getKiller() != p) {
                Player k = e.getEntity().getKiller();
                if (players1.contains(p)) {
                    p1.msg("§c" + playerkilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getSangre(k.getHealth())));
                    p2.msg("§a" + playerkilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getSangre(k.getHealth())));
                } else {
                    p1.msg("§a" + playerkilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getSangre(k.getHealth())));
                    p2.msg("§c" + playerkilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getSangre(k.getHealth())));
                }

                p.sendMessage(youkilled.replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getSangre(k.getHealth())));
                Extra.sonido(k, ORB_PICKUP);
            } else {
                if (players1.contains(p)) {
                    p1.msg("§c" + playerdeath.replaceAll("<player>", p.getName()));
                    p2.msg("§a" + playerdeath.replaceAll("<player>", p.getName()));
                } else {
                    p1.msg("§a" + playerdeath.replaceAll("<player>", p.getName()));
                    p2.msg("§c" + playerdeath.replaceAll("<player>", p.getName()));
                }
                p.sendMessage(youdeath);
            }

            Extra.sonido(p, VILLAGER_NO);

            if (players1.contains(p)) {
                players1.remove(p);
            }
            if (players2.contains(p)) {
                players2.remove(p);
            }

            espectadores.add(p);

            if (players1.isEmpty() || players2.isEmpty()) {
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
            Bukkit.getPluginManager().callEvent(new PartyDuelDeathEvent(this, p, partyControl.partys.get(p)));
        }
    }

    public void leave(Player p, boolean online) {
        if (players1.contains(p) || players2.contains(p)) {
            if (players1.contains(p)) {
                p1.msg("§c" + playerdeathdisconnect.replaceAll("<player>", p.getName()));
                p2.msg("§a" + playerdeathdisconnect.replaceAll("<player>", p.getName()));
                players1.remove(p);
            } else {
                p1.msg("§a" + playerdeathdisconnect.replaceAll("<player>", p.getName()));
                p2.msg("§c" + playerdeathdisconnect.replaceAll("<player>", p.getName()));
                players2.remove(p);
            }

            if (players1.isEmpty() || players2.isEmpty()) {
                if (partyControl.startingPartyDuel.contains(this)) {
                    partyControl.startingPartyDuel.remove(this);
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

    private void ganador() {
        daño = false;

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
                        .replaceAll("<map>", mapa.getName())
                        .replaceAll("<time>", Extra.secToMin(kit.maxTime - time)));
            } else {
                msgAll(s);
            }
        }

        for (Player p : w.players) {
            Extra.limpiarP(p);
            p.setMaximumNoDamageTicks(20);
            p.spigot().setCollidesWithEntities(true);
            Extra.setScore(p, Score.TipoScore.PARTYMAIN);
        }
        for (Player p : l.players) {
            Extra.limpiarP(p);
            p.setMaximumNoDamageTicks(20);
            p.spigot().setCollidesWithEntities(true);
            Extra.setScore(p, Score.TipoScore.PARTYMAIN);
        }

        for (Player o : espectadores) {
            mostrarPlayer(o);
        }
        for (ItemStack i : drops) {
            i.setTypeId(0);
        }
        Bukkit.getPluginManager().callEvent(new PartyDuelFinishEvent(this, w));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                removeParty(p1);
                removeParty(p2);
                Extra.terminarMapa(mapa, kit);
            }
        }, 20L);
    }

    public void removeParty(Party p) {
        partyControl.partysDuel.remove(p);
        for (Player o : p.players) {
            if (hotbars.esperandoEscojaHotbar.contains(o)) {
                hotbars.esperandoEscojaHotbar.remove(o);
            }
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
        espectadores.add(p);
        hotbars.setLeave(p);
    }

    private void ocultarPlayer(Player p) {
        for (Player o : players1) {
            if (o != p) {
                o.hidePlayer(p);
            }
        }
        for (Player o : players2) {
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
        if (!daño) {
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
    }

    public boolean place(Block b) {
        mapa.puesto = true;
        mapa.bloques.add(b);
        if (b.getLocation().getBlockY() > mapa.maxY) {
            mapa.maxY = b.getLocation().getBlockY();
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

    public void msgAll(String s) {
        p1.msg(s);
        p2.msg(s);
    }

    public void msgSpec(String s) {
        for (Player p : espectadores) {
            p.sendMessage(s);
        }
    }

    public void sonidoAll(String s) {
        p1.sonido(s);
        p2.sonido(s);
    }
}
