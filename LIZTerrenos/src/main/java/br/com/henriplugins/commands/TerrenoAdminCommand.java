package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTerrenos;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class TerrenoAdminCommand implements CommandExecutor {

    private static final Set<Player> bypassPlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("lizterreno.admin")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando.");
            return true;
        }

        if (bypassPlayers.contains(player)) {
            bypassPlayers.remove(player);
            player.sendMessage(ChatColor.YELLOW + "Modo Admin de terrenos desativado.");
        } else {
            bypassPlayers.add(player);
            player.sendMessage(ChatColor.GREEN + "Modo Admin de terrenos ativado.");
        }

        return true;
    }

    public static boolean hasBypass(Player player) {
        return bypassPlayers.contains(player);
    }
}
