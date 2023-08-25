package com.rmaafs.arenapvp.Juegos.Meetup;

import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.HORSE_ARMOR;
import static com.rmaafs.arenapvp.util.Extra.NOTE_BASS;
import static com.rmaafs.arenapvp.util.Extra.NOTE_PLING;
import static com.rmaafs.arenapvp.util.Extra.VILLAGER_YES;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static com.rmaafs.arenapvp.util.Extra.clang;
import com.rmaafs.arenapvp.manager.kit.Kit;
import static com.rmaafs.arenapvp.ArenaPvP.guis;
import static com.rmaafs.arenapvp.ArenaPvP.meetupControl;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PreConfigMeetup {

    Player p;
    Kit kit;

    String eventName;

    int minSlots, maxSlots, slots, maxLenght;
    int slotType;

    public boolean writing = false, ffa = true;

    String slotsname, cancelled, writename, nomaps;
    boolean useColors;

    Inventory invConfig;
    ItemStack itemSlots, itemFFA, itemGroup, itemStart;

    public PreConfigMeetup(Player pp, Kit k, String invName) {
        p = pp;
        kit = k;

        invConfig = Bukkit.createInventory(null, cconfig.getInt("gui.meetupconfig.rows") * 9, invName);
        minSlots = cconfig.getInt("meetup.config.minslots");
        maxSlots = cconfig.getInt("meetup.config.maxslots");
        slots = maxSlots;
        maxLenght = cconfig.getInt("meetup.config.namelenght");
        useColors = cconfig.getBoolean("meetup.config.namecolors");

        cancelled = Extra.tc(clang.getString("meetup.config.cancelled"));
        writename = Extra.tc(clang.getString("meetup.config.writename"));
        nomaps = Extra.tc(clang.getString("meetup.config.nomaps"));

        slotType = cconfig.getInt("gui.meetupconfig.type.slot");

        slotsname = clang.getString("gui.meetupconfig.items.slots.name");
        itemSlots = Extra.crearId(cconfig.getInt("gui.meetupconfig.slots.id"), cconfig.getInt("gui.meetupconfig.slots.data-value"), slotsname.replaceAll("<slots>", "" + slots), clang.getStringList("gui.meetupconfig.items.slots.lore"), slots);
        itemFFA = Extra.crearId(cconfig.getInt("gui.meetupconfig.type.ffa.id"), cconfig.getInt("gui.meetupconfig.type.ffa.data-value"), clang.getString("gui.meetupconfig.items.type.ffa.name").replaceAll("<slots>", "" + slots), clang.getStringList("gui.meetupconfig.items.type.ffa.lore"), 1);
        itemGroup = Extra.crearId(cconfig.getInt("gui.meetupconfig.type.group.id"), cconfig.getInt("gui.meetupconfig.type.group.data-value"), clang.getString("gui.meetupconfig.items.type.group.name").replaceAll("<slots>", "" + slots), clang.getStringList("gui.meetupconfig.items.type.group.lore"), 1);
        itemStart = Extra.crearId(cconfig.getInt("gui.meetupconfig.start.id"), cconfig.getInt("gui.meetupconfig.start.data-value"), clang.getString("gui.meetupconfig.items.start.name").replaceAll("<slots>", "" + slots), clang.getStringList("gui.meetupconfig.items.start.lore"), 1);

        invConfig.setItem(cconfig.getInt("gui.meetupconfig.slots.slot"), itemSlots);
        invConfig.setItem(slotType, itemFFA);
        invConfig.setItem(cconfig.getInt("gui.meetupconfig.start.slot"), itemStart);
        invConfig.setItem(invConfig.getSize() - 1, guis.itemLeave);

        openInv();
    }

    public void openInv() {
        p.openInventory(invConfig);
    }

    public void click(ItemStack i, boolean right) {
        if (i.isSimilar(itemSlots)) {
            if (right) {
                if (slots < maxSlots) {
                    slots++;
                }
            } else {
                if (slots > minSlots) {
                    slots--;
                }
            }
            Extra.changeName(i, slotsname.replaceAll("<slots>", "" + slots));
            i.setAmount(slots);
            itemSlots = i;
            Extra.sonido(p, NOTE_PLING);
        } else if (i.isSimilar(itemFFA)) {
//            invConfig.setItem(slotType, itemGroup);
            p.sendMessage("§cYou can change coming soon...");
            Extra.sonido(p, HORSE_ARMOR);
        } else if (i.isSimilar(itemGroup)) {
            invConfig.setItem(slotType, itemFFA);
            Extra.sonido(p, HORSE_ARMOR);
        } else if (i.isSimilar(guis.itemLeave)) {
            meetupControl.creandoEventoMeetup.remove(p);
            p.closeInventory();
            p.sendMessage(cancelled);
            Extra.sonido(p, NOTE_BASS);
        } else if (i.isSimilar(itemStart)) {
            writing = true;
            p.sendMessage(writename);
            Extra.sonido(p, VILLAGER_YES);
            p.closeInventory();
        }
    }

    public void start(String title) {
        if (title.length() > maxLenght) {
            title = title.substring(0, maxLenght);
        }
        if (!useColors) {
            title = ChatColor.stripColor(title);
        }
        meetupControl.creandoEventoMeetup.remove(p);
        if (meetupControl.existMap(kit)) {
            GameMeetup game = new GameMeetup(p, title, kit, slots, invConfig.getItem(slotType).isSimilar(itemFFA), meetupControl.getMap(kit), minSlots);
            meetupControl.meetupsPlaying.put(p, game);

            meetupControl.meetups.put(meetupControl.addEvent(game), game);
            Extra.setScore(p, Score.TipoScore.MEETUPWAITING);
        } else {
            p.sendMessage(nomaps);
            Extra.sonido(p, NOTE_BASS);
        }
    }
}
