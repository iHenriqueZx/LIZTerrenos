package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTerrenos;
import br.com.henriplugins.models.Terreno;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ComprarCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Uso correto: /comprar <pequeno|medio|grande>");
            return true;
        }

        String sizeArg = args[0].toLowerCase();
        int size;
        double cost;

        switch (sizeArg) {
            case "pequeno":
                size = LIZTerrenos.getInstance().getConfig().getInt("terrenos.pequeno.tamanho");
                cost = LIZTerrenos.getInstance().getConfig().getDouble("terrenos.pequeno.custo");
                break;
            case "medio":
                size = LIZTerrenos.getInstance().getConfig().getInt("terrenos.medio.tamanho");
                cost = LIZTerrenos.getInstance().getConfig().getDouble("terrenos.medio.custo");
                break;
            case "grande":
                size = LIZTerrenos.getInstance().getConfig().getInt("terrenos.grande.tamanho");
                cost = LIZTerrenos.getInstance().getConfig().getDouble("terrenos.grande.custo");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Tamanho inválido. Use: pequeno, medio ou grande.");
                return true;
        }

        // Verificar se o Vault está habilitado
        if (!LIZTerrenos.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) {
            player.sendMessage(ChatColor.RED + "Vault não está habilitado no servidor.");
            return true;
        }

        // Verificar saldo do jogador
        double balance = LIZTerrenos.getInstance().getEconomy().getBalance(player);
        if (balance < cost) {
            player.sendMessage(ChatColor.RED + "Você não tem dinheiro suficiente para comprar este terreno.");
            return true;
        }

        // Retirar o dinheiro do jogador
        LIZTerrenos.getInstance().getEconomy().withdrawPlayer(player, cost);

        // Definir localização inicial do terreno (posição atual do jogador)
        Location baseLocation = player.getLocation();

        // Criar e salvar terreno
        Terreno terreno = new Terreno(player.getUniqueId(), size, baseLocation.toString());
        try {
            LIZTerrenos.getInstance().getDatabaseManager().saveTerreno(terreno);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Erro ao salvar o terreno. Contate um administrador.");
            e.printStackTrace();
            return true;
        }

        // Marcar as bordas do terreno com cercas de carvalho
        markBorders(baseLocation, size);

        player.sendMessage(ChatColor.GREEN + "Terreno comprado com sucesso! Tamanho: " + size + " blocos.");
        player.sendMessage(ChatColor.GOLD + "Custo: " + cost + " foi deduzido do seu saldo.");
        return true;
    }

    private void markBorders(Location baseLocation, int size) {
        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                if (Math.abs(x) == halfSize || Math.abs(z) == halfSize) {
                    Location fenceLocation = baseLocation.clone().add(x, 0, z);
                    Block block = fenceLocation.getBlock();

                    // Subir até encontrar um bloco sólido para colocar a cerca
                    while (!block.getType().isSolid() && block.getY() > 0) {
                        block = block.getRelative(0, -1, 0);
                    }

                    // Colocar cerca de carvalho
                    block.getRelative(0, 1, 0).setType(Material.OAK_FENCE);
                }
            }
        }
    }
}
