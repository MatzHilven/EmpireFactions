package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InfoSubCommand implements SubCommand {

    private final EmpireFactions main;

    public InfoSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 2) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Optional<Empire> optionalEmpire = main.getEmpireManager().byName(args[1]);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.INVALID_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();

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
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_INFO;
    }

    @Override
    public String getPermission() {
        return "empire.info";
    }
}