package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetCenterSubCommand implements SubCommand {

    private final EmpireFactions main;

    public SetCenterSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 3) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Optional<Empire> optionalEmpire = main.getEmpireManager().byName(args[1]);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.INVALID_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();

        if (args[2].equalsIgnoreCase("BASE")) {
            empire.setBaseCenter(((Player) sender).getLocation());
        } else if (args[2].equalsIgnoreCase("JURISDICTION")) {
            empire.setJurisdictionCenter(((Player) sender).getLocation());
        } else {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        StringUtils.sendMessage(sender, Messager.SET_CENTER.replace("%empire%", empire.getNameColored()).replace("%type%", args[2].toUpperCase()));
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return StringUtil.copyPartialMatches(args[1],
                        main.getEmpireManager().getEmpires().stream().map(Empire::getName).collect(Collectors.toList()),
                        new ArrayList<>());
            case 3:
                return StringUtil.copyPartialMatches(args[2],
                        Arrays.asList("BASE", "JURISDICTION"),
                        new ArrayList<>());
        }

        return null;
    }

    @Override
    public String getName() {
        return "setcenter";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_SET_CENTER;
    }

    @Override
    public String getPermission() {
        return "empire.setcenter";
    }
}
