package br.net.christiano322.PlayMoreSounds.listeners.sounds.MC1_7_9;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

@SuppressWarnings("deprecation")
public class PlayerAchievement implements Listener {

    PMS main;

    public PlayerAchievement(PMS main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void MakeAchievement(PlayerAchievementAwardedEvent e) {
        EventName event = EventName.PLAYER_ACHIEVEMENT;
        String name = event.toName();
        try {
            if (e.isCancelled()) {
                if (main.sounds.getConfigurationSection(name).getBoolean("Cancellable")) {
                    return;
                }
            }
            new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, null, false, null);
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                main.logger.log(name + "(Achievement) -");
                System.out.println(e.getAchievement());
            }
        } catch (Exception l) {
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                l.printStackTrace();
            }
        }
    }
}
