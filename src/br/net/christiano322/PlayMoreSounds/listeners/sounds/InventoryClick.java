package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class InventoryClick implements Listener {

    PMS main;

    public InventoryClick(PMS main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void MakeInventoryClick(InventoryClickEvent e) {
        EventName event = EventName.INVENTORY_CLICK;
        String name = event.toName();
        try {
            if (e.isCancelled()) {
                if (main.sounds.getConfigurationSection(name).getBoolean("Cancellable")) {
                    return;
                }
            }
            Player p = (Player) e.getWhoClicked();
            if ((e.getClick().equals(ClickType.WINDOW_BORDER_LEFT))
				| (e.getClick().equals(ClickType.WINDOW_BORDER_RIGHT))
				| (e.getClick().equals(ClickType.CONTROL_DROP)) | (e.getClick().equals(ClickType.DROP))) {
                return;
            }
            new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, p, name, null, null, false, null);
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                main.logger.log(name + "(Action/Click/Slot/Cursor)");
                System.out.println(e.getAction() + "/" + e.getClick() + "/" + e.getSlot() + "(" + e.getSlotType() + ")"
								   + "/" + e.getCursor());
            }
        } catch (Exception l) {
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                l.printStackTrace();
            }
        }
    }
}
