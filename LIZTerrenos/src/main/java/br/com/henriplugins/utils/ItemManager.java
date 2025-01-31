package br.com.henriplugins.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemManager {
    public static ItemStack createTerrenoInicial() {
        ItemStack terrenoInicial = new ItemStack(Material.CHEST);
        ItemMeta meta = terrenoInicial.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Terreno Inicial");

            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Use para criar um terreno 10x10",
                    ChatColor.GRAY + "",
                    ChatColor.YELLOW + "ATENÇÃO: Sumirá ao ser colocado!",
                    ChatColor.GRAY + "",
                    ChatColor.WHITE + "" + ChatColor.BOLD + "ITEM COMUM"
            ));
            terrenoInicial.setItemMeta(meta);
        }

        return terrenoInicial;
    }
}