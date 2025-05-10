package felnull.dev.akasiweaponarsenal.commands;

import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import felnull.dev.akasiweaponarsenal.gui.awagui.page.ArsenalPage;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenArsenalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        InventoryGUI gui = new InventoryGUI((Player) sender);
        gui.openPage(new ArsenalPage(gui, AkasiWeaponArsenal.getPageDataMap().get(args[0])));
        return true;
    }
}
