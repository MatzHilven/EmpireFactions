package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetColorSubCommand implements SubCommand {

    private final EmpireFactions main;

    public SetColorSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length < 3) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Optional<Empire> optionalEmpire = main.getEmpireManager().byName(args[1]);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.INVALID_EMPIRE);
            return;
        }

        ChatColor color = getColor(args[2]);

        if (color == null || isInvalidColor(color)) {
            StringUtils.sendMessage(sender, Messager.INVALID_COLOR);
            return;
        }

        Empire empire = optionalEmpire.get();
        empire.setColor(color);

        StringUtils.sendMessage(sender, Messager.SET_COLOR.replace("%color%", color + color.asBungee().getName().toUpperCase()));
    }

    private boolean isInvalidColor(ChatColor color) {
        return color == ChatColor.MAGIC
                || color == ChatColor.BOLD
                || color == ChatColor.STRIKETHROUGH
                || color == ChatColor.UNDERLINE
                || color == ChatColor.ITALIC
                || color == ChatColor.RESET;
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
                        Arrays.stream(ChatColor.values()).filter(color -> color != ChatColor.MAGIC
                                && color != ChatColor.BOLD
                                && color != ChatColor.STRIKETHROUGH
                                && color != ChatColor.UNDERLINE
                                && color != ChatColor.ITALIC
                                && color != ChatColor.RESET)
                                .map(chatColor -> chatColor.asBungee().getName().toUpperCase()).collect(Collectors.toList()),
                        new ArrayList<>());
        }

        return null;
    }

    @Override
    public String getName() {
        return "setcolor";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_SET_COLOR;
    }

    @Override
    public String getPermission() {
        return "empire.setcolor";
    }

    private ChatColor getColor(String name) {
        for (ChatColor color : ChatColor.values()) {
            if (color.asBungee().getName().equalsIgnoreCase(name)) {
                return color;
            }
        }

        return null;
    }
}
