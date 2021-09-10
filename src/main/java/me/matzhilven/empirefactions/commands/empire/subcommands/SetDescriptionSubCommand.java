package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class SetDescriptionSubCommand implements SubCommand {

    private final EmpireFactions main;

    public SetDescriptionSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length < 2) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Optional<Empire> optionalEmpire = main.getEmpireManager().byName(args[1]);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.INVALID_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();
        empire.setDescription(String.join(" ", args)
                .replace(args[0], "")
                .replace(args[1], "")
                .replaceFirst("  ", ""));
    }

    @Override
    public String getName() {
        return "setdescription";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_SET_DESCRIPTION;
    }

    @Override
    public String getPermission() {
        return "empire.setdescription";
    }
}
