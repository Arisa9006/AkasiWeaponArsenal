package felnull.dev.akasiweaponarsenal.commands;

import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import felnull.dev.akasiweaponarsenal.gui.awagui.page.ArsenalPage;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenArsenalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            sender.sendMessage("ページ名を入力してください 例:/awaopen testpage playerid");
            return true;
        }
        if(!AkasiWeaponArsenal.getPageDataMap().containsKey(args[0])){
            sender.sendMessage("ページ名に誤りがありますもう一度入力してください");
            return true;
        }
        InventoryGUI gui;
        if(args.length == 2) {
            if(Bukkit.getPlayer(args[1]) == null){
                sender.sendMessage("指定されたプレイヤーは存在しないまたはオフラインです");
                return true;
            }
            gui = new InventoryGUI(Bukkit.getPlayer(args[1]));
        }else {
            if(sender instanceof Player) {
                gui = new InventoryGUI((Player) sender);
            }else {
                sender.sendMessage("コンソールの場合はプレイヤー名を指定してください");
                return true;
            }
        }
        gui.openPage(new ArsenalPage(gui, AkasiWeaponArsenal.getPageDataMap().get(args[0])));
        return true;
    }
}
