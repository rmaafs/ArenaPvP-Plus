package com.rmaafs.arenapvp.GUIS;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rmaafs.arenapvp.Juegos.Duel.PreDuelConfig;
import com.rmaafs.arenapvp.Extra;
import static com.rmaafs.arenapvp.Extra.CHEST_CLOSE;
import static com.rmaafs.arenapvp.Extra.HORSE_ARMOR;
import static com.rmaafs.arenapvp.Extra.VILLAGER_YES;
import static com.rmaafs.arenapvp.Extra.cconfig;
import static com.rmaafs.arenapvp.Extra.clang;
import static com.rmaafs.arenapvp.Extra.jugandoUno;
import static com.rmaafs.arenapvp.Extra.kits;
import static com.rmaafs.arenapvp.Extra.mapLibres;
import static com.rmaafs.arenapvp.Extra.mapMeetupLibres;
import static com.rmaafs.arenapvp.Extra.mapMeetupOcupadas;
import static com.rmaafs.arenapvp.Extra.mapOcupadas;
import static com.rmaafs.arenapvp.Extra.playerConfig;
import com.rmaafs.arenapvp.FileKits;
import com.rmaafs.arenapvp.Kit;
import static com.rmaafs.arenapvp.Main.duelControl;
import static com.rmaafs.arenapvp.Main.guis;
import static com.rmaafs.arenapvp.Main.meetupControl;
import static com.rmaafs.arenapvp.Main.plugin;
import com.rmaafs.arenapvp.Partida;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitGui {

    public List<Player> escojiendoCrearMapa = new ArrayList<>();

    public List<Player> viendoInvRanked = new ArrayList<>();
    public List<Player> viendoInvUnRanked = new ArrayList<>();
    public List<Player> viendoInvDuel = new ArrayList<>();

    public HashMap<Kit, Integer> playingKitRanked = new HashMap<>();
    public HashMap<Kit, Integer> playingKitUnRanked = new HashMap<>();

    public Inventory acomodacion, chooseKit;
    public Inventory invRanked, invUnRanked, invDuel, invChooseKit;
    public HashMap<Integer, ItemStack> items = new HashMap<>();
    public HashMap<ItemStack, Kit> itemKits = new HashMap<>();
    public HashMap<Kit, FileKits> kitsHotbar = new HashMap<>();
    public HashMap<Kit, Integer> kitsSlot = new HashMap<>();
    public List<String> loreRanked, loreUnRanked, loreDuel, loreStats;
    public String guiPreviewKitName, guiStatsName;
    public ItemStack itemLeave;
    private String kitinuse, kitdeleted;

    public KitGui() {
        setConfig();
    }

    public void setConfig() {
        kitinuse = Extra.tc(clang.getString("kitinuse"));
        kitdeleted = Extra.tc(clang.getString("kitdeleted"));

        guiPreviewKitName = Extra.tc(clang.getString("gui.kitpreview.name"));
        guiStatsName = Extra.tc(clang.getString("gui.stats.name"));

        itemLeave = Extra.crearId(cconfig.getInt("gui.kitpreview.id"), cconfig.getInt("gui.kitpreview.data-value"), clang.getString("gui.kitpreview.items.close.name"), clang.getStringList("gui.kitpreview.items.close.lore"), 1);

        acomodacion = Bukkit.createInventory(null, cconfig.getInt("gui.kits.rows") * 9, Extra.tc(clang.getString("gui.movekit.name")));
        chooseKit = Bukkit.createInventory(null, cconfig.getInt("gui.kits.rows") * 9, Extra.tc(clang.getString("gui.choosekit.name")));

        invRanked = Bukkit.createInventory(null, cconfig.getInt("gui.kits.rows") * 9, Extra.tc(clang.getString("gui.ranked.name")));
        invUnRanked = Bukkit.createInventory(null, cconfig.getInt("gui.kits.rows") * 9, Extra.tc(clang.getString("gui.unranked.name")));
        invDuel = Bukkit.createInventory(null, cconfig.getInt("gui.kits.rows") * 9, Extra.tc(clang.getString("gui.duel.name")));
        invChooseKit = Bukkit.createInventory(null, cconfig.getInt("gui.kits.rows") * 9, Extra.tc(clang.getString("gui.choosekit.name")));

        loreRanked = Extra.tCC(clang.getStringList("gui.ranked.kitslore"));
        loreUnRanked = Extra.tCC(clang.getStringList("gui.unranked.kitslore"));
        loreDuel = Extra.tCC(clang.getStringList("gui.duel.kitslore"));
        loreStats = Extra.tCC(clang.getStringList("gui.stats.kitslore"));
    }

    public void saveItems() {
        items.clear();
        chooseKit.clear();
        invRanked.clear();
        invUnRanked.clear();
        invDuel.clear();
        invChooseKit.clear();
        for (int i = 0; i < acomodacion.getContents().length; i++) {
            if (acomodacion.getContents()[i] != null) {
                kitsSlot.put(itemKits.get(acomodacion.getContents()[i]), i);

                items.put(i, acomodacion.getContents()[i]);
                chooseKit.setItem(i, acomodacion.getContents()[i]);

                invRanked.setItem(i, setItemRanked(acomodacion.getContents()[i].clone()));
                invUnRanked.setItem(i, setItemUnRanked(acomodacion.getContents()[i].clone()));
                invDuel.setItem(i, setItemDuel(acomodacion.getContents()[i].clone()));
                invChooseKit.setItem(i, acomodacion.getContents()[i].clone());

                if (itemKits.get(acomodacion.getContents()[i]).slot != i) {
                    itemKits.get(acomodacion.getContents()[i]).slot = i;
                    File elkit = new File(plugin.getDataFolder() + File.separator + "kits" + File.separator + itemKits.get(acomodacion.getContents()[i]).kitName + ".yml");
                    FileConfiguration ckit = YamlConfiguration.loadConfiguration(elkit);
                    ckit.set("slot", i);
                    Extra.guardar(elkit, ckit);
                }
            }
        }
    }

    public void openPlayerStats(Player p, Player t) {
        Inventory inv = Bukkit.createInventory(null, 54, guiStatsName);
        for (int i = 0; i < acomodacion.getContents().length; i++) {
            if (acomodacion.getContents()[i] != null) {
                inv.setItem(i, setItemStats(p, acomodacion.getContents()[i].clone(), i));
            }
        }
        t.openInventory(inv);
        Extra.sonido(t, VILLAGER_YES);
    }

    public void setNumberRankedPlaying(Kit k, boolean add) {
        int slot = kitsSlot.get(k);
        if (add) {
            playingKitRanked.put(k, playingKitRanked.get(k) + 2);
        } else {
            if (playingKitRanked.get(k) >= 2) {
                playingKitRanked.put(k, playingKitRanked.get(k) - 2);
            }
        }
        invRanked.setItem(slot, setItemRanked(items.get(slot).clone()));
    }

    public void setNumberUnRankedPlaying(Kit k, boolean add) {
        int slot = kitsSlot.get(k);
        if (add) {
            playingKitUnRanked.put(k, playingKitUnRanked.get(k) + 2);
        } else {
            if (playingKitUnRanked.get(k) >= 2) {
                playingKitUnRanked.put(k, playingKitUnRanked.get(k) - 2);
            }
        }
        invUnRanked.setItem(slot, setItemUnRanked(items.get(slot).clone()));
    }

    public ItemStack setItemRanked(ItemStack i) {
        List<String> lore = new ArrayList<>();
        lore.addAll(itemKits.get(i).getDescription());
        Kit k = itemKits.get(i);
        if (!playingKitRanked.containsKey(k)) {
            playingKitRanked.put(k, 0);
        }
        for (String s : loreRanked) {
            if (s.contains("<")) {
                int wa = 0;
                if (duelControl.esperandoRanked.containsKey(k)) {
                    wa++;
                }
                lore.add(s.replaceAll("<playing>", "" + playingKitRanked.get(k))
                        .replaceAll("<waiting>", "" + wa));
            } else {
                lore.add(s);
            }
        }
        Extra.changeLore(i, lore);
        if (playingKitRanked.get(k) != 0) {
            i.setAmount(playingKitRanked.get(k));
        } else {
            i.setAmount(1);
        }

        return i;
    }

    public ItemStack setItemUnRanked(ItemStack i) {
        List<String> lore = new ArrayList<>();
        lore.addAll(itemKits.get(i).getDescription());
        Kit k = itemKits.get(i);
        if (!playingKitUnRanked.containsKey(k)) {
            playingKitUnRanked.put(k, 0);
        }
        for (String s : loreRanked) {
            if (s.contains("<")) {
                int wa = 0;
                if (duelControl.esperandoUnRanked.containsKey(k)) {
                    wa++;
                }
                lore.add(s.replaceAll("<playing>", "" + playingKitUnRanked.get(k))
                        .replaceAll("<waiting>", "" + wa));
            } else {
                lore.add(s);
            }
        }
        Extra.changeLore(i, lore);
        if (playingKitUnRanked.get(k) != 0) {
            i.setAmount(playingKitUnRanked.get(k));
        } else {
            i.setAmount(1);
        }
        return i;
    }

    public ItemStack setItemDuel(ItemStack i) {
        List<String> lore = new ArrayList<>();
        lore.addAll(itemKits.get(i).getDescription());
        lore.addAll(loreDuel);
        Extra.changeLore(i, lore);
        return i;
    }

    public ItemStack setItemStats(Player p, ItemStack i, int slot) {
        Kit k = getKitSlot(slot);
        List<String> lore = new ArrayList<>();
        lore.addAll(itemKits.get(i).getDescription());
        for (String s : loreStats) {
            if (s.contains("<")) {
                lore.add(s
                        .replaceAll("<rank>", playerConfig.get(p).getRank(k))
                        .replaceAll("<elo>", "" + playerConfig.get(p).getElo(k))
                        .replaceAll("<wins>", "" + playerConfig.get(p).getWins(k))
                        .replaceAll("<played>", "" + playerConfig.get(p).getPlayed(k))
                        .replaceAll("<kills>", "" + playerConfig.get(p).getKillsMeetup(k))
                        .replaceAll("<meetupwins>", "" + playerConfig.get(p).getWinsMeetup(k))
                        .replaceAll("<meetupplayed>", "" + playerConfig.get(p).getPlayedMeetup(k)));
            } else {
                lore.add(s);
            }
        }
        Extra.changeLore(i, lore);
        return i;
    }

    public Kit getKitSlot(int slot) {
        return itemKits.get(items.get(slot));
    }
    
    public Kit getKitItem(ItemStack i){
        return itemKits.get(i);
    }

    public void kitPreview(Player p, int slot) {
        kitPreview2(p, itemKits.get(items.get(slot)));
    }

    public void kitPreview2(Player p, Kit k) {
        List<ItemStack> list = new ArrayList<>();
        list.addAll(Arrays.asList(k.hotbar));
        list.addAll(Arrays.asList(k.armor));
        Inventory inv = Bukkit.createInventory(null, 54, guiPreviewKitName);
        inv.setContents(list.toArray(new ItemStack[list.size()]));
        inv.setItem(53, itemLeave);
        if (duelControl.creandoDuel.containsKey(p)) {
            PreDuelConfig d = duelControl.creandoDuel.get(p);
            p.openInventory(inv);
            duelControl.creandoDuel.put(p, d);
            guis.viendoInvDuel.add(p);
        } else {
            p.openInventory(inv);
        }
        Extra.sonido(p, HORSE_ARMOR);
    }

    public void clickearLeave(Player p) {
        if (viendoInvRanked.contains(p)) {
            viendoInvRanked.remove(p);
            p.openInventory(invRanked);
        } else if (viendoInvUnRanked.contains(p)) {
            viendoInvUnRanked.remove(p);
            p.openInventory(invUnRanked);
        } else if (viendoInvDuel.contains(p)) {
            viendoInvDuel.remove(p);
            p.openInventory(invDuel);
        } else if (meetupControl.viendoMiddle.contains(p)) {
            meetupControl.viendoMiddle.remove(p);
            meetupControl.openInvMeetup(p);
        } else {
            p.closeInventory();//El codigo se repite porque dse buguea con InventoryCloseEvent
        }
        Extra.sonido(p, CHEST_CLOSE);
    }

    public void eliminarKit(Player p, int slot) {
        Kit k = getKitSlot(slot);
        boolean t = true;
        for (Map.Entry<Player, Partida> entry : jugandoUno.entrySet()) {
            Partida partida = entry.getValue();
            if (partida.kit.equals(k)) {
                p.sendMessage(kitinuse);
                t = false;
                break;
            }
        }
        if (t) {
            kits.remove(k.kitName);
            playingKitRanked.remove(k);
            playingKitUnRanked.remove(k);
            kitsSlot.remove(k);
            acomodacion.remove(k.itemOnGui);
            mapLibres.remove(k);
            mapOcupadas.remove(k);
            mapMeetupLibres.remove(k);
            mapMeetupOcupadas.remove(k);
            kitsHotbar.remove(k);
            guis.saveItems();

            File elkit = new File(plugin.getDataFolder() + File.separator + "maps" + File.separator + k.kitName + ".yml");
            if (elkit.exists()) {
                elkit.delete();
            }

            elkit = new File(plugin.getDataFolder() + File.separator + "hotbar" + File.separator + k.kitName + ".yml");
            if (elkit.exists()) {
                elkit.delete();
            }

            elkit = new File(plugin.getDataFolder() + File.separator + "kits" + File.separator + k.kitName + ".yml");
            if (elkit.exists()) {
                elkit.delete();
            }

            elkit = new File(plugin.getDataFolder() + File.separator + "meetupmaps" + File.separator + k.kitName + ".yml");
            if (elkit.exists()) {
                elkit.delete();
            }

            for (Player o : Bukkit.getServer().getOnlinePlayers()) {
                playerConfig.get(o).removeKit(k);
            }

            p.sendMessage(kitdeleted);
            Extra.sonido(p, VILLAGER_YES);
        }
    }

    public void sacarViendoInv(Player p) {
        if (viendoInvRanked.contains(p)) {
            viendoInvRanked.remove(p);
        }
        if (viendoInvUnRanked.contains(p)) {
            viendoInvUnRanked.remove(p);
        }
        if (viendoInvDuel.contains(p)) {
            viendoInvDuel.remove(p);
        }
    }

    public void sacar(Player p) {
        if (escojiendoCrearMapa.contains(p)) {
            escojiendoCrearMapa.remove(p);
        }
        if (meetupControl.viendoMiddle.contains(p)) {
            meetupControl.viendoMiddle.remove(p);
        }
        sacarViendoInv(p);
    }
}
