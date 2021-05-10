package com.rmaafs.arenapvp.Hotbars;

import com.rmaafs.arenapvp.Kit;

public class EditingHotbar {

    Kit kit;
    int slot;
    
    public EditingHotbar(Kit k, int s){
        kit = k;
        slot = s;
    }

    public Kit getKit() {
        return kit;
    }

    public int getSlot() {
        return slot;
    }
    
    
}
