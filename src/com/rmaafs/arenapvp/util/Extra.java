package com.rmaafs.arenapvp.util;

import com.rmaafs.arenapvp.ArenaPvP;
import com.rmaafs.arenapvp.GUIS.GuiEvent;
import com.rmaafs.arenapvp.KitControl.CrearKitEvent;
import com.rmaafs.arenapvp.MapControl.CrearMapaEvent;
import com.rmaafs.arenapvp.entity.GameMap;
import com.rmaafs.arenapvp.entity.MeetupMap;
import com.rmaafs.arenapvp.game.Game;
import com.rmaafs.arenapvp.manager.config.PlayerConfig;
import com.rmaafs.arenapvp.manager.data.SQL;
import com.rmaafs.arenapvp.manager.kit.Kit;
import com.rmaafs.arenapvp.manager.rank.Rangos;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Extra {

    public static File config, lang, spawns, stats, scoreboards, ranks, cache;
    public static FileConfiguration cconfig, clang, cspawns, cstats, cscoreboards, cranks, ccache;

    public static HashMap<Player, PlayerConfig> playerConfig = new HashMap<>();

    public static HashMap<String, Kit> kits = new HashMap<>();
    public static HashMap<Kit, List<GameMap>> mapLibres = new HashMap<>();
    public static HashMap<Kit, List<GameMap>> mapOcupadas = new HashMap<>();
    public static HashMap<Kit, List<MeetupMap>> mapMeetupLibres = new HashMap<>();
    public static HashMap<Kit, List<MeetupMap>> mapMeetupOcupadas = new HashMap<>();
    public static HashMap<Player, Game> jugandoUno = new HashMap<>();

    public static List<Game> preEmpezandoUno = new ArrayList<>();

    public static HashMap<Player, Score> scores = new HashMap<>();
    public static List<Rangos> rangos = new ArrayList<>();
    public static Rangos rangoDefault;

    public static String nopermission;

    public static boolean regenRankedsUnrankeds = false;

    public static void setFiles() {
        config = ArenaPvP.plugin.getcConfig();
        cconfig = YamlConfiguration.loadConfiguration(config);
        lang = ArenaPvP.plugin.getcLang();
        clang = YamlConfiguration.loadConfiguration(lang);
        spawns = ArenaPvP.plugin.getcSpawns();
        cspawns = YamlConfiguration.loadConfiguration(spawns);
        stats = ArenaPvP.plugin.getcSstats();
        cstats = YamlConfiguration.loadConfiguration(stats);
        scoreboards = ArenaPvP.plugin.getcScoreboards();
        cscoreboards = YamlConfiguration.loadConfiguration(scoreboards);
        ranks = ArenaPvP.plugin.getcRanks();
        cranks = YamlConfiguration.loadConfiguration(ranks);
        cache = ArenaPvP.plugin.getcCache();
        ccache = YamlConfiguration.loadConfiguration(cache);

        nopermission = tc(clang.getString("nopermission"));

        for (String path : cranks.getConfigurationSection("").getKeys(false)) {
            if (cranks.contains(path + ".name") && cranks.contains(path + ".elo")) {
                rangos.add(new Rangos(tc(cranks.getString(path + ".name")), cranks.getInt(path + ".elo")));
            }
        }

        detectNextDay();
    }

    public static Rangos setRank(int elo) {
        for (Rangos r : rangos) {
            if (elo >= r.getElo()) {
                return r;
            }
        }
        return rangos.get(0);
    }

    public static String getKitVariable(String m, String type) {
        String kit = "";
        boolean inicio = false;
        for (int i = 0; i < m.length(); i++) {
            if (!inicio) {
                if (m.charAt(i) == '<' && i + 1 < m.length() && m.charAt(i + 1) == type.charAt(0)) {
                    inicio = true;
                    kit = kit + m.charAt(i);
                }
            } else {
                if (m.charAt(i) == '>') {
                    break;
                } else {
                    kit = kit + m.charAt(i);
                }
            }
        }
        kit = kit.replaceAll("<" + type + ":", "");
        if (kits.containsKey(kit)) {
            return kit;
        }
        return "Unknown Kit";
    }

    public static void detectNextDay() {
        String s = new SimpleDateFormat("dd").format(new Date());
        if (ccache.contains("day")) {
            if (!ccache.getString("day").equals(s)) {
                regenRankedsUnrankeds = true;
                ccache.set("day", s);
                ccache.set("players", null);
                ccache.set("gift", null);
                guardar(cache, ccache);
            }
        } else {
            regenRankedsUnrankeds = true;
            ccache.set("day", s);
            ccache.set("players", null);
            ccache.set("gift", null);
            guardar(cache, ccache);
        }
    }

    public static boolean addPlayerNextDay(Player p) {
        List<String> players = new ArrayList<>();
        if (ccache.contains("players")) {
            players = ccache.getStringList("players");
            if (players.contains(p.getName())) {
                return false;
            } else {
                players.add(p.getName());
                ccache.set("players", players);
                guardar(cache, ccache);
            }
        } else {
            players.add(p.getName());
            ccache.set("players", players);
            guardar(cache, ccache);
        }
        return true;
    }

    public static boolean noHaDadoRankeds(Player p) {
        List<String> players = new ArrayList<>();
        if (ccache.contains("gift")) {
            players = ccache.getStringList("gift");
            if (players.contains(p.getName())) {
                return false;
            } else {
                players.add(p.getName());
                ccache.set("gift", players);
                guardar(cache, ccache);
            }
        } else {
            players.add(p.getName());
            ccache.set("gift", players);
            guardar(cache, ccache);
        }
        return true;
    }

    public static void resetRankedsUnRankeds() {
        regenRankedsUnrankeds = true;
        ccache.set("day", new SimpleDateFormat("dd").format(new Date()));
        ccache.set("players", null);
        ccache.set("gift", null);
        guardar(cache, ccache);
    }

    public static boolean isExist(Player checar, Player online) {
        if (checar != null && checar.isOnline()) {
            return true;
        }
        online.sendMessage(ArenaPvP.extraLang.playerOffline);
        return false;
    }

    public static boolean isPlaying(Player p) {
        if (jugandoUno.containsKey(p)
                || ArenaPvP.duelControl.esperandoRanked.containsValue(p)
                || ArenaPvP.duelControl.esperandoUnRanked.containsValue(p)
                || CrearKitEvent.creandoKit.containsKey(p)
                || CrearKitEvent.editandoKit.containsKey(p)
                || CrearKitEvent.esperandoEditandoKit.contains(p)
                || GuiEvent.esperandoEliminarKit.contains(p)
                || CrearMapaEvent.creandoMapa.containsKey(p)
                || ArenaPvP.meetupControl.meetupsPlaying.containsKey(p)
                || ArenaPvP.meetupControl.creandoEventoMeetup.containsKey(p)
                || ArenaPvP.meetupControl.creandoMapaMeetup.containsKey(p)
                || ArenaPvP.meetupControl.esperandoCrearEvento.contains(p)
                || ArenaPvP.meetupControl.esperandoMapaMeetup.contains(p)
                || ArenaPvP.hotbars.editingSlotHotbar.containsKey(p)
                || ArenaPvP.hotbars.editingSlotHotbar.containsKey(p)
                || ArenaPvP.hotbars.esperandoEscojaHotbar.contains(p)
                || ArenaPvP.partyControl.partyHash.containsKey(p)
                || !ArenaPvP.extraLang.worlds.contains(p.getWorld().getName())
                || ArenaPvP.specControl.mirando.containsKey(p)) {
            return false;
        }
        return true;
    }

    public static boolean isCheckPlayerPlaying(Player p, Player mensaje) {
        if (jugandoUno.containsKey(p)
                || ArenaPvP.duelControl.esperandoRanked.containsValue(p)
                || ArenaPvP.duelControl.esperandoUnRanked.containsValue(p)
                || CrearKitEvent.creandoKit.containsKey(p)
                || CrearKitEvent.editandoKit.containsKey(p)
                || CrearKitEvent.esperandoEditandoKit.contains(p)
                || GuiEvent.esperandoEliminarKit.contains(p)
                || CrearMapaEvent.creandoMapa.containsKey(p)
                || ArenaPvP.meetupControl.meetupsPlaying.containsKey(p)
                || ArenaPvP.meetupControl.creandoEventoMeetup.containsKey(p)
                || ArenaPvP.meetupControl.creandoMapaMeetup.containsKey(p)
                || ArenaPvP.meetupControl.esperandoCrearEvento.contains(p)
                || ArenaPvP.meetupControl.esperandoMapaMeetup.contains(p)
                || ArenaPvP.hotbars.editingSlotHotbar.containsKey(p)
                || ArenaPvP.hotbars.esperandoEscojaHotbar.contains(p)
                || ArenaPvP.meetupControl.meetupsPlaying.containsKey(p)
                || ArenaPvP.meetupControl.esperandoMapaMeetup.contains(p)
                || ArenaPvP.meetupControl.esperandoCrearEvento.contains(p)
                || ArenaPvP.meetupControl.creandoMapaMeetup.containsKey(p)
                || ArenaPvP.meetupControl.creandoEventoMeetup.containsKey(p)
                || ArenaPvP.specControl.mirando.containsKey(p)) {
            mensaje.sendMessage(ArenaPvP.extraLang.playerPlayingOne);
            return false;
        } else if (ArenaPvP.partyControl.partyHash.containsKey(p)) {
            mensaje.sendMessage(ArenaPvP.extraLang.playerInParty);
            return false;
        } else if (!ArenaPvP.extraLang.worlds.contains(p.getWorld().getName())) {
            mensaje.sendMessage(ArenaPvP.extraLang.playernotintheworld);
            return false;
        }
        return true;
    }

    public static boolean isCheckYouPlaying(Player p) {
        if (jugandoUno.containsKey(p)
                || ArenaPvP.duelControl.esperandoRanked.containsValue(p)
                || ArenaPvP.duelControl.esperandoUnRanked.containsValue(p)
                || CrearKitEvent.creandoKit.containsKey(p)
                || CrearKitEvent.editandoKit.containsKey(p)
                || CrearKitEvent.esperandoEditandoKit.contains(p)
                || GuiEvent.esperandoEliminarKit.contains(p)
                || CrearMapaEvent.creandoMapa.containsKey(p)
                || ArenaPvP.meetupControl.meetupsPlaying.containsKey(p)
                || ArenaPvP.meetupControl.creandoEventoMeetup.containsKey(p)
                || ArenaPvP.meetupControl.creandoMapaMeetup.containsKey(p)
                || ArenaPvP.meetupControl.esperandoCrearEvento.contains(p)
                || ArenaPvP.meetupControl.esperandoMapaMeetup.contains(p)
                || ArenaPvP.hotbars.editingSlotHotbar.containsKey(p)
                || ArenaPvP.hotbars.esperandoEscojaHotbar.contains(p)
                || ArenaPvP.specControl.mirando.containsKey(p)) {
            p.sendMessage(ArenaPvP.extraLang.youPlayingOne);
            return false;
        } else if (ArenaPvP.partyControl.partyHash.containsKey(p)) {
            p.sendMessage(ArenaPvP.extraLang.youAreInParty);
            return false;
        } else if (!ArenaPvP.extraLang.worlds.contains(p.getWorld().getName())) {
            p.sendMessage(ArenaPvP.extraLang.playernotintheworld);
            return false;
        }
        return true;
    }

    public static void sacar(Player p) {

        SQL.guardarStats(p, true);

        ArenaPvP.specControl.leave(p, false);

        if (CrearKitEvent.creandoKit.containsKey(p)) {
            CrearKitEvent.creandoKit.remove(p);
        }
        if (CrearKitEvent.editandoKit.containsKey(p)) {
            CrearKitEvent.editandoKit.remove(p);
        }
        if (CrearKitEvent.esperandoEditandoKit.contains(p)) {
            CrearKitEvent.esperandoEditandoKit.remove(p);
        }
        if (GuiEvent.esperandoEliminarKit.contains(p)) {
            GuiEvent.esperandoEliminarKit.remove(p);
        }

        if (CrearMapaEvent.creandoMapa.containsKey(p)) {
            CrearMapaEvent.creandoMapa.remove(p);
        }

        if (jugandoUno.containsKey(p)) {
            jugandoUno.get(p).finish(p);
        }
        if (ArenaPvP.meetupControl.meetupsPlaying.containsKey(p)) {
            ArenaPvP.meetupControl.meetupsPlaying.get(p).leave(p, false);
        }
        if (playerConfig.containsKey(p)) {
            playerConfig.get(p).stats.componer();
            playerConfig.remove(p);
        }

        ArenaPvP.guis.sacar(p);
        ArenaPvP.duelControl.sacar(p);
        ArenaPvP.hotbars.sacar(p);
        ArenaPvP.partyControl.sacar(p);
        ArenaPvP.meetupControl.sacar(p);
    }

    public static GameMap getMap(Kit k) {
        GameMap m;
        if (ArenaPvP.extraLang.chooserandommaps) {
            Random r = new Random();
            m = mapLibres.get(k).get(r.nextInt(mapLibres.get(k).size()));
        } else {
            m = mapLibres.get(k).get(0);
        }
        mapLibres.get(k).remove(m);
        mapOcupadas.get(k).add(m);
        return m;
    }

    public static boolean checkMapAvailables(Kit kit) {
        if (mapLibres.get(kit) == null || !mapLibres.containsKey(kit) || mapLibres.get(kit).isEmpty()) {
            return false;
        }
        return true;
    }

    public static void terminarMapa(GameMap m, Kit k) {
        m.regen(k);
        mapOcupadas.get(k).remove(m);
        if (!mapLibres.get(k).contains(m)) {
            mapLibres.get(k).add(m);
        }
    }

    public static void terminarMapaMeetup(MeetupMap m, Kit k) {
        m.regen(k);
        mapMeetupOcupadas.get(k).remove(m);
        mapMeetupLibres.get(k).add(m);
    }

    public static boolean isPerm(Player p, String perm) {
        if (p.hasPermission(perm)) {
            return true;
        }
        p.sendMessage(nopermission + " " + perm);
        sonido(p, NOTE_BASS);
        return false;
    }

    public static boolean isPerm2(Player p, String perm, String perm2) {
        if (p.hasPermission(perm) || p.hasPermission(perm2)) {
            return true;
        }
        p.sendMessage(nopermission + " " + perm + ", " + perm2);
        sonido(p, NOTE_BASS);
        return false;
    }

    public static void setScore(Player p, Score.TipoScore t) {
        scores.put(p, new Score(p, t));
    }

    public static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte['?'];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void guardar(File file, FileConfiguration fc) {
        try {
            fc.save(file);

        } catch (IOException ex) {
            Logger.getLogger(ArenaPvP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String tc(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> tCC(List<String> list) {
        List<String> finalList = new ArrayList();
        int size = list.size();
        for (int index = 0; index < size; index++) {
            String string = ChatColor.translateAlternateColorCodes('&', (String) list.get(index));
            finalList.add(string);
        }
        return finalList;
    }

    public static void changeName(ItemStack item, String s) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Extra.tc(s));
        item.setItemMeta(meta);
    }

    public static void changeLore(ItemStack item, List<String> l) {
        ItemMeta meta = item.getItemMeta();
        if (!(l == null)) {
            List<String> Lore = new ArrayList();
            Lore.addAll(tCC(l));
            meta.setLore(Lore);
        }
        item.setItemMeta(meta);
    }

    public static ItemStack crearId(int id, int dv, String d, List<String> l, int a) {
        ItemStack item = new ItemStack(Material.getMaterial(id), a, (byte) dv);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', d));
        if (!(l == null)) {
            List<String> Lore = new ArrayList();
            Lore.addAll(tCC(l));
            meta.setLore(Lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static void sonido(Player player, String sonido) {
        Sounds.song(player, sonido);
    }

    public static void avisoConsola(String s) {
        ArenaPvP.plugin.getServer().getConsoleSender().sendMessage(s);
    }

    public static boolean existColor(String color) {
        switch (color) {
            case "AQUA":
            case "BLACK":
            case "BLUE":
            case "DARK_AQUA":
            case "DARK_BLUE":
            case "DARK_GRAY":
            case "DARK_GREEN":
            case "DARK_PURPLE":
            case "DARK_RED":
            case "GOLD":
            case "GRAY":
            case "GREEN":
            case "LIGHT_PURPLE":
            case "RED":
            case "WHITE":
            case "YELLOW":
                return true;
        }
        return false;
    }

    public static void text(Player p, String s, String hover, String cmd, String color) {
        TextComponent l1 = new TextComponent();
        l1.setText(s);
        l1.setColor(net.md_5.bungee.api.ChatColor.valueOf(color));
        l1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        l1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
        p.spigot().sendMessage(l1);
    }

    public static void cleanPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        player.setLevel(0);
        player.setFoodLevel(20);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setHealth(20.0);
        player.setFireTicks(0);
    }

    public static double getHealt(double sangre) {
        if (sangre >= 19) {
            sangre = 10.0;
        } else if (sangre >= 18) {
            sangre = 9.5;
        } else if (sangre >= 17) {
            sangre = 9.0;
        } else if (sangre >= 16) {
            sangre = 8.5;
        } else if (sangre >= 15) {
            sangre = 8.0;
        } else if (sangre >= 14) {
            sangre = 7.5;
        } else if (sangre >= 13) {
            sangre = 7.0;
        } else if (sangre >= 12) {
            sangre = 6.5;
        } else if (sangre >= 11) {
            sangre = 6.0;
        } else if (sangre >= 10) {
            sangre = 5.5;
        } else if (sangre >= 9) {
            sangre = 5.0;
        } else if (sangre >= 8) {
            sangre = 4.5;
        } else if (sangre >= 7) {
            sangre = 4.0;
        } else if (sangre >= 6) {
            sangre = 3.5;
        } else if (sangre >= 5) {
            sangre = 3.0;
        } else if (sangre >= 4) {
            sangre = 2.5;
        } else if (sangre >= 3) {
            sangre = 2.0;
        } else if (sangre >= 2) {
            sangre = 1.5;
        } else if (sangre >= 1) {
            sangre = 1.0;
        } else if (sangre >= 0.01) {
            sangre = 0.5;
        }
        return sangre;
    }

    public static String NOTE_BASS, CHICKEN_EGG_POP, NOTE_PLING, BURP, ORB_PICKUP, SPLASH2, LEVEL_UP, WITHER_DEATH,
            ANVIL_USE, CAT_MEOW, DRINK, EAT, NOTE_STICKS, ZOMBIE_WOODBREAK, WITHER_SPAWN, FIREWORK_LARGE_BLAST,
            CHEST_CLOSE, CHEST_OPEN, ITEM_PICKUP, VILLAGER_YES, VILLAGER_NO, HORSE_ARMOR, EXPLODE;

    public static void setSoundsVersion() {
        if (ArenaPvP.CVERSION.contains("1_7") || ArenaPvP.CVERSION.contains("1_8")) {
            NOTE_BASS = "NOTE_BASS";
            CHICKEN_EGG_POP = "CHICKEN_EGG_POP";
            NOTE_PLING = "NOTE_PLING";
            BURP = "BURP";
            ORB_PICKUP = "ORB_PICKUP";
            SPLASH2 = "SPLASH2";
            LEVEL_UP = "LEVEL_UP";
            WITHER_DEATH = "WITHER_DEATH";
            ANVIL_USE = "ANVIL_USE";
            CAT_MEOW = "CAT_MEOW";
            DRINK = "DRINK";
            EAT = "EAT";
            NOTE_STICKS = "NOTE_STICKS";
            ZOMBIE_WOODBREAK = "ZOMBIE_WOODBREAK";
            WITHER_SPAWN = "WITHER_SPAWN";
            FIREWORK_LARGE_BLAST = "FIREWORK_LARGE_BLAST";
            CHEST_CLOSE = "CHEST_CLOSE";
            CHEST_OPEN = "CHEST_OPEN";
            ITEM_PICKUP = "ITEM_PICKUP";
            VILLAGER_YES = "VILLAGER_YES";
            VILLAGER_NO = "VILLAGER_NO";
            HORSE_ARMOR = "HORSE_ARMOR";
            EXPLODE = "EXPLODE";
        } else {
            //if (version.equals("9")){
            NOTE_BASS = "BLOCK_NOTE_BASS";
            CHICKEN_EGG_POP = "ENTITY_CHICKEN_EGG";
            NOTE_PLING = "BLOCK_NOTE_PLING";
            BURP = "ENTITY_PLAYER_BURP";
            ORB_PICKUP = "ENTITY_EXPERIENCE_ORB_PICKUP";
            SPLASH2 = "ENTITY_PLAYER_SPLASH";
            LEVEL_UP = "ENTITY_PLAYER_LEVELUP";
            WITHER_DEATH = "ENTITY_WITHER_DEATH";
            ANVIL_USE = "BLOCK_ANVIL_USE";
            CAT_MEOW = "ENTITY_CAT_AMBIENT";
            DRINK = "ENTITY_WITCH_DRINK";
            EAT = "ENTITY_PLAYER_BURP";
            NOTE_STICKS = "BLOCK_NOTE_SNARE";
            ZOMBIE_WOODBREAK = "ENTITY_ZOMBIE_BREAK_DOOR_WOOD";
            WITHER_SPAWN = "ENTITY_WITHER_SPAWN";
            FIREWORK_LARGE_BLAST = "ENTITY_FIREWORK_LARGE_BLAST";
            CHEST_CLOSE = "BLOCK_CHEST_CLOSE";
            CHEST_OPEN = "BLOCK_CHEST_OPEN";
            ITEM_PICKUP = "ENTITY_ITEM_PICKUP";
            VILLAGER_YES = "ENTITY_VILLAGER_YES";
            VILLAGER_NO = "ENTITY_VILLAGER_NO";
            HORSE_ARMOR = "ENTITY_HORSE_ARMOR";
            EXPLODE = "BLOCK_GLASS_BREAK";
        }
    }

    public static String secToMin(int i) {
        String f = "0";
        if (i < 0) {
            i = 0;
        }
        if (i >= 3600 && i < 7200) {
            i = i - 3600;
            int ms = i / 60;
            int ss = i % 60;
            String m = (ms < 10 ? "0" : "") + ms;
            String s = (ss < 10 ? "0" : "") + ss;
            f = "1:" + m + ":" + s;
        } else if (i >= 7200 && i < 10800) {
            i = i - 7200;
            int ms = i / 60;
            int ss = i % 60;
            String m = (ms < 10 ? "0" : "") + ms;
            String s = (ss < 10 ? "0" : "") + ss;
            f = "2:" + m + ":" + s;
        } else if (i >= 10800 && i < 14400) {
            i = i - 10800;
            int ms = i / 60;
            int ss = i % 60;
            String m = (ms < 10 ? "0" : "") + ms;
            String s = (ss < 10 ? "0" : "") + ss;
            f = "3:" + m + ":" + s;
        } else {
            int ms = i / 60;
            int ss = i % 60;
            String m = (ms < 10 ? "0" : "") + ms;
            String s = (ss < 10 ? "0" : "") + ss;
            f = m + ":" + s;
        }

        return f;
    }

    public static int getDiferenciaElo(int total) {
        if (total <= -500) {
            total = 31;
        } else if (total <= -450 && total >= -499) {
            total = 30;
        } else if (total <= -400 && total >= -449) {
            total = 29;
        } else if (total <= -350 && total >= -399) {
            total = 28;
        } else if (total <= -300 && total >= -349) {
            total = 27;
        } else if (total <= -250 && total >= -299) {
            total = 26;
        } else if (total <= -200 && total >= -249) {
            total = 24;
        } else if (total <= -150 && total >= -199) {
            total = 22;
        } else if (total <= -100 && total >= -149) {
            total = 20;
        } else if (total <= -50 && total >= -99) {
            total = 19;
        } else if (total <= 0 && total >= -49) {
            total = 17;
        } //--------------
        else if (total >= 450) {
            total = 1;
        } else if (total >= 400 && total <= 449) {
            total = 2;
        } else if (total >= 350 && total <= 399) {
            total = 3;
        } else if (total >= 300 && total <= 349) {
            total = 4;
        } else if (total >= 250 && total <= 299) {
            total = 6;
        } else if (total >= 200 && total <= 249) {
            total = 8;
        } else if (total >= 150 && total <= 199) {
            total = 10;
        } else if (total >= 100 && total <= 149) {
            total = 12;
        } else if (total >= 50 && total <= 99) {
            total = 14;
        } else if (total >= 1 && total <= 49) {
            total = 17;
        }
        return total;
    }
}
