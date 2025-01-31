package br.com.henriplugins.listeners;

import br.com.henriplugins.database.DatabaseManager;
import br.com.henriplugins.models.Terreno;
import br.com.henriplugins.commands.TerrenoAdminCommand;
import br.com.henriplugins.utils.TerrenoManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static br.com.henriplugins.utils.TerrenoManager.createTerreno;

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
    @EventHandler
    public void onTerrenoInicialPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = event.getPlayer();

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.CHEST) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.WHITE + "" + ChatColor.BOLD + "Terreno Inicial")) {

            Location location = event.getClickedBlock().getLocation();
            int size = 10;
            createTerreno(player, location, size);

            player.sendMessage(ChatColor.GREEN + "Terreno criado com sucesso!");
        }
    }
}
