package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class GamemodeChange implements Listener {

    PMS main;

    public GamemodeChange(PMS main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void MakeChangeGamemode(PlayerGameModeChangeEvent e) {
        EventName event = EventName.GAMEMODE_CHANGE;
        try {
            if (main.getConfig().getConfigurationSection("PerGamemodeSounds").getBoolean("Enabled")) {
                Set<String> keys = main.gamemodes.getKeys(false);
                if (keys.contains(e.getNewGameMode().toString())) {
                    if (e.isCancelled()) {
                        if (main.gamemodes.getBoolean(e.getNewGameMode().toString() + ".Cancellable")) {
                            return;
                        }
                    }
                    new SoundPlayer(main).playSound(event, main.gamemodesFile, main.gamemodes, e.getPlayer(), e.getNewGameMode().toString(), null, null, false, null);
                    return;
                }
            }
			String name = event.toName();
            if (e.isCancelled()) {
                if (main.sounds.getConfigurationSection(name).getBoolean("Cancellable")) {
                    return;
                }
            }
            new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, null, false, null);
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                main.logger.log(name + "(Gamemode) -");
                System.out.println(e.getNewGameMode());
            }
        } catch (Exception l) {
            if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
                l.printStackTrace();
            }
        }
    }
}
