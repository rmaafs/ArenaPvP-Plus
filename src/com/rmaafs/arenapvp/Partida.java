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

public class Partida {

    enum Estilo {

        DUEL, UNRANKED, RANKED
    };

    boolean daño = false;

    HashMap<Player, List<Integer>> listClicks = new HashMap<>();
    int hitsp1 = 0, hitsp2 = 0, premaxp1 = 0, premaxp2 = 0, maxhitsp1 = 0, maxhitsp2 = 0;
    int flechasp1 = 0, flechasp2 = 0;

    List<Player> spectators = new ArrayList<>();
    Player p1, p2;
    public Kit kit;
    Mapa mapa;
    int bestOf = 1;
    int winsp1 = 0, winsp2 = 0;
    int time = 600, pretime;
    String bestFormat = "";
    Estilo estilo = Estilo.DUEL;

    public Partida(Player p, Player pp, Kit k, Mapa m, String bestFor, int best) {
        p1 = p;
        p2 = pp;
        kit = k;
        mapa = m;
        bestFormat = bestFor;
        bestOf = best;
        preEmpezar();
        
        if (!playerConfig.containsKey(p1)){
            playerConfig.put(p1, new PlayerConfig(p1));
        }
        if (!playerConfig.containsKey(p2)){
            playerConfig.put(p2, new PlayerConfig(p2));
        }
    }

    public Partida(Player p, Player pp, Kit k, Mapa m, boolean ranked) {
        p1 = p;
        p2 = pp;
        kit = k;
        mapa = m;
        
        if (!playerConfig.containsKey(p1)){
            playerConfig.put(p1, new PlayerConfig(p1));
        }
        if (!playerConfig.containsKey(p2)){
            playerConfig.put(p2, new PlayerConfig(p2));
        }
        
        if (ranked) {
            estilo = Estilo.RANKED;
            if (!p1.hasPermission("apvp.rankedfree." + k.getKitName().toLowerCase())) {
                playerConfig.get(p1).removeRanked();
            }
            playerConfig.get(p1).addPlayed(kit);
            if (!p2.hasPermission("apvp.rankedfree." + k.getKitName().toLowerCase())) {
                playerConfig.get(p2).removeRanked();
            }
            playerConfig.get(p2).addPlayed(kit);
        } else {
            estilo = Estilo.UNRANKED;
            if (!p1.hasPermission("apvp.unrankedfree." + k.getKitName().toLowerCase())) {
                playerConfig.get(p1).removeUnRanked();
            }
            if (!p2.hasPermission("apvp.unrankedfree." + k.getKitName().toLowerCase())) {
                playerConfig.get(p2).removeUnRanked();
            }
        }
        preEmpezar();
    }

    private void preEmpezar() {
        List<Integer> lista1 = new ArrayList<>();
        List<Integer> lista2 = new ArrayList<>();
        listClicks.put(p1, lista1);
        listClicks.put(p2, lista2);
        pretime = extraLang.pretimematch;
        time = kit.maxTime + pretime;
        preparar(p1);
        preparar(p2);
    }

