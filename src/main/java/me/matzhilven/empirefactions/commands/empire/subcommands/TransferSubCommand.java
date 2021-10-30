package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.rank.EmpireRank;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TransferSubCommand implements SubCommand {

    private final EmpireFactions main;

    public TransferSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 2) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Player player = (Player) sender;
        if (!main.getEmpireManager().isInEmpire(player)) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_EMPIRE);
            return;
        }

        Empire empire = main.getEmpireManager().getEmpire(player).get();

        if (empire.getRank(player) != EmpireRank.LEADER) {
            StringUtils.sendMessage(sender, Messager.INVALID_PERMISSION);
            return;
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            StringUtils.sendMessage(sender, Messager.INVALID_TARGET);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (player == target) {
            StringUtils.sendMessage(sender, Messager.INVALID_TARGET_SELF);
            return;
        }

        empire.setLeader(target.getUniqueId());

        StringUtils.sendMessage(sender, Messager.TRANSFER_SUCCESS.replace("%target%", target.getName()));
        StringUtils.sendMessage(target, Messager.TRANSFER_SUCCESS_TARGET);
    }

    @Override
    public String getName() {
        return "transfer";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_TRANSFER;
    }
}
