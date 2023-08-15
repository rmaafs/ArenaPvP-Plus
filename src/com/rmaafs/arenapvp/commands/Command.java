package com.rmaafs.arenapvp.commands;

import com.rmaafs.arenapvp.ArenaPvP;
import com.rmaafs.arenapvp.GUIS.GuiEvent;
import com.rmaafs.arenapvp.KitControl.CrearKit;
import com.rmaafs.arenapvp.KitControl.CrearKitEvent;
import com.rmaafs.arenapvp.manager.data.Stats;
import com.rmaafs.arenapvp.util.Extra;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

import static com.rmaafs.arenapvp.util.Extra.*;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("apvp")) {
            if (!(sender instanceof Player)) {
                if (args[0].equalsIgnoreCase("dailyreset")) {
                    resetRankedsUnRankeds();
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if (playerConfig.containsKey(p)) {
                            playerConfig.get(p).saveStats();
                            playerConfig.get(p).stats = new Stats(p);
                        }
                    }
                    sender.sendMessage("§aAPVP > Daily rankeds reseted!");
                } else {
                    sender.sendMessage("§cAPVP > Command only for players.");
                }
                return true;
            }
            Player p = (Player) sender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("createkit")) {
                    if (isPerm(p, "apvp.create.kit")) {
                        CrearKitEvent.creandoKit.put(p, new CrearKit(p));
                    }
                } else if (args[0].equalsIgnoreCase("createmap")) {
                    if (isPerm(p, "apvp.create.map")) {
                        if (!ArenaPvP.guis.escojiendoCrearMapa.contains(p)) {
                            ArenaPvP.guis.escojiendoCrearMapa.add(p);
                        }
                        p.openInventory(ArenaPvP.guis.chooseKit);
                    }
                } else if (args[0].equalsIgnoreCase("setspawn")) {
                    if (isPerm(p, "apvp.setspawn")) {
                        setSpawn(p, false);
                    }
                } else if (args[0].equalsIgnoreCase("sethotbarspawn")) {
                    if (isPerm(p, "apvp.sethotbarspawn")) {
                        setSpawn(p, true);
                    }
                } else if (args[0].equalsIgnoreCase("tohead")) {
                    if (isPerm(p, "apvp.command.tohead")) {
                        if (p.getItemInHand().getType().equals(Material.GOLDEN_APPLE)) {
                            ItemMeta meta = p.getItemInHand().getItemMeta();
                            meta.setDisplayName(ArenaPvP.extraLang.goldenname);
                            p.getItemInHand().setItemMeta(meta);
                            sonido(p, BURP);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("editkit")) {
                    if (isPerm(p, "apvp.create.editkit")) {
                        if (!CrearKitEvent.esperandoEditandoKit.contains(p)) {
                            CrearKitEvent.esperandoEditandoKit.add(p);
                        }
                        p.openInventory(ArenaPvP.guis.chooseKit);
                    }
                } else if (args[0].equalsIgnoreCase("deletekit")) {
                    if (isPerm(p, "apvp.create.deletekit")) {
                        if (!GuiEvent.esperandoEliminarKit.contains(p)) {
                            GuiEvent.esperandoEliminarKit.add(p);
                        }
                        p.openInventory(ArenaPvP.guis.chooseKit);
                    }
                } else if (args[0].equalsIgnoreCase("createmapmeetup")) {
                    if (isPerm(p, "apvp.create.map")) {
                        if (!ArenaPvP.meetupControl.esperandoMapaMeetup.contains(p)) {
                            ArenaPvP.meetupControl.esperandoMapaMeetup.add(p);
                        }
                        p.openInventory(ArenaPvP.guis.chooseKit);
                    }
                } else if (args[0].equalsIgnoreCase("resetrankeds")) {
                    if (isPerm(p, "apvp.command.resetrankeds")) {
                        resetRankedsUnRankeds();
                        for (Player o : Bukkit.getServer().getOnlinePlayers()) {
                            playerConfig.get(o).stats.reloadRankedsUnrankeds();
                        }
                        p.sendMessage("§aRankeds and Unrankeds reseted!");
                    }
                } else {
                    sendApvp(p);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("sharemap")) {
                    if (isPerm(p, "apvp.command.sharemap")) {
                        shareMap(p, args[1], args[2]);
                    }
                } else if (args[0].equalsIgnoreCase("setrankeds")) {
                    if (isPerm(p, "apvp.command.setrankeds")) {
                        setRankeds(p, args[1], args[2], true);
                    }
                } else if (args[0].equalsIgnoreCase("setunrankeds")) {
                    if (isPerm(p, "apvp.command.setunrankeds")) {
                        setRankeds(p, args[1], args[2], false);
                    }
                } else {
                    sendApvp(p);
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("setelo")) {
                    if (isPerm(p, "apvp.command.setelo")) {
                        setElo(p, args[1], args[2], args[3]);
                    }
                } else {
                    sendApvp(p);
                }
            } else {
                sendApvp(p);
            }
        }

        if (cmd.getName().equalsIgnoreCase("duel")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    if (!args[0].equalsIgnoreCase(p.getName())) {
                        if (isPerm(p, "apvp.duel.create")) {
                            ArenaPvP.duelControl.createDuel(p, args[0]);
                        }
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("accept")) {
                        if (isPerm(p, "apvp.duel.accept")) {
                            ArenaPvP.duelControl.aceptarDuel(p, args[1]);
                        }
                    }
                } else {
                    sendDuel(p);
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("stats")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    if (isPerm(p, "apvp.command.stats.other")) {
                        Player t = Bukkit.getPlayer(args[0]);
                        if (isExist(t, p)) {
                            ArenaPvP.guis.openPlayerStats(t, p);
                        }
                    }
                } else if (args.length == 0) {
                    if (isPerm(p, "apvp.command.stats")) {
                        ArenaPvP.guis.openPlayerStats(p, p);
                    }
                } else {
                    sendStats(p);
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("aparty")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("invite")) {
                        if (isPerm(p, "apvp.party.invite")) {
                            ArenaPvP.partyControl.partyInvite(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("accept")) {
                        if (isPerm(p, "apvp.party.accept")) {
                            ArenaPvP.partyControl.aceptarInvitacion(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("kick")) {
                        if (isPerm(p, "apvp.party.kick")) {
                            ArenaPvP.partyControl.partyKick(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("promote")) {
                        if (isPerm(p, "apvp.party.promote")) {
                            ArenaPvP.partyControl.partyPromote(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("duelaccept")) {
                        if (isPerm(p, "apvp.party.duelaccept")) {
                            ArenaPvP.partyControl.aceptarDuel(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("acceptplayer")) {
                        if (isPerm(p, "apvp.party.acceptplayer")) {
                            ArenaPvP.partyControl.aceptarPlayerAbierta(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("acceptplayer")) {
                        ArenaPvP.partyControl.partyLeave(p);
                    } else {
                        sendParty(p);
                    }
                } else {
                    sendParty(p);
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("pc")) {
            if (sender instanceof Player) {
                if (args.length >= 1) {
                    Player p = (Player) sender;
                    if (isPerm(p, "apvp.party.chat")) {
                        String s = args[0];
                        for (int i = 1; i < args.length; i++) {
                            s = s + " " + args[i];
                        }
                        ArenaPvP.partyControl.partyChat(p, s);
                    }
                } else {
                    sendParty((Player) sender);
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("giftrankeds")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    if (isPerm(p, "apvp.giftrankeds")) {
                        giftRankeds(p, args[0]);
                    }
                } else {
                    sendGift(p);
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("spec")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (isPerm(p, "apvp.spectate")) {
                    if (args.length == 1) {
                        ArenaPvP.specControl.spec(p, args[0]);
                    } else {
                        sendSpec(p);
                    }
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("uinventario")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (isPerm(p, "apvp.viewlastinventory")) {
                    ArenaPvP.duelControl.abrirUltimoInv(p, args[0]);
                }
            }
        }
        return false;
    }

    private void shareMap(Player p, String kit, String toKit) {
        if (kits.containsKey(kit) && kits.containsKey(toKit)) {
            File f = new File(ArenaPvP.plugin.getDataFolder() + File.separator + "maps");
            if (!f.exists()) {
                f.mkdir();
            }
            File elkit = new File(ArenaPvP.plugin.getDataFolder() + File.separator + "maps" + File.separator + kit + ".yml");
            if (elkit.exists()) {
                File toFile = new File(ArenaPvP.plugin.getDataFolder() + File.separator + "kits" + File.separator + toKit + ".yml");
                FileConfiguration ckit = YamlConfiguration.loadConfiguration(toFile);
                ckit.set("mapsharing", kit);
                guardar(toFile, ckit);
                mapLibres.put(kits.get(toKit), mapLibres.get(kits.get(kit)));
                mapOcupadas.put(kits.get(toKit), mapOcupadas.get(kits.get(kit)));

                p.sendMessage(tc(clang.getString("sharingmap.mapshared"))
                        .replaceAll("<size>", "" + (mapLibres.get(kits.get(toKit)).size() + mapOcupadas.get(kits.get(toKit)).size()))
                        .replaceAll("<kit>", kit).replaceAll("<kitshared>", toKit));
            } else {
                p.sendMessage(tc(clang.getString("sharingmap.nomapsforthiskit")));
            }
        } else {
            p.sendMessage(tc(clang.getString("sharingmap.kitinvalid")));
        }
    }

    private void setSpawn(Player p, boolean hotbar) {
        Location loc = p.getLocation();
        String path = "spawn.";
        if (hotbar) {
            path = "hotbar.";
            ArenaPvP.extraLang.spawnHotbar = p.getLocation();
            p.sendMessage(ArenaPvP.extraLang.spawnHotbarSet);
        } else {
            ArenaPvP.extraLang.spawn = p.getLocation();
            p.sendMessage(ArenaPvP.extraLang.spawnSet);
        }
        cspawns.set(path + ".w", loc.getWorld().getName());
        cspawns.set(path + ".x", loc.getX());
        cspawns.set(path + ".y", loc.getY());
        cspawns.set(path + ".z", loc.getZ());
        cspawns.set(path + ".ya", loc.getYaw());
        cspawns.set(path + ".p", loc.getPitch());
        guardar(spawns, cspawns);
        sonido(p, LEVEL_UP);
    }

    public void setElo(Player p, String t, String k, String elo) {
        Player o = Bukkit.getPlayer(t);
        if (isExist(o, p)) {
            if (kits.containsKey(k)) {
                playerConfig.get(o).stats.setElo(kits.get(k), Integer.valueOf(elo));
                p.sendMessage("§aElo of " + o.getName() + " changed. " + k + ": " + elo);
                o.sendMessage("§aElo of " + o.getName() + " changed. " + k + ": " + elo);
            } else {
                p.sendMessage("§cUnknown kit. Check Uppercase and lowercase.");
            }
        }
    }

    public void setRankeds(Player p, String t, String num, boolean ranked) {
        Player o = Bukkit.getPlayer(t);
        if (isExist(o, p)) {
            if (ranked) {
                playerConfig.get(o).stats.setRankeds(Integer.valueOf(num));
                p.sendMessage("§aRankeds of " + o.getName() + " changed: " + num);
                o.sendMessage("§aRankeds of " + o.getName() + " changed: " + num);
            } else {
                playerConfig.get(o).stats.setUnRankeds(Integer.valueOf(num));
                p.sendMessage("§aUnRankeds of " + o.getName() + " changed: " + num);
                o.sendMessage("§aUnRankeds of " + o.getName() + " changed: " + num);
            }
        }
    }

    public void giftRankeds(Player p, String o) {
        Player t = Bukkit.getPlayer(o);
        if (isExist(t, p) && !p.getName().equalsIgnoreCase(o)) {
            if (noHaDadoRankeds(p)) {
                int r = playerConfig.get(t).stats.getRankeds();
                playerConfig.get(t).stats.setRankeds(r + ArenaPvP.extraLang.giftrankeds);
                p.sendMessage(ArenaPvP.extraLang.gived.replaceAll("<player>", t.getName()).replaceAll("<number>", "" + ArenaPvP.extraLang.giftrankeds));
                t.sendMessage(ArenaPvP.extraLang.yougived.replaceAll("<player>", p.getName()).replaceAll("<number>", "" + ArenaPvP.extraLang.giftrankeds));
                sonido(p, LEVEL_UP);
                sonido(t, LEVEL_UP);
            } else {
                p.sendMessage(ArenaPvP.extraLang.alreadygift);
            }
        }
    }

    public void sendApvp(Player p) {
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
        p.sendMessage("§c/apvp createkit§7 - Create new kit.");
        p.sendMessage("§c/apvp createmap§7 - Create new map.");
        p.sendMessage("§c/apvp setspawn§7 - Set main spawn");
        p.sendMessage("§c/apvp sethotbarspawn§7 - Set spawn of hotbar edition");
        p.sendMessage("§c/apvp tohead§7 - Change Golden Apple to Golden Head.");
        p.sendMessage("§c/apvp editkit§7 - Edit a kit");
        p.sendMessage("§c/apvp deletekit§7 - Delete a kit");
        p.sendMessage("§c/apvp createmapmeetup§7 - Create a map of meetup");
        p.sendMessage("§c/apvp resetrankeds§7 - Reset daily rankeds and unrankeds.");
        p.sendMessage("§c/apvp sharemap <Maps of original Kit> <Kit to share maps>§7 - Share maps of a kit.");
        p.sendMessage("§c/apvp setunrankeds " + p.getName() + " 10");
        p.sendMessage("§c/apvp setrankeds " + p.getName() + " 10");
        p.sendMessage("§c/apvp setelo " + p.getName() + " BuildUHC 2000");
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
    }

    public void sendDuel(Player p) {
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
        p.sendMessage("§c/duel <player>");
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
    }

    public void sendStats(Player p) {
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
        p.sendMessage("§c/stats");
        p.sendMessage("§c/stats <player>");
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
    }

    public void sendParty(Player p) {
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
        p.sendMessage("§c/party invite <player>");
        p.sendMessage("§c/party kick <player>");
        p.sendMessage("§c/party promote <player>§7 - Give owner of a player");
        p.sendMessage("§c/pc <msg>§7 - Party chat");
        p.sendMessage("§c/party leave");
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
    }

    public void sendGift(Player p) {
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
        p.sendMessage("§c/giftrankeds <player>§7 - Gift ranked matches.");
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
    }

    public void sendSpec(Player p) {
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
        p.sendMessage("§c/spec <player>.");
        p.sendMessage("§c/spec leave.");
        p.sendMessage("§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-§4§l§m-§c§l§m-");
    }
}
