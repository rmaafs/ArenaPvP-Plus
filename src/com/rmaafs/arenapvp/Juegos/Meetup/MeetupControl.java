package com.rmaafs.arenapvp.Juegos.Meetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import com.rmaafs.arenapvp.Extra;
import static com.rmaafs.arenapvp.Extra.FIREWORK_LARGE_BLAST;
import static com.rmaafs.arenapvp.Extra.cconfig;
import static com.rmaafs.arenapvp.Extra.clang;
import static com.rmaafs.arenapvp.Extra.mapMeetupLibres;
import static com.rmaafs.arenapvp.Extra.mapMeetupOcupadas;
import com.rmaafs.arenapvp.Kit;
import static com.rmaafs.arenapvp.Main.extraLang;
import static com.rmaafs.arenapvp.Main.guis;
import com.rmaafs.arenapvp.MapaMeetup;
import com.rmaafs.arenapvp.Score;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MeetupControl {

    public List<Player> esperandoMapaMeetup = new ArrayList<>();
    public List<Player> esperandoCrearEvento = new ArrayList<>();
    public HashMap<Player, CreandoMapaMeetup> creandoMapaMeetup = new HashMap<>();
    public HashMap<Player, PreConfigMeetup> creandoEventoMeetup = new HashMap<>();

    public HashMap<Integer, GameMeetup> meetups = new HashMap<>();
    public HashMap<Player, GameMeetup> meetupsPlaying = new HashMap<>();

    public List<GameMeetup> meetupStarting = new ArrayList<>();

    public List<Player> viendoMiddle = new ArrayList<>();

    public Inventory invMeetup;
    public ItemStack itemCreate;

    public String invMeetupConfigName, noexistmeetup;

    public MeetupControl() {
        setConfig();
    }

    public void setConfig() {

        invMeetupConfigName = Extra.tc(clang.getString("gui.meetupconfig.name"));
        noexistmeetup = Extra.tc(clang.getString("meetup.game.noexistmeetup"));

        invMeetup = Bukkit.createInventory(null, cconfig.getInt("gui.meetup.rows") * 9, Extra.tc(clang.getString("gui.meetup.name")));

        itemCreate = Extra.crearId(cconfig.getInt("gui.meetup.create.id"), cconfig.getInt("gui.meetup.create.data-value"), clang.getString("gui.meetup.items.create.name"), clang.getStringList("gui.meetup.items.create.lore"), 1);

        invMeetup.setItem(invMeetup.getSize() - 1, guis.itemLeave);
        invMeetup.setItem(invMeetup.getSize() - 5, itemCreate);

    }

    public void crearEvento(Player p, ItemStack slot) {
        Kit k = guis.getKitItem(slot);
        if (Extra.isPerm(p, "apvp.meetup.create")) {
            if ((p.hasPermission("apvp.meetup.create." + k.getKitName().toLowerCase())) || (p.hasPermission("apvp.meetup.create.*") && p.hasPermission("apvp.meetup.create." + k.getKitName().toLowerCase()))) {
                creandoEventoMeetup.put(p, new PreConfigMeetup(p, k, invMeetupConfigName));
            } else {
                p.sendMessage("§cNo permission.");
            }
        }
    }

    public void crearMapa(Player p, int slot) {
        creandoMapaMeetup.put(p, new CreandoMapaMeetup(p, guis.getKitSlot(slot)));
    }

    public void openInvMeetup(Player p) {
        p.openInventory(invMeetup);
        Extra.sonido(p, FIREWORK_LARGE_BLAST);
    }

    public int addEvent(GameMeetup e) {
        for (int i = 0; i < invMeetup.getSize() - 9; i++) {
            if (invMeetup.getItem(i) == null) {
                ItemStack item = Extra.crearId(e.kit.itemOnGui.getTypeId(), e.kit.itemOnGui.getData().getData(), "§e" + e.title, e.lore, 1);
                invMeetup.setItem(i, item);
                e.guislot = i;
                meetupStarting.add(e);
                return i;
            }
        }
        return 0;
    }

    public void clickItem(Player p, int slot, boolean middle) {
        if (Extra.isCheckYouPlaying(p)) {
            if (meetups.containsKey(slot)) {
                if (middle) {
                    viendoMiddle.add(p);
                    guis.kitPreview2(p, meetups.get(slot).kit);
                } else {
                    if (Extra.isPerm2(p, "apvp.meetup.join.kit.*", "apvp.meetup.join.kit." + meetups.get(slot).kit.getKitName().toLowerCase())) {
                        p.closeInventory();
                        Extra.limpiarP(p);
                        meetups.get(slot).join(p);
                        meetupsPlaying.put(p, meetups.get(slot));
                        Extra.setScore(p, Score.TipoScore.MEETUPWAITING);
                        refreshItem(slot);
                    }
                }
            } else {
                p.sendMessage(noexistmeetup);
            }
        } else {
            p.closeInventory();
        }
    }

    public void refreshItem(int slot) {
        ItemStack item = invMeetup.getItem(slot);
        Extra.changeLore(item, meetups.get(slot).lore);
        item.setAmount(meetups.get(slot).players.size());
    }

    public void removeItem(int slot) {
        meetups.remove(slot);
        invMeetup.remove(invMeetup.getItem(slot));
    }

    public boolean existMap(Kit k) {
        return (mapMeetupLibres.containsKey(k) && !mapMeetupLibres.get(k).isEmpty());
    }

    public MapaMeetup getMap(Kit k) {
        MapaMeetup m;
        if (extraLang.chooserandommaps) {
            Random r = new Random();
            m = mapMeetupLibres.get(k).get(r.nextInt(mapMeetupLibres.get(k).size()));
        } else {
            m = mapMeetupLibres.get(k).get(0);
        }
        mapMeetupLibres.get(k).remove(m);
        mapMeetupOcupadas.get(k).add(m);
        return m;
    }

    public void sacar(Player p) {
        if (esperandoMapaMeetup.contains(p)) {
            esperandoMapaMeetup.remove(p);
        }
        if (esperandoCrearEvento.contains(p)) {
            esperandoCrearEvento.remove(p);
        }
        if (viendoMiddle.contains(p)) {
            viendoMiddle.remove(p);
        }
        if (creandoMapaMeetup.containsKey(p)) {
            creandoMapaMeetup.remove(p);
        }
        if (creandoEventoMeetup.containsKey(p)) {
            creandoEventoMeetup.remove(p);
        }

        if (meetupsPlaying.containsKey(p)) {
            meetupsPlaying.remove(p);
        }
    }
}
