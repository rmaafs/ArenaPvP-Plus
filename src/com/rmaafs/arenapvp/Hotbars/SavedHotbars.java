package com.rmaafs.arenapvp.Hotbars;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.rmaafs.arenapvp.Convertor;
import com.rmaafs.arenapvp.Kit;
import static com.rmaafs.arenapvp.Main.guis;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SavedHotbars {

    Kit kit;
    Player p;
    HashMap<Integer, ItemStack[]> hotbar = new HashMap<>();

    public SavedHotbars(Player pp, Kit k) {
        p = pp;
        kit = k;
    }

    public void setHotbar(int i, ItemStack[] hot) {
        hotbar.put(i, hot);
    }

    public ItemStack[] getHotbar(int i) {
        if (!hotbar.containsKey(i)) {
            if (guis.kitsHotbar.get(kit).cfile.contains(p.getName() + "." + i)) {
                try {
                    hotbar.put(i, Convertor.itemFromBase64(guis.kitsHotbar.get(kit).cfile.getString(p.getName() + "." + i)));
                    return hotbar.get(i);
                } catch (IOException ex) {
                    Logger.getLogger(SavedHotbars.class.getName()).log(Level.SEVERE, null, ex);
                    return kit.hotbar;
                }
            }
            return kit.hotbar;
        }
        return hotbar.get(i);
    }
}
