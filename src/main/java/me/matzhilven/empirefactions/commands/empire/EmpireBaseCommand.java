package me.matzhilven.empirefactions.commands.empire;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpireBaseCommand implements CommandExecutor, TabExecutor {

    private final EmpireFactions main;

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public EmpireBaseCommand(EmpireFactions main) {
        this.main = main;

        main.getCommand("empire").setExecutor(this);
        main.getCommand("empire").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("empire.empire")) {
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
                Empire empire = main.getEmpireManager().getEmpire(player).get();

                List<String> msg = new ArrayList<>();

                for (String s : Messager.EMPIRE_INFO) {
                    msg.add(s
                            .replace("%empire%", empire.getName())
                            .replace("%description%", empire.getDescription())
                            .replace("%members_count%", String.valueOf(empire.getAll().size()))
                            .replace("%power%", StringUtils.format(empire.getPower()))
                            .replace("%land%", "TODO")
                            .replace("%emperor%", Bukkit.getOfflinePlayer(empire.getLeader()).getName())
                            .replace("%admins%", StringUtils.getFormattedList(empire.getAdmins()))
                            .replace("%moderators%", StringUtils.getFormattedList(empire.getModerators()))
                            .replace("%members%", StringUtils.getFormattedList(empire.getMembers())));
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
        }

        subCommands.get(args[0]).onCommand(sender, command, args);

        return true;
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> cmds = new ArrayList<>();
        switch (args.length) {
            case 1:

                return StringUtil.copyPartialMatches(args[0], subCommands.keySet(), new ArrayList<>());
        }
        return null;
    }
}
