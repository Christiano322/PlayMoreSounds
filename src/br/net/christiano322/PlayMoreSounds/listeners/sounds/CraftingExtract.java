package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class CraftingExtract implements Listener {

    PMS main;

    public CraftingExtract(PMS main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void MakeCrafting(CraftItemEvent e) {
        EventName event = EventName.CRAFTING_EXTRACT;
        String name = event.toName();
        try {
            if (e.getWhoClicked() instanceof Player) {
                if (e.isCancelled()) {
                    if (main.sounds.getConfigurationSection(name).getBoolean("Cancellable")) {
                        return;
                    }
                }
                new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, (Player) e.getWhoClicked(), name, null, null, false, null);
                if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                    main.logger.log(name + "(Action/Click/Slot/Cursor)");
                    System.out.println(e.getAction() + "/" + e.getClick() + "/" + e.getSlot() + "(" + e.getSlotType()
									   + ")" + "/" + e.getCursor());
                }
            }
        } catch (Exception l) {
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                l.printStackTrace();
            }
        }
    }
}
