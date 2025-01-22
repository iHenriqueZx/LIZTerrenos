package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTerrenos;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TerrenoCommand implements CommandExecutor {

    private final ComprarCommand comprarCommand;
    private final ConfiarCommand confiarCommand;
    private final DesconfiarCommand desconfiarCommand;
    private final InfoCommand infoCommand;
    private final ReloadCommand reloadCommand;
    private final TerrenoAdminCommand terrenoAdminCommand;
    private final VenderCommand venderCommand;

    public TerrenoCommand() {
        this.comprarCommand = new ComprarCommand();
        this.confiarCommand = new ConfiarCommand();
        this.desconfiarCommand = new DesconfiarCommand();
        this.infoCommand = new InfoCommand();
        this.reloadCommand = new ReloadCommand();
        this.terrenoAdminCommand = new TerrenoAdminCommand();
        this.venderCommand = new VenderCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Uso incorreto. Use /terreno <vender | info>");
            return false;
        }

        String subcomando = args[0].toLowerCase();

        switch (subcomando) {
            case "info":
                return infoCommand.onCommand(sender, command, label, args);
            case "reload":
                return reloadCommand.onCommand(sender, command, label, args);
            case "admin":
                return terrenoAdminCommand.onCommand(sender, command, label, args);
            case "vender":
                return venderCommand.onCommand(sender, command, label, args);
            default:
                sender.sendMessage(ChatColor.RED + "Comando desconhecido. Use /terreno <vender | info>");
                return false;
        }
    }
}