    private void preparar(Player p) {
        p.closeInventory();
        p.setGameMode(GameMode.ADVENTURE);
        Extra.limpiarP(p);
        if (extraLang.duelEffectTeleport) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
        }
//        if (kit.combo) {
//            p.setMaximumNoDamageTicks(1);
//        }
//        if (!kit.potionList.isEmpty()) {
//            for (PotionEffect pot : kit.potionList) {
//                p.addPotionEffect(pot);
//            }
//        }
        mandarSpawn(p);
        hotbars.ponerItemsHotbar(p);
    }

    public void mandarSpawn(Player p) {
        if (p1 == p) {
            p1.teleport(mapa.getSpawn1());
        } else {
            p2.teleport(mapa.getSpawn2());
        }
        Extra.sonido(p, SPLASH2);
    }

    public void starting(String s) {
        msg(s);
        sonido(NOTE_STICKS);
        if (pretime == 3) {
            mandarSpawn(p1);
            mandarSpawn(p2);
        }
    }

    public void startGame(List<String> msg) {
        hitsp1 = 0;
        hitsp2 = 0;
        maxhitsp1 = 0;
        maxhitsp2 = 0;
        premaxp1 = 0;
        premaxp2 = 0;
        flechasp1 = 0;
        flechasp2 = 0;

        if (estilo == Estilo.RANKED) {
            for (String s : duelControl.startingRanked) {
                msg(s.replaceAll("<player1>", p1.getName())
                        .replaceAll("<player2>", p2.getName())
                        .replaceAll("<elo1>", "" + playerConfig.get(p1).getElo(kit))
                        .replaceAll("<elo2>", "" + playerConfig.get(p2).getElo(kit))
                        .replaceAll("<rank1>", playerConfig.get(p1).getRank(kit))
                        .replaceAll("<rank2>", playerConfig.get(p2).getRank(kit))
                        .replaceAll("<map>", mapa.getName())
                        .replaceAll("<time>", Extra.secToMin(time))
                        .replaceAll("<kit>", kit.kitName));
            }
            Extra.setScore(p1, Score.TipoScore.RANKED);
            Extra.setScore(p2, Score.TipoScore.RANKED);
            Bukkit.getPluginManager().callEvent(new RankedStartEvent(p1, p2, kit.getKitName(), mapa));
        } else {
            for (String s : msg) {
                msg(s.replaceAll("<player1>", p1.getName())
                        .replaceAll("<player2>", p2.getName())
                        .replaceAll("<kit>", kit.kitName)
                        .replaceAll("<time>", Extra.secToMin(time))
                        .replaceAll("<format>", bestFormat)
                        .replaceAll("<map>", mapa.getName()));
            }
            if (estilo == Estilo.UNRANKED) {
                Extra.setScore(p1, Score.TipoScore.UNRANKED);
                Extra.setScore(p2, Score.TipoScore.UNRANKED);
                Bukkit.getPluginManager().callEvent(new UnRankedStartEvent(p1, p2, kit.getKitName(), mapa));
            } else {
                Extra.setScore(p1, Score.TipoScore.DUEL);
                Extra.setScore(p2, Score.TipoScore.DUEL);
                Bukkit.getPluginManager().callEvent(new DuelStartEvent(p1, p2, kit.getKitName(), mapa, bestOf, winsp1, winsp2));
            }

        }

        sonido(BURP);
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

        daño = true;

        mandarSpawn(p1);
        mandarSpawn(p2);
    }

    public void finish(final Player l) {
        daño = false;
        Player f = p2;
        if (p2 == l) {
            f = p1;
            winsp1++;
        } else {
            winsp2++;
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

        if (premaxp1 > maxhitsp1) {
            maxhitsp1 = premaxp1;
        }
        if (premaxp2 > maxhitsp2) {
            maxhitsp2 = premaxp2;
        }

        int h1 = hitsp1, h2 = hitsp2, m1 = maxhitsp1, m2 = maxhitsp2;
        if (w == p2) {
            h1 = hitsp2;
            h2 = hitsp1;
            m1 = maxhitsp2;
            m2 = maxhitsp1;
        }

        int f1 = flechasp1, f2 = flechasp2;
        if (w == p2) {
            f1 = flechasp2;
            f2 = flechasp1;
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
                .replaceAll("<health>", "" + Extra.getSangre(w.getHealth()))
                .replaceAll("<kit>", kit.kitName));

        ponerLastInventory(w);
        ponerLastInventory(l);

        Extra.text(w, duelControl.msgLastInv, duelControl.hoverLastInv.replaceAll("<player>", l.getName()), "/uinventario " + l.getName(), "AQUA");
        Extra.text(l, duelControl.msgLastInv, duelControl.hoverLastInv.replaceAll("<player>", w.getName()), "/uinventario " + w.getName(), "AQUA");

        Extra.limpiarP(w);
        Extra.limpiarP(l);

        if (extraLang.duelEffectDeathBlindness) {
            l.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
        }
        if (extraLang.duelEffectDeathSlow) {
            l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 5));
        }
        if (extraLang.duelEffectDeathInvi) {
            l.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 1));
            w.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 1));
        }
        sonido(LEVEL_UP);

        if (Extra.preEmpezandoUno.contains(jugandoUno.get(w))) {
            Extra.preEmpezandoUno.remove(jugandoUno.get(w));
        }
        if (hotbars.esperandoEscojaHotbar.contains(w)) {
            hotbars.esperandoEscojaHotbar.remove(w);
        }
        if (hotbars.esperandoEscojaHotbar.contains(l)) {
            hotbars.esperandoEscojaHotbar.remove(l);
        }

        if (estilo == Estilo.RANKED) {
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
            Bukkit.getPluginManager().callEvent(new RankedFinishEvent(w, l, kit.getKitName(), mapa, elo));
        } else if (estilo == Estilo.UNRANKED) {
            Bukkit.getPluginManager().callEvent(new UnRankedFinishEvent(w, l, kit.getKitName(), mapa));
        } else {
            if (w == p1) {
                Bukkit.getPluginManager().callEvent(new DuelFinishEvent(w, l, kit.getKitName(), mapa, bestOf, winsp1, winsp2));
            } else {
                Bukkit.getPluginManager().callEvent(new DuelFinishEvent(w, l, kit.getKitName(), mapa, bestOf, winsp2, winsp1));
            }
        }

        p1.setMaximumNoDamageTicks(20);
        p1.spigot().setCollidesWithEntities(true);
        p2.setMaximumNoDamageTicks(20);
        p2.spigot().setCollidesWithEntities(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                if ((bestOf != 1 && w.isOnline() && l.isOnline())
                        && ((bestOf == 3 && (winsp1 != 2 && winsp2 != 2))
                        || (bestOf == 5 && (winsp1 != 3 && winsp2 != 3)))) {
                    preEmpezandoUno.add(jugandoUno.get(w));
                    mapa.regen(kit);
                    preEmpezar();
                } else {
                    sacarPartida(w);
                    sacarPartida(l);
                    if (bestOf != 1) {
                        int points1 = winsp1;
                        int points2 = winsp2;
                        if (w == p2) {
                            points1 = winsp2;
                            points2 = winsp1;
                        }
                        msg(duelControl.won.replaceAll("<winner>", w.getName())
                                .replaceAll("<loser>", l.getName())
                                .replaceAll("<points1>", "" + points1)
                                .replaceAll("<points2>", "" + points2));
                    }
                    Extra.terminarMapa(mapa, kit);
                    if (estilo == Estilo.RANKED) {
                        guis.setNumberRankedPlaying(kit, false);
                        SQL.guardarStats(w, false);
                        SQL.guardarStats(l, false);
                    } else if (estilo == Estilo.UNRANKED) {
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

    public void sonido(String s) {
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
        if (daño) {
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

    public void dañar(final EntityDamageByEntityEvent e) {
        if (!daño) {
            e.setCancelled(true);
        } else {
            if (e.getDamager() instanceof Player) {
                if (((Player) e.getEntity()) == p1) {
                    hitsp1++;
                    premaxp1++;
                    if (premaxp2 > maxhitsp2) {
                        maxhitsp2 = premaxp2;
                    }
                    premaxp2 = 0;
                } else {
                    hitsp2++;
                    premaxp2++;
                    if (premaxp1 > maxhitsp1) {
                        maxhitsp1 = premaxp1;
                    }
                    premaxp1 = 0;
                }
            } else if (e.getDamager() instanceof Arrow) {
                final Arrow a = (Arrow) e.getDamager();
                if (a.getShooter() instanceof Player && ((Player) a.getShooter()) != ((Player) e.getEntity())) {
                    Player p = (Player) a.getShooter();
                    if (p == p1) {
                        flechasp1++;
                    } else {
                        flechasp2++;
                    }
                    if (extraLang.showhealwitharrow) {
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
