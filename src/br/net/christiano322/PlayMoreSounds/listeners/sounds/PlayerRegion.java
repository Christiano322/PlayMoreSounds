package br.net.christiano322.PlayMoreSounds.listeners.sounds;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.api.events.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import br.net.christiano322.PlayMoreSounds.sound.*;

public class PlayerRegion implements Listener {

	private PMS main;

	public PlayerRegion(PMS main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeRegionEnter(EnterRegionEvent e) {
		EventName event = EventName.REGION_ENTER;
		Player p = e.getPlayer();
		if (main.regions.contains("PlayMoreSounds")) {
			Set<String> keys = main.regions.getConfigurationSection("PlayMoreSounds").getKeys(true);
			if (keys.contains(e.getRegion().getName() + ".Enter")) {
				if (e.isCancelled()) {
					if (main.regions.getBoolean("PlayMoreSounds." + e.getRegion().getName() + ".Enter.Cancellable")) {
						return;
					}
				}
				new SoundPlayer(main).playSound(event, main.regionsFile, main.regions, p, "PlayMoreSounds." + e.getRegion().getName() + ".Enter", null, null, false, null);
				return;
			}
		}
		String name = event.toName();
		if (e.isCancelled()) {
			if (main.sounds.getConfigurationSection(name).getBoolean("Cancellable")) {
				return;
			}
		}
		new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, p, "RegionEnter", null, null, false, null);
		if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
			main.logger.log(name + "#PlayMoreSounds (Region) -");
			System.out.println(e.getRegion());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MakeRegionEnter(ExitRegionEvent e) {
		Player p = e.getPlayer();
		EventName event = EventName.REGION_LEAVE;
		if (main.sounds.getBoolean("RegionEnter.StopOnExit")) {
			for (String s : main.sounds.getConfigurationSection("RegionEnter.Sounds").getKeys(false)) {
				p.stopSound(SoundEnum.valueOf(main.sounds.getConfigurationSection("RegionEnter.Sounds").getConfigurationSection(s).getString("Sound")).bukkitSound());
			}
		}
		if (main.regions.contains("PlayMoreSounds")) {
			Set<String> keys = main.regions.getConfigurationSection("PlayMoreSounds").getKeys(true);
			String enter = "PlayMoreSounds." + e.getRegion().getName() + ".Enter";
			if (keys.contains(e.getRegion().getName() + ".Enter")) {
				if (main.regions.getBoolean(enter + ".StopOnExit")) {
					for (String s : main.regions.getConfigurationSection(enter + ".Sounds").getKeys(false)) {
						p.stopSound(SoundEnum.valueOf(main.regions.getConfigurationSection(enter + ".Sounds").getConfigurationSection(s).getString("Sound")).bukkitSound());
					}
				}
			}
			if (keys.contains(e.getRegion().getName() + ".Leave")) {
				if (e.isCancelled()) {
					if (main.regions.getBoolean("PlayMoreSounds." + e.getRegion().getName() + "Leave.Cancellable")) {
						return;
					}
				}
				new SoundPlayer(main).playSound(event, main.regionsFile, main.regions, p, "PlayMoreSounds." + e.getRegion().getName() + ".Leave", null, null, false, null);
				return;
			}
		}
		String name = event.toName();
		if (e.isCancelled()) {
			if (main.sounds.getConfigurationSection(name).getBoolean("Cancellable")) {
				return;
			}
		}
		new SoundPlayer(main).playSound(event, main.soundsFile, main.sounds, p, "RegionLeave", null, null, false, null);
		if (main.getConfig().getString("DebugOutput").equalsIgnoreCase("developer")) {
			main.logger.log(name + "#PlayMoreSounds (Region) -");
			System.out.println(e.getRegion());
		}
	}
}
