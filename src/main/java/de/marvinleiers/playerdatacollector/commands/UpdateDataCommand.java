package de.marvinleiers.playerdatacollector.commands;

import de.marvinleiers.playerdatacollector.PlayerDataCollector;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpdateDataCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if (args.length != 1)
        {
            sender.sendMessage("§cUsage: /updatedata <player>");
            return true;
        }

        Player target;

        if ((target = Bukkit.getPlayer(args[0])) == null)
        {
            sender.sendMessage("§c" + args[0] + " is not online!");
            return true;
        }

        PlayerDataCollector.getInstance().update(target);

        sender.sendMessage("§aSuccessfully updated " + target.getName() + "'s data!");
        return true;
    }
}
