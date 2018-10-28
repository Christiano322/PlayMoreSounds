package br.net.christiano322.PlayMoreSounds.commands;

import br.net.christiano322.PlayMoreSounds.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private PMS main;

    public TabCompleter(PMS main) {
        this.main = main;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ((sender instanceof Player)) {
            if (args[0].equalsIgnoreCase("play")) {
                if (args.length == 2) {
                    if (sender.hasPermission("playmoresounds.play")) {
                        List<Sound> obtainedList = Arrays.asList(Sound.values());
                        List<String> newList = new ArrayList<>();
                        for (Sound s : obtainedList) {
                            if (args[1].startsWith(s.toString())) {
                                newList.add(s.toString());
                            } else {
                                newList.add(s.toString());
                            }
                        }
                        return newList;
                    } else {
                        return null;
                    }
                }
                if (args.length == 3) {
                    if (sender.hasPermission("playmoresounds.play")) {
                        String obtainedList = "me * ";
                        String[] split = obtainedList.split(" ");
                        List<String> list = Arrays.asList(split);
                        List<String> newList = new ArrayList<>();
                        for (String s : list) {
                            newList.add(s);
                        }
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            newList.add(p.getName());
                        }
                        return newList;
                    } else {
                        return null;
                    }
                }
            }
            if (args.length == 1) {
                String obtainedList = "";
                if (main.getConfig().getBoolean("HelpBasedOnPermissions")) {
                    if (sender.hasPermission("playmoresounds.help")) {
                        obtainedList = obtainedList + "help ";
                    }
                    if (sender.hasPermission("playmoresounds.reload")) {
                        obtainedList = obtainedList + "reload ";
                    }
                    if (sender.hasPermission("playmoresounds.confirm")) {
                        obtainedList = obtainedList + "confirm ";
                    }
                    if (sender.hasPermission("playmoresounds.restore")) {
                        obtainedList = obtainedList + "restore ";
                    }
                    if (sender.hasPermission("playmoresounds.toggle")) {
                        obtainedList = obtainedList + "toggle ";
                    }
                    if (sender.hasPermission("playmoresounds.toggle.check")) {
                        obtainedList = obtainedList + "sounds ";
                    }
                    if (sender.hasPermission("playmoresounds.play")) {
                        obtainedList = obtainedList + "play ";
                    }
                    if (sender.hasPermission("playmoresounds.list")) {
                        obtainedList = obtainedList + "list ";
                    }
                    if (sender.hasPermission("playmoresounds.update")) {
                        obtainedList = obtainedList + "update";
                    }
                } else {
                    obtainedList = "help reload confirm restore toggle sounds play list update";
                }
                String[] split = obtainedList.split(" ", 9);
                List<String> list = Arrays.asList(split);
                List<String> newList = new ArrayList<>();
                for (String s : list) {
                    newList.add(s);
                }
                return newList;
            }
        }
        return null;
    }
}
