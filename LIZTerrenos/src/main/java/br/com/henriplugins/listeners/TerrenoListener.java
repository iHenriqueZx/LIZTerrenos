package br.com.henriplugins.listeners;

import br.com.henriplugins.LIZTerrenos;
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
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

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
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!canPlayerInteract(event.getPlayer(), event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não tem permissão para usar baldes aqui.");
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
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block ->
                LIZTerrenos.getInstance().getDatabaseManager().getTerrenoByLocation(block.getLocation()) != null);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block ->
                LIZTerrenos.getInstance().getDatabaseManager().getTerrenoByLocation(block.getLocation()) != null);
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
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();
        List<String> mundosBloqueados = LIZTerrenos.getInstance().getConfig().getStringList("mundos_bloqueados");

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!(event.getPlayer() instanceof Player)) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.CHEST) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.WHITE + "" + ChatColor.BOLD + "Terreno Inicial")) {
            if (mundosBloqueados.contains(worldName)) {
                player.sendMessage(ChatColor.RED + "Não é possível colocar terrenos neste mundo.");
                event.setCancelled(true);
                return;
            }
            Location location = event.getClickedBlock().getLocation();
            int size = 10;
            int halfSize = size / 2;
            for (int x = -halfSize; x <= halfSize; x++) {
                for (int z = -halfSize; z <= halfSize; z++) {
                    Location checkLocation = location.clone().add(x, 0, z);
                    if (databaseManager.getTerrenoByLocation(checkLocation) != null) {
                        player.sendMessage(ChatColor.RED + "Este local já está protegido por outro terreno.");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            createTerreno(player, location, size);

            player.sendMessage(ChatColor.GREEN + "Terreno criado com sucesso!");
        }
    }
}
