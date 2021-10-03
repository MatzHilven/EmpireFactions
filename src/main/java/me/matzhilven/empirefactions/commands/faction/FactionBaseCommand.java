package me.matzhilven.empirefactions.commands.faction;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class FactionBaseCommand implements CommandExecutor, TabExecutor {

    private final EmpireFactions main;

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public FactionBaseCommand(EmpireFactions main) {
        this.main = main;

        main.getCommand("faction").setExecutor(this);
        main.getCommand("faction").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("empire.faction")) {
            StringUtils.sendMessage(sender, Messager.INVALID_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                StringUtils.sendMessage(sender, Messager.INVALID_SENDER);
                return true;
            }
            Player player = (Player) sender;
            if (main.getEmpireManager().isInEmpire(player)) {
                Optional<Faction> optionalFaction = main.getEmpireManager().getEmpire(player).get().getFaction(player);
                if (!optionalFaction.isPresent()) {
                    StringUtils.sendMessage(player, Messager.NOT_IN_FACTION);
                    return true;
                }

                Faction faction = optionalFaction.get();

                List<String> msg = new ArrayList<>();

                for (String s : Messager.FACTION_INFO) {
                    msg.add(s
                            .replace("%faction%", faction.getNameColored())
                            .replace("%description%", faction.getDescription())
                            .replace("%members_count%", String.valueOf(faction.getMembers().size() + 1))
                            .replace("%leader%", Bukkit.getOfflinePlayer(faction.getLeader()).getName() == null ? "N/A" :  Bukkit.getOfflinePlayer(faction.getLeader()).getName())
                            .replace("%members%", StringUtils.getFormattedList(faction.getMembers())));
                }

                StringUtils.sendMessage(sender, msg);
            } else {
                StringUtils.sendMessage(sender, Messager.NOT_IN_EMPIRE);
            }

            return true;
        }

        String subCommandString = args[0];

        if (!subCommands.containsKey(subCommandString)) {
            StringUtils.sendMessage(sender, Messager.USAGE_EMPIRE_COMMAND);
            return true;
        }

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission(subCommands.get(args[0]).getPermission())) {
                StringUtils.sendMessage(sender, Messager.INVALID_PERMISSION);
                return true;
            }
            subCommands.get(args[0]).onCommand(sender, command, args);
        }



        return true;
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], subCommands.keySet(), new ArrayList<>());
        } else {
            return subCommands.get(args[0]).onTabComplete(sender, args);
        }
    }
}
