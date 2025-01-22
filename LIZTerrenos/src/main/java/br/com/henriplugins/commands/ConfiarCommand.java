package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTerrenos;
import br.com.henriplugins.models.Terreno;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfiarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Uso correto: /confiar <jogador>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Jogador não encontrado.");
            return true;
        }
        if (player.equals(target)) {
            player.sendMessage(ChatColor.RED + "Você não pode confiar em si mesmo.");
            return true;
        }
        String targetUUID = target.getUniqueId().toString();
        Terreno terreno = LIZTerrenos.getInstance().getDatabaseManager().getTerrenoByOwner(player.getUniqueId().toString());

        if (terreno == null) {
            player.sendMessage(ChatColor.RED + "Você não possui um terreno.");
            return true;
        }

        if (terreno.getTrustedPlayers().contains(targetUUID)) {
            player.sendMessage(ChatColor.RED + "Esse jogador já é confiável no seu terreno.");
            return true;
        }

        terreno.getTrustedPlayers().add(targetUUID);

        try {
            LIZTerrenos.getInstance().getDatabaseManager().updateTerreno(terreno);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Erro ao salvar confiável. Contate um administrador.");
            e.printStackTrace();
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Jogador " + target.getName() + " agora é confiável no seu terreno.");
        return true;
    }
}