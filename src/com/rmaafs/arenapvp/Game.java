package com.rmaafs.arenapvp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.rmaafs.arenapvp.Juegos.Stats.ClickPerSecond;
import com.rmaafs.arenapvp.API.DuelFinishEvent;
import com.rmaafs.arenapvp.API.DuelStartEvent;
import com.rmaafs.arenapvp.API.RankedFinishEvent;
import com.rmaafs.arenapvp.API.RankedStartEvent;
import com.rmaafs.arenapvp.API.UnRankedFinishEvent;
import com.rmaafs.arenapvp.API.UnRankedStartEvent;

import static com.rmaafs.arenapvp.Extra.BURP;
import static com.rmaafs.arenapvp.Extra.LEVEL_UP;
import static com.rmaafs.arenapvp.Extra.NOTE_STICKS;
import static com.rmaafs.arenapvp.Extra.SPLASH2;
import static com.rmaafs.arenapvp.Extra.VILLAGER_NO;
import static com.rmaafs.arenapvp.Extra.jugandoUno;
import static com.rmaafs.arenapvp.Extra.playerConfig;
import static com.rmaafs.arenapvp.Extra.preEmpezandoUno;
import static com.rmaafs.arenapvp.Main.duelControl;
import static com.rmaafs.arenapvp.Main.extraLang;
import static com.rmaafs.arenapvp.Main.guis;
import static com.rmaafs.arenapvp.Main.hotbars;
import static com.rmaafs.arenapvp.Main.plugin;
import static com.rmaafs.arenapvp.Main.ver;
import static org.bukkit.potion.PotionEffectType.BLINDNESS;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Game {

    enum GameType {
        DUEL, UNRANKED, RANKED
    }

    private boolean damage = false;
    private final HashMap<Player, List<Integer>> listClicks = new HashMap<>();
    public int hitSp1 = 0;
    public int hitSp2 = 0;
    public int preMaxP1 = 0;
    public int preMaxP2 = 0;
    public int maxHitsP1 = 0;
    public int maxHitsP2 = 0;
    public int arrowsP1 = 0;
    public int arrowsP2 = 0;
    public List<Player> spectators = new ArrayList<>();
    public Player p1, p2;
    public Kit kit;
    public Map map;
    public int bestOf = 1;
    public int winsP1 = 0;
    public int winsP2 = 0;
    public int time = 600;
    public int preTime;
    public String bestFormat = "";
    public GameType gameType = GameType.DUEL;

    public Game(Player p, Player pp, Kit k, Map m, String bestFor, int best) {
        p1 = p;
        p2 = pp;
        kit = k;
        map = m;
        bestFormat = bestFor;
        bestOf = best;
        preStart();

        if (!playerConfig.containsKey(p1)) {
            playerConfig.put(p1, new PlayerConfig(p1));
        }
        if (!playerConfig.containsKey(p2)) {
            playerConfig.put(p2, new PlayerConfig(p2));
        }
    }

    public Game(Player p, Player pp, Kit k, Map m, boolean ranked) {
        p1 = p;
        p2 = pp;
        kit = k;
        map = m;

        if (!playerConfig.containsKey(p1)) {
            playerConfig.put(p1, new PlayerConfig(p1));
        }
        if (!playerConfig.containsKey(p2)) {
            playerConfig.put(p2, new PlayerConfig(p2));
        }

        if (ranked) {
            gameType = GameType.RANKED;
            if (!p1.hasPermission("apvp.rankedfree." + k.getKitName().toLowerCase())) {
                playerConfig.get(p1).removeRanked();
            }
            playerConfig.get(p1).addPlayed(kit);
            if (!p2.hasPermission("apvp.rankedfree." + k.getKitName().toLowerCase())) {
                playerConfig.get(p2).removeRanked();
            }
            playerConfig.get(p2).addPlayed(kit);
        } else {
            gameType = GameType.UNRANKED;
            if (!p1.hasPermission("apvp.unrankedfree." + k.getKitName().toLowerCase())) {
                playerConfig.get(p1).removeUnRanked();
            }
            if (!p2.hasPermission("apvp.unrankedfree." + k.getKitName().toLowerCase())) {
                playerConfig.get(p2).removeUnRanked();
            }
        }
        preStart();
    }

    private void preStart() {
        listClicks.put(p1, new ArrayList<>());
        listClicks.put(p2, new ArrayList<>());
        preTime = extraLang.pretimematch;
        time = kit.maxTime + preTime;
        preparePlayers(p1);
        preparePlayers(p2);
    }

    private void preparePlayers(Player p) {
        p.closeInventory();
        p.setGameMode(GameMode.ADVENTURE);
        Extra.cleanPlayer(p);
        if (extraLang.duelEffectTeleport) {
            p.addPotionEffect(new PotionEffect(BLINDNESS, 30, 1));
        }
//        if (kit.combo) {
//            p.setMaximumNoDamageTicks(1);
//        }
//        if (!kit.potionList.isEmpty()) {
//            for (PotionEffect pot : kit.potionList) {
//                p.addPotionEffect(pot);
//            }
//        }
        teleportToSpawn(p);
        hotbars.ponerItemsHotbar(p);
    }

    public void teleportToSpawn(Player p) {
        if (p1 == p) {
            p1.teleport(map.getSpawn1());
        } else {
            p2.teleport(map.getSpawn2());
        }
        Extra.sonido(p, SPLASH2);
    }

    public void starting(String s) {
        msg(s);
        playSound(NOTE_STICKS);
        if (preTime == 3) {
            teleportToSpawn(p1);
            teleportToSpawn(p2);
        }
    }

    public void startGame(List<String> msg) {
        hitSp1 = 0;
        hitSp2 = 0;
        maxHitsP1 = 0;
        maxHitsP2 = 0;
        preMaxP1 = 0;
        preMaxP2 = 0;
        arrowsP1 = 0;
        arrowsP2 = 0;

        if (gameType == GameType.RANKED) {
            for (String s : duelControl.startingRanked) {
                msg(s.replaceAll("<player1>", p1.getName())
                        .replaceAll("<player2>", p2.getName())
                        .replaceAll("<elo1>", "" + playerConfig.get(p1).getElo(kit))
                        .replaceAll("<elo2>", "" + playerConfig.get(p2).getElo(kit))
                        .replaceAll("<rank1>", playerConfig.get(p1).getRank(kit))
                        .replaceAll("<rank2>", playerConfig.get(p2).getRank(kit))
                        .replaceAll("<map>", map.getName())
                        .replaceAll("<time>", Extra.secToMin(time))
                        .replaceAll("<kit>", kit.kitName));
            }
            Extra.setScore(p1, Score.TipoScore.RANKED);
            Extra.setScore(p2, Score.TipoScore.RANKED);
            Bukkit.getPluginManager().callEvent(new RankedStartEvent(p1, p2, kit.getKitName(), map));
        } else {
            for (String s : msg) {
                msg(s.replaceAll("<player1>", p1.getName())
                        .replaceAll("<player2>", p2.getName())
                        .replaceAll("<kit>", kit.kitName)
                        .replaceAll("<time>", Extra.secToMin(time))
                        .replaceAll("<format>", bestFormat)
                        .replaceAll("<map>", map.getName()));
            }
            if (gameType == GameType.UNRANKED) {
                Extra.setScore(p1, Score.TipoScore.UNRANKED);
                Extra.setScore(p2, Score.TipoScore.UNRANKED);
                Bukkit.getPluginManager().callEvent(new UnRankedStartEvent(p1, p2, kit.getKitName(), map));
            } else {
                Extra.setScore(p1, Score.TipoScore.DUEL);
                Extra.setScore(p2, Score.TipoScore.DUEL);
                Bukkit.getPluginManager().callEvent(new DuelStartEvent(p1, p2, kit.getKitName(), map, bestOf, winsP1, winsP2));
            }

        }

        playSound(BURP);
        p1.setGameMode(GameMode.SURVIVAL);
        p2.setGameMode(GameMode.SURVIVAL);
        if (hotbars.esperandoEscojaHotbar.contains(p1)) {
            try {
                playerConfig.get(p1).putInv(1, kit);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getServer().getConsoleSender().sendMessage("§4§lARENAPVP >> §cPlease send this error to @Royendero1.");
                Bukkit.getServer().getConsoleSender().sendMessage("§6§lARENAPVP >> §eplayerConfig p1 = " + playerConfig.get(p1) + ", kit = " + kit);
                p1.getInventory().setArmorContents(kit.armor);
                p1.getInventory().setContents(kit.hotbar);
            }
            hotbars.esperandoEscojaHotbar.remove(p1);
        }
        if (hotbars.esperandoEscojaHotbar.contains(p2)) {
            try {
                playerConfig.get(p2).putInv(1, kit);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getServer().getConsoleSender().sendMessage("§4§lARENAPVP >> §cPlease send this error to @Royendero1.");
                Bukkit.getServer().getConsoleSender().sendMessage("§6§lARENAPVP >> §eplayerConfig p2 = " + playerConfig.get(p2) + ", kit = " + kit);
                p2.getInventory().setArmorContents(kit.armor);
                p2.getInventory().setContents(kit.hotbar);
            }
            hotbars.esperandoEscojaHotbar.remove(p2);
        }

        if (kit.combo) {
            p1.setMaximumNoDamageTicks(1);
            p2.setMaximumNoDamageTicks(1);
        }
        if (!kit.potionList.isEmpty()) {
            for (PotionEffect pot : kit.potionList) {
                p1.addPotionEffect(pot);
                p2.addPotionEffect(pot);
            }
        }

        damage = true;

        teleportToSpawn(p1);
        teleportToSpawn(p2);
    }

    public void finish(final Player l) {
        damage = false;
        Player f = p2;
        if (p2 == l) {
            f = p1;
            winsP1++;
        } else {
            winsP2++;
        }
        final Player w = f;

        int maxcs1 = 0, maxcs2 = 0;
        float promcs1 = 5, promcs2 = 5;

        for (int i : listClicks.get(w)) {
            if (i > maxcs1) {
                maxcs1 = i;
            }
            promcs1 = promcs1 + (i / listClicks.get(w).size());
        }

        for (int i : listClicks.get(l)) {
            if (i > maxcs2) {
                maxcs2 = i;
            }
            promcs2 = promcs2 + (i / listClicks.get(l).size());
        }

        if (preMaxP1 > maxHitsP1) {
            maxHitsP1 = preMaxP1;
        }
        if (preMaxP2 > maxHitsP2) {
            maxHitsP2 = preMaxP2;
        }

        int h1 = hitSp1, h2 = hitSp2, m1 = maxHitsP1, m2 = maxHitsP2;
        if (w == p2) {
            h1 = hitSp2;
            h2 = hitSp1;
            m1 = maxHitsP2;
            m2 = maxHitsP1;
        }

        int f1 = arrowsP1, f2 = arrowsP2;
        if (w == p2) {
            f1 = arrowsP2;
            f2 = arrowsP1;
        }

        for (String s : duelControl.winStats) {
            if (s.contains("<")) {
                msg(s.replaceAll("<winner>", w.getName())
                        .replaceAll("<loser>", l.getName())
                        .replaceAll("<time>", Extra.secToMin(kit.maxTime - time))
                        .replaceAll("<maxcswinner>", "" + maxcs1)
                        .replaceAll("<maxcsloser>", "" + maxcs2)
                        .replaceAll("<promcswinner>", "" + (int) promcs1)
                        .replaceAll("<promcsloser>", "" + (int) promcs2)
                        .replaceAll("<hitswinner>", "" + h2)
                        .replaceAll("<hitsloser>", "" + h1)
                        .replaceAll("<combowinner>", "" + m2)
                        .replaceAll("<comboloser>", "" + m1)
                        .replaceAll("<arrowswinner>", "" + f1)
                        .replaceAll("<arrowsloser>", "" + f2)
                        .replaceAll("<pingwinner>", "" + ver.verPing(w))
                        .replaceAll("<pingloser>", "" + ver.verPing(l)));
            } else {
                msg(s);
            }
        }

        msg(duelControl.win.replaceAll("<winner>", w.getName())
                .replaceAll("<loser>", l.getName())
                .replaceAll("<health>", "" + Extra.getHealt(w.getHealth()))
                .replaceAll("<kit>", kit.kitName));

        ponerLastInventory(w);
        ponerLastInventory(l);

        Extra.text(w, duelControl.msgLastInv, duelControl.hoverLastInv.replaceAll("<player>", l.getName()), "/uinventario " + l.getName(), "AQUA");
        Extra.text(l, duelControl.msgLastInv, duelControl.hoverLastInv.replaceAll("<player>", w.getName()), "/uinventario " + w.getName(), "AQUA");

        Extra.cleanPlayer(w);
        Extra.cleanPlayer(l);

        if (extraLang.duelEffectDeathBlindness) {
            l.addPotionEffect(new PotionEffect(BLINDNESS, 30, 1));
        }
        if (extraLang.duelEffectDeathSlow) {
            l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 5));
        }
        if (extraLang.duelEffectDeathInvi) {
            l.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 1));
            w.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 1));
        }
        playSound(LEVEL_UP);

        Extra.preEmpezandoUno.remove(jugandoUno.get(w));
        hotbars.esperandoEscojaHotbar.remove(w);
        hotbars.esperandoEscojaHotbar.remove(l);

        if (gameType == GameType.RANKED) {
            int elo = Extra.getDiferenciaElo(playerConfig.get(w).getElo(kit) - playerConfig.get(l).getElo(kit));

            playerConfig.get(w).addElo(kit, elo);
            playerConfig.get(l).removeElo(kit, elo);

            playerConfig.get(w).addWins(kit);

            msg(duelControl.rankedFinish.replaceAll("<winner>", w.getName())
                    .replaceAll("<loser>", l.getName())
                    .replaceAll("<elo1>", "" + playerConfig.get(w).getElo(kit))
                    .replaceAll("<elo2>", "" + playerConfig.get(l).getElo(kit))
                    .replaceAll("<elo>", "" + elo));
            Extra.sonido(w, LEVEL_UP);
            Extra.sonido(l, VILLAGER_NO);
            Bukkit.getPluginManager().callEvent(new RankedFinishEvent(w, l, kit.getKitName(), map, elo));
        } else if (gameType == GameType.UNRANKED) {
            Bukkit.getPluginManager().callEvent(new UnRankedFinishEvent(w, l, kit.getKitName(), map));
        } else {
            if (w == p1) {
                Bukkit.getPluginManager().callEvent(new DuelFinishEvent(w, l, kit.getKitName(), map, bestOf, winsP1, winsP2));
            } else {
                Bukkit.getPluginManager().callEvent(new DuelFinishEvent(w, l, kit.getKitName(), map, bestOf, winsP2, winsP1));
            }
        }

        p1.setMaximumNoDamageTicks(20);
        p1.spigot().setCollidesWithEntities(true);
        p2.setMaximumNoDamageTicks(20);
        p2.spigot().setCollidesWithEntities(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                if ((bestOf != 1 && w.isOnline() && l.isOnline())
                        && ((bestOf == 3 && (winsP1 != 2 && winsP2 != 2))
                        || (bestOf == 5 && (winsP1 != 3 && winsP2 != 3)))) {
                    preEmpezandoUno.add(jugandoUno.get(w));
                    map.regen(kit);
                    preStart();
                } else {
                    sacarPartida(w);
                    sacarPartida(l);
                    if (bestOf != 1) {
                        int points1 = winsP1;
                        int points2 = winsP2;
                        if (w == p2) {
                            points1 = winsP2;
                            points2 = winsP1;
                        }
                        msg(duelControl.won.replaceAll("<winner>", w.getName())
                                .replaceAll("<loser>", l.getName())
                                .replaceAll("<points1>", "" + points1)
                                .replaceAll("<points2>", "" + points2));
                    }
                    Extra.terminarMapa(map, kit);
                    if (gameType == GameType.RANKED) {
                        guis.setNumberRankedPlaying(kit, false);
                        SQL.guardarStats(w, false);
                        SQL.guardarStats(l, false);
                    } else if (gameType == GameType.UNRANKED) {
                        guis.setNumberUnRankedPlaying(kit, false);
                    }
                }
            }
        }, 20L);
    }

    private void sacarPartida(Player p) {
        if (p.isOnline()) {
            extraLang.teleportSpawn(p);
            hotbars.setMain(p);
            p.setFireTicks(0);
            p.setMaximumNoDamageTicks(20);
            Extra.setScore(p, Score.TipoScore.MAIN);
        }
        jugandoUno.remove(p);
    }

    private void ponerLastInventory(Player p) {
        List<ItemStack> list = new ArrayList<>();
        list.addAll(Arrays.asList(p.getInventory().getContents()));
        list.addAll(Arrays.asList(p.getInventory().getArmorContents()));
        duelControl.ultimosInv.put(p.getName(), list.toArray(new ItemStack[list.size()]));
    }

    public void msg(String s) {
        p1.sendMessage(s);
        p2.sendMessage(s);
    }

    public void playSound(String s) {
        Extra.sonido(p1, s);
        Extra.sonido(p2, s);
    }

    public void msgSpec(String s) {
        for (Player p : spectators) {
            p.sendMessage(s);
        }
    }

    public void removerSec() {
        time--;
        if (damage) {
            if (ClickPerSecond.cooldown.containsKey(p1)) {
                listClicks.get(p1).add(ClickPerSecond.cooldown.get(p1));
                ClickPerSecond.cooldown.remove(p1);
            }
            if (ClickPerSecond.cooldown.containsKey(p2)) {
                listClicks.get(p2).add(ClickPerSecond.cooldown.get(p2));
                ClickPerSecond.cooldown.remove(p2);
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

    public void dañar(final EntityDamageByEntityEvent e) {
        if (!damage) {
            e.setCancelled(true);
        } else {
            if (e.getDamager() instanceof Player) {
                if (e.getEntity() == p1) {
                    hitSp1++;
                    preMaxP1++;
                    if (preMaxP2 > maxHitsP2) {
                        maxHitsP2 = preMaxP2;
                    }
                    preMaxP2 = 0;
                } else {
                    hitSp2++;
                    preMaxP2++;
                    if (preMaxP1 > maxHitsP1) {
                        maxHitsP1 = preMaxP1;
                    }
                    preMaxP1 = 0;
                }
            } else if (e.getDamager() instanceof Arrow) {
                final Arrow a = (Arrow) e.getDamager();
                if (a.getShooter() instanceof Player && a.getShooter() != e.getEntity()) {
                    Player p = (Player) a.getShooter();
                    if (p == p1) {
                        arrowsP1++;
                    } else {
                        arrowsP2++;
                    }
                    if (extraLang.showhealwitharrow) {
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

    public int getCs(Player p) {
        if (p == p1) {
            if (ClickPerSecond.cooldown.containsKey(p1)) {
                return ClickPerSecond.cooldown.get(p1);
            }
        } else {
            if (ClickPerSecond.cooldown.containsKey(p2)) {
                return ClickPerSecond.cooldown.get(p2);
            }
        }
        return 0;
    }
}
