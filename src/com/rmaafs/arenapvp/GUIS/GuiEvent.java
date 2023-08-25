package com.rmaafs.arenapvp.GUIS;

import java.util.ArrayList;
import java.util.List;

import com.rmaafs.arenapvp.KitControl.CrearKit;
import com.rmaafs.arenapvp.KitControl.CrearKitEvent;
import com.rmaafs.arenapvp.KitControl.EditandoKit;
import com.rmaafs.arenapvp.MapControl.CreateMap;
import com.rmaafs.arenapvp.MapControl.CrearMapaEvent;

import static com.rmaafs.arenapvp.ArenaPvP.duelControl;
import static com.rmaafs.arenapvp.ArenaPvP.guis;
import static com.rmaafs.arenapvp.ArenaPvP.hotbars;
import static com.rmaafs.arenapvp.ArenaPvP.meetupControl;
import static com.rmaafs.arenapvp.ArenaPvP.partyControl;
import static com.rmaafs.arenapvp.ArenaPvP.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class GuiEvent implements Listener {

    public static List<Player> esperandoEliminarKit = new ArrayList<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
            if (p.getInventory().contains(hotbars.itemRanked) || p.getInventory().contains(hotbars.itemUnRanked)) {
                e.setCancelled(true);
            }
        }
        if (e.getCurrentItem() != null && e.getCurrentItem().getTypeId() != 0) {
            ItemStack i = e.getCurrentItem();
            if (guis.escojiendoCrearMapa.contains(p)) {
                e.setCancelled(true);
                clickCrearMapa(i, p);
            } else if (CrearKitEvent.esperandoEditandoKit.contains(p)) {
                e.setCancelled(true);
                p.closeInventory();
                CrearKitEvent.editandoKit.put(p, new EditandoKit(p, guis.getKitSlot(e.getSlot())));
            } else if (esperandoEliminarKit.contains(p)) {
                e.setCancelled(true);
                p.closeInventory();
                guis.eliminarKit(p, e.getSlot());
            } else if (meetupControl.esperandoMapaMeetup.contains(p)) {
                e.setCancelled(true);
                p.closeInventory();
                meetupControl.crearMapa(p, e.getSlot());
            } else if (CrearKitEvent.creandoKit.containsKey(p)) {
                e.setCancelled(true);
                CrearKit ck = CrearKitEvent.creandoKit.get(p);
                if (ck.accion == CrearKit.Accion.POTIONS) {
                    ck.click(i, e.isRightClick());
                } else {
                    e.setCancelled(false);
                }
            } else if (CrearKitEvent.editandoKit.containsKey(p)) {
                e.setCancelled(true);
                EditandoKit ck = CrearKitEvent.editandoKit.get(p);
                if (ck.accion == EditandoKit.Accion.POTIONS) {
                    ck.click(i, e.isRightClick());
                } else {
                    e.setCancelled(false);
                }
            } //----------------- PARTYS ------------------------
            else if (e.getInventory().getName().equals(partyControl.invSelectName)) {
                e.setCancelled(true);
                partyControl.clickItem(p, i);
            } else if (e.getInventory().getName().equals(partyControl.invPartysOpenName)) {
                e.setCancelled(true);
                partyControl.clickOpenParty(p, i);
            } else if (e.getInventory().getName().equals(partyControl.invEventsName)
                    || e.getInventory().getName().equals(partyControl.invConfigName)) {
                e.setCancelled(true);
                partyControl.partyHash.get(p).clickGui(p, i);
            } else if (e.getInventory().getName().equals(partyControl.invPartysName)) {
                e.setCancelled(true);
                partyControl.partyHash.get(p).clickGuiPartys(p, i);
            } else if (partyControl.partyHash.containsKey(p)
                    && partyControl.partyEvents.containsKey(partyControl.partyHash.get(p))
                    && e.getInventory().getName().equals(guis.invChooseKit.getName())) {
                e.setCancelled(true);
                partyControl.partyEvents.get(partyControl.partyHash.get(p)).preStartGame(guis.getKitSlot(e.getSlot()));
            } else if (e.getInventory().getName().equals(guis.invChooseKit.getName())
                    && partyControl.preDuels.containsKey(p)) {
                e.setCancelled(true);
                partyControl.partyHash.get(p).mandarDuel(guis.getKitSlot(e.getSlot()));
            } //---------------------------------------- 
            else if (e.getClickedInventory() != null && e.getClickedInventory().equals(guis.invChooseKit)) {
                e.setCancelled(true);
                hotbars.clickInv(p, i);
            } else if (e.getInventory().equals(hotbars.invHotbars)) {
                e.setCancelled(true);
                hotbars.clickLibro(p, i.getAmount(), hotbars.editingHotbar.get(p), e.getClick().equals(ClickType.MIDDLE));
            } else if (e.getInventory().equals(guis.invUnRanked)) {
                e.setCancelled(true);
                if (e.getClick() == ClickType.MIDDLE) {
                    guis.kitPreview(p, e.getSlot());
                    guis.viendoInvUnRanked.add(p);
                } else {
                    duelControl.clickRanked(p, guis.getKitSlot(e.getSlot()), false);
                }
            } else if (e.getInventory().equals(guis.invRanked)) {
                e.setCancelled(true);
                if (e.getClick() == ClickType.MIDDLE) {
                    guis.kitPreview(p, e.getSlot());
                    guis.viendoInvRanked.add(p);
                } else {
                    duelControl.clickRanked(p, guis.getKitSlot(e.getSlot()), true);
                }
            } else if (e.getInventory().getName().equals(duelControl.guiLastInventoryName) || e.getInventory().getName().equals(guis.guiPreviewKitName)) {
                e.setCancelled(true);
                if (i.isSimilar(guis.itemLeave)) {
                    guis.clickearLeave(p);
                }
            } else if (duelControl.creandoDuel.containsKey(p)) {
                e.setCancelled(true);
                if (e.getClickedInventory().equals(guis.invDuel)) {
                    if (e.getClick() == ClickType.MIDDLE) {
                        guis.kitPreview(p, e.getSlot());
                    } else {
                        duelControl.clickKit(p, i.getAmount(), e.getSlot(), true);
                    }
                } else if (e.getClickedInventory().equals(duelControl.invBestof)) {
                    duelControl.clickKit(p, i.getAmount(), e.getSlot(), false);
                } else if (e.getInventory().getName().equals(duelControl.guiLastInventoryName) || e.getInventory().getName().equals(guis.guiPreviewKitName)) {
                    if (i.isSimilar(guis.itemLeave)) {
                        guis.clickearLeave(p);
                    }
                }
            } //------------  MEETUP  -------------------------------
            else if (e.getInventory().getName().equals(meetupControl.invMeetup.getName())) {
                e.setCancelled(true);
                if (i.isSimilar(guis.itemLeave)) {
                    p.closeInventory();
                } else if (i.isSimilar(meetupControl.itemCreate)) {
                    p.closeInventory();
                    meetupControl.esperandoCrearEvento.add(p);
                    p.openInventory(guis.chooseKit);
                } else {
                    meetupControl.clickItem(p, e.getSlot(), e.getClick().equals(ClickType.MIDDLE));
                }
            } else if (meetupControl.esperandoCrearEvento.contains(p)) {
                e.setCancelled(true);
                p.closeInventory();
                meetupControl.crearEvento(p, i);
            } else if (meetupControl.creandoEventoMeetup.containsKey(p)) {
                e.setCancelled(true);
                meetupControl.creandoEventoMeetup.get(p).click(i, e.isRightClick());
            } //---------------------------------------------
            else if (e.getInventory().getName().equals(guis.guiStatsName)) {
                e.setCancelled(true);
            }
            else if (i.isSimilar(hotbars.itemRanked)
                    || i.isSimilar(hotbars.itemUnRanked)
                    || i.isSimilar(hotbars.itemEditHotbar)
                    || i.isSimilar(hotbars.itemParty)
                    || i.isSimilar(hotbars.itemMeetup)
                    || i.isSimilar(hotbars.itemLeave)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        final Player p = (Player) e.getPlayer();
        if (e.getInventory().equals(hotbars.invHotbars) && hotbars.editingHotbar.containsKey(p)) {
            hotbars.editingHotbar.remove(p);
        }
        if (duelControl.creandoDuel.containsKey(p)) {
            if (((e.getInventory().equals(guis.invDuel) && duelControl.creandoDuel.get(p).getKit() == null)
                    || (e.getInventory().equals(duelControl.invBestof) && duelControl.creandoDuel.get(p).getTotal() == 0))) {
                duelControl.creandoDuel.remove(p);
            }
        }
        if (meetupControl.creandoEventoMeetup.containsKey(p) && !meetupControl.creandoEventoMeetup.get(p).writing && e.getInventory().getName().equals(meetupControl.invMeetupConfigName)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    meetupControl.creandoEventoMeetup.get(p).openInv();
                }
            }, 1L);
        }
        if (e.getInventory().getName().equals(guis.invChooseKit.getName())) {
            if (CrearKitEvent.esperandoEditandoKit.contains(p)) {
                CrearKitEvent.esperandoEditandoKit.remove(p);
            } else if (esperandoEliminarKit.contains(p)) {
                esperandoEliminarKit.remove(p);
            } else if (meetupControl.esperandoMapaMeetup.contains(p)) {
                meetupControl.esperandoMapaMeetup.remove(p);
            } else if (meetupControl.esperandoCrearEvento.contains(p)) {
                meetupControl.esperandoCrearEvento.remove(p);
            } else if (partyControl.partyHash.containsKey(p)
                    && partyControl.partyEvents.containsKey(partyControl.partyHash.get(p))
                    && partyControl.partyEvents.get(partyControl.partyHash.get(p)).kit == null /*&& e.getInventory().getName().equals(guis.invChooseKit.getName())*/) {
                partyControl.partyEvents.remove(partyControl.partyHash.get(p));
            } else if (partyControl.partyHash.containsKey(p)
                    && partyControl.preDuels.containsKey(p)
                    && partyControl.preDuels.get(p).getKit() == null) {
                partyControl.preDuels.remove(p);
            }
        } else if (e.getInventory().getName().equals(guis.guiPreviewKitName)) {
            if (meetupControl.viendoMiddle.contains(p)) {
                meetupControl.viendoMiddle.remove(p);
            }
        }
        guis.sacarViendoInv(p);
    }

    public void clickCrearMapa(ItemStack i, Player p) {
        p.closeInventory();
        guis.escojiendoCrearMapa.remove(p);
        CrearMapaEvent.creandoMapa.put(p, new CreateMap(p, guis.itemKits.get(i)));
    }
}
