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
    
    private long current_h=0;
    private long current_m=0;
    private long current_s=0;
    private boolean enabled=false;
    
    private long timeToTicks(long h,long m, long s){
        s+=60*(m + 60*h);
        return s*TPS;
    }
    private String retlog(String s){
        getLogger().info(s);
        return s;
    }
    
    private void loadConfig(){
        saveDefaultConfig();
        reloadConfig();
        enabled=this.getConfig().getBoolean("interval.enabled");
        current_h=this.getConfig().getLong("interval.time.h");
        current_m=this.getConfig().getLong("interval.time.m");
        current_s=this.getConfig().getLong("interval.time.s");
    }
    private String reload(){
        cancelRestart();
        loadConfig();
        String action="interval disabled in configuration.";
        if(enabled) action=scheduleRestart(current_h,current_m,current_s);
        return retlog("Reloaded config - "+action);
    }
    
    private String cancelRestart(){
        enabled=false;
        if(task_warn==null && task_warn==null) return retlog("No restart was scheduled.");
        getLogger().info("Stopping AR tasks...");
        if(task_warn!=null) task_warn.cancel();
        if(task_restart!=null) task_restart.cancel();
        task_warn=null;
        task_restart=null;
        getLogger().info("Stopped AR tasks.");
        return retlog("The scheduled restart was cancelled for this session.");
    }
    
    private String scheduleRestart(long h, long m, long s){
        cancelRestart();
        enabled=true;
        getLogger().info("Scheduling new AR tasks...");
        current_h=h;
        current_m=m;
        current_s=s;
        long ticks_warn = timeToTicks(h,m,s);
        long ticks_restart = timeToTicks(h,m,s+30);
        task_warn = new ARTask_warn(this).runTaskLater(this, ticks_warn);
        task_restart = new ARTask_restart(this).runTaskLater(this, ticks_restart);
        getLogger().info("Scheduled new AR tasks.");
        return retlog("Scheduled restart for "+h+"h "+m+"m "+s+"s from now.");
    }
    
    
    @Override
    public void onEnable() {
        getLogger().info("AR Enabling...");
        reload();
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
            if(!sender.hasPermission("automaticrestart.ar")){ sender.sendMessage("You don't have permission to do this."); return true; }
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
                    if(args[0].equalsIgnoreCase("reload")){
                            sender.sendMessage(reload());
                            return true;
                    }
                    if(args[0].equalsIgnoreCase("status")){
                            String[] status={"ENABLED","DISABLED"};
                            int i=enabled?0:1;
                            sender.sendMessage("Scheduled restart is "+status[i]+" and was last set to: "+current_h+"h "+current_m+"m "+current_s+"s.");
                            return true;
                    }
                }
                if(args.length==3){
                    try {
                        sender.sendMessage(scheduleRestart(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[0])));
                        return true;  
                     } catch (NumberFormatException e) {  
                        sender.sendMessage(retlog("The time values entered could not be understood."));
                        return false;
                     }  
                }
            }
        }
        
        return false;
    }

    
   
    
    
    
}
