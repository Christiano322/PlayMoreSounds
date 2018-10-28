package br.net.christiano322.PlayMoreSounds.listeners.sounds.MC1_12;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerAdvancementDone implements Listener {

    private PMS main;

    public PlayerAdvancementDone(PMS main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void MakeAdvancement(PlayerAdvancementDoneEvent e) {
        EventName event = EventName.PLAYER_ADVANCEMENT_DONE;
        try {
            new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), event.toName(), null, null, false, null);
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                main.logger.log(event.toName() + "(Advancement) -");
                System.out.println(e.getAdvancement());
            }
        } catch (Exception l) {
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                l.printStackTrace();
            }
        }
    }
}
