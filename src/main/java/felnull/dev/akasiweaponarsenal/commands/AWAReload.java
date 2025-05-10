package felnull.dev.akasiweaponarsenal.commands;

import felnull.dev.akasiweaponarsenal.dataio.PageDataIO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AWAReload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PageDataIO.load();
        sender.sendMessage("提督! リロードが完了いたしました!");
        return true;
    }
}
