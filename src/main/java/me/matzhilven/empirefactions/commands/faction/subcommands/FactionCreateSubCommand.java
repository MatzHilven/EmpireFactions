package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Optional;

public class FactionCreateSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionCreateSubCommand(EmpireFactions main) {
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
            StringUtils.sendMessage(sender,Messager.NOT_IN_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();

        if (empire.getFaction(player).isPresent()) {
            StringUtils.sendMessage(sender,Messager.ALREADY_IN_FACTION);
            return;
        }

        empire.addSubFaction(new Faction(args[1], player.getUniqueId(), empire.getUniqueId()));

        StringUtils.sendMessage(player, Messager.CREATE_SUCCESS_FACTION.replace("%faction%", args[1]));
        empire.getStaff().stream().filter(player1 -> !player1.equals(player))
                .forEach(player1 -> StringUtils.sendMessage(player1, Messager.CREATE_SUCCESS_FACTION.replace("%faction%", args[1])));
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_CREATE_FACTION;
    }
}
