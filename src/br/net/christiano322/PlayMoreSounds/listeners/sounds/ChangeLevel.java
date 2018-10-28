package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class ChangeLevel implements Listener {

    PMS main;

    public ChangeLevel(PMS main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void MakeChangeLevel(PlayerLevelChangeEvent e) {
        EventName event = EventName.CHANGE_LEVEL;
        String name = event.toName();
        try {
            new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, null, false, null);
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                main.logger.log(name + "(Levels) -");
                System.out.println("(New) " + e.getNewLevel() + "/(Old)" + e.getOldLevel());
            }
        } catch (Exception l) {
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                l.printStackTrace();
            }
        }
    }
}
