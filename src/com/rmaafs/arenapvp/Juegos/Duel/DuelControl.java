package com.rmaafs.arenapvp.Juegos.Duel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.CHICKEN_EGG_POP;
import static com.rmaafs.arenapvp.util.Extra.HORSE_ARMOR;
import static com.rmaafs.arenapvp.util.Extra.NOTE_BASS;
import static com.rmaafs.arenapvp.util.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.util.Extra.VILLAGER_NO;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static com.rmaafs.arenapvp.util.Extra.clang;
import static com.rmaafs.arenapvp.util.Extra.jugandoUno;
import static com.rmaafs.arenapvp.util.Extra.mapLibres;
import static com.rmaafs.arenapvp.util.Extra.playerConfig;
import static com.rmaafs.arenapvp.util.Extra.preEmpezandoUno;
import com.rmaafs.arenapvp.manager.kit.Kit;
import static com.rmaafs.arenapvp.ArenaPvP.extraLang;
import static com.rmaafs.arenapvp.ArenaPvP.guis;
import static com.rmaafs.arenapvp.ArenaPvP.hotbars;
import com.rmaafs.arenapvp.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DuelControl {

    public HashMap<Player, PreDuelConfig> creandoDuel = new HashMap<>();
    public HashMap<Kit, Player> esperandoUnRanked = new HashMap<>();
    public HashMap<Kit, Player> esperandoRanked = new HashMap<>();

    public HashMap<String, ItemStack[]> ultimosInv = new HashMap<>();

    public String win, won, rankedFinish, guiLastInventoryName, msgLastInv, hoverLastInv;
    public List<String> winStats, startingRanked, noHaveRankeds, noHaveUnRankeds;

    public String thisplayernowantduel;
    private String bestofone, bestofthree, bestoffive, defaultcolor, hovertext, sended,
            waitingUnranked, waitingRanked, cantSeeLastInv;
    private List<String> text;

    public Inventory invBestof;

    public DuelControl() {
        setConfig();
    }

    public void setConfig() {
        win = Extra.tc(clang.getString("starting.win"));
        won = Extra.tc(clang.getString("starting.won"));
        rankedFinish = Extra.tc(clang.getString("starting.ranked.finish"));
        winStats = Extra.tCC(clang.getStringList("starting.stats"));
        startingRanked = Extra.tCC(clang.getStringList("starting.ranked.start"));
        noHaveRankeds = Extra.tCC(clang.getStringList("donthave.rankeds"));
        noHaveUnRankeds = Extra.tCC(clang.getStringList("donthave.unrankeds"));

        waitingUnranked = Extra.tc(clang.getString("queue.unranked"));
        waitingRanked = Extra.tc(clang.getString("queue.ranked"));

        guiLastInventoryName = Extra.tc(clang.getString("gui.lastinventory.name"));
        cantSeeLastInv = Extra.tc(clang.getString("lastinventory.cantsee"));
        msgLastInv = Extra.tc(clang.getString("lastinventory.msg"));
        hoverLastInv = Extra.tc(clang.getString("lastinventory.hovertext"));

        bestofone = Extra.tc(clang.getString("duelformat.bestofone"));
        bestofthree = Extra.tc(clang.getString("duelformat.bestofthree"));
        bestoffive = Extra.tc(clang.getString("duelformat.bestoffive"));
        defaultcolor = Extra.tc(clang.getString("duelformat.defaultcolor"));
        hovertext = Extra.tc(clang.getString("duelformat.hovertext"));
        text = Extra.tCC(clang.getStringList("duelformat.text"));
        sended = Extra.tc(clang.getString("duelformat.sended"));
        thisplayernowantduel = Extra.tc(clang.getString("duelformat.thisplayernowantduel"));

        if (!Extra.existColor(defaultcolor)) {
            Extra.avisoConsola("§3ARENAPVP >> §bDefault color of duelformat " + defaultcolor + " no exist. Changing to AQUA.");
            defaultcolor = "AQUA";
        }

        invBestof = Bukkit.createInventory(null, cconfig.getInt("gui.bestof.rows") * 9, Extra.tc(clang.getString("gui.bestof.name")));

        if (cconfig.getBoolean("gui.bestof.one.use")) {
            invBestof.setItem(cconfig.getInt("gui.bestof.one.slot"), Extra.crearId(cconfig.getInt("gui.bestof.one.id"), cconfig.getInt("gui.bestof.one.data-value"), Extra.tc(clang.getString("gui.bestof.one.name")), Extra.tCC(clang.getStringList("gui.bestof.one.lore")), 1));
        }
        if (cconfig.getBoolean("gui.bestof.three.use")) {
            invBestof.setItem(cconfig.getInt("gui.bestof.three.slot"), Extra.crearId(cconfig.getInt("gui.bestof.three.id"), cconfig.getInt("gui.bestof.three.data-value"), Extra.tc(clang.getString("gui.bestof.three.name")), Extra.tCC(clang.getStringList("gui.bestof.three.lore")), 3));
        }
        if (cconfig.getBoolean("gui.bestof.five.use")) {
            invBestof.setItem(cconfig.getInt("gui.bestof.five.slot"), Extra.crearId(cconfig.getInt("gui.bestof.five.id"), cconfig.getInt("gui.bestof.five.data-value"), Extra.tc(clang.getString("gui.bestof.five.name")), Extra.tCC(clang.getStringList("gui.bestof.five.lore")), 5));
        }
    }

    public void createDuel(Player p, String r) {
        if (Extra.isCheckYouPlaying(p)) {
            Player o = Bukkit.getPlayer(r);
            if (Extra.isExist(o, p)) {
                if (Extra.isCheckPlayerPlaying(o, p)) {
                    creandoDuel.put(p, new PreDuelConfig(p, o));
                    p.openInventory(guis.invDuel);
                    Extra.sonido(p, CHICKEN_EGG_POP);
                }
            }
        }
    }

    public void clickKit(Player p, int amount, int slot, boolean kit) {
        if (Extra.isCheckYouPlaying(p)) {
            if (kit) {
                PreDuelConfig pre = creandoDuel.get(p);
                Player p2 = pre.getP2();
                if (Extra.isCheckPlayerPlaying(p2, p)) {
                    if (Extra.isExist(p2, p) && Extra.isPerm2(p, "apvp.duel.kit.*", "apvp.duel.kit." + guis.itemKits.get(guis.items.get(slot)).getKitName().toLowerCase())) {
                        creandoDuel.get(p).setKit(guis.itemKits.get(guis.items.get(slot)));
                        p.openInventory(invBestof);
                        Extra.sonido(p, CHICKEN_EGG_POP);
                    } else {
                        p.closeInventory();
                        creandoDuel.remove(p);
                    }
                } else {
                    p.closeInventory();
                }
            } else {
                PreDuelConfig pre = creandoDuel.get(p);
                Player p2 = pre.getP2();
                if (Extra.isCheckPlayerPlaying(p2, p)) {
                    if (Extra.isExist(p2, p) && Extra.isPerm(p, "apvp.duel.best." + amount)) {
                        pre.setTotal(amount);
                        p.closeInventory();
                        String best = bestofone;
                        if (amount == 3) {
                            best = bestofthree;
                        } else if (amount == 5) {
                            best = bestoffive;
                        }
                        for (String s : text) {
                            Extra.text(p2, s.replaceAll("<player>", p.getName())
                                    .replaceAll("<kit>", pre.getKit().getKitName())
                                    .replaceAll("<bestof>", best), hovertext.replaceAll("<player>", p.getName()), "/duel accept " + p.getName(), defaultcolor);
                        }
                        p.sendMessage(sended.replaceAll("<player>", p2.getName()));
                        Extra.sonido(p, ORB_PICKUP);
                        Extra.sonido(p2, ORB_PICKUP);
                    } else {
                        p.closeInventory();
                        creandoDuel.remove(p);
                    }
                } else {
                    p.closeInventory();
                }
            }
        } else {
            p.closeInventory();
        }
    }

    public void aceptarDuel(Player o, String f) {
        if (Extra.isCheckYouPlaying(o)) {
            Player p = Bukkit.getPlayer(f);
            if (Extra.isExist(p, o)) {
                if (creandoDuel.containsKey(p) && creandoDuel.get(p).getP2() == o && creandoDuel.get(p).getKit() != null) {
                    if (Extra.isCheckPlayerPlaying(p, o)) {
                        if (checkMapAvailables(p, o, creandoDuel.get(p).getKit())) {
                            PreDuelConfig duel = creandoDuel.get(p);
                            String best = bestofone;
                            if (duel.getTotal() == 3) {
                                best = bestofthree;
                            } else if (duel.getTotal() == 5) {
                                best = bestoffive;
                            }
                            Game game = new Game(p, o, duel.getKit(), Extra.getMap(duel.getKit()), best, duel.getTotal());
                            jugandoUno.put(p, game);
                            jugandoUno.put(o, game);
                            preEmpezandoUno.add(game);
                            creandoDuel.remove(p);
                        } else {
                            creandoDuel.remove(p);
                        }
                    }
                } else {
                    o.sendMessage(thisplayernowantduel);
                }
            }
        }
    }
    
    public void createFakeDuel(Player o, Player bot) {
        if (Extra.isCheckYouPlaying(o)) {
            Player p = bot;
            if (Extra.isExist(p, o)) {
                if (creandoDuel.containsKey(p) && creandoDuel.get(p).getP2() == o && creandoDuel.get(p).getKit() != null) {
                    if (Extra.isCheckPlayerPlaying(p, o)) {
                        if (checkMapAvailables(p, o, creandoDuel.get(p).getKit())) {
                            PreDuelConfig duel = creandoDuel.get(p);
                            String best = bestofone;
                            if (duel.getTotal() == 3) {
                                best = bestofthree;
                            } else if (duel.getTotal() == 5) {
                                best = bestoffive;
                            }
                            Game game = new Game(p, o, duel.getKit(), Extra.getMap(duel.getKit()), best, duel.getTotal());
                            jugandoUno.put(p, game);
                            jugandoUno.put(o, game);
                            preEmpezandoUno.add(game);
                            creandoDuel.remove(p);
                        } else {
                            creandoDuel.remove(p);
                        }
                    }
                } else {
                    o.sendMessage(thisplayernowantduel);
                }
            }
        }
    }

    public void clickRanked(Player p, Kit k, boolean ranked) {
        if (ranked) {
            if (Extra.isPerm2(p, "apvp.ranked.kit.*", "apvp.ranked.kit." + k.getKitName().toLowerCase())) {
                if (p.hasPermission("apvp.rankedfree." + k.getKitName().toLowerCase()) || p.hasPermission("apvp.rankeds") || playerConfig.get(p).getRankeds() > 0) {
                    if (esperandoRanked.containsKey(k)) {
                        if (checkMapAvailables(p, esperandoRanked.get(k), k)) {
                            Game game = new Game(esperandoRanked.get(k), p, k, Extra.getMap(k), true);
                            jugandoUno.put(p, game);
                            jugandoUno.put(esperandoRanked.get(k), game);
                            preEmpezandoUno.add(game);
                            esperandoRanked.remove(k);
                            guis.setNumberRankedPlaying(k, true);
                        } else {
                            esperandoRanked.remove(k);
                        }
                    } else {
                        esperandoRanked.put(k, p);
                        p.sendMessage(waitingRanked.replaceAll("<kit>", k.kitName));
                        Extra.sonido(p, ORB_PICKUP);
                        hotbars.setLeave(p);
                    }
                } else {
                    for (String s : noHaveRankeds) {
                        p.sendMessage(s);
                    }
                    Extra.sonido(p, VILLAGER_NO);
                }
            }
        } else {
            if (Extra.isPerm2(p, "apvp.unranked.kit.*", "apvp.unranked.kit." + k.getKitName().toLowerCase())) {
                if (p.hasPermission("apvp.unrankedfree." + k.getKitName().toLowerCase()) || p.hasPermission("apvp.unrankeds") || playerConfig.get(p).getUnRankeds() > 0) {
                    if (esperandoUnRanked.containsKey(k)) {
                        if (checkMapAvailables(p, esperandoUnRanked.get(k), k)) {
                            Game game = new Game(esperandoUnRanked.get(k), p, k, Extra.getMap(k), false);
                            jugandoUno.put(p, game);
                            jugandoUno.put(esperandoUnRanked.get(k), game);
                            preEmpezandoUno.add(game);
                            esperandoUnRanked.remove(k);
                            guis.setNumberUnRankedPlaying(k, true);
                        } else {
                            esperandoUnRanked.remove(k);
                        }
                    } else {
                        esperandoUnRanked.put(k, p);
                        p.sendMessage(waitingUnranked.replaceAll("<kit>", k.kitName));
                        Extra.sonido(p, ORB_PICKUP);
                        hotbars.setLeave(p);
                    }
                } else {
                    for (String s : noHaveUnRankeds) {
                        p.sendMessage(s);
                    }
                    Extra.sonido(p, VILLAGER_NO);
                }
            }
        }
        p.closeInventory();
    }

    public boolean checkMapAvailables(Player p, Player o, Kit kit) {
        if (mapLibres.get(kit) == null || !mapLibres.containsKey(kit) || mapLibres.get(kit).isEmpty()) {
            p.sendMessage(extraLang.noMapsAvailable);
            o.sendMessage(extraLang.noMapsAvailable);
            Extra.sonido(p, NOTE_BASS);
            Extra.sonido(o, NOTE_BASS);
            Extra.cleanPlayer(p);
            Extra.cleanPlayer(o);
            hotbars.setMain(p);
            hotbars.setMain(o);
            return false;
        }
        return true;
    }

    public void abrirUltimoInv(Player p, String o) {
        if (Extra.isPerm(p, "apvp.viewlastinventory")) {
            if (ultimosInv.containsKey(o)) {
                Inventory uinventario = Bukkit.createInventory(p, 54, guiLastInventoryName);
                uinventario.setContents(ultimosInv.get(o));
                uinventario.setItem(53, guis.itemLeave);
                p.openInventory(uinventario);
                Extra.sonido(p, HORSE_ARMOR);
            } else {
                p.sendMessage(cantSeeLastInv);
            }
        }
    }

    public void sacarUnRanked(Player p) {
        Kit remove = null;
        for (Map.Entry<Kit, Player> entry : esperandoUnRanked.entrySet()) {
            Kit k = entry.getKey();
            Player pp = entry.getValue();
            if (pp == p) {
                remove = k;
                break;
            }
        }
        esperandoUnRanked.remove(remove);
    }

    public void sacarRanked(Player p) {
        Kit remove = null;
        for (Map.Entry<Kit, Player> entry : esperandoRanked.entrySet()) {
            Kit k = entry.getKey();
            Player pp = entry.getValue();
            if (pp == p) {
                remove = k;
                break;
            }
        }
        esperandoRanked.remove(remove);
    }

    public void sacar(Player p) {
        if (creandoDuel.containsKey(p)) {
            creandoDuel.remove(p);
        }
        if (esperandoUnRanked.containsValue(p)) {
            sacarUnRanked(p);
        }
        if (esperandoRanked.containsValue(p)) {
            sacarRanked(p);
        }
    }
}
