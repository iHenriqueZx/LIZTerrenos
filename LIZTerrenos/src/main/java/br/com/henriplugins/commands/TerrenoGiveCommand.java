package br.com.henriplugins.commands;

import br.com.henriplugins.utils.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TerrenoGiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem executar este comando.");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("lizterreno.admin")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar esse comando.");
            return false;
        }

        ItemStack terrenoInicial = ItemManager.createTerrenoInicial();

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Seu inventário está cheio.");
            return false;
        }

        player.getInventory().addItem(terrenoInicial);

        player.sendMessage(ChatColor.GREEN + "Você recebeu um Baú Inicial.");

        return true;
    }
}
