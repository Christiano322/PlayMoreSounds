package br.net.christiano322.PlayMoreSounds.utils;

import org.bukkit.*;

public class Logger {

    public void log(String message) {
        Bukkit.getConsoleSender()
			.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&9PlayMoreSounds&6]&r " + message));
    }

    public void warn(String message) {
        Bukkit.getConsoleSender()
			.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&9PlayMoreSounds&6]&c ") + message);
    }

    public void error(String message) {
        Bukkit.getConsoleSender()
			.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&9PlayMoreSounds&6]&4 ") + message);
    }

    public void info(String message) {
        Bukkit.getConsoleSender()
			.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&9PlayMoreSounds&6]&a ") + message);
    }

    public void success(String message) {
        Bukkit.getConsoleSender()
			.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6[&9PlayMoreSounds&6]&2 ") + message);
    }
}
