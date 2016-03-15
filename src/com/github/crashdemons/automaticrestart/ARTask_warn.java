/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.automaticrestart;

/**
 *
 * @author crash
 */
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ARTask_warn extends BukkitRunnable {

    private final JavaPlugin plugin;

    public ARTask_warn(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    

    @Override
    public void run() {
        // What you want to schedule goes here
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        plugin.getServer().broadcastMessage("Restarting server in 30 seconds.");
        Bukkit.dispatchCommand(console, "title @a times 10 60 10");
        Bukkit.dispatchCommand(console, "title @a subtitle {\"text\":\"A server restart will occur in 30 seconds.\"}");
        Bukkit.dispatchCommand(console, "title @a title {\"text\": \"Restarting soon!\",\"color\": \"red\"}");
    }

}