package com.rmaafs.arenapvp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.rmaafs.arenapvp.Extra.clang;
import static com.rmaafs.arenapvp.Extra.jugandoUno;
import static com.rmaafs.arenapvp.Extra.scores;

import com.rmaafs.arenapvp.Juegos.Meetup.GameMeetup;
import com.rmaafs.arenapvp.Party.DuelGame;
import com.rmaafs.arenapvp.Party.EventGame;
import com.rmaafs.arenapvp.Party.Party;

import static com.rmaafs.arenapvp.Main.meetupControl;
import static com.rmaafs.arenapvp.Main.partyControl;
import static com.rmaafs.arenapvp.Main.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reloj {

    public Reloj() {
        setConfig();
    }

    String startingGame;
    List<String> startingDuelStart;

    public void setConfig() {
        startingGame = Extra.tc(clang.getString("starting.game"));

        startingDuelStart = Extra.tCC(clang.getStringList("starting.duel.start"));
        tiempo();
    }

    private void tiempo() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                new Runnable() {
                    int sec = 0;
                    int day = 0;

                    @Override
                    public void run() {
                        sec++;
                        if (sec == 20) {
                            try {
                                segundo();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            day++;
                            if (day > 600) {
                                day = -1;
                                Extra.detectNextDay();
                            }
                            sec = -1;
                        }
                    }
                }, 0, 1L);
    }

    private void segundo() {
        actualizarScore();
        preEmpezarDuels();
        quitarSegundoPartidas();
        quitarSegundoPreMeetups();
        quitarSegundoPrePartyEvents();
        quitarSegundoPrePartyDuels();

        quitarSegundoMeetups();
        quitarSegundoPartyDuel();
        quitarSegundoPartyEvent();
    }

    public void actualizarScore() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (scores.containsKey(p)) {
                scores.get(p).update();
            }
        }
    }

    private void preEmpezarDuels() {
        List<Game> terminadas = new ArrayList<>();
        for (Game game : Extra.preEmpezandoUno) {
            if (game.preTime == 0) {
                game.startGame(startingDuelStart);
                terminadas.add(game);
            } else {
                game.starting(startingGame.replaceAll("<time>", "" + game.preTime));
                game.preTime--;
            }
        }
        Extra.preEmpezandoUno.removeAll(terminadas);
    }

    private void quitarSegundoPartidas() {
        List<Game> editadas = new ArrayList<>();
        for (Map.Entry<Player, Game> entry : jugandoUno.entrySet()) {
            Game game = entry.getValue();
            if (!editadas.contains(game)) {
                game.removerSec();
                editadas.add(game);
            }
        }
    }

    private void quitarSegundoMeetups() {
        List<GameMeetup> editadas = new ArrayList<>();
        for (Map.Entry<Integer, GameMeetup> entry : meetupControl.meetups.entrySet()) {
            GameMeetup partida = entry.getValue();
            if (!editadas.contains(partida)) {
                partida.removerSec();
                editadas.add(partida);
            }
        }
    }

    private void quitarSegundoPartyDuel() {
        List<DuelGame> editadas = new ArrayList<>();
        for (Map.Entry<Party, DuelGame> entry : partyControl.partysDuel.entrySet()) {
            DuelGame partida = entry.getValue();
            if (!editadas.contains(partida)) {
                partida.removerSec();
                editadas.add(partida);
            }
        }
    }

    private void quitarSegundoPartyEvent() {
        List<EventGame> editadas = new ArrayList<>();
        for (Map.Entry<Party, EventGame> entry : partyControl.partysEvents.entrySet()) {
            EventGame partida = entry.getValue();
            if (!editadas.contains(partida)) {
                partida.removerSec();
                editadas.add(partida);
            }
        }
    }

    private void quitarSegundoPreMeetups() {
        List<GameMeetup> empezadas = new ArrayList<>();
        for (GameMeetup game : meetupControl.meetupStarting) {
            if (game.removePretime()) {
                game.start();
                empezadas.add(game);
            }
        }
        meetupControl.meetupStarting.removeAll(empezadas);
    }

    private void quitarSegundoPrePartyEvents() {
        List<EventGame> empezadas = new ArrayList<>();
        for (EventGame game : partyControl.startingsEvents) {
            if (game.removePretime()) {
                game.start();
                empezadas.add(game);
            }
        }
        partyControl.startingsEvents.removeAll(empezadas);
    }

    private void quitarSegundoPrePartyDuels() {
        List<DuelGame> empezadas = new ArrayList<>();
        for (DuelGame game : partyControl.startingPartyDuel) {
            if (game.removePretime()) {
                game.start();
                empezadas.add(game);
            }
        }
        partyControl.startingPartyDuel.removeAll(empezadas);
    }
}
