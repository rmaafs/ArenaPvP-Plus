package com.rmaafs.arenapvp.Party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.rmaafs.arenapvp.Extra;
import static com.rmaafs.arenapvp.Extra.CAT_MEOW;
import static com.rmaafs.arenapvp.Extra.FIREWORK_LARGE_BLAST;
import static com.rmaafs.arenapvp.Extra.NOTE_BASS;
import static com.rmaafs.arenapvp.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.Extra.VILLAGER_YES;
import static com.rmaafs.arenapvp.Extra.cconfig;
import static com.rmaafs.arenapvp.Extra.clang;
import static com.rmaafs.arenapvp.Main.duelControl;
import static com.rmaafs.arenapvp.Main.extraLang;
import com.rmaafs.arenapvp.Score;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PartyControl {

    public HashMap<Player, Party> partys = new HashMap<>();
    public HashMap<Party, EventGame> partysEvents = new HashMap<>();
    public List<EventGame> startingsEvents = new ArrayList<>();

    public HashMap<Player, PreDuelGame> preduels = new HashMap<>();
    public HashMap<Party, DuelGame> partysDuel = new HashMap<>();
    public List<DuelGame> startingPartyDuel = new ArrayList<>();

    //public HashMap<Player, EventGame> partyEvents = new HashMap<>();
    Inventory invSelect, invEvents, invPartys, invPartysOpen;

    public String invSelectName, invEventsName, invPartysName, invConfigName, invPartysOpenName;
    String noexist, youdonthaveparty, younotaretheowner, playernointheparty,
            partyduelsent, duelmatch, duelmatchclick, youareplaying, partyplaying,
            playerhasparty, chatformat, chatformatstaff;

    ItemStack itemCreate, itemJoin;
    ItemStack itemPartyFFA, itemPartyGroup, itemPartyRR;

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
        if (partys.containsKey(p)) {
            String s = chatformatstaff.replaceAll("<player>", p.getName()).replaceAll("<msg>", msg);
            partys.get(p).msg(chatformat.replaceAll("<player>", p.getName()).replaceAll("<msg>", msg));
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
        List<Party> allp = new ArrayList<>();
        for (Map.Entry<Player, Party> entry : partys.entrySet()) {
            Party party = entry.getValue();
            if (!allp.contains(party)) {
                allp.add(party);
            }
        }
        int i = 0;
        for (Party p : allp) {
            List<String> lore = new ArrayList<>();
            lore.add("§6Players:");
            for (Player o : p.players) {
                lore.add("§e  - " + o.getName());
            }
            invPartys.setItem(i, Extra.crearId(421, 0, "§e" + p.owner.getName(), lore, p.players.size()));
            i++;
        }
        refreshPartyOpeneds(allp);
    }

    public void refreshPartyOpeneds() {
        invPartysOpen.clear();
        List<Party> allp = new ArrayList<>();
        for (Map.Entry<Player, Party> entry : partys.entrySet()) {
            Party party = entry.getValue();
            if (!allp.contains(party) && party.open) {
                allp.add(party);
            }
        }
        int i = 0;
        for (Party p : allp) {
            List<String> lore = new ArrayList<>();
            lore.add("§6Players:");
            for (Player o : p.players) {
                lore.add("§e  - " + o.getName());
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
                for (Player o : p.players) {
                    lore.add("§e  - " + o.getName());
                }
                invPartysOpen.setItem(i, Extra.crearId(324, 0, "§e" + p.owner.getName(), lore, p.players.size()));
                i++;
            }
        }
    }

    public void clickItem(Player p, ItemStack i) {
        if (i.isSimilar(itemCreate)) {
            if (Extra.isPerm(p, "apvp.party.create")) {
                partys.put(p, new Party(p));
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
            partys.get(o).preguntarEntrar(p);
        }
    }

    public void aceptarPlayerAbierta(Player p, String a) {
        Player o = Bukkit.getPlayer(a);
        if (Extra.isExist(o, p)) {
            if (partys.containsKey(p)) {
                if (partys.get(p).owner == p) {
                    if (!partys.containsKey(o)) {
                        partys.get(p).aceptarPreguntar(o);
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
        if (partys.containsKey(p)) {
            Player o = Bukkit.getPlayer(a);
            if (Extra.isExist(o, p)) {
                if (Extra.isCheckPlayerPlaying(o, p)) {
                    partys.get(p).invitar(o);
                }
            }
        } else {
            p.sendMessage(youdonthaveparty);
        }
    }

    public void aceptarInvitacion(Player p, String a) {
        if (Extra.isCheckYouPlaying(p)) {
            Player o = Bukkit.getPlayer(a);
            if (partys.containsKey(o)) {
                partys.get(o).aceptarInvitado(p);
            } else {
                p.sendMessage(noexist);
                Extra.sonido(p, NOTE_BASS);
            }
        }
    }

    public void partyLeave(Player p) {
        if (partys.containsKey(p)) {
            partys.get(p).leave(p, true);
        } else {
            p.sendMessage(youdonthaveparty);
        }
    }

    public void partyKick(Player p, String a) {
        if (partys.containsKey(p)) {
            if (partys.get(p).owner == p) {
                Player o = Bukkit.getPlayer(a);
                if (Extra.isExist(o, p)) {
                    if (partys.get(p).players.contains(o)) {
                        partys.get(p).kick(o);
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
        if (partys.containsKey(p)) {
            if (partys.get(p).owner == p) {
                Player o = Bukkit.getPlayer(a);
                if (Extra.isExist(o, p)) {
                    if (partys.get(p).players.contains(o)) {
                        partys.get(p).promote(o);
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
        if (!partysDuel.containsKey(p1) || partysEvents.containsKey(game.p1)) {
            if (!partysDuel.containsKey(p2) || partysEvents.containsKey(game.p2)) {
                if (partys.containsValue(p2)) {
                    try {
                        p1.msg(partyduelsent.replaceAll("<player>", p2.owner.getName()));
                    } catch (Exception e) {
                        Bukkit.getServer().getConsoleSender().sendMessage("§4§lARENAPVP >> §cPlease send this error to @Royendero1.");
                        Bukkit.getServer().getConsoleSender().sendMessage("§6§lARENAPVP >> §ep2 = " + p2 + ", p2.owner = " + p2.owner);
                    }
                    p1.sonido(ORB_PICKUP);

                    p2.msg(duelmatch.replaceAll("<player>", p1.owner.getName()).replaceAll("<kit>", game.getKit().getKitName()));
                    p2.sonido(VILLAGER_YES);

                    String pla = p1.players.get(0).getName();
                    for (int i = 1; i < p1.players.size(); i++) {
                        pla = ", " + p1.players.get(i).getName();
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
        if (partys.containsKey(p)) {
            if (partys.get(p).owner == p) {
                Player p2 = Bukkit.getPlayer(a);
                if (preduels.containsKey(p2) && preduels.get(p2).getParty2().equals(partys.get(p))) {
                    PreDuelGame game = preduels.get(p2);
                    preduels.remove(p2);
                    if (!partysDuel.containsKey(game.p1) || partysEvents.containsKey(game.p1)) {
                        if (!partysDuel.containsKey(game.p2) || partysEvents.containsKey(game.p2)) {
                            if (Extra.checkMapAvailables(game.getKit())) {
                                DuelGame dgame = new DuelGame(game.p1, game.p2, game.getKit(), Extra.getMap(game.getKit()));
                                partysDuel.put(game.p1, dgame);
                                partysDuel.put(game.p2, dgame);
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
        if (partys.containsKey(p)) {
            partys.get(p).leave(p, false);
        }
    }
}
