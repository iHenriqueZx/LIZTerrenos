package br.com.henriplugins.utils;

import br.com.henriplugins.LIZTerrenos;
import br.com.henriplugins.models.Terreno;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TerrenoManager {

    public static void createTerreno(Player player, Location location, int size) {
        int halfSize = size / 2;
        Location corner1 = location.clone().add(-halfSize, 0, -halfSize);
        Location corner2 = location.clone().add(halfSize, 0, halfSize);

        int id = generateUniqueId();
        String owner = player.getUniqueId().toString();
        Terreno terreno = new Terreno(id, owner, size, corner1, corner2, new ArrayList<>(), 0);

        try {
            LIZTerrenos.getInstance().getDatabaseManager().saveTerreno(player, terreno);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Erro ao salvar o terreno. Contate um administrador.");
            e.printStackTrace();
        }

        markBorders(location, size);

        player.sendMessage(ChatColor.GREEN + "Você criou um terreno 10x10 com sucesso!");
    }

    private static void markBorders(Location baseLocation, int size) {
        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                if (Math.abs(x) == halfSize || Math.abs(z) == halfSize) {
                    Location fenceLocation = baseLocation.clone().add(x, 0, z);
                    Block block = fenceLocation.getBlock();

                    while (!block.getType().isSolid() && block.getY() > 0) {
                        block = block.getRelative(0, -1, 0);
                    }

                    block.getRelative(0, 1, 0).setType(Material.OAK_FENCE);
                }
            }
        }
    }
    private static int generateUniqueId() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }
}
