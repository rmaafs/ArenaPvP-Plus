package com.rmaafs.arenapvp.Juegos.Meetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.rmaafs.arenapvp.API.MeetupDeathEvent;
import com.rmaafs.arenapvp.API.MeetupFinishEvent;
import com.rmaafs.arenapvp.API.MeetupKillByPlayerEvent;
import com.rmaafs.arenapvp.API.MeetupStartEvent;
import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.CHICKEN_EGG_POP;
import static com.rmaafs.arenapvp.util.Extra.FIREWORK_LARGE_BLAST;
import static com.rmaafs.arenapvp.util.Extra.LEVEL_UP;
import static com.rmaafs.arenapvp.util.Extra.NOTE_BASS;
import static com.rmaafs.arenapvp.util.Extra.NOTE_PLING;
import static com.rmaafs.arenapvp.util.Extra.ORB_PICKUP;
import static com.rmaafs.arenapvp.util.Extra.VILLAGER_NO;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static com.rmaafs.arenapvp.util.Extra.clang;
import static com.rmaafs.arenapvp.util.Extra.playerConfig;
import com.rmaafs.arenapvp.manager.kit.Kit;
import static com.rmaafs.arenapvp.ArenaPvP.extraLang;
import static com.rmaafs.arenapvp.ArenaPvP.hotbars;
import static com.rmaafs.arenapvp.ArenaPvP.meetupControl;
import static com.rmaafs.arenapvp.ArenaPvP.plugin;
import com.rmaafs.arenapvp.entity.MeetupMap;
import com.rmaafs.arenapvp.manager.scoreboard.Score;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameMeetup {

    enum Accion {

        WAITING, STARTING, PLAYING
    };

    public String title;
    public String owner;
    public Kit kit;
    public MeetupMap mapa;
    boolean ffa = false, count = false;
    int slots;
    public int pretime, time;
    int guislot = 0, minplayers;
    Accion accion = Accion.WAITING;
    public List<Player> players = new ArrayList<>();
    public List<Player> espectadores = new ArrayList<>();

    HashMap<Player, Location> preLocs = new HashMap<>();
    List<ItemStack> drops = new ArrayList<>();

    String broadcast, wordtype, leaveplayer, joinplayer, starting, staringgame,
            playerkilled, playerdeath, playerdeathdisconnect, youdeath, youkilled,
            spectatormode, spectatornoperm, broadcastwin, needminplayers, countcancelled;
    List<String> started, winner;
    List<String> loreCrudo = new ArrayList<>();
    List<String> lore = new ArrayList<>();

    public HashMap<Player, Integer> kills = new HashMap<>();
    int totalplayers = 0;
    boolean daño = false;

    public GameMeetup(Player p, String ti, Kit k, int slot, boolean f, MeetupMap ma, int min) {
        owner = p.getName();
        title = ti;
        kit = k;
        slots = slot;
        ffa = f;
        mapa = ma;
        players.add(p);
        minplayers = min;
//        minplayers = 2;

        time = cconfig.getInt("meetup.game.gametime");

        joinplayer = Extra.tc(clang.getString("meetup.game.joinplayer"));
        leaveplayer = Extra.tc(clang.getString("meetup.game.leaveplayer"));
        starting = Extra.tc(clang.getString("meetup.game.starting"));
        staringgame = Extra.tc(clang.getString("meetup.game.startinggame"));
        playerkilled = Extra.tc(clang.getString("meetup.game.playerkilled"));
        playerdeath = Extra.tc(clang.getString("meetup.game.playerdeath"));
        playerdeathdisconnect = Extra.tc(clang.getString("meetup.game.playerdeathdisconnect"));
        youdeath = Extra.tc(clang.getString("meetup.game.youdeath"));
        youkilled = Extra.tc(clang.getString("meetup.game.youkilled"));
        spectatormode = Extra.tc(clang.getString("meetup.game.spectatormode"));
        spectatornoperm = Extra.tc(clang.getString("meetup.game.spectatornoperm"));
        broadcastwin = Extra.tc(clang.getString("meetup.game.broadcastwin"));
        needminplayers = Extra.tc(clang.getString("meetup.game.needminplayers"));
        countcancelled = Extra.tc(clang.getString("meetup.game.countcancelled"));

        started = Extra.tCC(clang.getStringList("meetup.game.started"));
        winner = Extra.tCC(clang.getStringList("meetup.game.winner"));

        if (f) {
            wordtype = clang.getString("gui.meetup.wordffa");
        } else {
            wordtype = clang.getString("gui.meetup.wordgroups");
        }
        broadcast = Extra.tc(clang.getString("meetup.game.broadcast"));

        loreCrudo = Extra.tCC(clang.getStringList("gui.meetup.items.games.lore"));
        refreshLore();

        pretime = cconfig.getInt("meetup.game.pretime");
//        pretime = 20;

        Extra.cleanPlayer(p);
        p.setGameMode(GameMode.ADVENTURE);
        p.setLevel(pretime);
        hotbars.setLeave(p);
        Extra.sonido(p, LEVEL_UP);

        Bukkit.broadcastMessage(broadcast.replaceAll("<player>", owner).replaceAll("<kit>", k.getKitName()).replaceAll("<name>", title));

        if (clang.getBoolean("meetup.game.clickjoin.use")) {
            String text = Extra.tc(clang.getString("meetup.game.clickjoin.msg"));
            String hover = Extra.tc(clang.getString("meetup.game.clickjoin.hover").replaceAll("<owner>", owner));
            for (Player o : Bukkit.getServer().getOnlinePlayers()) {
                if (!players.contains(o)) {
                    Extra.sonido(o, FIREWORK_LARGE_BLAST);
                    Extra.text(o, text, hover, "/joinmeetupevent " + guislot, "GREEN");
                }
            }
        }
    }

    public void join(Player p) {
        players.add(p);
        sonido(NOTE_PLING);
        msg(joinplayer.replaceAll("<player>", p.getName()));
        hotbars.setLeave(p);
        refreshLore();
        if (!count && accion == Accion.WAITING) {
            if (players.size() >= minplayers) {
                count = true;
            } else {
                msg(needminplayers.replaceAll("<number>", "" + (minplayers - players.size())));
            }
        }
    }

    public void leave(Player p, boolean online) {
        if (accion == Accion.WAITING) {
            msg(leaveplayer.replaceAll("<player>", p.getName()));
            if (players.contains(p)) {
                players.remove(p);
            } else if (espectadores.contains(p)) {
                espectadores.remove(p);
            }
            if (count && players.size() < minplayers) {
                count = false;
                pretime = cconfig.getInt("meetup.game.pretime");
                msg(countcancelled);
                msg(needminplayers.replaceAll("<number>", "" + (minplayers - players.size())));
            }
            sonido(NOTE_BASS);
            refreshLore();
            meetupControl.refreshItem(guislot);
            Extra.setScore(p, Score.TipoScore.MAIN);
        } else {
            if (players.contains(p)) {
                msg(playerdeathdisconnect.replaceAll("<player>", p.getName()));
                players.remove(p);

                if (players.size() == 1) {
                    if (meetupControl.meetupStarting.contains(meetupControl.meetupsPlaying.get(p))) {
                        meetupControl.meetupStarting.remove(meetupControl.meetupsPlaying.get(p));
                    }
                    ganador();
                } else {
                    if (accion == Accion.PLAYING) {
                        for (ItemStack i : p.getInventory().getContents()) {
                            if (i != null && i.getTypeId() != 0) {
                                p.getWorld().dropItemNaturally(p.getLocation(), i);
                            }
                        }
                        for (ItemStack i : p.getInventory().getArmorContents()) {
                            if (i != null && i.getTypeId() != 0) {
                                p.getWorld().dropItemNaturally(p.getLocation(), i);
                            }
                        }
                    }
                }
                if (online) {
                    p.setFlying(false);
                    extraLang.teleportSpawn(p);
                    hotbars.setMain(p);
                    p.spigot().setCollidesWithEntities(true);
                    Extra.setScore(p, Score.TipoScore.MAIN);
                }
            } else if (espectadores.contains(p)) {
                espectadores.remove(p);
                if (online) {
                    mostrarPlayer(p);
                    p.setFlying(false);
                    extraLang.teleportSpawn(p);
                    hotbars.setMain(p);
                    p.spigot().setCollidesWithEntities(true);
                    Extra.setScore(p, Score.TipoScore.MAIN);
                }
            }
        }
        meetupControl.meetupsPlaying.remove(p);
    }

    public void refreshLore() {
        lore.clear();
        for (String s : loreCrudo) {
            lore.add(s.replaceAll("<owner>", owner)
                    .replaceAll("<title>", title)
                    .replaceAll("<kit>", kit.kitName)
                    .replaceAll("<type>", wordtype)
                    .replaceAll("<map>", mapa.getName())
                    .replaceAll("<players>", "" + players.size())
                    .replaceAll("<slots>", "" + slots)
                    .replaceAll("<time>", Extra.secToMin(pretime)));
        }
    }

    public boolean removePretime() {
        if (!count) {
            return false;
        }
        pretime--;

        if (pretime <= 0) {
            if (accion == Accion.STARTING) {
                preTeleportar();
                return true;
            } else if (accion == Accion.WAITING) {
                accion = Accion.STARTING;
                pretime = extraLang.pretimematch;
                meetupControl.removeItem(guislot);
                preTeleportar();
            }
        }

        if (accion == Accion.WAITING) {
            if (pretime <= 5 || pretime == 10 || pretime == 15 || pretime == 30) {
                msg(starting.replaceAll("<time>", "" + pretime));
                sonido(CHICKEN_EGG_POP);
            }
            if (pretime == 10 && clang.getBoolean("meetup.game.clickjoinstarting.use")) {
                String broad = Extra.tc(clang.getString("meetup.game.clickjoinstarting.broadcast").replaceAll("<owner>", owner).replaceAll("<title>", title));
                String text = Extra.tc(clang.getString("meetup.game.clickjoinstarting.msg"));
                String hover = Extra.tc(clang.getString("meetup.game.clickjoinstarting.hover").replaceAll("<owner>", owner));
                for (Player o : Bukkit.getServer().getOnlinePlayers()) {
                    if (!players.contains(o)) {
                        o.sendMessage(broad);
                        Extra.sonido(o, FIREWORK_LARGE_BLAST);
                        Extra.text(o, text, hover, "/joinmeetupevent " + guislot, "GREEN");
                    }
                }
            }
            refreshLore();
            meetupControl.refreshItem(guislot);
        } else if (accion == Accion.STARTING) {
            msg(staringgame.replaceAll("<time>", "" + pretime));
            sonido(CHICKEN_EGG_POP);
            if (pretime == 3) {
                preTeleportar();
            }
        }
        return false;
    }

    public void start() {
        lore.clear();
        accion = Accion.PLAYING;
        totalplayers = players.size();
        for (String s : started) {
            if (s.contains("<")) {
                msg(s.replaceAll("<kit>", kit.getKitName())
                        .replaceAll("<title>", title)
                        .replaceAll("<owner>", owner)
                        .replaceAll("<map>", mapa.getName())
                        .replaceAll("<time>", Extra.secToMin(time))
                        .replaceAll("<players>", "" + players.size()));
            } else {
                msg(s);
            }
        }
        for (Player p : players) {
            playerConfig.get(p).addPlayedMeetup(kit);
            if (hotbars.esperandoEscojaHotbar.contains(p)) {
                playerConfig.get(p).putInv(1, kit);
                hotbars.esperandoEscojaHotbar.remove(p);
            }
            p.setGameMode(GameMode.SURVIVAL);
            Extra.sonido(p, FIREWORK_LARGE_BLAST);

            if (kit.combo) {
                p.setMaximumNoDamageTicks(1);
            }
            if (!kit.potionList.isEmpty()) {
                for (PotionEffect pots : kit.potionList) {
                    p.addPotionEffect(pots);
                }
            }
            Extra.setScore(p, Score.TipoScore.MEETUP);
        }
        daño = true;
        Bukkit.getPluginManager().callEvent(new MeetupStartEvent(this, preLocs));
        preLocs.clear();
    }

    public void preTeleportar() {
        boolean noTienePreSpawns = preLocs.isEmpty();

        PotionEffect pot = new PotionEffect(PotionEffectType.BLINDNESS, 30, 1);
        for (Player p : players) {
            if (noTienePreSpawns) {
                Extra.cleanPlayer(p);
                if (extraLang.duelEffectTeleport) {
                    p.addPotionEffect(pot);
                }
//                if (kit.combo) {
//                    p.setMaximumNoDamageTicks(1);
//                }
//                if (!kit.potionList.isEmpty()) {
//                    for (PotionEffect pots : kit.potionList) {
//                        p.addPotionEffect(pots);
//                    }
//                }
                hotbars.ponerItemsHotbar(p);
            }

            if (noTienePreSpawns) {
                preLocs.put(p, mapa.getLoc());
            }
            p.teleport(preLocs.get(p));
        }
    }

    public void morir(Player p, PlayerDeathEvent e) {
        if (players.contains(p)) {
            p.setHealth(20);
            int mykills = 0;
            if (kills.containsKey(p)) {
                mykills = kills.get(p);
            }
            if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player && e.getEntity().getKiller() != p) {
                Player k = e.getEntity().getKiller();
                msg(playerkilled.replaceAll("<player>", p.getName()).replaceAll("<killer>", k.getName()));
                p.sendMessage(youkilled.replaceAll("<killer>", k.getName()).replaceAll("<health>", "" + Extra.getHealt(k.getHealth())).replaceAll("<kills>", "" + mykills));
                Extra.sonido(k, ORB_PICKUP);

                if (!kills.containsKey(k)) {
                    kills.put(k, 1);
                } else {
                    kills.put(k, kills.get(k) + 1);
                }
                playerConfig.get(k).addKillsMeetup(kit, 1);
                Bukkit.getPluginManager().callEvent(new MeetupKillByPlayerEvent(this, p, k));
            } else {
                msg(playerdeath.replaceAll("<player>", p.getName()));
                p.sendMessage(youdeath.replaceAll("<kills>", "" + mykills));
                Bukkit.getPluginManager().callEvent(new MeetupDeathEvent(this, p, e));
            }

            Extra.sonido(p, VILLAGER_NO);

            if (players.contains(p)) {
                players.remove(p);
            }

            if (players.size() == 1) {
                Extra.cleanPlayer(p);
                espectadores.add(p);
                p.setGameMode(GameMode.ADVENTURE);
                e.getDrops().clear();
                ganador();
            } else {
                ponerSpec(p);
            }
            if (extraLang.duelEffectDeathBlindness) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
            }
            if (extraLang.duelEffectDeathSlow) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 5));
            }
            drops.addAll(e.getDrops());
        }
    }

    private void ponerSpec(final Player p) {
        Extra.cleanPlayer(p);
        if (extraLang.usespectatormode) {
            p.setGameMode(GameMode.valueOf("SPECTATOR"));
        } else {
            ocultarPlayer(p);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setGameMode(GameMode.CREATIVE);
            p.spigot().setCollidesWithEntities(false);
        }
        if (p.hasPermission("apvp.meetup.spectator")) {
            espectadores.add(p);
            hotbars.setLeave(p);
            p.sendMessage(spectatormode);
        } else {
            meetupControl.meetupsPlaying.remove(p);
            p.sendMessage(spectatornoperm);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    if (p.isOnline()) {
                        mostrarPlayer(p);
                        p.setFlying(false);
                        extraLang.teleportSpawn(p);
                        hotbars.setMain(p);
                        p.spigot().setCollidesWithEntities(true);
                    }
                }
            }, 60L);
        }
    }

    private void ganador() {
        daño = false;
        final Player p = players.get(0);
        playerConfig.get(p).addWinsMeetup(kit);
        int tkills = 0;
        int maxtime = cconfig.getInt("meetup.game.gametime");
        if (kills.containsKey(p)) {
            tkills = kills.get(p);
        }
        for (String s : winner) {
            if (s.contains("<")) {
                msg(s.replaceAll("<player>", p.getName())
                        .replaceAll("<kills>", "" + tkills)
                        .replaceAll("<players>", "" + totalplayers)
                        .replaceAll("<spectators>", "" + espectadores.size())
                        .replaceAll("<time>", Extra.secToMin(maxtime - time))
                        .replaceAll("<map>", mapa.getName())
                        .replaceAll("<title>", title)
                        .replaceAll("<owner>", owner));
            } else {
                msg(s);
            }
        }
        Extra.cleanPlayer(p);
        p.setMaximumNoDamageTicks(20);
        p.spigot().setCollidesWithEntities(true);
        for (Player o : espectadores) {
            mostrarPlayer(o);
            Extra.setScore(o, Score.TipoScore.MAIN);
        }
        Extra.setScore(p, Score.TipoScore.MAIN);
        meetupControl.meetupsPlaying.remove(p);
        for (ItemStack i : drops) {
            i.setTypeId(0);
        }
        Bukkit.getPluginManager().callEvent(new MeetupFinishEvent(this, p));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                if (p.isOnline()) {
                    for (Player o : espectadores) {
                        meetupControl.meetupsPlaying.remove(o);
                        extraLang.teleportSpawn(o);
                        hotbars.setMain(o);
                    }
                    extraLang.teleportSpawn(p);
                    hotbars.setMain(p);
                    Extra.terminarMapaMeetup(mapa, kit);
                }
            }
        }, 20L);
    }

    private void ocultarPlayer(Player p) {
        for (Player o : players) {
            if (o != p) {
                o.hidePlayer(p);
            }
        }
        for (Player o : espectadores) {
            if (o != p) {
                o.hidePlayer(p);
            }
        }
    }

    private void mostrarPlayer(Player p) {
        p.setMaximumNoDamageTicks(20);
        p.spigot().setCollidesWithEntities(true);
        for (Player o : Bukkit.getServer().getOnlinePlayers()) {
            if (o != p) {
                o.showPlayer(p);
            }
        }
    }

    public void hit(final EntityDamageByEntityEvent e) {
        if (!daño || (e.getDamager() != null && e.getDamager() instanceof Player && !((Player) e.getDamager()).spigot().getCollidesWithEntities())) {
            e.setCancelled(true);
        }
        if (extraLang.showhealwitharrow) {
            if (e.getDamager() instanceof Arrow) {
                final Arrow a = (Arrow) e.getDamager();
                if (a.getShooter() instanceof Player && ((Player) a.getShooter()) != ((Player) e.getEntity())) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            final Player t = (Player) e.getEntity();
                            final Player dam = (Player) a.getShooter();
                            String s = extraLang.viewheal.replaceAll("<player>", t.getName()).replaceAll("<heal>", "" + Extra.getHealt(t.getHealth()));
                            dam.sendMessage(s);
                        }
                    }, 1L);
                }
            }
        }
    }

    public boolean place(Block b) {
        mapa.set = true;
        mapa.blocks.add(b);
        if (b.getLocation().getBlockY() > mapa.maxY) {
            mapa.maxY = b.getLocation().getBlockY();
        }
        if (b.getType().equals(Material.FIRE) && kit.deleteBlocks.contains(new ItemStack(Material.getMaterial(259)))) {
            return false;
        }
        for (ItemStack it : kit.deleteBlocks) {
            if (b.getType().name().equals(it.getData().getItemType().name())) {
                return false;
            }
        }
        return true;
    }

    public boolean romper(Block b) {
        if (b.getType().equals(Material.FIRE) && kit.deleteBlocks.contains(new ItemStack(Material.getMaterial(259)))) {
            return false;
        }
        for (ItemStack it : kit.deleteBlocks) {
            if (b.getType().name().equals(it.getData().getItemType().name())) {
                return false;
            }
        }
        return true;
    }

    public void setLava(int y) {
        mapa.lava = true;
        mapa.set = true;
        if (y > mapa.maxY) {
            mapa.maxY = y;
        }
    }

    public int getKills(Player p) {
        if (kills.containsKey(p)) {
            return kills.get(p);
        }
        return 0;
    }

    public void removerSec() {
        if (accion == Accion.PLAYING) {
            time--;
        }
    }

    private void msg(String s) {
        for (Player p : players) {
            p.sendMessage(s);
        }

        for (Player p : espectadores) {
            p.sendMessage(s);
        }
    }

    private void sonido(String s) {
        for (Player p : players) {
            Extra.sonido(p, s);
        }

        for (Player p : espectadores) {
            Extra.sonido(p, s);
        }
    }
}
