package com.rmaafs.arenapvp.Hotbars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.rmaafs.arenapvp.util.Convertor;
import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.ITEM_PICKUP;
import static com.rmaafs.arenapvp.util.Extra.NOTE_BASS;
import static com.rmaafs.arenapvp.util.Extra.VILLAGER_YES;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static com.rmaafs.arenapvp.util.Extra.clang;
import static com.rmaafs.arenapvp.util.Extra.jugandoUno;
import static com.rmaafs.arenapvp.util.Extra.playerConfig;
import com.rmaafs.arenapvp.manager.kit.Kit;
import static com.rmaafs.arenapvp.ArenaPvP.duelControl;
import static com.rmaafs.arenapvp.ArenaPvP.extraLang;
import static com.rmaafs.arenapvp.ArenaPvP.guis;
import static com.rmaafs.arenapvp.ArenaPvP.hotbars;
import static com.rmaafs.arenapvp.ArenaPvP.meetupControl;
import static com.rmaafs.arenapvp.ArenaPvP.partyControl;
import static com.rmaafs.arenapvp.ArenaPvP.specControl;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Hotbars {

    public HashMap<Player, Kit> editingHotbar = new HashMap<>();
    public HashMap<Player, EditingHotbar> editingSlotHotbar = new HashMap<>();

    public List<Player> esperandoEscojaHotbar = new ArrayList<>();

    public Inventory invHotbars;

    public ItemStack itemRanked, itemUnRanked, itemEditHotbar, itemLeave, itemMeetup,
            itemParty, itemExtra1, itemExtra2;
    public ItemStack itemHotbar1, itemHotbar2, itemHotbar3, itemHotbar4, itemHotbar5;
    public int slotRanked, slotUnRanked, slotEditHotbar, slotLeave, slotMeetup, slotParty, slotExtra1, slotExtra2;
    public boolean useRanked, useUnRanked, useEditHotbar, useMeetup, useParty, useExtra1, useExtra2;
    public String editinghotbarmsg, editinghotbarsaved, commandExtra1, commandExtra2;

    public Hotbars() {
        setConfig();
    }

    public void setConfig() {
        editinghotbarmsg = Extra.tc(clang.getString("editinghotbar.msg"));
        editinghotbarsaved = Extra.tc(clang.getString("editinghotbar.saved"));

        invHotbars = Bukkit.createInventory(null, 9, Extra.tc(clang.getString("gui.editinghotbarchoosenumber.name")));

        itemHotbar1 = Extra.crearId(cconfig.getInt("gui.editinghotbarchoosenumber.id"), cconfig.getInt("gui.editinghotbarchoosenumber.data-value"), clang.getString("gui.editinghotbarchoosenumber.items.name").replaceAll("<number>", "1"), clang.getStringList("gui.editinghotbarchoosenumber.items.lore"), 1);
        itemHotbar2 = Extra.crearId(cconfig.getInt("gui.editinghotbarchoosenumber.id"), cconfig.getInt("gui.editinghotbarchoosenumber.data-value"), clang.getString("gui.editinghotbarchoosenumber.items.name").replaceAll("<number>", "2"), clang.getStringList("gui.editinghotbarchoosenumber.items.lore"), 2);
        itemHotbar3 = Extra.crearId(cconfig.getInt("gui.editinghotbarchoosenumber.id"), cconfig.getInt("gui.editinghotbarchoosenumber.data-value"), clang.getString("gui.editinghotbarchoosenumber.items.name").replaceAll("<number>", "3"), clang.getStringList("gui.editinghotbarchoosenumber.items.lore"), 3);
        itemHotbar4 = Extra.crearId(cconfig.getInt("gui.editinghotbarchoosenumber.id"), cconfig.getInt("gui.editinghotbarchoosenumber.data-value"), clang.getString("gui.editinghotbarchoosenumber.items.name").replaceAll("<number>", "4"), clang.getStringList("gui.editinghotbarchoosenumber.items.lore"), 4);
        itemHotbar5 = Extra.crearId(cconfig.getInt("gui.editinghotbarchoosenumber.id"), cconfig.getInt("gui.editinghotbarchoosenumber.data-value"), clang.getString("gui.editinghotbarchoosenumber.items.name").replaceAll("<number>", "5"), clang.getStringList("gui.editinghotbarchoosenumber.items.lore"), 5);

        itemLeave = Extra.crearId(cconfig.getInt("hotbar.leave.id"), cconfig.getInt("hotbar.leave.data-value"), clang.getString("hotbar.leave.name"), clang.getStringList("hotbar.leave.lore"), 1);
        slotLeave = cconfig.getInt("hotbar.leave.slot");
        
        itemExtra1 = Extra.crearId(cconfig.getInt("hotbar.extra1.id"), cconfig.getInt("hotbar.extra1.data-value"), clang.getString("hotbar.extra1.name"), clang.getStringList("hotbar.extra1.lore"), 1);
        slotExtra1 = cconfig.getInt("hotbar.extra1.slot");
        useExtra1 = cconfig.getBoolean("hotbar.extra1.use");
        commandExtra1 = cconfig.getString("hotbar.extra1.cmd");
        
        itemExtra2 = Extra.crearId(cconfig.getInt("hotbar.extra2.id"), cconfig.getInt("hotbar.extra2.data-value"), clang.getString("hotbar.extra2.name"), clang.getStringList("hotbar.extra2.lore"), 1);
        slotExtra2 = cconfig.getInt("hotbar.extra2.slot");
        useExtra2 = cconfig.getBoolean("hotbar.extra2.use");
        commandExtra2 = cconfig.getString("hotbar.extra2.cmd");

        invHotbars.setItem(2, itemHotbar1);
        invHotbars.setItem(3, itemHotbar2);
        invHotbars.setItem(4, itemHotbar3);
        invHotbars.setItem(5, itemHotbar4);
        invHotbars.setItem(6, itemHotbar5);

        useRanked = cconfig.getBoolean("hotbar.ranked.use");
        if (useRanked) {
            itemRanked = Extra.crearId(cconfig.getInt("hotbar.ranked.id"), cconfig.getInt("hotbar.ranked.data-value"), clang.getString("hotbar.ranked.name"), clang.getStringList("hotbar.ranked.lore"), 1);
            slotRanked = cconfig.getInt("hotbar.ranked.slot");
        }

        useUnRanked = cconfig.getBoolean("hotbar.unranked.use");
        if (useUnRanked) {
            itemUnRanked = Extra.crearId(cconfig.getInt("hotbar.unranked.id"), cconfig.getInt("hotbar.unranked.data-value"), clang.getString("hotbar.unranked.name"), clang.getStringList("hotbar.unranked.lore"), 1);
            slotUnRanked = cconfig.getInt("hotbar.unranked.slot");
        }

        useEditHotbar = cconfig.getBoolean("hotbar.edithotbar.use");
        if (useEditHotbar) {
            itemEditHotbar = Extra.crearId(cconfig.getInt("hotbar.edithotbar.id"), cconfig.getInt("hotbar.edithotbar.data-value"), clang.getString("hotbar.edithotbar.name"), clang.getStringList("hotbar.edithotbar.lore"), 1);
            slotEditHotbar = cconfig.getInt("hotbar.edithotbar.slot");
        }

        useMeetup = cconfig.getBoolean("hotbar.meetup.use");
        if (useMeetup) {
            itemMeetup = Extra.crearId(cconfig.getInt("hotbar.meetup.id"), cconfig.getInt("hotbar.meetup.data-value"), clang.getString("hotbar.meetup.name"), clang.getStringList("hotbar.meetup.lore"), 1);
            slotMeetup = cconfig.getInt("hotbar.meetup.slot");
        }

        useParty = cconfig.getBoolean("hotbar.party.use");
        if (useParty) {
            itemParty = Extra.crearId(cconfig.getInt("hotbar.party.id"), cconfig.getInt("hotbar.party.data-value"), clang.getString("hotbar.party.name"), clang.getStringList("hotbar.party.lore"), 1);
            slotParty = cconfig.getInt("hotbar.party.slot");
        }
    }

    public void setMain(Player p) {
        if (useRanked) {
            p.getInventory().setItem(slotRanked, itemRanked);
        }
        if (useUnRanked) {
            p.getInventory().setItem(slotUnRanked, itemUnRanked);
        }
        if (useEditHotbar) {
            p.getInventory().setItem(slotEditHotbar, itemEditHotbar);
        }
        if (useMeetup) {
            p.getInventory().setItem(slotMeetup, itemMeetup);
        }

        if (useParty) {
            p.getInventory().setItem(slotParty, itemParty);
        }
        
        if (useExtra1) {
            p.getInventory().setItem(slotExtra1, itemExtra1);
        }
        if (useExtra2) {
            p.getInventory().setItem(slotExtra2, itemExtra2);
        }
        p.spigot().setCollidesWithEntities(true);
    }

    public void setLeave(Player p) {
        Extra.cleanPlayer(p);
        p.getInventory().setItem(slotLeave, itemLeave);
    }

    public void clickLeave(Player p) {
        if (duelControl.esperandoUnRanked.containsValue(p)) {
            duelControl.sacarUnRanked(p);
        } else if (duelControl.esperandoRanked.containsValue(p)) {
            duelControl.sacarRanked(p);
        } else if (meetupControl.meetupsPlaying.containsKey(p)) {
            meetupControl.meetupsPlaying.get(p).leave(p, true);
        } else if (partyControl.partys.containsKey(p)) {
            partyControl.partys.get(p).leave(p, true);
        } else if (specControl.mirando.containsKey(p)) {
            specControl.leave(p, true);
        }
        Extra.cleanPlayer(p);
        setMain(p);
        Extra.sonido(p, NOTE_BASS);
        Extra.setScore(p, Score.TipoScore.MAIN);
    }

    public void clickInv(Player p, ItemStack i) {
        p.openInventory(invHotbars);
        editingHotbar.put(p, guis.itemKits.get(i));
    }

    public void clickLibro(Player p, int amount, Kit k, boolean middle) {
        extraLang.teleportSpawnHotbar(p);
        Extra.cleanPlayer(p);
        p.setGameMode(GameMode.ADVENTURE);
        playerConfig.get(p).putHotbar(amount, k);
        editingHotbar.remove(p);
        editingSlotHotbar.put(p, new EditingHotbar(k, amount));
        p.sendMessage(editinghotbarmsg.replaceAll("<cmd>", extraLang.commandsavehotbar));
        Extra.sonido(p, VILLAGER_YES);
        p.closeInventory();
    }

    public void guardarHotbar(Player p) {
        Kit k = editingSlotHotbar.get(p).getKit();
        int slot = editingSlotHotbar.get(p).getSlot();
        playerConfig.get(p).getHotbars(k).setHotbar(slot, p.getInventory().getContents());

        guis.kitsHotbar.get(k).cfile.set(p.getName() + "." + slot, Convertor.itemToBase64(p.getInventory().getContents()));
        //guis.kitsHotbar.get(k).save();

        editingSlotHotbar.remove(p);
        Extra.cleanPlayer(p);
        extraLang.teleportSpawn(p);
        p.sendMessage(editinghotbarsaved.replaceAll("<kit>", k.kitName).replaceAll("<slot>", "" + slot));
        Extra.sonido(p, VILLAGER_YES);
        hotbars.setMain(p);
    }

    public void ponerItemsHotbar(Player p) {
        p.getInventory().setItem(0, itemHotbar1);
        p.getInventory().setItem(2, itemHotbar2);
        p.getInventory().setItem(3, itemHotbar3);
        p.getInventory().setItem(4, itemHotbar4);
        p.getInventory().setItem(5, itemHotbar5);
        esperandoEscojaHotbar.add(p);
    }

    public void clickPonerHotbar(Player p, int amount) {
        if (jugandoUno.containsKey(p)) {
            Extra.cleanPlayer(p);
            playerConfig.get(p).putInv(amount, jugandoUno.get(p).kit);
            Extra.sonido(p, ITEM_PICKUP);
            esperandoEscojaHotbar.remove(p);
            p.updateInventory();
        } else if (meetupControl.meetupsPlaying.containsKey(p)) {
            Extra.cleanPlayer(p);
            playerConfig.get(p).putInv(amount, meetupControl.meetupsPlaying.get(p).kit);
            Extra.sonido(p, ITEM_PICKUP);
            esperandoEscojaHotbar.remove(p);
            p.updateInventory();
        } else if (partyControl.partys.containsKey(p)) {
            Extra.cleanPlayer(p);
            if (partyControl.partysEvents.containsKey(partyControl.partys.get(p))) {
                playerConfig.get(p).putInv(amount, partyControl.partysEvents.get(partyControl.partys.get(p)).kit);
            } else if (partyControl.partysDuel.containsKey(partyControl.partys.get(p))) {
                playerConfig.get(p).putInv(amount, partyControl.partysDuel.get(partyControl.partys.get(p)).kit);
            }
            Extra.sonido(p, ITEM_PICKUP);
            esperandoEscojaHotbar.remove(p);
            p.updateInventory();
        }
    }

    public void sacar(Player p) {
        if (editingHotbar.containsKey(p)) {
            editingHotbar.remove(p);
        }
        if (editingSlotHotbar.containsKey(p)) {
            editingSlotHotbar.remove(p);
        }
        if (esperandoEscojaHotbar.contains(p)) {
            esperandoEscojaHotbar.remove(p);
        }
    }
}
