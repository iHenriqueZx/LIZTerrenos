package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTerrenos;
import br.com.henriplugins.models.Terreno;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class DesconfiarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Uso correto: /terreno desconfiar <nick>");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "O jogador " + targetName + " não está online ou não existe.");
            return true;
        }

        String targetUUID = target.getUniqueId().toString();

        Terreno terreno = LIZTerrenos.getInstance().getDatabaseManager().getTerrenoByOwner(player.getUniqueId().toString());

        if (terreno == null) {
            player.sendMessage(ChatColor.RED + "Você não possui um terreno para gerenciar.");
            return true;
        }

        if (!terreno.getTrustedPlayers().contains(targetUUID)) {
            player.sendMessage(ChatColor.RED + "O jogador " + targetName + " não está na sua lista de confiança.");
            return true;
        }

        terreno.getTrustedPlayers().remove(targetUUID);

        try {
            LIZTerrenos.getInstance().getDatabaseManager().updateTerreno(terreno);
            player.sendMessage(ChatColor.YELLOW + "O jogador " + targetName + " foi removido da sua lista de confiança.");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Erro ao atualizar o banco de dados. Tente novamente.");
            e.printStackTrace();
        }

        return true;
    }
}