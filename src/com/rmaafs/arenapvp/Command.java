package com.rmaafs.arenapvp;

import java.io.File;

import com.rmaafs.arenapvp.GUIS.GuiEvent;
import com.rmaafs.arenapvp.KitControl.CrearKit;
import com.rmaafs.arenapvp.KitControl.CrearKitEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("apvp")) {
            if (!(sender instanceof Player)) {
                if (args[0].equalsIgnoreCase("dailyreset")) {
                    Extra.resetRankedsUnRankeds();
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if (Extra.playerConfig.containsKey(p)) {
                            Extra.playerConfig.get(p).saveStats();
                            Extra.playerConfig.get(p).stats = new Stats(p);
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
                    if (Extra.isPerm(p, "apvp.create.kit")) {
                        CrearKitEvent.creandoKit.put(p, new CrearKit(p));
                    }
                } else if (args[0].equalsIgnoreCase("createmap")) {
                    if (Extra.isPerm(p, "apvp.create.map")) {
                        if (!Main.guis.escojiendoCrearMapa.contains(p)) {
                            Main.guis.escojiendoCrearMapa.add(p);
                        }
                        p.openInventory(Main.guis.chooseKit);
                    }
                } else if (args[0].equalsIgnoreCase("setspawn")) {
                    if (Extra.isPerm(p, "apvp.setspawn")) {
                        setSpawn(p, false);
                    }
                } else if (args[0].equalsIgnoreCase("sethotbarspawn")) {
                    if (Extra.isPerm(p, "apvp.sethotbarspawn")) {
                        setSpawn(p, true);
                    }
                } else if (args[0].equalsIgnoreCase("tohead")) {
                    if (Extra.isPerm(p, "apvp.command.tohead")) {
                        if (p.getItemInHand().getType().equals(Material.GOLDEN_APPLE)) {
                            ItemMeta meta = p.getItemInHand().getItemMeta();
                            meta.setDisplayName(Main.extraLang.goldenname);
                            p.getItemInHand().setItemMeta(meta);
                            Extra.sonido(p, Extra.BURP);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("editkit")) {
                    if (Extra.isPerm(p, "apvp.create.editkit")) {
                        if (!CrearKitEvent.esperandoEditandoKit.contains(p)) {
                            CrearKitEvent.esperandoEditandoKit.add(p);
                        }
                        p.openInventory(Main.guis.chooseKit);
                    }
                } else if (args[0].equalsIgnoreCase("deletekit")) {
                    if (Extra.isPerm(p, "apvp.create.deletekit")) {
                        if (!GuiEvent.esperandoEliminarKit.contains(p)) {
                            GuiEvent.esperandoEliminarKit.add(p);
                        }
                        p.openInventory(Main.guis.chooseKit);
                    }
                } else if (args[0].equalsIgnoreCase("createmapmeetup")) {
                    if (Extra.isPerm(p, "apvp.create.map")) {
                        if (!Main.meetupControl.esperandoMapaMeetup.contains(p)) {
                            Main.meetupControl.esperandoMapaMeetup.add(p);
                        }
                        p.openInventory(Main.guis.chooseKit);
                    }
                } else if (args[0].equalsIgnoreCase("resetrankeds")) {
                    if (Extra.isPerm(p, "apvp.command.resetrankeds")) {
                        Extra.resetRankedsUnRankeds();
                        for (Player o : Bukkit.getServer().getOnlinePlayers()) {
                            Extra.playerConfig.get(o).stats.reloadRankedsUnrankeds();
                        }
                        p.sendMessage("§aRankeds and Unrankeds reseted!");
                    }
                } else {
                    sendApvp(p);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("sharemap")) {
                    if (Extra.isPerm(p, "apvp.command.sharemap")) {
                        shareMap(p, args[1], args[2]);
                    }
                } else if (args[0].equalsIgnoreCase("setrankeds")) {
                    if (Extra.isPerm(p, "apvp.command.setrankeds")) {
                        setRankeds(p, args[1], args[2], true);
                    }
                } else if (args[0].equalsIgnoreCase("setunrankeds")) {
                    if (Extra.isPerm(p, "apvp.command.setunrankeds")) {
                        setRankeds(p, args[1], args[2], false);
                    }
                } else {
                    sendApvp(p);
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("setelo")) {
                    if (Extra.isPerm(p, "apvp.command.setelo")) {
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
                        if (Extra.isPerm(p, "apvp.duel.create")) {
                            Main.duelControl.createDuel(p, args[0]);
                        }
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("accept")) {
                        if (Extra.isPerm(p, "apvp.duel.accept")) {
                            Main.duelControl.aceptarDuel(p, args[1]);
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
                    if (Extra.isPerm(p, "apvp.command.stats.other")) {
                        Player t = Bukkit.getPlayer(args[0]);
                        if (Extra.isExist(t, p)) {
                            Main.guis.openPlayerStats(t, p);
                        }
                    }
                } else if (args.length == 0) {
                    if (Extra.isPerm(p, "apvp.command.stats")) {
                        Main.guis.openPlayerStats(p, p);
                    }
                } else {
                    sendStats(p);
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("party")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("invite")) {
                        if (Extra.isPerm(p, "apvp.party.invite")) {
                            Main.partyControl.partyInvite(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("accept")) {
                        if (Extra.isPerm(p, "apvp.party.accept")) {
                            Main.partyControl.aceptarInvitacion(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("kick")) {
                        if (Extra.isPerm(p, "apvp.party.kick")) {
                            Main.partyControl.partyKick(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("promote")) {
                        if (Extra.isPerm(p, "apvp.party.promote")) {
                            Main.partyControl.partyPromote(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("duelaccept")) {
                        if (Extra.isPerm(p, "apvp.party.duelaccept")) {
                            Main.partyControl.aceptarDuel(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("acceptplayer")) {
                        if (Extra.isPerm(p, "apvp.party.acceptplayer")) {
                            Main.partyControl.aceptarPlayerAbierta(p, args[1]);
                        }
                    } else if (args[0].equalsIgnoreCase("acceptplayer")) {
                        Main.partyControl.partyLeave(p);
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
                    if (Extra.isPerm(p, "apvp.party.chat")) {
                        String s = args[0];
                        for (int i = 1; i < args.length; i++) {
                            s = s + " " + args[i];
                        }
                        Main.partyControl.partyChat(p, s);
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
                    if (Extra.isPerm(p, "apvp.giftrankeds")) {
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
                if (Extra.isPerm(p, "apvp.spectate")) {
                    if (args.length == 1) {
                        Main.specControl.spec(p, args[0]);
                    } else {
                        sendSpec(p);
                    }
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("uinventario")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (Extra.isPerm(p, "apvp.viewlastinventory")) {
                    Main.duelControl.abrirUltimoInv(p, args[0]);
                }
            }
        }
        return false;
    }

    private void shareMap(Player p, String kit, String toKit) {
        if (Extra.kits.containsKey(kit) && Extra.kits.containsKey(toKit)) {
            File f = new File(Main.plugin.getDataFolder() + File.separator + "maps");
            if (!f.exists()) {
                f.mkdir();
            }
            File elkit = new File(Main.plugin.getDataFolder() + File.separator + "maps" + File.separator + kit + ".yml");
            if (elkit.exists()) {
                File toFile = new File(Main.plugin.getDataFolder() + File.separator + "kits" + File.separator + toKit + ".yml");
                FileConfiguration ckit = YamlConfiguration.loadConfiguration(toFile);
                ckit.set("mapsharing", kit);
                Extra.guardar(toFile, ckit);
                Extra.mapLibres.put(Extra.kits.get(toKit), Extra.mapLibres.get(Extra.kits.get(kit)));
                Extra.mapOcupadas.put(Extra.kits.get(toKit), Extra.mapOcupadas.get(Extra.kits.get(kit)));

                p.sendMessage(Extra.tc(Extra.clang.getString("sharingmap.mapshared"))
                        .replaceAll("<size>", "" + (Extra.mapLibres.get(Extra.kits.get(toKit)).size() + Extra.mapOcupadas.get(Extra.kits.get(toKit)).size()))
                        .replaceAll("<kit>", kit).replaceAll("<kitshared>", toKit));
            } else {
                p.sendMessage(Extra.tc(Extra.clang.getString("sharingmap.nomapsforthiskit")));
            }
        } else {
            p.sendMessage(Extra.tc(Extra.clang.getString("sharingmap.kitinvalid")));
        }
    }

    private void setSpawn(Player p, boolean hotbar) {
        Location loc = p.getLocation();
        String path = "spawn.";
        if (hotbar) {
            path = "hotbar.";
            Main.extraLang.spawnHotbar = p.getLocation();
            p.sendMessage(Main.extraLang.spawnHotbarSet);
        } else {
            Main.extraLang.spawn = p.getLocation();
            p.sendMessage(Main.extraLang.spawnSet);
        }
        Extra.cspawns.set(path + ".w", loc.getWorld().getName());
        Extra.cspawns.set(path + ".x", loc.getX());
        Extra.cspawns.set(path + ".y", loc.getY());
        Extra.cspawns.set(path + ".z", loc.getZ());
        Extra.cspawns.set(path + ".ya", loc.getYaw());
        Extra.cspawns.set(path + ".p", loc.getPitch());
        Extra.guardar(Extra.spawns, Extra.cspawns);
        Extra.sonido(p, Extra.LEVEL_UP);
    }

    public void setElo(Player p, String t, String k, String elo) {
        Player o = Bukkit.getPlayer(t);
        if (Extra.isExist(o, p)) {
            if (Extra.kits.containsKey(k)) {
                Extra.playerConfig.get(o).stats.setElo(Extra.kits.get(k), Integer.valueOf(elo));
                p.sendMessage("§aElo of " + o.getName() + " changed. " + k + ": " + elo);
                o.sendMessage("§aElo of " + o.getName() + " changed. " + k + ": " + elo);
            } else {
                p.sendMessage("§cUnknown kit. Check Uppercase and lowercase.");
            }
        }
    }

    public void setRankeds(Player p, String t, String num, boolean ranked) {
        Player o = Bukkit.getPlayer(t);
        if (Extra.isExist(o, p)) {
            if (ranked) {
                Extra.playerConfig.get(o).stats.setRankeds(Integer.valueOf(num));
                p.sendMessage("§aRankeds of " + o.getName() + " changed: " + num);
                o.sendMessage("§aRankeds of " + o.getName() + " changed: " + num);
            } else {
                Extra.playerConfig.get(o).stats.setUnRankeds(Integer.valueOf(num));
                p.sendMessage("§aUnRankeds of " + o.getName() + " changed: " + num);
                o.sendMessage("§aUnRankeds of " + o.getName() + " changed: " + num);
            }
        }
    }

    public void giftRankeds(Player p, String o) {
        Player t = Bukkit.getPlayer(o);
        if (Extra.isExist(t, p) && !p.getName().equalsIgnoreCase(o)) {
            if (Extra.noHaDadoRankeds(p)) {
                int r = Extra.playerConfig.get(t).stats.getRankeds();
                Extra.playerConfig.get(t).stats.setRankeds(r + Main.extraLang.giftrankeds);
                p.sendMessage(Main.extraLang.gived.replaceAll("<player>", t.getName()).replaceAll("<number>", "" + Main.extraLang.giftrankeds));
                t.sendMessage(Main.extraLang.yougived.replaceAll("<player>", p.getName()).replaceAll("<number>", "" + Main.extraLang.giftrankeds));
                Extra.sonido(p, Extra.LEVEL_UP);
                Extra.sonido(t, Extra.LEVEL_UP);
            } else {
                p.sendMessage(Main.extraLang.alreadygift);
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
