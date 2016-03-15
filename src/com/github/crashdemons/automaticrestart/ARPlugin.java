/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.automaticrestart;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
/**
 *
 * @author crash
 */
public final class ARPlugin extends JavaPlugin {
    
    // Fired when plugin is first enabled
    private static final int TPS = 20;
    
    private BukkitTask task_warn=null;
    private BukkitTask task_restart=null;
    
    private long timeToTicks(long h,long m, long s){
        s+=60*(m + 60*h);
        return s*TPS;
    }
    
    private String cancelRestart(){
        if(task_warn==null && task_warn==null) return "No restart was scheduled.";
        getLogger().info("Stopping AR tasks...");
        if(task_warn!=null) task_warn.cancel();
        if(task_restart!=null) task_restart.cancel();
        task_warn=null;
        task_restart=null;
        getLogger().info("Stopped AR tasks.");
        return "The scheduled restart was cancelled for this session.";
    }
    
    private String scheduleRestart(long h, long m, long s){
        cancelRestart();
        getLogger().info("Scheduling new AR tasks...");
        long ticks_warn = timeToTicks(h,m,s);
        long ticks_restart = timeToTicks(h,m,s+30);
        task_warn = new ARTask_warn(this).runTaskLater(this, ticks_warn);
        task_restart = new ARTask_restart(this).runTaskLater(this, ticks_restart);
        getLogger().info("Scheduled new AR tasks.");
        return "Scheduled restart for "+h+"h "+m+"m "+s+"s from now.";
    }
    
    
    @Override
    public void onEnable() {
        getLogger().info("AR Enabling...");
        scheduleRestart(0,5,0);
        getLogger().info("AR Enabled.");
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
        getLogger().info("AR Disabling...");
        cancelRestart();
        getLogger().info("AR Disabed.");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ar")) {
            if(!sender.hasPermission("automaticrestart.ar") || !sender.isOp()) sender.sendMessage("You don't have permission to do this.");
            else{
                if(args.length==1){
                    if(args[0].equalsIgnoreCase("now")){
                            sender.sendMessage(scheduleRestart(0,0,1));
                            return true;
                    }
                    if(args[0].equalsIgnoreCase("cancel")){
                            sender.sendMessage(cancelRestart());
                            return true;
                    }
                }
                if(args.length==3){
                    try {
                        sender.sendMessage(scheduleRestart(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[0])));
                        return true;  
                     } catch (NumberFormatException e) {  
                        sender.sendMessage("The time values entered could not be understood.");
                        return false;
                     }  
                }
            }
        }
        
        return false;
    }

    
   
    
    
    
}
