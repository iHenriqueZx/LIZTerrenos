package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTerrenos;
import br.com.henriplugins.models.Terreno;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;
        Location playerLocation = player.getLocation();
        Terreno terreno = LIZTerrenos.getInstance().getDatabaseManager().getTerrenoByLocation(playerLocation);

        if (terreno == null) {
            player.sendMessage(ChatColor.RED + "Você não está em um terreno registrado.");
            return true;
        }

        StringBuilder message = new StringBuilder();
        message.append(ChatColor.GREEN).append("Informações do Terreno:\n");
        message.append(ChatColor.YELLOW).append("Dono: ").append(terreno.getOwner()).append("\n");

        message.append(ChatColor.YELLOW).append("Pessoas de Confiança: ");
        if (terreno.getTrustedPlayers().isEmpty()) {
            message.append(ChatColor.RED).append("Nenhuma pessoa de confiança.");
        } else {
            for (String trustedPlayer : terreno.getTrustedPlayers()) {
                message.append(ChatColor.AQUA).append(trustedPlayer).append(" ");
            }
        }
        message.append("\n");

        message.append(ChatColor.YELLOW).append("Tamanho: ").append(terreno.getSize()).append(" blocos");

        player.sendMessage(message.toString());
        return true;
    }
}
