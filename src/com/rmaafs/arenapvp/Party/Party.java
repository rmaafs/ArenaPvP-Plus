package com.rmaafs.arenapvp.Party;

import java.util.ArrayList;
import java.util.List;
import com.rmaafs.arenapvp.Extra;
import static com.rmaafs.arenapvp.Extra.BURP;
import static com.rmaafs.arenapvp.Extra.CAT_MEOW;
import static com.rmaafs.arenapvp.Extra.LEVEL_UP;
import static com.rmaafs.arenapvp.Extra.NOTE_BASS;
import static com.rmaafs.arenapvp.Extra.NOTE_PLING;
import static com.rmaafs.arenapvp.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.Extra.VILLAGER_YES;
import static com.rmaafs.arenapvp.Extra.cconfig;
import static com.rmaafs.arenapvp.Extra.clang;
import com.rmaafs.arenapvp.Kit;
import static com.rmaafs.arenapvp.Main.extraLang;
import static com.rmaafs.arenapvp.Main.guis;
import static com.rmaafs.arenapvp.Main.hotbars;
import static com.rmaafs.arenapvp.Main.partyControl;
import com.rmaafs.arenapvp.Score;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Party {

    public Player owner;
    public List<Player> players = new ArrayList<>();
    List<Player> invitados = new ArrayList<>();
    List<Player> preguntar = new ArrayList<>();
    boolean open = false;

    public Inventory invConfig;
    public ItemStack itemConfigOpen;

    public ItemStack itemRanked2, itemRanked3, itemRanked4, itemEvent, itemDuel, itemConfig;
    int slotRanked2, slotRanked3, slotRanked4, slotEvent, slotDuel, slotConfig;
    boolean useRanked2, useRanked3, useRanked4, useEvent, useDuel, useConfig;

    List<String> created;
    public int minplayers, maxplayers;
    String alreadyinvitedthisplayer, youareinvited, clickMsg, dontinvited, playerjoined,
            duelsent, playerintheparty, playerleave, newowner, needminplayers, partyfull,
            playerkicked, partyopen, partyclosed, wantjoin, wantjoinclick, wantjoinhover,
            invitesent, youalreadysent, noarewaiting;

    public Party(Player o) {
        owner = o;
        players.add(o);
        Extra.cleanPlayer(o);

        minplayers = cconfig.getInt("party.events.minplayers");
        maxplayers = cconfig.getInt("party.maxplayers");

        created = Extra.tCC(clang.getStringList("party.create.created"));

        itemRanked2 = Extra.crearId(cconfig.getInt("hotbar.party.ranked2.id"), cconfig.getInt("hotbar.party.ranked2.data-value"), clang.getString("hotbar.party.ranked2.name"), clang.getStringList("hotbar.party.ranked2.lore"), 2);
        itemRanked3 = Extra.crearId(cconfig.getInt("hotbar.party.ranked3.id"), cconfig.getInt("hotbar.party.ranked3.data-value"), clang.getString("hotbar.party.ranked3.name"), clang.getStringList("hotbar.party.ranked3.lore"), 3);
        itemRanked4 = Extra.crearId(cconfig.getInt("hotbar.party.ranked4.id"), cconfig.getInt("hotbar.party.ranked4.data-value"), clang.getString("hotbar.party.ranked4.name"), clang.getStringList("hotbar.party.ranked4.lore"), 4);
        itemEvent = Extra.crearId(cconfig.getInt("hotbar.party.event.id"), cconfig.getInt("hotbar.party.event.data-value"), clang.getString("hotbar.party.event.name"), clang.getStringList("hotbar.party.event.lore"), 1);
        itemDuel = Extra.crearId(cconfig.getInt("hotbar.party.duel.id"), cconfig.getInt("hotbar.party.duel.data-value"), clang.getString("hotbar.party.duel.name"), clang.getStringList("hotbar.party.duel.lore"), 1);
        itemConfig = Extra.crearId(cconfig.getInt("hotbar.party.config.id"), cconfig.getInt("hotbar.party.config.data-value"), clang.getString("hotbar.party.config.name"), clang.getStringList("hotbar.party.config.lore"), 1);

        slotRanked2 = cconfig.getInt("hotbar.party.ranked2.slot");
        slotRanked3 = cconfig.getInt("hotbar.party.ranked3.slot");
        slotRanked4 = cconfig.getInt("hotbar.party.ranked4.slot");
        slotEvent = cconfig.getInt("hotbar.party.event.slot");
        slotDuel = cconfig.getInt("hotbar.party.duel.slot");
        slotConfig = cconfig.getInt("hotbar.party.config.slot");

        useRanked2 = cconfig.getBoolean("hotbar.party.ranked2.use");
        useRanked3 = cconfig.getBoolean("hotbar.party.ranked3.use");
        useRanked4 = cconfig.getBoolean("hotbar.party.ranked4.use");
        useEvent = cconfig.getBoolean("hotbar.party.event.use");
        useDuel = cconfig.getBoolean("hotbar.party.duel.use");
        useConfig = cconfig.getBoolean("hotbar.party.config.use");

        dontinvited = Extra.tc(clang.getString("party.create.dontinvited"));
        alreadyinvitedthisplayer = Extra.tc(clang.getString("party.create.alreadyinvitedthisplayer"));
        youareinvited = Extra.tc(clang.getString("party.create.youareinvited"));
        clickMsg = Extra.tc(clang.getString("party.create.clicktoacceptmsg"));
        playerjoined = Extra.tc(clang.getString("party.create.playerjoined"));
        duelsent = Extra.tc(clang.getString("party.create.duelsent"));
        playerintheparty = Extra.tc(clang.getString("party.create.playerintheparty"));
        playerleave = Extra.tc(clang.getString("party.create.playerleave"));
        newowner = Extra.tc(clang.getString("party.create.newowner"));
        needminplayers = Extra.tc(clang.getString("party.create.needminplayers"));
        partyfull = Extra.tc(clang.getString("party.create.partyfull"));
        playerkicked = Extra.tc(clang.getString("party.create.playerkicked"));
        partyopen = Extra.tc(clang.getString("party.create.partyopen"));
        partyclosed = Extra.tc(clang.getString("party.create.partyclosed"));
        wantjoin = Extra.tc(clang.getString("party.create.wantjoin"));
        wantjoinclick = Extra.tc(clang.getString("party.create.wantjoinclick"));
        wantjoinhover = Extra.tc(clang.getString("party.create.wantjoinhover"));
        invitesent = Extra.tc(clang.getString("party.create.invitesent"));
        youalreadysent = Extra.tc(clang.getString("party.create.youalreadysent"));
        noarewaiting = Extra.tc(clang.getString("party.create.noarewaiting"));

        invConfig = Bukkit.createInventory(null, cconfig.getInt("gui.party.config.rows") * 9, partyControl.invConfigName);
        itemConfigOpen = Extra.crearId(cconfig.getInt("gui.party.config.open.id"), cconfig.getInt("gui.party.config.open.data-value"), clang.getString("gui.party.config.items.open.name"), clang.getStringList("gui.party.config.items.open.lore"), 1);

        invConfig.setItem(cconfig.getInt("gui.party.config.open.slot"), itemConfigOpen);

        extraLang.teleportSpawn(o);
        setHotbar(o);
        for (String s : created) {
            o.sendMessage(s);
        }
        Extra.sonido(o, CAT_MEOW);
    }

    public void setHotbar(Player p) {
        if (!partyControl.partysEvents.containsKey(this)) {
            if (useRanked2) {
                p.getInventory().setItem(slotRanked2, itemRanked2);
            }
            if (useRanked3) {
                p.getInventory().setItem(slotRanked3, itemRanked3);
            }
            if (useRanked4) {
                p.getInventory().setItem(slotRanked4, itemRanked4);
            }
            if (useEvent) {
                p.getInventory().setItem(slotEvent, itemEvent);
            }
            if (useDuel) {
                p.getInventory().setItem(slotDuel, itemDuel);
            }
            if (useConfig) {
                p.getInventory().setItem(slotConfig, itemConfig);
            }
            p.getInventory().setItem(8, hotbars.itemLeave);
        }
    }

    public void clickItemHotbar(Player p, PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item.isSimilar(itemEvent)) {
            if (Extra.isPerm(p, "apvp.party.events")) {
                partyControl.openInvEvents(p);
            }
        } else if (item.isSimilar(itemDuel)) {
            if (Extra.isPerm(p, "apvp.party.duel")) {
                partyControl.openInvPartys(p);
            }
        } else if (item.isSimilar(itemConfig)) {
            if (Extra.isPerm(p, "apvp.party.config")) {
                p.openInventory(invConfig);
            }
        } else if (item.isSimilar(itemRanked2)
                || item.isSimilar(itemRanked3)
                || item.isSimilar(itemRanked4)) {
            p.sendMessage("§cComing soon...");
        } else {
            e.setCancelled(false);
        }
    }

    public void clickGui(Player p, ItemStack i) {
        if (i.isSimilar(partyControl.itemPartyFFA)) {
            if (Extra.isPerm(p, "apvp.party.event.ffa")) {
                if (players.size() >= minplayers) {
                    p.openInventory(guis.invChooseKit);
                    partyControl.partysEvents.put(this, new EventGame(EventGame.Tipo.FFA, p));
                } else {
                    p.closeInventory();
                    p.sendMessage(needminplayers.replaceAll("<min>", "" + minplayers));
                    Extra.sonido(p, NOTE_BASS);
                }
            }
        } else if (i.isSimilar(itemConfigOpen)) {
            if (Extra.isPerm(p, "apvp.party.config.open")) {
                if (i.containsEnchantment(Enchantment.LUCK)) {
                    i.removeEnchantment(Enchantment.LUCK);
                    open = false;
                    msg(partyclosed);
                    sonido(NOTE_PLING);
                    partyControl.refreshPartyOpeneds();
                } else {
                    i.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    open = true;
                    msg(partyopen);
                    sonido(NOTE_BASS);
                    partyControl.refreshPartyOpeneds();
                }
                itemConfigOpen = i;
            }
        } else if (i.isSimilar(partyControl.itemPartyGroup)
                || i.isSimilar(partyControl.itemPartyRR)) {
            p.sendMessage("§cComing soon...");
            p.closeInventory();
        }
    }

    public void clickGuiPartys(Player p, ItemStack i) {
        if (p == owner) {
            String nick = ChatColor.stripColor(i.getItemMeta().getDisplayName());
            if (!nick.equals(p.getName())) {
                Player p2 = Bukkit.getPlayer(nick);
                partyControl.preduels.put(p, new PreDuelGame(partyControl.partys.get(p), partyControl.partys.get(p2)));
                p.openInventory(guis.invChooseKit);
            }
        }
    }

    public void mandarDuel(Kit k) {
        PreDuelGame game = partyControl.preduels.get(owner);
        game.setKit(k);
        partyControl.preguntarDuel(game);
        owner.closeInventory();
    }

    public void invitar(Player p) {
        if (!players.contains(p)) {
            if (players.size() < maxplayers) {
                if (!invitados.contains(p) && p != owner) {
                    String pla = players.get(0).getName();
                    for (int i = 1; i < players.size(); i++) {
                        pla = ", " + players.get(i).getName();
                    }
                    invitados.add(p);
                    p.sendMessage(youareinvited.replaceAll("<player>", owner.getName()));
                    Extra.text(p, clickMsg, "§e" + pla, "/party accept " + owner.getName(), "GREEN");
                    Extra.sonido(owner, CAT_MEOW);
                    owner.sendMessage(duelsent.replaceAll("<player>", p.getName()));
                    Extra.sonido(owner, VILLAGER_YES);
                } else {
                    owner.sendMessage(alreadyinvitedthisplayer);
                }
            } else {
                msg(partyfull);
            }
        } else {
            owner.sendMessage(playerintheparty);
            Extra.sonido(owner, NOTE_BASS);
        }
    }

    public void aceptarInvitado(Player p) {
        if (invitados.contains(p)) {
            if (players.size() < maxplayers) {
                invitados.remove(p);
                join(p);
            } else {
                p.sendMessage(partyfull);
            }
        } else {
            p.sendMessage(dontinvited);
            Extra.sonido(p, NOTE_BASS);
        }
    }

    public void preguntarEntrar(Player p) {
        if (!preguntar.contains(p)) {
            if (Extra.isCheckYouPlaying(p)) {
                preguntar.add(p);
                msg(wantjoin.replaceAll("<player>", p.getName()));
                sonido(BURP);
                Extra.text(owner, wantjoinclick, wantjoinhover, "/party acceptplayer " + p.getName(), "AQUA");
                p.sendMessage(invitesent);
                Extra.sonido(p, LEVEL_UP);
            }
            p.closeInventory();
        } else {
            p.sendMessage(youalreadysent);
            Extra.sonido(p, NOTE_BASS);
            p.closeInventory();
        }
    }

    public void aceptarPreguntar(Player p) {
        if (preguntar.contains(p)) {
            if (Extra.isExist(p, owner)) {
                if (Extra.isCheckPlayerPlaying(p, owner)) {
                    preguntar.remove(p);
                    join(p);
                }
            }
        } else {
            owner.sendMessage(noarewaiting);
        }
    }

    public void join(Player p) {
        partyControl.partys.put(p, partyControl.partys.get(owner));
        players.add(p);
        Extra.cleanPlayer(p);
        hotbars.setLeave(p);
        msg(playerjoined.replaceAll("<player>", p.getName()));
        sonido(ORB_PICKUP);
        partyControl.refreshPartyItems();
        Extra.setScore(p, Score.TipoScore.PARTYMAIN);
    }

    public void leave(Player p, boolean online) {
        msg(playerleave.replaceAll("<player>", p.getName()));
        sonido(NOTE_BASS);
        partyControl.partys.remove(p);
        players.remove(p);

        if (partyControl.partysEvents.containsKey(this)) {
            partyControl.partysEvents.get(this).leave(p, online);
        }
        if (partyControl.partysDuel.containsKey(this)) {
            partyControl.partysDuel.get(this).leave(p, online);
        }

        if (online) {
            Extra.cleanPlayer(p);
            extraLang.teleportSpawn(p);
            hotbars.setMain(p);
            Extra.setScore(p, Score.TipoScore.MAIN);
        }

        if (p == owner && players.size() > 0) {
            promote(players.get(0));
        }
        partyControl.refreshPartyItems();
    }

    public void kick(Player p) {
        if (p != owner) {
            msg(playerkicked.replaceAll("<player>", p.getName()));
            leave(p, p.isOnline());
        }
    }

    public void promote(Player p) {
        if (p != owner) {
            Extra.cleanPlayer(owner);
            hotbars.setLeave(owner);
            owner = p;
            setHotbar(p);
            msg(newowner.replaceAll("<player>", p.getName()));
            sonido(CAT_MEOW);
            partyControl.refreshPartyItems();
        }
    }

    public void msg(String s) {
        for (Player p : players) {
            p.sendMessage(s);
        }
    }

    public void sonido(String s) {
        for (Player p : players) {
            Extra.sonido(p, s);
        }
    }
}
