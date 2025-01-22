package br.com.henriplugins.listeners;

import br.com.henriplugins.database.DatabaseManager;
import br.com.henriplugins.models.Terreno;
import br.com.henriplugins.commands.TerrenoAdminCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TerrenoListener implements Listener {
    private final DatabaseManager databaseManager;

    public TerrenoListener(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!canPlayerInteract(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não tem permissão para quebrar blocos aqui.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!canPlayerInteract(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não tem permissão para colocar blocos aqui.");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null &&
                !canPlayerInteract(event.getPlayer(), event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não tem permissão para interagir aqui.");
        }
    }

    private boolean canPlayerInteract(Player player, Location location) {
        if (TerrenoAdminCommand.hasBypass(player)) {
            return true;
        }

        Terreno terreno = databaseManager.getTerrenoByLocation(location);

        if (terreno == null) {
            return true;
        }

        if (terreno.getOwner().equals(player.getUniqueId().toString())) {
            return true;
        }

        return terreno.getTrustedPlayers().contains(player.getUniqueId().toString());
    }
}
