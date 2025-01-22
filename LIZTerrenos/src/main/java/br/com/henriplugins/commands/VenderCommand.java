package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTerrenos;
import br.com.henriplugins.models.Terreno;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VenderCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;

        String playerUUID = player.getUniqueId().toString();
        Terreno terreno = LIZTerrenos.getInstance().getDatabaseManager().getTerrenoByOwner(playerUUID);
        if (terreno == null) {
            player.sendMessage(ChatColor.RED + "Você não possui um terreno para vender.");
            return true;
        }

        double refund = terreno.getCost() * 0.5;

        removeBorders(terreno.getCorner1(), terreno.getSize());

        try {
            LIZTerrenos.getInstance().getDatabaseManager().deleteTerreno(terreno);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Erro ao remover o terreno. Contate um administrador.");
            e.printStackTrace();
            return true;
        }

        LIZTerrenos.getInstance().getEconomy().depositPlayer(player, refund);

        player.sendMessage(ChatColor.GREEN + "Você vendeu seu terreno por " + ChatColor.YELLOW + "$" + refund);
        return true;
    }
    private void removeBorders(Location baseLocation, int size) {
        int halfSize = size / 2;
        int minY = baseLocation.getBlockY();
        int maxY = minY + 2;

        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                if (Math.abs(x) == halfSize || Math.abs(z) == halfSize) {
                    for (int y = minY; y <= maxY; y++) {
                        Location fenceLocation = baseLocation.clone().add(x, y - baseLocation.getBlockY(), z);
                        Block block = fenceLocation.getBlock();

                        if (block.getType() == Material.OAK_FENCE) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}
