package com.rmaafs.arenapvp.KitControl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rmaafs.arenapvp.entity.GameMap;
import com.rmaafs.arenapvp.util.Extra;
import static com.rmaafs.arenapvp.util.Extra.cconfig;
import static com.rmaafs.arenapvp.util.Extra.clang;
import static com.rmaafs.arenapvp.util.Extra.kits;
import com.rmaafs.arenapvp.util.Convertor;
import static com.rmaafs.arenapvp.util.Extra.mapLibres;
import static com.rmaafs.arenapvp.util.Extra.mapOcupadas;
import com.rmaafs.arenapvp.util.file.FileKits;
import com.rmaafs.arenapvp.manager.kit.Kit;
import static com.rmaafs.arenapvp.ArenaPvP.guis;
import static com.rmaafs.arenapvp.ArenaPvP.hotbars;
import static com.rmaafs.arenapvp.ArenaPvP.plugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CrearKit {

    public enum Accion {

        NOMBRE, NOMBRECOLOR, INVENTARIO, ITEMSBORRAR, POTIONS, ITEM
    };

    String name, namecolor, inventory, itemstodelete, potions, item, created;

    public Accion accion = Accion.NOMBRE;
    Player p;
    public Inventory inv;

    String kitName, kitNameColor;
    ItemStack[] hotbar, armor;
    List<ItemStack> deleteBlocks = new ArrayList<>();
    ItemStack itemSpeed, itemStrength, itemSlow, itemFire, itemCombo, itemTime, itemReady, itemNatural;
    public ItemStack itemOnGui;
    int levelSpeed = 1, levelStrength = 1, levelSlow = 1, slot = 0;
    int time;
    List<PotionEffect> potionList = new ArrayList<>();
    boolean regen = false, combo = false, natural = true;

    public CrearKit(Player player) {
        p = player;
        setConfig();
        paso();
    }

    public void setConfig() {
        inv = Bukkit.createInventory(null, cconfig.getInt("creating.gui.rows") * 9, Extra.tc(clang.getString("creating.gui.guiname")));

        time = cconfig.getInt("defaultmaxtime");

        itemSpeed = Extra.crearId(cconfig.getInt("creating.gui.speed.id"), cconfig.getInt("creating.gui.speed.data-value"), Extra.tc(clang.getString("creating.gui.speed.name").replaceAll("<level>", "" + levelSpeed)), Extra.tCC(clang.getStringList("creating.gui.speed.lore")), 1);
        itemStrength = Extra.crearId(cconfig.getInt("creating.gui.strength.id"), cconfig.getInt("creating.gui.strength.data-value"), Extra.tc(clang.getString("creating.gui.strength.name").replaceAll("<level>", "" + levelStrength)), Extra.tCC(clang.getStringList("creating.gui.strength.lore")), 1);
        itemSlow = Extra.crearId(cconfig.getInt("creating.gui.slowness.id"), cconfig.getInt("creating.gui.slowness.data-value"), Extra.tc(clang.getString("creating.gui.slowness.name").replaceAll("<level>", "" + levelSlow)), Extra.tCC(clang.getStringList("creating.gui.slowness.lore")), 1);
        itemFire = Extra.crearId(cconfig.getInt("creating.gui.fire_resistance.id"), cconfig.getInt("creating.gui.fire_resistance.data-value"), Extra.tc(clang.getString("creating.gui.fire_resistance.name")), Extra.tCC(clang.getStringList("creating.gui.fire_resistance.lore")), 1);
        itemCombo = Extra.crearId(cconfig.getInt("creating.gui.hitdelay.id"), cconfig.getInt("creating.gui.hitdelay.data-value"), Extra.tc(clang.getString("creating.gui.hitdelay.name")), Extra.tCC(clang.getStringList("creating.gui.hitdelay.lore")), 1);
        itemTime = Extra.crearId(cconfig.getInt("creating.gui.matchtime.id"), cconfig.getInt("creating.gui.matchtime.data-value"), Extra.tc(clang.getString("creating.gui.matchtime.name").replaceAll("<time>", Extra.secToMin(time))), Extra.tCC(clang.getStringList("creating.gui.matchtime.lore")), 1);
        itemReady = Extra.crearId(cconfig.getInt("creating.gui.ready.id"), cconfig.getInt("creating.gui.ready.data-value"), Extra.tc(clang.getString("creating.gui.ready.name")), Extra.tCC(clang.getStringList("creating.gui.ready.lore")), 1);
        itemNatural = Extra.crearId(cconfig.getInt("creating.gui.naturalregeneration.id"), cconfig.getInt("creating.gui.naturalregeneration.data-value"), Extra.tc(clang.getString("creating.gui.naturalregeneration.name")), Extra.tCC(clang.getStringList("creating.gui.naturalregeneration.lore")), 1);

        inv.setItem(cconfig.getInt("creating.gui.speed.slot"), itemSpeed);
        inv.setItem(cconfig.getInt("creating.gui.strength.slot"), itemStrength);
        inv.setItem(cconfig.getInt("creating.gui.slowness.slot"), itemSlow);
        inv.setItem(cconfig.getInt("creating.gui.fire_resistance.slot"), itemFire);
        inv.setItem(cconfig.getInt("creating.gui.hitdelay.slot"), itemCombo);
        inv.setItem(cconfig.getInt("creating.gui.matchtime.slot"), itemTime);
        inv.setItem(cconfig.getInt("creating.gui.ready.slot"), itemReady);
        inv.setItem(cconfig.getInt("creating.gui.naturalregeneration.slot"), itemNatural);

        name = Extra.tc(clang.getString("creating.kit.name"));
        namecolor = Extra.tc(clang.getString("creating.kit.namecolor"));
        inventory = Extra.tc(clang.getString("creating.kit.inventory"));
        itemstodelete = Extra.tc(clang.getString("creating.kit.itemstodelete"));
        potions = Extra.tc(clang.getString("creating.kit.potions"));
        item = Extra.tc(clang.getString("creating.kit.item"));
        created = Extra.tc(clang.getString("creating.kit.created"));
    }

    private void paso() {
        switch (accion) {
            case NOMBRE:
                p.sendMessage(name);
                break;
            case NOMBRECOLOR:
                p.sendMessage(namecolor);
                break;
            case INVENTARIO:
                p.sendMessage(inventory);
                Extra.cleanPlayer(p);
                p.setGameMode(GameMode.CREATIVE);
                break;
            case ITEMSBORRAR:
                p.sendMessage(itemstodelete);
                Extra.cleanPlayer(p);
                p.setGameMode(GameMode.CREATIVE);
                break;
            case POTIONS:
                p.sendMessage(potions);
                Extra.cleanPlayer(p);
                p.openInventory(inv);
                break;
            case ITEM:
                p.sendMessage(item);
                p.setGameMode(GameMode.CREATIVE);
                break;
        }
    }

    public void setKitName(String s, boolean color) {
        if (color) {
            kitNameColor = Extra.tc(s);
            accion = Accion.INVENTARIO;
        } else {
            kitName = s;
            accion = Accion.NOMBRECOLOR;
        }
        paso();
    }

    public void setInventory() {
        armor = p.getInventory().getArmorContents();
        hotbar = p.getInventory().getContents();
        accion = Accion.ITEMSBORRAR;
        paso();
    }

    public void setRegenItems() {
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null) {
                if (i.getAmount() != 1) {
                    i.setAmount(1);
                }
                deleteBlocks.add(i);
                regen = true;
            }
        }
        accion = Accion.POTIONS;
        paso();
    }

    public void click(ItemStack item, boolean right) {
        int level = 1;
        if (item.isSimilar(itemSpeed)) {
            if (right) {
                if (++levelSpeed > 2) {
                    levelSpeed = 1;
                }
                level = levelSpeed;
            }
            itemSpeed = item;
            Extra.changeName(item, clang.getString("creating.gui.speed.name").replaceAll("<level>", "" + levelSpeed));
        } else if (item.isSimilar(itemStrength)) {
            if (right) {
                if (++levelStrength > 2) {
                    levelStrength = 1;
                }
                level = levelStrength;
            }
            itemStrength = item;
            Extra.changeName(item, clang.getString("creating.gui.strength.name").replaceAll("<level>", "" + levelStrength));
        } else if (item.isSimilar(itemSlow)) {
            if (right) {
                if (++levelSlow > 2) {
                    levelSlow = 1;
                }
                level = levelSlow;
            }
            itemSlow = item;
            Extra.changeName(item, clang.getString("creating.gui.slowness.name").replaceAll("<level>", "" + levelSlow));
        } else if (item.isSimilar(itemFire)) {
            level = 1;
            itemFire = item;
        } else if (item.isSimilar(itemCombo)) {
            level = 1;
            itemCombo = item;
        } else if (item.isSimilar(itemNatural)) {
            level = 1;
            itemNatural = item;
        } else if (item.isSimilar(itemTime)) {
            if (right) {
                time += 60;
                if (time % 60 != 0) {
                    time = 60;
                }
            } else {
                time -= 60;
                if (time < 10) {
                    time = 30;
                }
            }
            itemTime = item;
            Extra.changeName(item, clang.getString("creating.gui.matchtime.name").replaceAll("<time>", Extra.secToMin(time)));
        } else if (item.isSimilar(itemReady)) {
            saveEnchant();
        }
        if (!right && !item.equals(itemTime) && !item.equals(itemReady)) {
            if (item.containsEnchantment(Enchantment.LUCK)) {
                item.removeEnchantment(Enchantment.LUCK);
            } else {
                item.addUnsafeEnchantment(Enchantment.LUCK, level);
            }
        }
    }

    private void saveEnchant() {
        if (itemSpeed.containsEnchantment(Enchantment.LUCK)) {
            potionList.add(new PotionEffect(PotionEffectType.SPEED, 99999, levelSpeed));
        }
        if (itemStrength.containsEnchantment(Enchantment.LUCK)) {
            potionList.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, levelStrength));
        }
        if (itemSlow.containsEnchantment(Enchantment.LUCK)) {
            potionList.add(new PotionEffect(PotionEffectType.SLOW, 99999, levelSlow));
        }
        if (itemFire.containsEnchantment(Enchantment.LUCK)) {
            potionList.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999, 1));
        }
        if (itemCombo.containsEnchantment(Enchantment.LUCK)) {
            combo = true;
        }
        if (itemNatural.containsEnchantment(Enchantment.LUCK)) {
            natural = false;
        }
        accion = Accion.ITEM;
        p.closeInventory();
        paso();
    }

    public void clickItem(ItemStack item) {
        if (item.getAmount() != 1) {
            item.setAmount(1);
        }
        Extra.changeName(item, kitNameColor);
        itemOnGui = item;
        p.openInventory(guis.acomodacion);
    }

    public void createKit() {
        File f = new File(plugin.getDataFolder() + File.separator + "kits");
        if (!f.exists()) {
            f.mkdir();
        }
        f = new File(plugin.getDataFolder() + File.separator + "hotbar");
        if (!f.exists()) {
            f.mkdir();
        }

        guis.saveItems();
        for (int i = 0; i < guis.acomodacion.getContents().length; i++) {
            if (guis.acomodacion.getContents()[i] != null && guis.acomodacion.getContents()[i].isSimilar(itemOnGui)) {
                slot = i;
            }
        }
        Kit k = new Kit(kitName, kitNameColor, deleteBlocks, potionList, hotbar, armor, itemOnGui, slot, time, combo, natural);
        kits.put(kitName, k);
        guis.itemKits.put(itemOnGui, k);
        guis.saveItems();
        p.sendMessage(created.replaceAll("<kit>", kitName));
        if (CrearKitEvent.creandoKit.containsKey(p)) {
            CrearKitEvent.creandoKit.remove(p);
        }
        hotbars.setMain(p);

        List<GameMap> lista1 = new ArrayList<>();
        mapLibres.put(k, lista1);
        
        List<GameMap> lista2 = new ArrayList<>();
        mapOcupadas.put(k, lista2);
        
        File elkit = new File(plugin.getDataFolder() + File.separator + "kits" + File.separator + kitName + ".yml");
        try {
            elkit.createNewFile();

            FileConfiguration ckit = YamlConfiguration.loadConfiguration(elkit);
            ckit.set("name", kitName);
            ckit.set("namecolor", kitNameColor);
            ckit.set("regen", regen);
            ckit.set("slot", slot);
            ckit.set("time", time);
            ckit.set("naturalregeneration", natural);
            ckit.set("combo", combo);

            for (PotionEffect e : potionList) {
                ckit.set("potionlist." + e.getType().getName(), e.getAmplifier());
            }

            List<ItemStack> it = new ArrayList<>();
            it.add(itemOnGui);
            ckit.set("item", Convertor.itemToBase64(it.toArray(new ItemStack[0])));

            ItemStack[] items = deleteBlocks.toArray(new ItemStack[0]);

            ckit.set("delete", Convertor.itemToBase64(items));

            ckit.set("hotbar", Convertor.itemToBase64(hotbar));
            ckit.set("armor", Convertor.itemToBase64(armor));

            Extra.guardar(elkit, ckit);

            File hot = new File(plugin.getDataFolder() + File.separator + "hotbar" + File.separator + kitName + ".yml");
            hot.createNewFile();
            FileConfiguration chot = YamlConfiguration.loadConfiguration(hot);
            guis.kitsHotbar.put(k, new FileKits(hot, chot));
            Extra.guardar(hot, chot);
        } catch (IOException ex) {
            Logger.getLogger(CrearKit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
