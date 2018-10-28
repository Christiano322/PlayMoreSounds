package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class ChangeHotbar implements Listener {

    PMS main;

    public ChangeHotbar(PMS main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void MakeChangeHotbar(PlayerItemHeldEvent e) {
        EventName event = EventName.CHANGE_HOTBAR;
        String name = event.toName();
        try {
            if (e.isCancelled()) {
                if (main.sounds.getConfigurationSection(name).getBoolean("Cancellable")) {
                    return;
                }
            }
            new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, null, false, null);
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                main.logger.log(name + "(Slots) -");
                System.out.println("(New) " + e.getNewSlot() + "/(Old)" + e.getPreviousSlot());
            }
        } catch (Exception l) {
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                l.printStackTrace();
            }
        }
    }
}
