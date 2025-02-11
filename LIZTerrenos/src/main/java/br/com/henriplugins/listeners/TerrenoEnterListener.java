package br.com.henriplugins.listeners;

import br.com.henriplugins.database.DatabaseManager;
import br.com.henriplugins.models.Terreno;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class TerrenoEnterListener implements Listener {
    private final DatabaseManager databaseManager;
    private final HashMap<Player, String> ultimoTerreno = new HashMap<>();

    public TerrenoEnterListener(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to == null || from == null || (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ())) return;

        Terreno terrenoAtual = databaseManager.getTerrenoByLocation(to);
        String donoAtual = (terrenoAtual != null) ? terrenoAtual.getOwner() : null;
        String donoAnterior = ultimoTerreno.get(player);

        if (donoAnterior != null && (donoAtual == null || !donoAnterior.equals(donoAtual))) {
            player.sendTitle(ChatColor.RED + "Você saiu do terreno!", "", 10, 40, 10);
            ultimoTerreno.remove(player);
        }

        if (donoAtual != null && (donoAnterior == null || !donoAtual.equals(donoAnterior))) {
            UUID ownerUUID = UUID.fromString(donoAtual);
            String ownerName = Bukkit.getOfflinePlayer(ownerUUID).getName();
            if (ownerName == null) ownerName = "Desconhecido";

            player.sendTitle(ChatColor.GREEN + "Terreno de", ChatColor.YELLOW + ownerName, 10, 40, 10);
            ultimoTerreno.put(player, donoAtual);
        }
    }
}
