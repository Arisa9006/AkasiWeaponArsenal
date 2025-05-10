package felnull.dev.akasiweaponarsenal.commands;

import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PageList implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("+---- ページリスト! ----+");
        AkasiWeaponArsenal.getPageDataMap().forEach((key, value) -> {
            sender.sendMessage(key);
        });
        sender.sendMessage("+-------------------+");
        return true;
    }
}
