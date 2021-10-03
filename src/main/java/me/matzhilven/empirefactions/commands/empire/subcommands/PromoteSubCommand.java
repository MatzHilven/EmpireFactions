package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.rank.FactionRank;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PromoteSubCommand implements SubCommand {

    private final EmpireFactions main;

    public PromoteSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 3) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Player player = (Player) sender;
        if (!main.getEmpireManager().isInEmpire(player)) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_EMPIRE);
            return;
        }

        Empire empire = main.getEmpireManager().getEmpire(player).get();

        if (!(empire.getRank(player) == FactionRank.LEADER || empire.getRank(player) == FactionRank.ADMIN)) {
            StringUtils.sendMessage(sender, Messager.INVALID_PERMISSION);
            return;
        }

        if (Bukkit.getPlayer(args[2]) == null) {
            StringUtils.sendMessage(sender, Messager.INVALID_TARGET);
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);

        if (player == target) {
            StringUtils.sendMessage(sender, Messager.INVALID_TARGET_SELF);
            return;
        }

        if (empire.promote(player, Bukkit.getPlayer(args[2]))) {
            FactionRank targetRank = empire.getRank(target);
            StringUtils.sendMessage(sender, Messager.PROMOTE_SUCCESS.replace("%target%", target.getName()).replace("%rank%", targetRank.getName()));
            StringUtils.sendMessage(target, Messager.PROMOTE_SUCCESS_TARGET.replace("%rank%", targetRank.getName()));
        }

    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1],
                    main.getEmpireManager().getEmpires().stream().map(Empire::getName).collect(Collectors.toList()),
                    new ArrayList<>());
        }
        return null;
    }

    @Override
    public String getName() {
        return "promote";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_PROMOTE;
    }

    @Override
    public String getPermission() {
        return "empire.promote";
    }
}
