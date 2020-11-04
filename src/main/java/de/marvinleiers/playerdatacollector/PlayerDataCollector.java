package de.marvinleiers.playerdatacollector;

import de.marvinleiers.playerdatacollector.commands.UpdateDataCommand;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public final class PlayerDataCollector extends JavaPlugin implements Listener
{
    private HashMap<Player, List<Integer>> avgPing = new HashMap<>();
    private int taskId;

    @Override
    public void onEnable()
    {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("updatedata").setExecutor(new UpdateDataCommand());
        this.startCollecting();
    }

    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        update(event.getPlayer());

        this.getLogger().info("Updated data of " + event.getPlayer().getName());
    }

    private void startCollecting()
    {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            @Override
            public void run()
            {
                Bukkit.getOnlinePlayers().stream().forEach(player -> avgPing.get(player).add(((CraftPlayer) player).getHandle().ping));
            }
        }, 0, 20 * 60 * 5);
    }

    public void update(Player target)
    {
        int ping = PlayerDataCollector.getInstance().getAveragePing(target) / 1000;
        String ip = ((CraftPlayer) target).getAddress().getAddress().getHostAddress();
        long firstTimeLoggedIn = target.getFirstPlayed();
        String locale = target.getLocale();

        PlayerDataCollector.getInstance().getConfig().set(target.getUniqueId().toString() + ".ping", ping);
        PlayerDataCollector.getInstance().getConfig().set(target.getUniqueId().toString() + ".ip", ip);
        PlayerDataCollector.getInstance().getConfig().set(target.getUniqueId().toString() + ".firstLogin", firstTimeLoggedIn);
        PlayerDataCollector.getInstance().getConfig().set(target.getUniqueId().toString() + ".locale", locale);
        PlayerDataCollector.getInstance().saveConfig();
    }

    public int getAveragePing(Player player)
    {
        if (!avgPing.containsKey(player))
            return ((CraftPlayer) player).getHandle().ping;

        int i = 0;

        for (int integer : avgPing.get(player))
        {
            i += integer / 1000;
        }

        return i / avgPing.get(player).size();
    }

    public static PlayerDataCollector getInstance()
    {
        return getPlugin(PlayerDataCollector.class);
    }
}
