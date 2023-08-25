package com.rmaafs.arenapvp.Party;

import java.util.*;

import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.CAT_MEOW;
import static com.rmaafs.arenapvp.util.Extra.FIREWORK_LARGE_BLAST;
import static com.rmaafs.arenapvp.util.Extra.NOTE_BASS;
import static com.rmaafs.arenapvp.util.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.util.Extra.VILLAGER_YES;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static com.rmaafs.arenapvp.util.Extra.clang;
import static com.rmaafs.arenapvp.ArenaPvP.duelControl;
import static com.rmaafs.arenapvp.ArenaPvP.extraLang;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PartyControl {

    public HashMap<Player, Party> partyHash = new HashMap<>();
    public HashMap<Party, EventGame> partyEvents = new HashMap<>();
    public List<EventGame> startingEvents = new ArrayList<>();

    public HashMap<Player, PreDuelGame> preDuels = new HashMap<>();
    public HashMap<Party, DuelGame> partyDuels = new HashMap<>();
    public List<DuelGame> startingPartyDuel = new ArrayList<>();

    //public HashMap<Player, EventGame> partyEvents = new HashMap<>();
    public Inventory invSelect;
    public Inventory invEvents;
    public Inventory invPartys;
    public Inventory invPartysOpen;

    public String invSelectName;
    public String invEventsName;
    public String invPartysName;
    public String invConfigName;
    public String invPartysOpenName;
    public  String noexist;
    public String youdonthaveparty;
    public String younotaretheowner;
    public String playernointheparty;
    public String partyduelsent;
    public String duelmatch;
    public String duelmatchclick;
    public String youareplaying;
    public String partyplaying;
    public String playerhasparty;
    public String chatformat;
    public String chatformatstaff;
    public ItemStack itemCreate, itemJoin;
    public  ItemStack itemPartyFFA, itemPartyGroup, itemPartyRR;

    public PartyControl() {
        noexist = Extra.tc(clang.getString("party.create.noexist"));
        youdonthaveparty = Extra.tc(clang.getString("party.create.youdonthaveparty"));
        younotaretheowner = Extra.tc(clang.getString("party.create.younotaretheowner"));
        playernointheparty = Extra.tc(clang.getString("party.create.playernointheparty"));
        partyduelsent = Extra.tc(clang.getString("party.create.partyduelsent"));
        duelmatch = Extra.tc(clang.getString("party.create.duelmatch"));
        duelmatchclick = Extra.tc(clang.getString("party.create.duelmatchclick"));
        youareplaying = Extra.tc(clang.getString("party.create.youareplaying"));
        partyplaying = Extra.tc(clang.getString("party.create.partyplaying"));
        playerhasparty = Extra.tc(clang.getString("party.create.playerhasparty"));
        chatformat = Extra.tc(clang.getString("party.create.chatformat"));
        chatformatstaff = Extra.tc(clang.getString("party.create.chatformatstaff"));

        invSelectName = Extra.tc(clang.getString("gui.party.select.name"));
        invEventsName = Extra.tc(clang.getString("gui.party.events.name"));
        invPartysName = Extra.tc(clang.getString("gui.party.partys.name"));
        invConfigName = Extra.tc(clang.getString("gui.party.config.name"));
        invPartysOpenName = Extra.tc(clang.getString("gui.party.open.name"));

        invSelect = Bukkit.createInventory(null, cconfig.getInt("gui.party.select.rows") * 9, invSelectName);
        invEvents = Bukkit.createInventory(null, cconfig.getInt("gui.party.events.rows") * 9, invEventsName);
        invPartys = Bukkit.createInventory(null, 54, invPartysName);
        invPartysOpen = Bukkit.createInventory(null, 54, invPartysOpenName);

        itemCreate = Extra.crearId(cconfig.getInt("gui.party.select.create.id"), cconfig.getInt("gui.party.select.create.data-value"), clang.getString("gui.party.select.items.create.name"), clang.getStringList("gui.party.select.items.create.lore"), 1);
        itemJoin = Extra.crearId(cconfig.getInt("gui.party.select.join.id"), cconfig.getInt("gui.party.select.join.data-value"), clang.getString("gui.party.select.items.join.name"), clang.getStringList("gui.party.select.items.join.lore"), 1);

        itemPartyFFA = Extra.crearId(cconfig.getInt("gui.party.events.ffa.id"), cconfig.getInt("gui.party.events.ffa.data-value"), clang.getString("gui.party.events.items.ffa.name"), clang.getStringList("gui.party.events.items.ffa.lore"), 1);
        itemPartyGroup = Extra.crearId(cconfig.getInt("gui.party.events.group.id"), cconfig.getInt("gui.party.events.group.data-value"), clang.getString("gui.party.events.items.group.name"), clang.getStringList("gui.party.events.items.group.lore"), 1);
        itemPartyRR = Extra.crearId(cconfig.getInt("gui.party.events.redrover.id"), cconfig.getInt("gui.party.events.redrover.data-value"), clang.getString("gui.party.events.items.redrover.name"), clang.getStringList("gui.party.events.items.redrover.lore"), 1);

        invSelect.setItem(cconfig.getInt("gui.party.select.create.slot"), itemCreate);
        if (cconfig.getBoolean("gui.party.select.join.use")) {
            invSelect.setItem(cconfig.getInt("gui.party.select.join.slot"), itemJoin);
        }

        if (cconfig.getBoolean("gui.party.events.ffa.use")) {
            invEvents.setItem(cconfig.getInt("gui.party.events.ffa.slot"), itemPartyFFA);
        }
        if (cconfig.getBoolean("gui.party.events.group.use")) {
            invEvents.setItem(cconfig.getInt("gui.party.events.group.slot"), itemPartyGroup);
        }
        if (cconfig.getBoolean("gui.party.events.redrover.use")) {
            invEvents.setItem(cconfig.getInt("gui.party.events.redrover.slot"), itemPartyRR);
        }
    }

    public void openInvSelect(Player p) {
        p.openInventory(invSelect);
        Extra.sonido(p, CAT_MEOW);
    }

    public void openinvPartysOpen(Player p) {
        p.openInventory(invPartysOpen);
        Extra.sonido(p, FIREWORK_LARGE_BLAST);
    }

    public void openInvEvents(Player p) {
        p.openInventory(invEvents);
        Extra.sonido(p, FIREWORK_LARGE_BLAST);
    }

    public void openInvPartys(Player p) {
        p.openInventory(invPartys);
        Extra.sonido(p, FIREWORK_LARGE_BLAST);
    }

    public void partyChat(Player p, String msg) {
        if (partyHash.containsKey(p)) {
            String s = chatformatstaff.replaceAll("<player>", p.getName()).replaceAll("<msg>", msg);
            partyHash.get(p).msg(chatformat.replaceAll("<player>", p.getName()).replaceAll("<msg>", msg));
            for (Player o : Bukkit.getServer().getOnlinePlayers()) {
                if (o.hasPermission("apvp.party.chat.staff")) {
                    o.sendMessage(s);
                }
            }
        } else {
            p.sendMessage(youdonthaveparty);
        }
    }

    public void refreshPartyItems() {
        invPartys.clear();
        List<Party> all = new ArrayList<>();
        for (Map.Entry<Player, Party> entry : partyHash.entrySet()) {
            Party party = entry.getValue();
            if (!all.contains(party)) {
                all.add(party);
            }
        }
        int i = 0;
        for (Party p : all) {
            List<String> lore = new ArrayList<>();
            lore.add("§6Players:");
            for (UUID o : p.players) {
                lore.add("§e  - " + Bukkit.getPlayer(o).getName());
            }
            invPartys.setItem(i, Extra.crearId(421, 0, "§e" + p.owner.getName(), lore, p.players.size()));
            i++;
        }
        refreshPartyOpeneds(all);
    }

    public void refreshPartyOpeneds() {
        invPartysOpen.clear();
        List<Party> all = new ArrayList<>();
        for (Map.Entry<Player, Party> entry : partyHash.entrySet()) {
            Party party = entry.getValue();
            if (!all.contains(party) && party.open) {
                all.add(party);
            }
        }
        int i = 0;
        for (Party p : all) {
            List<String> lore = new ArrayList<>();
            lore.add("§6Players:");
            for (UUID o : p.players) {
                lore.add("§e  - " + Bukkit.getPlayer(o).getName());
            }
            invPartysOpen.setItem(i, Extra.crearId(324, 0, "§e" + p.owner.getName(), lore, p.players.size()));
            i++;
        }
    }

    public void refreshPartyOpeneds(List<Party> partys) {
        invPartysOpen.clear();
        int i = 0;
        for (Party p : partys) {
            if (p.open) {
                List<String> lore = new ArrayList<>();
                lore.add("§6Players:");
                for (UUID o : p.players) {
                    lore.add("§e  - " + Bukkit.getPlayer(o).getName());
                }
                invPartysOpen.setItem(i, Extra.crearId(324, 0, "§e" + p.owner.getName(), lore, p.players.size()));
                i++;
            }
        }
    }

    public void clickItem(Player p, ItemStack i) {
        if (i.isSimilar(itemCreate)) {
            if (Extra.isPerm(p, "apvp.party.create")) {
                partyHash.put(p, new Party(p));
                Extra.setScore(p, Score.TipoScore.PARTYMAIN);
                refreshPartyItems();
            } else {
                p.closeInventory();
            }
        } else if (i.isSimilar(itemJoin)) {
            if (Extra.isPerm(p, "apvp.party.joinopen")) {
                openinvPartysOpen(p);
            }
        }
    }

    public void clickOpenParty(Player p, ItemStack i) {
        Player o = Bukkit.getPlayer(ChatColor.stripColor(i.getItemMeta().getDisplayName()));
        if (Extra.isExist(o, p)) {
            partyHash.get(o).preguntarEntrar(p);
        }
    }

    public void aceptarPlayerAbierta(Player p, String a) {
        Player o = Bukkit.getPlayer(a);
        if (Extra.isExist(o, p)) {
            if (partyHash.containsKey(p)) {
                if (partyHash.get(p).owner == p) {
                    if (!partyHash.containsKey(o)) {
                        partyHash.get(p).aceptarPreguntar(o);
                    } else {
                        p.sendMessage(playerhasparty);
                    }
                } else {
                    p.sendMessage(younotaretheowner);
                }
            } else {
                p.sendMessage(youdonthaveparty);
            }
        }
    }

    public void partyInvite(Player p, String a) {
        if (partyHash.containsKey(p)) {
            Player o = Bukkit.getPlayer(a);
            if (Extra.isExist(o, p)) {
                if (Extra.isCheckPlayerPlaying(o, p)) {
                    partyHash.get(p).invitar(o);
                }
            }
        } else {
            p.sendMessage(youdonthaveparty);
        }
    }

    public void aceptarInvitacion(Player p, String a) {
        if (Extra.isCheckYouPlaying(p)) {
            Player o = Bukkit.getPlayer(a);
            if (partyHash.containsKey(o)) {
                partyHash.get(o).aceptarInvitado(p);
            } else {
                p.sendMessage(noexist);
                Extra.sonido(p, NOTE_BASS);
            }
        }
    }

    public void partyLeave(Player p) {
        if (partyHash.containsKey(p)) {
            partyHash.get(p).leave(p, true);
        } else {
            p.sendMessage(youdonthaveparty);
        }
    }

    public void partyKick(Player p, String a) {
        if (partyHash.containsKey(p)) {
            if (partyHash.get(p).owner == p) {
                Player o = Bukkit.getPlayer(a);
                if (Extra.isExist(o, p)) {
                    if (partyHash.get(p).players.contains(o)) {
                        partyHash.get(p).kick(o);
                    } else {
                        p.sendMessage(playernointheparty);
                    }
                }
            } else {
                p.sendMessage(younotaretheowner);
            }
        } else {
            p.sendMessage(youdonthaveparty);
        }
    }

    public void partyPromote(Player p, String a) {
        if (partyHash.containsKey(p)) {
            if (partyHash.get(p).owner == p) {
                Player o = Bukkit.getPlayer(a);
                if (Extra.isExist(o, p)) {
                    if (partyHash.get(p).players.contains(o)) {
                        partyHash.get(p).promote(o);
                    } else {
                        p.sendMessage(playernointheparty);
                    }
                }
            } else {
                p.sendMessage(younotaretheowner);
            }
        } else {
            p.sendMessage(youdonthaveparty);
        }
    }

    public void preguntarDuel(PreDuelGame game) {
        Party p1 = game.getParty1();
        Party p2 = game.getParty2();
        if (!partyDuels.containsKey(p1) || partyEvents.containsKey(game.p1)) {
            if (!partyDuels.containsKey(p2) || partyEvents.containsKey(game.p2)) {
                if (partyHash.containsValue(p2)) {
                    try {
                        p1.msg(partyduelsent.replaceAll("<player>", p2.owner.getName()));
                    } catch (Exception e) {
                        Bukkit.getServer().getConsoleSender().sendMessage("§4§lARENAPVP >> §cPlease send this error to @Royendero1.");
                        Bukkit.getServer().getConsoleSender().sendMessage("§6§lARENAPVP >> §ep2 = " + p2 + ", p2.owner = " + p2.owner);
                    }
                    p1.sonido(ORB_PICKUP);
                    p2.msg(duelmatch.replaceAll("<player>", p1.owner.getName()).replaceAll("<kit>", game.getKit().getKitName()));
                    p2.sonido(VILLAGER_YES);

                    StringBuilder pla = new StringBuilder(p1.players.size() > 0 ? Bukkit.getPlayer(p1.players.iterator().next()).getName() : "");
                    for (UUID playerUUID : p1.players) {
                        Player player = Bukkit.getPlayer(playerUUID);
                        if (player != null) {
                            pla.append(", ").append(player.getName());
                        }
                    }
                    Extra.text(p2.owner, duelmatchclick, "§e" + pla, "/party duelaccept " + p1.owner.getName(), "AQUA");
                } else {
                    p1.msg(noexist);
                }
            } else {
                p1.owner.sendMessage(partyplaying);
            }
        } else {
            p1.owner.sendMessage(youareplaying);
        }
    }

    public void aceptarDuel(Player p, String a) {
        if (partyHash.containsKey(p)) {
            if (partyHash.get(p).owner == p) {
                Player p2 = Bukkit.getPlayer(a);
                if (preDuels.containsKey(p2) && preDuels.get(p2).getParty2().equals(partyHash.get(p))) {
                    PreDuelGame game = preDuels.get(p2);
                    preDuels.remove(p2);
                    if (!partyDuels.containsKey(game.p1) || partyEvents.containsKey(game.p1)) {
                        if (!partyDuels.containsKey(game.p2) || partyEvents.containsKey(game.p2)) {
                            if (Extra.checkMapAvailables(game.getKit())) {
                                DuelGame dgame = new DuelGame(game.p1, game.p2, game.getKit(), Extra.getMap(game.getKit()));
                                partyDuels.put(game.p1, dgame);
                                partyDuels.put(game.p2, dgame);
                                startingPartyDuel.add(dgame);
                            } else {
                                game.p1.msg(extraLang.noMapsAvailable);
                                game.p1.sonido(NOTE_BASS);
                                game.p2.msg(extraLang.noMapsAvailable);
                                game.p2.sonido(NOTE_BASS);
                            }
                        } else {
                            p.sendMessage(youareplaying);
                        }
                    } else {
                        p.sendMessage(partyplaying);
                    }
                } else {
                    p.sendMessage(duelControl.thisplayernowantduel);
                }
            } else {
                p.sendMessage(younotaretheowner);
            }
        } else {
            p.sendMessage(youdonthaveparty);
        }
    }

    public void sacar(Player p) {
        if (partyHash.containsKey(p)) {
            partyHash.get(p).leave(p, false);
        }
    }
}
