package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.rank.EmpireRank;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        if (args.length != 2) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Player player = (Player) sender;

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_EMPIRE);
            return;
        }

        ChatColor color = getColor(args[1]);

        if (color == null || isInvalidColor(color)) {
            StringUtils.sendMessage(sender, Messager.INVALID_COLOR);
            return;
        }

        Empire empire = optionalEmpire.get();

        if (!(empire.getRank(player) == EmpireRank.LEADER || empire.getRank(player) == EmpireRank.ADMIN)) {
            StringUtils.sendMessage(sender, Messager.INVALID_PERMISSION);
            return;
        }

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
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1],
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

    private ChatColor getColor(String name) {
        for (ChatColor color : ChatColor.values()) {
            if (color.asBungee().getName().equalsIgnoreCase(name)) {
                return color;
            }
        }

        return null;
    }
}
