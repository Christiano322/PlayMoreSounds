package br.net.christiano322.PlayMoreSounds.api;

import br.net.christiano322.PlayMoreSounds.*;
import br.net.christiano322.PlayMoreSounds.regions.*;
import br.net.christiano322.PlayMoreSounds.utils.*;
import java.io.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;

import br.net.christiano322.PlayMoreSounds.utils.Utility;

public class RegionManager {
	public enum RegionCreationResult {
		ALREADY_EXISTS, DIFFERENT_WORLDS, SUCCESS, UNEXPECTED
		}

	public File formatFile(final File s) {
        File file = s;
		long i = 2;
		while (file.exists()) {
			String string = file.getName().substring(0, file.getName().lastIndexOf("."));
			String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(file.getName()));
			String extension = file.getName().substring(file.getName().lastIndexOf("."));
			boolean bool = false;
			if (string.charAt(string.length() - 1) != ' ') {
				if (string.charAt(string.lastIndexOf(" ") + 1) != '(') {
					bool = true;
				}
			}
			String space = "";
			if (bool) {
				space = " ";
			}
			if (is(string)) {
				i = Long.parseLong(string.substring(string.lastIndexOf("(") + 1).replace(")", ""));
				file = new File(path + string.substring(0, string.lastIndexOf("(")) + space + "(" + (i + 1) + ")" + extension);
			} else {
				file = new File(path + string + space + "(2)" + extension);
			}
			i++;
		}
		return file;
	}

	boolean is(String string) {
		if (string.charAt(string.length() - 1) == ')') {
			if (string.length() > 1) {
				if (Utility.isLong(String.valueOf(string.charAt(string.length() - 2)))) {
					return true;
				}
			}
		}
		return false;
	}

	public RegionCreationResult createRegion(Location p1, Location p2, String name) {
		if (p1.getWorld().getName().equals(p2.getWorld().getName())) {
			File folder = new File(PMS.plugin.getDataFolder(), "regions");
			if (folder.exists()) {
				if (folder.listFiles().length > 0) {
					for (File f : folder.listFiles()) {
						FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
						if (conf.contains("Name")) {
							if (conf.getString("Name").equals(name)) {
								return RegionCreationResult.ALREADY_EXISTS;
							}
						}
					}
				}
			} else {
				folder.mkdirs();
			}
			String data;
			data = "Name: " + name;
			data = data + "\nWorld: " + p1.getWorld().getName();
			data = data + "\nLocations:";
			if (p1.getY() > p2.getY()) {
				data = data + "\n  P1:";
				data = data + "\n    X: " + p1.getX();
				data = data + "\n    Y: " + p1.getY();
				data = data + "\n    Z: " + p1.getZ();
				data = data + "\n  P2:";
				data = data + "\n    X: " + p2.getX();
				data = data + "\n    Y: " + p2.getY();
				data = data + "\n    Z: " + p2.getZ();
			} else {
				data = data + "\n  P1:";
				data = data + "\n    X: " + p2.getX();
				data = data + "\n    Y: " + p2.getY();
				data = data + "\n    Z: " + p2.getZ();
				data = data + "\n  P2:";
				data = data + "\n    X: " + p1.getX();
				data = data + "\n    Y: " + p1.getY();
				data = data + "\n    Z: " + p1.getZ();
			}
			if (data.equals(null)) {
				return RegionCreationResult.UNEXPECTED;
			} else {
				try {
					ErrorReport.stringToFile(data, formatFile(new File(folder, name.toLowerCase() + ".yml")), false);
				} catch (IOException e) {
					ErrorReport.errorReport(e, "RegionCreateIOException Exception: (Unknown)");
					return RegionCreationResult.UNEXPECTED;
				}
			}
		} else {
			return RegionCreationResult.DIFFERENT_WORLDS;
		}
		return RegionCreationResult.SUCCESS;
	}

	public void renameRegion(String oldName, String newName) {
		getRegionByName(oldName).renameTo(newName);
	}

	public void renameRegion(SoundRegion region, String newName) {
		region.renameTo(newName);
	}

	public SoundRegion getRegionByName(String name) throws NullPointerException {
		SoundRegion s = null;
		for (File file : new File(PMS.plugin.getDataFolder(), "regions").listFiles()) {
			FileConfiguration f = YamlConfiguration.loadConfiguration(file);
			if (f.contains("Name")) {
				if (f.getString("Name").equals(name)) {
					s = new SoundRegion(f.getString("Name"),
										new Location(Bukkit.getWorld(f.getString("World")), f.getDouble("Locations.P1.X"),
													 f.getDouble("Locations.P1.Y"), f.getDouble("Locations.P1.Z")),
										new Location(Bukkit.getWorld(f.getString("World")), f.getDouble("Locations.P2.X"),
													 f.getDouble("Locations.P2.Y"), f.getDouble("Locations.P2.Z")),
										file);
				}
			}
		}
		return s;
	}

	public boolean isInsideARegion(Location loc) {
		File folder = new File(PMS.plugin.getDataFolder(), "regions");
		if (folder.exists()) {
			if (folder.listFiles().length != 0) {
				for (File file : folder.listFiles()) {
					FileConfiguration f = YamlConfiguration.loadConfiguration(file);
					Location l1 = new Location(Bukkit.getWorld(f.getString("World")), f.getDouble("Locations.P1.X"),
											   f.getDouble("Locations.P1.Y"), f.getDouble("Locations.P1.Z"));
					Location l2 = new Location(Bukkit.getWorld(f.getString("World")), f.getDouble("Locations.P2.X"),
											   f.getDouble("Locations.P2.Y"), f.getDouble("Locations.P2.Z"));
					int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
					int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
					int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
					int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
					int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
					int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

					return loc.getBlockX() >= x1 & loc.getBlockX() <= x2 & loc.getBlockY() >= y1 & loc.getBlockY() <= y2
						& loc.getBlockZ() >= z1 & loc.getBlockZ() <= z2;
				}
			}
		}
		return false;
	}

	public SoundRegion getRegionByLocation(Location loc) {
		File folder = new File(PMS.plugin.getDataFolder(), "regions");
		if (folder.exists()) {
			if (folder.listFiles().length != 0) {
				for (File file : folder.listFiles()) {
					FileConfiguration f = YamlConfiguration.loadConfiguration(file);
					Location l1 = new Location(Bukkit.getWorld(f.getString("World")), f.getDouble("Locations.P1.X"),
											   f.getDouble("Locations.P1.Y"), f.getDouble("Locations.P1.Z"));
					Location l2 = new Location(Bukkit.getWorld(f.getString("World")), f.getDouble("Locations.P2.X"),
											   f.getDouble("Locations.P2.Y"), f.getDouble("Locations.P2.Z"));
					int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
					int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
					int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
					int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
					int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
					int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

					if (loc.getBlockX() >= x1 & loc.getBlockX() <= x2 & loc.getBlockY() >= y1 & loc.getBlockY() <= y2
						& loc.getBlockZ() >= z1 & loc.getBlockZ() <= z2) {
						return new SoundRegion(f.getString("Name"), l1, l2, file);
					}
				}
			}
		}
		return null;
	}
}
