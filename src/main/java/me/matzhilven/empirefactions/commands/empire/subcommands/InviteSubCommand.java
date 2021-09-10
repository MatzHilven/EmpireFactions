package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.utils.Messager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InviteSubCommand implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {

    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_INVITE;
    }

    @Override
    public String getPermission() {
        return "empire.invite";
    }
}
