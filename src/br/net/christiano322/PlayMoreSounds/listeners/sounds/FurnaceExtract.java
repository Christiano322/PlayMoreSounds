package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class FurnaceExtract implements Listener {

    PMS main;

    public FurnaceExtract(PMS main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void MakeFurnace(FurnaceExtractEvent e) {
        EventName event = EventName.FURNACE_EXTRACT;
        String name = event.toName();
        try {
            new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, e.getBlock().getLocation(), false, null);
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                System.out.println("(Block) " + e.getBlock().getType().toString() + "/(ExpToDrop)" + e.getExpToDrop() + "/(ItemAmount)" + e.getItemAmount() + "/(ItemType)" + e.getItemType().toString());
            }
        } catch (Exception l) {
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                l.printStackTrace();
            }
        }
    }
}
