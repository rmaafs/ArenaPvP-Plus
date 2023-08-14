package com.rmaafs.arenapvp.manager.scoreboard;

import java.util.ArrayList;
import java.util.List;

import com.rmaafs.arenapvp.API.ScoreBoardRefreshed;
import com.rmaafs.arenapvp.Juegos.Meetup.GameMeetup;

import static com.rmaafs.arenapvp.util.Extra.jugandoUno;
import static com.rmaafs.arenapvp.util.Extra.kits;
import static com.rmaafs.arenapvp.util.Extra.playerConfig;
import static com.rmaafs.arenapvp.ArenaPvP.extraLang;
import static com.rmaafs.arenapvp.ArenaPvP.meetupControl;
import static com.rmaafs.arenapvp.ArenaPvP.partyControl;
import static com.rmaafs.arenapvp.ArenaPvP.ver;

import com.rmaafs.arenapvp.game.Game;
import com.rmaafs.arenapvp.manager.kit.Kit;
import com.rmaafs.arenapvp.util.Extra;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class Score {

    public enum TipoScore {

        MAIN, DUEL, UNRANKED, RANKED, MEETUPWAITING, MEETUP, PARTYMAIN, PARTYEVENT,
        PARTYDUEL
    };

    Player p;
    Scoreboard b;
    Objective obj;
    boolean first = false;
    List<Player> amigos = new ArrayList<>();
    List<Player> rivales = new ArrayList<>();
    List<String> lista;
    TipoScore tipo;
    String title;
    String l1 = "nope", l2 = "nope", l3 = "nope", l4 = "nope", l5 = "nope", l6 = "nope", l7 = "nope", l8 = "nope", l9 = "nope", l10 = "nope", l11 = "nope", l12 = "nope", l13 = "nope", l14 = "nope", l15 = "nope";

    public Score(Player player, TipoScore ti) {
        p = player;
        if (player.hasPermission("apvp.scoreboard")) {
            b = Bukkit.getScoreboardManager().getNewScoreboard();
            tipo = ti;
            obj = b.registerNewObjective("arenapvpscore", "apvp");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);

            if (ti == TipoScore.MAIN) {
                lista = extraLang.scoreMain;
                title = extraLang.scoreMainTitle;
            } else if (ti == TipoScore.RANKED) {
                lista = extraLang.scoreRanked;
                title = extraLang.scoreRankedTitle;
            } else if (ti == TipoScore.UNRANKED) {
                lista = extraLang.scoreUnRanked;
                title = extraLang.scoreUnRankedTitle;
            } else if (ti == TipoScore.DUEL) {
                lista = extraLang.scoreDuel;
                title = extraLang.scoreDuelTitle;
            } else if (ti == TipoScore.MEETUPWAITING) {
                lista = extraLang.scoreMeetupWaiting;
                title = extraLang.scoreMeetupWaitingTitle;
            } else if (ti == TipoScore.MEETUP) {
                lista = extraLang.scoreMeetup;
                title = extraLang.scoreMeetupTitle;
            } else if (ti == TipoScore.PARTYMAIN) {
                lista = extraLang.scorePartyWait;
                title = extraLang.scorePartyWaitTitle;
            } else if (ti == TipoScore.PARTYEVENT) {
                lista = extraLang.scorePartyEvent;
                title = extraLang.scorePartyEventTitle;
            } else if (ti == TipoScore.PARTYDUEL) {
                lista = extraLang.scorePartyDuel;
                title = extraLang.scorePartyDuelTitle;
            }

            int l = lista.size();
            int numlinea = 0;
            for (String h : lista) {
                Team line;
                if (b.getTeam("main" + l) == null) {
                    line = b.registerNewTeam("main" + l);
                } else {
                    line = b.getTeam("main" + l);
                }
                line.addEntry(getId(l) + "§r");
                obj.getScore(getId(l) + "§r").setScore(l);
                numlinea++;
                if (numlinea == 1) {
                    l1 = h;
                } else if (numlinea == 2) {
                    l2 = h;
                } else if (numlinea == 3) {
                    l3 = h;
                } else if (numlinea == 4) {
                    l4 = h;
                } else if (numlinea == 5) {
                    l5 = h;
                } else if (numlinea == 6) {
                    l6 = h;
                } else if (numlinea == 7) {
                    l7 = h;
                } else if (numlinea == 8) {
                    l8 = h;
                } else if (numlinea == 9) {
                    l9 = h;
                } else if (numlinea == 10) {
                    l10 = h;
                } else if (numlinea == 11) {
                    l11 = h;
                } else if (numlinea == 12) {
                    l12 = h;
                } else if (numlinea == 13) {
                    l13 = h;
                } else if (numlinea == 14) {
                    l14 = h;
                } else if (numlinea == 15) {
                    l15 = h;
                }
                l--;
            }
            setTitle(title);
            update();
            first = true;
            if (extraLang.damageindicators) {
                ponerCorazones();
            }
            ponerColores();
            Bukkit.getPluginManager().callEvent(new ScoreBoardRefreshed(b, p));
        } else {
            p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }

    public Player getPlayer() {
        return p;
    }

    public Scoreboard getScoreboard() {
        return b;
    }

    public void setScore() {
        p.setScoreboard(b);
    }

    public void setTitle(String msg) {
        if (msg.length() >= 16) {
            msg = msg.substring(0, 16);
        }
        obj.setDisplayName(msg);
    }

    public void update() {
        if (!p.hasPermission("apvp.scoreboard")){
            p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            return;
        }
        if (p.getScoreboard() != b) {
            p.setScoreboard(b);
        }
        if (!p.isOnline() || !playerConfig.containsKey(p)){
            return;
        }
        ya.clear();
        c = 0;
        int l = lista.size();
        if (l1.contains("<") && l1.contains(">")) {
            String e = ponerVariables(l1);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l1.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l1), l);
                }
            }
        }
        l--;
        if (l2.contains("<") && l2.contains(">")) {
            String e = ponerVariables(l2);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l2.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l2), l);
                }
            }
        }
        l--;
        if (l3.contains("<") && l3.contains(">")) {
            String e = ponerVariables(l3);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l3.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l3), l);
                }
            }
        }
        l--;
        if (l4.contains("<") && l4.contains(">")) {
            String e = ponerVariables(l4);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l4.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l4), l);
                }
            }
        }
        l--;
        if (l5.contains("<") && l5.contains(">")) {
            String e = ponerVariables(l5);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l5.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l5), l);
                }
            }
        }
        l--;
        if (l6.contains("<") && l6.contains(">")) {
            String e = ponerVariables(l6);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l6.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l6), l);
                }
            }
        }
        l--;
        if (l7.contains("<") && l7.contains(">")) {
            String e = ponerVariables(l7);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l7.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l7), l);
                }
            }
        }
        l--;
        if (l8.contains("<") && l8.contains(">")) {
            String e = ponerVariables(l8);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l8.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l8), l);
                }
            }
        }
        l--;
        if (l9.contains("<") && l9.contains(">")) {
            String e = ponerVariables(l9);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l9.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l9), l);
                }
            }
        }
        l--;
        if (l10.contains("<") && l10.contains(">")) {
            String e = ponerVariables(l10);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l10.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l10), l);
                }
            }
        }
        l--;
        if (l11.contains("<") && l11.contains(">")) {
            String e = ponerVariables(l11);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l11.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l11), l);
                }
            }
        }
        l--;
        if (l12.contains("<") && l12.contains(">")) {
            String e = ponerVariables(l12);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l12.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l12), l);
                }
            }
        }
        l--;
        if (l13.contains("<") && l13.contains(">")) {
            String e = ponerVariables(l13);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l13.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l13), l);
                }
            }
        }
        l--;
        if (l14.contains("<") && l14.contains(">")) {
            String e = ponerVariables(l14);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l14.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l14), l);
                }
            }
        }
        l--;
        if (l15.contains("<") && l15.contains(">")) {
            String e = ponerVariables(l15);
            setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', e), l);
        } else {
            if (!l15.equals("nope")) {
                if (!first) {
                    setLine(b.getTeam("main" + l), ChatColor.translateAlternateColorCodes('&', l15), l);
                }
            }
        }
    }

    List<Player> ya = new ArrayList<>();
    int c = 0;

    public void ponerColores() {
        List<String> lis = new ArrayList<>();
        for (Team t : b.getTeams()) {
            lis.add(t.getName());
        }
        if (!amigos.isEmpty()) {
            Team t;
            if (lis.contains("team" + p.getName())) {
                t = b.getTeam("team" + p.getName());
            } else {
                t = b.registerNewTeam("team" + p.getName());
            }
            t.setPrefix("§a");
            for (Player o : amigos) {
                t.addPlayer(o);
            }
            t.setAllowFriendlyFire(false);
        }

        if (!rivales.isEmpty()) {
            Team t;
            if (lis.contains("nteam" + p.getName())) {
                t = b.getTeam("nteam" + p.getName());
            } else {
                t = b.registerNewTeam("nteam" + p.getName());
            }
            t.setPrefix("§c");
            for (Player o : rivales) {
                t.addPlayer(o);
            }
        }
    }

    public void ponerCorazones() {
        if (b.getObjective(DisplaySlot.BELOW_NAME) == null) {
            b.registerNewObjective("sangre", "health").setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        b.getObjective(DisplaySlot.BELOW_NAME).setDisplayName("§" + extraLang.damageindicatorscolor + "§l❤");
    }

    private void setLine(Team t, String msg, int l) {
        if (extraLang.usePH) {
            msg = PlaceholderAPI.setPlaceholders(p, msg);
        }
        
        if (msg.length() <= 16) {
            t.setPrefix(msg);
            if (t.getSuffix().length() > 0) {
                t.setSuffix("");
            }
        } else {
            String pref = "", suf = "";
            pref = Prefix(ChatColor.translateAlternateColorCodes('&', msg));
            suf = Suffix(ChatColor.translateAlternateColorCodes('&', msg));
            t.setPrefix(pref);
            t.setSuffix(suf);
        }
    }

    private static String Suffix(String text) {
        String suffix = null;
        String SuffixText;
        String colors = "§r";
        if (text.length() > 16) {
            SuffixText = text.substring(16);
            if (text.charAt(0) == '§') {
                colors = colors + "§" + text.charAt(1);
            }
            if (text.charAt(1) == '§') {
                colors = colors + "§" + text.charAt(2);
            }
            if (text.charAt(2) == '§') {
                colors = colors + "§" + text.charAt(3);
            }
            if (text.charAt(3) == '§') {
                colors = colors + "§" + text.charAt(4);
            }
            if (text.charAt(4) == '§') {
                colors = colors + "§" + text.charAt(5);
            }
            if (text.charAt(5) == '§') {
                colors = colors + "§" + text.charAt(6);
            }
            if (text.charAt(6) == '§') {
                colors = colors + "§" + text.charAt(7);
            }
            if (text.charAt(7) == '§') {
                colors = colors + "§" + text.charAt(8);
            }
            if (text.charAt(8) == '§') {
                colors = colors + "§" + text.charAt(9);
            }
            if (text.charAt(9) == '§') {
                colors = colors + "§" + text.charAt(10);
            }
            if (text.charAt(10) == '§') {
                colors = colors + "§" + text.charAt(11);
            }
            if (text.charAt(11) == '§') {
                colors = colors + "§" + text.charAt(12);
            }
            if (text.charAt(12) == '§') {
                colors = colors + "§" + text.charAt(13);
            }
            if (text.charAt(13) == '§') {
                colors = colors + "§" + text.charAt(14);
            }
            if (text.charAt(14) == '§') {
                colors = colors + "§" + text.charAt(15);
            }
            if (text.charAt(15) == '§') {
                colors = colors + "§" + text.charAt(16);
                SuffixText = SuffixText.substring(1);
            }
            if (colors.length() > 4) {
                colors = colors.substring(colors.length() - 4);
            }
            suffix = colors + SuffixText;
            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }
        }
        if (text.length() <= 16) {
            suffix = ChatColor.RED.toString();
        }
        return suffix;
    }

    private static String Prefix(String text) {
        String PrefixText = text;
        if (text.length() > 16) {
            PrefixText = text.substring(0, 16);
        }
        if (text.length() > 15) {
            if (text.charAt(15) == '§') {
                PrefixText = PrefixText.substring(0, 15);
            }
        }
        return PrefixText;
    }

    private static ChatColor getId(int i) {
        if (i == 0) {
            return ChatColor.AQUA;
        } else if (i == 1) {
            return ChatColor.BLACK;
        } else if (i == 2) {
            return ChatColor.BLUE;
        } else if (i == 3) {
            return ChatColor.BOLD;
        } else if (i == 4) {
            return ChatColor.DARK_AQUA;
        } else if (i == 5) {
            return ChatColor.DARK_BLUE;
        } else if (i == 6) {
            return ChatColor.DARK_GRAY;
        } else if (i == 7) {
            return ChatColor.DARK_GREEN;
        } else if (i == 8) {
            return ChatColor.DARK_PURPLE;
        } else if (i == 9) {
            return ChatColor.DARK_RED;
        } else if (i == 10) {
            return ChatColor.GOLD;
        } else if (i == 11) {
            return ChatColor.GRAY;
        } else if (i == 12) {
            return ChatColor.GREEN;
        } else if (i == 13) {
            return ChatColor.ITALIC;
        } else if (i == 14) {
            return ChatColor.LIGHT_PURPLE;
        } else if (i == 15) {
            return ChatColor.RED;
        }
        return ChatColor.RED;
    }

    public String ponerVariables(String m) {
        String total = m;

        if (m.contains("<player>")) {
            total = total.replaceAll("<player>", p.getName());
        }
        if (m.contains("<rank:")) {
            String kit = Extra.getKitVariable(m, "rank");
            total = total.replaceAll("<rank:" + kit + ">", playerConfig.get(p).getRank(kits.get(kit)));
        }
        if (m.contains("<elo:")) {
            String kit = Extra.getKitVariable(m, "elo");
            total = total.replaceAll("<elo:" + kit + ">", "" + playerConfig.get(p).getElo(kits.get(kit)));
        }

        if (tipo == TipoScore.MAIN) {
            if (m.contains("<rankeds>")) {
                if (p.hasPermission("apvp.rankeds")) {
                    total = total.replaceAll("<rankeds>", extraLang.wordilimited);
                } else {
                    total = total.replaceAll("<rankeds>", "" + playerConfig.get(p).getRankeds());
                }

            }
            if (m.contains("<unrankeds>")) {
                if (p.hasPermission("apvp.unrankeds")) {
                    total = total.replaceAll("<unrankeds>", extraLang.wordilimited);
                } else {
                    total = total.replaceAll("<unrankeds>", "" + playerConfig.get(p).getUnRankeds());
                }
            }
        } else if (tipo == TipoScore.RANKED || tipo == TipoScore.UNRANKED || tipo == TipoScore.DUEL) {
            Game game = jugandoUno.get(p);
            Player p1 = game.p1;
            Player p2 = game.p2;
            Kit k = jugandoUno.get(p).kit;
            if (m.contains("<player1>")) {
                total = total.replaceAll("<player1>", p1.getName());
            }
            if (m.contains("<player2>")) {
                total = total.replaceAll("<player2>", p2.getName());
            }
            if (tipo == TipoScore.RANKED) {
                if (m.contains("<elo1>")) {
                    total = total.replaceAll("<elo1>", "" + playerConfig.get(p1).getElo(k));
                }
                if (m.contains("<elo2>") && p2.isOnline()) {
                    total = total.replaceAll("<elo2>", "" + playerConfig.get(p2).getElo(k));
                }
            } else if (tipo == TipoScore.DUEL) {
                if (m.contains("<point1>")) {
                    total = total.replaceAll("<point1>", "" + game.winsP1);
                }
                if (m.contains("<point2>")) {
                    total = total.replaceAll("<point2>", "" + game.winsP2);
                }
                if (m.contains("<best>")) {
                    total = total.replaceAll("<best>", "" + game.bestOf);
                }
            }
            if (m.contains("<cs1>")) {
                total = total.replaceAll("<cs1>", "" + game.getCs(p1));
            }
            if (m.contains("<cs2>") && p2.isOnline()) {
                total = total.replaceAll("<cs2>", "" + game.getCs(p2));
            }
            if (m.contains("<time>")) {
                total = total.replaceAll("<time>", Extra.secToMin(game.time));
            }
            if (m.contains("<kit>")) {
                total = total.replaceAll("<kit>", k.getKitName());
            }
            if (m.contains("<map>")) {
                total = total.replaceAll("<map>", game.map.getName());
            }
            if (m.contains("<ms1>")) {
                total = total.replaceAll("<ms1>", "" + ver.verPing(p1));
            }
            if (m.contains("<ms2>") && p2.isOnline()) {
                total = total.replaceAll("<ms2>", "" + ver.verPing(p2));
            }
        } else if (tipo == TipoScore.MEETUPWAITING || tipo == TipoScore.MEETUP) {
            GameMeetup game = meetupControl.meetupsPlaying.get(p);
            if (tipo == TipoScore.MEETUPWAITING) {
                if (m.contains("<time>")) {
                    total = total.replaceAll("<time>", Extra.secToMin(game.pretime - 1));
                }
            } else {
                if (m.contains("<time>")) {
                    total = total.replaceAll("<time>", Extra.secToMin(game.time - 1));
                }
                if (m.contains("<spectators>")) {
                    total = total.replaceAll("<spectators>", "" + game.espectadores.size());
                }
                if (m.contains("<kills>")) {
                    total = total.replaceAll("<kills>", "" + game.getKills(p));
                }
                if (m.contains("<ms>")) {
                    total = total.replaceAll("<ms>", "" + ver.verPing(p));
                }
                if (m.contains("<map>")) {
                    total = total.replaceAll("<map>", game.mapa.getName());
                }
            }
            if (m.contains("<players>")) {
                total = total.replaceAll("<players>", "" + game.players.size());
            }
            if (m.contains("<kit>")) {
                total = total.replaceAll("<kit>", game.kit.getKitName());
            }
            if (m.contains("<owner>")) {
                total = total.replaceAll("<owner>", game.owner);
            }
            if (m.contains("<title>")) {
                total = total.replaceAll("<title>", game.title);
            }
        } else if (tipo == TipoScore.PARTYMAIN || tipo == TipoScore.PARTYEVENT || tipo == TipoScore.PARTYDUEL) {
            if (tipo == TipoScore.PARTYMAIN) {
                if (m.contains("<max>") && partyControl.partys.get(p) != null) {
                    total = total.replaceAll("<max>", "" + partyControl.partys.get(p).maxplayers);
                }
            } else {
                if (tipo == TipoScore.PARTYEVENT) {
                    if (m.contains("<alive>")) {
                        total = total.replaceAll("<alive>", "" + partyControl.partysEvents.get(partyControl.partys.get(p)).players.size());
                    }
                    if (m.contains("<ms>")) {
                        total = total.replaceAll("<ms>", "" + ver.verPing(p));
                    }
                    if (m.contains("<time>")) {
                        total = total.replaceAll("<time>", Extra.secToMin(partyControl.partysEvents.get(partyControl.partys.get(p)).time));
                    }
                    if (m.contains("<kit>")) {
                        total = total.replaceAll("<kit>", partyControl.partysEvents.get(partyControl.partys.get(p)).kit.getKitName());
                    }
                    if (m.contains("<map>")) {
                        total = total.replaceAll("<map>", partyControl.partysEvents.get(partyControl.partys.get(p)).map.getName());
                    }
                    if (m.contains("<spectators>")) {
                        total = total.replaceAll("<spectators>", "" + partyControl.partysEvents.get(partyControl.partys.get(p)).espectadores.size());
                    }
                } else if (tipo == TipoScore.PARTYDUEL) {
                    if (m.contains("<alive1>")) {
                        total = total.replaceAll("<alive1>", "" + partyControl.partysDuel.get(partyControl.partys.get(p)).players1.size());
                    }
                    if (m.contains("<alive2>")) {
                        total = total.replaceAll("<alive2>", "" + partyControl.partysDuel.get(partyControl.partys.get(p)).players2.size());
                    }
                    if (m.contains("<players1>")) {
                        total = total.replaceAll("<players1>", "" + partyControl.partysDuel.get(partyControl.partys.get(p)).p1.players.size());
                    }
                    if (m.contains("<players2>")) {
                        total = total.replaceAll("<players2>", "" + partyControl.partysDuel.get(partyControl.partys.get(p)).p2.players.size());
                    }
                    if (m.contains("<owner1>")) {
                        total = total.replaceAll("<owner1>", partyControl.partysDuel.get(partyControl.partys.get(p)).p1.owner.getName());
                    }
                    if (m.contains("<owner2>")) {
                        total = total.replaceAll("<owner2>", partyControl.partysDuel.get(partyControl.partys.get(p)).p2.owner.getName());
                    }
                    if (m.contains("<kit>")) {
                        total = total.replaceAll("<kit>", partyControl.partysDuel.get(partyControl.partys.get(p)).kit.getKitName());
                    }
                    if (m.contains("<map>")) {
                        total = total.replaceAll("<map>", partyControl.partysDuel.get(partyControl.partys.get(p)).map.getName());
                    }
                    if (m.contains("<time>")) {
                        total = total.replaceAll("<time>", Extra.secToMin(partyControl.partysDuel.get(partyControl.partys.get(p)).time));
                    }
                    if (m.contains("<spectators>")) {
                        total = total.replaceAll("<spectators>", "" + partyControl.partysDuel.get(partyControl.partys.get(p)).espectadores.size());
                    }
                }

            }
            if (m.contains("<players>")) {
                total = total.replaceAll("<players>", "" + partyControl.partys.get(p).players.size());
            }
            if (m.contains("<owner>")) {
                total = total.replaceAll("<owner>", partyControl.partys.get(p).owner.getName());
            }
        }
        return total;
    }
}
