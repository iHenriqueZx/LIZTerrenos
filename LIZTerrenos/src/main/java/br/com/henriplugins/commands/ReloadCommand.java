package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTerrenos;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lizterreno.admin")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando.");
            return true;
        }

        try {
            LIZTerrenos.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Configurações recarregadas com sucesso!");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Ocorreu um erro ao recarregar as configurações. Verifique o console.");
            e.printStackTrace();
        }

        return true;
    }
}
