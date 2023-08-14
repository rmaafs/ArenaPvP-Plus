package com.rmaafs.arenapvp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.rmaafs.arenapvp.util.Extra.cspawns;
import static com.rmaafs.arenapvp.util.Extra.cstats;
import static com.rmaafs.arenapvp.util.Extra.kits;
import static com.rmaafs.arenapvp.util.Extra.mapLibres;
import static com.rmaafs.arenapvp.util.Extra.mapMeetupLibres;
import static com.rmaafs.arenapvp.util.Extra.mapMeetupOcupadas;
import static com.rmaafs.arenapvp.util.Extra.mapOcupadas;
import static com.rmaafs.arenapvp.util.Extra.playerConfig;

import com.rmaafs.arenapvp.GUIS.GuiEvent;
import com.rmaafs.arenapvp.GUIS.KitGui;
import com.rmaafs.arenapvp.Hotbars.CommandEvent;
import com.rmaafs.arenapvp.Hotbars.HotbarEvent;
import com.rmaafs.arenapvp.Hotbars.Hotbars;
import com.rmaafs.arenapvp.Juegos.Duel.DuelControl;
import com.rmaafs.arenapvp.Juegos.Duel.PlayerEvent;
import com.rmaafs.arenapvp.Juegos.Duel.WorldEvent;
import com.rmaafs.arenapvp.Juegos.Meetup.MeetupControl;
import com.rmaafs.arenapvp.Juegos.Meetup.PreCommandEvent;
import com.rmaafs.arenapvp.Juegos.Stats.ClickPerSecond;
import com.rmaafs.arenapvp.KitControl.CrearKitEvent;
import com.rmaafs.arenapvp.MapControl.CrearMapaEvent;
import com.rmaafs.arenapvp.Party.PartyControl;
import com.rmaafs.arenapvp.commands.Command;
import com.rmaafs.arenapvp.entity.Map;
import com.rmaafs.arenapvp.entity.MeetupMap;
import com.rmaafs.arenapvp.manager.config.Lang;
import com.rmaafs.arenapvp.manager.config.PlayerConfig;
import com.rmaafs.arenapvp.manager.data.MySQL;
import com.rmaafs.arenapvp.manager.kit.Kit;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
import com.rmaafs.arenapvp.manager.spec.SpecControl;
import com.rmaafs.arenapvp.util.Convertor;
import com.rmaafs.arenapvp.util.Extra;
import com.rmaafs.arenapvp.util.Reloj;
import com.rmaafs.arenapvp.util.file.FileKits;
import com.rmaafs.arenapvp.versions.Packets;
import com.rmaafs.arenapvp.versions.v1_7_R4;
import com.rmaafs.arenapvp.versions.v1_X;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArenaPvP extends JavaPlugin implements Listener {

    //Todo listo al iniciar, leave de la party.
    public static Packets ver;
    public static ArenaPvP plugin;
    public static KitGui guis;
    public static Hotbars hotbars;
    public static DuelControl duelControl;
    public static Lang extraLang;
    public static Reloj reloj;
    public static MeetupControl meetupControl;
    public static PartyControl partyControl;
    public static MySQL mysql;
    public static SpecControl specControl;

    public static final String CVERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public static int VERSIONNUMERO = 7;

    File config = new File(getDataFolder() + File.separator + "config.yml");
    File lang = new File(getDataFolder() + File.separator + "lang.yml");
    File spawns = new File(getDataFolder() + File.separator + "spawns.yml");
    File stats = new File(getDataFolder() + File.separator + "stats.yml");
    File scoreboards = new File(getDataFolder() + File.separator + "scoreboards.yml");
    File ranks = new File(getDataFolder() + File.separator + "ranks.yml");
    File cache = new File(getDataFolder() + File.separator + "cache.yml");

    @Override
    public void onDisable() {
        try {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                playerConfig.get(p).saveStats();
            }
            for (FileKits e : guis.kitsHotbar.values()) {
                e.save();
            }
            cstats.save(stats);
            cspawns.save(spawns);
        } catch (IOException ex) {
            Logger.getLogger(ArenaPvP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        if (!lang.exists()) {
            Extra.copy(getResource("lang.yml"), new File(getDataFolder(), "lang.yml"));
        }
        if (!spawns.exists()) {
            Extra.copy(getResource("spawns.yml"), new File(getDataFolder(), "spawns.yml"));
        }
        if (!stats.exists()) {
            Extra.copy(getResource("stats.yml"), new File(getDataFolder(), "stats.yml"));
        }
        if (!scoreboards.exists()) {
            Extra.copy(getResource("scoreboards.yml"), new File(getDataFolder(), "scoreboards.yml"));
        }
        if (!ranks.exists()) {
            Extra.copy(getResource("ranks.yml"), new File(getDataFolder(), "ranks.yml"));
        }
        if (!cache.exists()) {
            Extra.copy(getResource("cache.yml"), new File(getDataFolder(), "cache.yml"));
        }

        if (CVERSION.equals("v1_7_R4")) {
            getServer().getConsoleSender().sendMessage("§2§lArenaPvP+> §cDetected 1.7.10 version.");
            ver = new v1_7_R4();
        } else {
            VERSIONNUMERO = 8;
            getServer().getConsoleSender().sendMessage("§2§lArenaPvP+ > §cDetected " + CVERSION +" version.");
            ver = new v1_X();
        }

        Extra.setFiles();
        Extra.setSoundsVersion();
        extraLang = new Lang();

        getCommand("apvp").setExecutor(new Command());
        getCommand("duel").setExecutor(new Command());
        getCommand("stats").setExecutor(new Command());
        getCommand("uinventario").setExecutor(new Command());
        getCommand("party").setExecutor(new Command());
        getCommand("pc").setExecutor(new Command());
        getCommand("giftrankeds").setExecutor(new Command());
        getCommand("spec").setExecutor(new Command());
        duelControl = new DuelControl();
        guis = new KitGui();
        registerEvents();
        loadKits();

        hotbars = new Hotbars();

//        extraLang = new Lang();
        meetupControl = new MeetupControl();
        partyControl = new PartyControl();

        reloj = new Reloj();
        guis.saveItems();
        specControl = new SpecControl();
        ConnectMySQL();
        getServer().getConsoleSender().sendMessage("§3----------------------------");
        getServer().getConsoleSender().sendMessage("§bArenaPvP++ (" + getDescription().getVersion() + ") by " + "§4Royendero");
        getServer().getConsoleSender().sendMessage("§3----------------------------");

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Extra.cleanPlayer(p);
            hotbars.setMain(p);
            playerConfig.put(p, new PlayerConfig(p));
            Extra.setScore(p, Score.TipoScore.MAIN);
            extraLang.teleportSpawn(p);
            p.spigot().setCollidesWithEntities(true);
        }
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new CrearKitEvent(), this);
        getServer().getPluginManager().registerEvents(new HotbarEvent(), this);
        getServer().getPluginManager().registerEvents(new CrearMapaEvent(), this);
        getServer().getPluginManager().registerEvents(new GuiEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvent(), this);
        getServer().getPluginManager().registerEvents(new CommandEvent(), this);
        getServer().getPluginManager().registerEvents(new ClickPerSecond(), this);
        getServer().getPluginManager().registerEvents(new PreCommandEvent(), this);
        getServer().getPluginManager().registerEvents(new WorldEvent(), this);
    }

    public void loadKits() {
        File folder = new File(getDataFolder() + File.separator + "kits");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    try {
                        File elkit = new File(getDataFolder() + File.separator + "kits" + File.separator + listOfFiles[i].getName());
                        FileConfiguration ckit = YamlConfiguration.loadConfiguration(elkit);
                        ItemStack item = new ItemStack(Material.AIR);
                        List<ItemStack> deleteBlocks = new ArrayList<>();
                        List<PotionEffect> potionList = new ArrayList<>();
                        if (ckit.contains("potionlist")) {
                            for (String s : ckit.getConfigurationSection("potionlist").getKeys(false)) {
                                potionList.add(new PotionEffect(PotionEffectType.getByName(s), 99999, ckit.getInt("potionlist." + s) - 1));
                            }
                        }
                        for (ItemStack ite : Convertor.itemFromBase64(ckit.getString("item"))) {
                            if (ite != null) {
                                item = ite;
                                break;
                            }
                        }
                        for (ItemStack ite : Convertor.itemFromBase64(ckit.getString("delete"))) {
                            if (ite != null) {
                                deleteBlocks.add(new ItemStack(ite.getType()));
                            }
                        }
                        Kit k = new Kit(
                                ckit.getString("name"),
                                ckit.getString("namecolor"),
                                deleteBlocks,
                                potionList,
                                Convertor.itemFromBase64(ckit.getString("hotbar")),
                                Convertor.itemFromBase64(ckit.getString("armor")),
                                item,
                                ckit.getInt("slot"),
                                ckit.getInt("time"),
                                ckit.getBoolean("combo"),
                                ckit.getBoolean("naturalregeneration"));
                        Bukkit.getConsoleSender().sendMessage("§eKit: " + k.getKitName() + " cargado.");
                        if (ckit.contains("description")) {
                            k.setDescription(Extra.tCC(ckit.getStringList("description")));
                        }

                        kits.put(k.kitName, k);
                        guis.acomodacion.setItem(k.slot, item);
                        guis.itemKits.put(item, k);
                        
                        
                        File carpetaHotbar = new File(plugin.getDataFolder() + File.separator + "hotbar");
                        if (!carpetaHotbar.exists()){
                            carpetaHotbar.mkdir();
                        }
                        
                        
                        File hot = new File(plugin.getDataFolder() + File.separator + "hotbar" + File.separator + listOfFiles[i].getName());
                        boolean g = false;
                        if (!hot.exists()) {
                            hot.createNewFile();
                            g = true;
                        }
                        FileConfiguration chot = YamlConfiguration.loadConfiguration(hot);
                        guis.kitsHotbar.put(k, new FileKits(hot, chot));
                        if (g) {
                            Extra.guardar(hot, chot);
                        }
                        if (ckit.contains("mapsharing")) {
                            if (kits.containsKey(ckit.getString("mapsharing"))
                                    && mapLibres.containsKey(kits.get(ckit.getString("mapsharing")))) {
                                mapLibres.put(k, mapLibres.get(kits.get(ckit.getString("mapsharing"))));
                                mapOcupadas.put(k, mapOcupadas.get(kits.get(ckit.getString("mapsharing"))));
                                Bukkit.getConsoleSender().sendMessage("§6Compartiendo " + mapLibres.get(kits.get(ckit.getString("mapsharing"))).size() + " mapas a " + k.kitName + " de " + ckit.getString("mapsharing"));
                                if (mapMeetupLibres.containsKey(kits.get(ckit.getString("mapsharing")))) {
                                    mapMeetupLibres.put(k, mapMeetupLibres.get(kits.get(ckit.getString("mapsharing"))));
                                    mapMeetupOcupadas.put(k, mapMeetupOcupadas.get(kits.get(ckit.getString("mapsharing"))));
                                    Bukkit.getConsoleSender().sendMessage("§6Compartiendo " + mapMeetupLibres.get(kits.get(ckit.getString("mapsharing"))).size() + " mapas de meetup a " + k.kitName + " de " + ckit.getString("mapsharing"));
                                } else {
                                    Bukkit.getConsoleSender().sendMessage("§4ArenaPvP++ >> §bError sharing meetup maps to " + k.kitName + ", maps of " + ckit.getString("mapsharing") + " no exist.");
                                }
                            } else {
//                                if (kits.containsKey(ckit.getString("mapsharing"))){
//                                    Bukkit.getConsoleSender().sendMessage("§4ArenaPvP++ >> §bError sharing maps to " + k.kitName + ", maps of " + ckit.getString("mapsharing") + " no exist.");
//                                }
                                List<Kit> lis = new ArrayList<>();
                                if (sharingMaps.containsKey(ckit.getString("mapsharing"))){
                                    lis = sharingMaps.get(ckit.getString("mapsharing"));
                                }
                                lis.add(k);
                                sharingMaps.put(ckit.getString("mapsharing"), lis);
                                List<Map> lista = new ArrayList<>();
                                List<Map> lista2 = new ArrayList<>();
                                List<MeetupMap> lista3 = new ArrayList<>();
                                List<MeetupMap> lista4 = new ArrayList<>();
                                mapLibres.put(k, lista);
                                mapOcupadas.put(k, lista2);
                                mapMeetupLibres.put(k, lista3);
                                mapMeetupOcupadas.put(k, lista4);
                            }
                        } else {
                            loadMaps(k);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ArenaPvP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            kitsSharingMaps();
        }
    }

    HashMap<String, List<Kit>> sharingMaps = new HashMap<>();

    public void kitsSharingMaps() {
        if (!sharingMaps.isEmpty()) {
            for (java.util.Map.Entry<String, List<Kit>> entry : sharingMaps.entrySet()) {
                String p = entry.getKey();
                List<Kit> kl = entry.getValue();
                for (Kit k : kl) {
                    if (mapLibres.containsKey(kits.get(p))) {
                        mapLibres.put(k, mapLibres.get(kits.get(p)));
                        Bukkit.getConsoleSender().sendMessage("§6Compartiendo " + mapLibres.get(kits.get(p)).size() + " mapas a " + k.kitName + " de " + p);
                    } else {
                        Bukkit.getConsoleSender().sendMessage("§4ArenaPvP++ >> §bError sharing maps to " + k.kitName + ", maps of " + p + " no exist.");
                    }
                    if (mapMeetupLibres.containsKey(kits.get(p))) {
                        mapMeetupLibres.put(k, mapMeetupLibres.get(kits.get(p)));
                        Bukkit.getConsoleSender().sendMessage("§6Compartiendo " + mapMeetupLibres.get(kits.get(p)).size() + " mapas de meetup a " + k.kitName + " de " + p);
                    } else {
                        Bukkit.getConsoleSender().sendMessage("§4ArenaPvP++ >> §bError sharing meetup maps to " + k.kitName + ", maps of " + p + " no exist.");
                    }
                }
            }
        }
    }

    public void loadMaps(Kit k) {
        File f = new File(getDataFolder() + File.separator + "maps" + File.separator + k.kitName + ".yml");
        if (f.exists()) {
            FileConfiguration cf = YamlConfiguration.loadConfiguration(f);
            List<Map> lista = new ArrayList<>();
            List<Map> lista2 = new ArrayList<>();
            for (String s : cf.getKeys(false)) {
                String w = cf.getString(s + ".w");
                if (cf.contains(s + ".c1")) {
                    lista.add(new Map(s,
                            setLoc(cf, w, s + ".c1", false),
                            setLoc(cf, w, s + ".c2", false),
                            setLoc(cf, w, s + ".s1", true),
                            setLoc(cf, w, s + ".s2", true)));
                } else {
                    lista.add(new Map(s,
                            setLoc(cf, w, s + ".s1", true),
                            setLoc(cf, w, s + ".s2", true)));
                }
            }
            mapLibres.put(k, lista);
            mapOcupadas.put(k, lista2);
            Bukkit.getConsoleSender().sendMessage("§a" + lista.size() + " mapas registrados para " + k.kitName + ".");
            if (sharingMaps.containsKey(k.kitName)) {
                for (Kit kitSharing : sharingMaps.get(k.kitName)) {
                    mapLibres.put(kitSharing, lista);
                    mapOcupadas.put(kitSharing, lista2);
                    Bukkit.getConsoleSender().sendMessage("§2Compartiendo " + lista.size() + " mapas a " + kitSharing.kitName + " de " + k.kitName);
                    //sharingMaps.remove(k.kitName);
                }
            }
        }
        loadMapasMeetup(k);
    }

    public void loadMapasMeetup(Kit k) {
        File f = new File(getDataFolder() + File.separator + "meetupmaps" + File.separator + k.kitName + ".yml");
        if (f.exists()) {
            FileConfiguration cf = YamlConfiguration.loadConfiguration(f);
            List<MeetupMap> lista = new ArrayList<>();
            List<MeetupMap> lista2 = new ArrayList<>();
            for (String s : cf.getKeys(false)) {
                String w = cf.getString(s + ".w");
                if (cf.contains(s + ".c1")) {
                    List<Location> locs = new ArrayList<>();
                    for (String number : cf.getConfigurationSection(s + ".spawn").getKeys(false)) {
                        locs.add(new Location(Bukkit.getWorld(w),
                                cf.getInt(s + ".spawn." + number + ".x"),
                                cf.getInt(s + ".spawn." + number + ".y"),
                                cf.getInt(s + ".spawn." + number + ".z")));
                    }
                    lista.add(new MeetupMap(s,
                            setLoc(cf, w, s + ".c1", false),
                            setLoc(cf, w, s + ".c2", false),
                            locs));
                }
            }
            mapMeetupLibres.put(k, lista);
            mapMeetupOcupadas.put(k, lista2);
            Bukkit.getConsoleSender().sendMessage("§a" + lista.size() + " mapas de meetup registrados para " + k.kitName + ".");
            if (sharingMaps.containsKey(k.kitName)) {
                for (Kit kitSharing : sharingMaps.get(k.kitName)) {
                    mapMeetupLibres.put(kitSharing, lista);
                    mapMeetupOcupadas.put(kitSharing, lista2);
                    Bukkit.getConsoleSender().sendMessage("§2Compartiendo " + lista.size() + " mapas de meetup a " + kitSharing.kitName + " de " + k.kitName);
                    //sharingMaps.remove(k.kitName);
                }
            }
        }
    }

    public Location setLoc(FileConfiguration cf, String w, String s, boolean dou) {
        if (dou) {
            return new Location(Bukkit.getWorld(w),
                    cf.getDouble(s + ".x"),
                    cf.getDouble(s + ".y"),
                    cf.getDouble(s + ".z"),
                    cf.getInt(s + ".ya"),
                    cf.getInt(s + ".p"));
        } else {
            return new Location(Bukkit.getWorld(w),
                    cf.getDouble(s + ".x"),
                    cf.getDouble(s + ".y"),
                    cf.getDouble(s + ".z"));
        }
    }

    public static void ConnectMySQL() {
        if (extraLang.sql) {
            mysql = new MySQL();
            mysql.update("CREATE TABLE IF NOT EXISTS APVP(UUID VARCHAR(255), NICK VARCHAR(255), STATS VARCHAR(255), MEETUP VARCHAR(255));");
        }
    }

    public File getcConfig() {
        return config;
    }

    public File getcLang() {
        return lang;
    }

    public File getcSpawns() {
        return spawns;
    }

    public File getcSstats() {
        return stats;
    }

    public File getcScoreboards() {
        return scoreboards;
    }
    
    public File getcRanks() {
        return ranks;
    }
    
    public File getcCache() {
        return cache;
    }
}
