package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.data.PlayerData;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionSetPowerSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionSetPowerSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (!main.getAdminManager().isIn((Player) sender)) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_ADMIN_MODE);
            return;
        }

        if (args.length != 3) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            StringUtils.sendMessage(sender, Messager.INVALID_TARGET);
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            StringUtils.sendMessage(sender, Messager.INVALID_NUMBER);
            return;
        }

        if (amount < 0) {
            StringUtils.sendMessage(sender, Messager.INVALID_NUMBER);
            return;
        }

        PlayerData.get(target.getUniqueId()).setPower(amount);
        StringUtils.sendMessage(sender, Messager.SET_POWER
                .replace("%player%", target.getName())
                .replace("%power%", StringUtils.format(amount)));
    }

    @Override
    public String getName() {
        return "setpower";
    }

    @Override
    public String getPermission() {
        return "empirefactions.admin";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_ADMIN;
    }
}
