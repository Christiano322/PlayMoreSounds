package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class PlayerCommand implements Listener {

	PMS main;

	public PlayerCommand(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeCommand(PlayerCommandPreprocessEvent e) {
		try {
			EventName event = EventName.PLAYER_COMMAND;
			if (main.getConfig().getConfigurationSection("PerCommandSounds").getBoolean("Enabled")) {
				Set<String> keys = main.commands.getKeys(false);
				String[] split = e.getMessage().split(" ");
				if (keys.contains(split[0])) {
					try {
						if (e.isCancelled()) {
							if (main.commands.getBoolean(split[0] + ".Cancellable")) {
								return;
							}
						}
						new SoundPlayer(main).playSound(event, main.commandsFile, main.commands, e.getPlayer(), split[0], null, null, false, null);
						return;
					} catch (Exception l) {
					}
				}
			}
			String name = event.toName();
			if (e.isCancelled()) {
				if (main.sounds.getBoolean(name + ".Cancellable")) {
					return;
				}
			}
			new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, e.getPlayer(), name, null, null, false, null);
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				main.logger.log(name + "(Message) -");
				System.out.println(e.getMessage());
			}

		} catch (Exception l) {
			if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
				l.printStackTrace();
			}
		}
	}
}
