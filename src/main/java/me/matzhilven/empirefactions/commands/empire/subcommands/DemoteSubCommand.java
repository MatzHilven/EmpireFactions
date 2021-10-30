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
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DemoteSubCommand implements SubCommand {

    private final EmpireFactions main;

    public DemoteSubCommand(EmpireFactions main) {
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

        if (!(empire.getRank(player) == EmpireRank.LEADER || empire.getRank(player) == EmpireRank.ADMIN)) {
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

        EmpireRank targetRank = empire.getRank(target);

        if (targetRank == EmpireRank.MEMBER) {
            StringUtils.sendMessage(sender, Messager.DEMOTE_MEMBER);
            return;
        }

        if (empire.demote(player, target)) {
            targetRank = empire.getRank(target);
            StringUtils.sendMessage(sender, Messager.DEMOTE_SUCCESS.replace("%target%", target.getName()).replace("%rank%", targetRank.getName()));
            StringUtils.sendMessage(target, Messager.DEMOTE_SUCCESS_TARGET.replace("%rank%", targetRank.getName()));
        }

    }

    @Override
    public String getName() {
        return "demote";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_DEMOTE;
    }
}
