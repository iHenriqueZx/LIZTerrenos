package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTerrenos;
import br.com.henriplugins.models.Terreno;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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

        String ownerName = ChatColor.RED + "Desconhecido";
        String ownerId = terreno.getOwner();

        try {
            UUID ownerUUID = UUID.fromString(ownerId);
            OfflinePlayer ownerPlayer = Bukkit.getOfflinePlayer(ownerUUID);
            if (ownerPlayer.getName() != null) {
                ownerName = ChatColor.AQUA + ownerPlayer.getName();
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Erro: O dono do terreno tem um UUID inválido.");
            return true;
        }

        StringBuilder message = new StringBuilder();
        message.append(ChatColor.GREEN).append("Informações do Terreno:\n");
        message.append(ChatColor.YELLOW).append("Dono: ").append(ownerName).append("\n");

        message.append(ChatColor.YELLOW).append("Pessoas de Confiança: ");
        if (terreno.getTrustedPlayers().isEmpty()) {
            message.append(ChatColor.RED).append("Nenhuma pessoa de confiança.");
        } else {
            for (String trustedId : terreno.getTrustedPlayers()) {
                try {
                    UUID trustedUUID = UUID.fromString(trustedId);
                    OfflinePlayer trustedPlayer = Bukkit.getOfflinePlayer(trustedUUID);
                    String trustedName = (trustedPlayer.getName() != null) ? trustedPlayer.getName() : " ";
                    message.append(ChatColor.AQUA).append(trustedName).append(" ");
                } catch (IllegalArgumentException e) {
                    message.append(ChatColor.RED).append(" ");
                }
            }
        }
        message.append("\n");

        message.append(ChatColor.YELLOW).append("Tamanho: ").append(ChatColor.AQUA).append(terreno.getSize()).append(" blocos");

        player.sendMessage(message.toString());
        return true;
    }
}
