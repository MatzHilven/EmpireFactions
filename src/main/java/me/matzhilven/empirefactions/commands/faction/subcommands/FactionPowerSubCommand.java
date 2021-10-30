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
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class FactionPowerSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionPowerSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length < 1) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Player player = (Player) sender;

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();

        Faction faction;

        Optional<Faction> optionalFaction;

        if (args.length == 1) {
            optionalFaction = empire.getFaction(player);

            if (!optionalFaction.isPresent()) {
                StringUtils.sendMessage(sender, Messager.NOT_IN_FACTION);
                return;
            }
            faction = optionalFaction.get();
            StringUtils.sendMessage(player, Messager.POWER_SELF.replace("%power%", StringUtils.format(faction.getPower())));
        } else {
            optionalFaction = empire.getFaction(args[1]);

            if (!optionalFaction.isPresent()) {
                StringUtils.sendMessage(sender, Messager.INVALID_FACTION);
                return;
            }
            faction = optionalFaction.get();
            StringUtils.sendMessage(player, Messager.POWER_FACTION.replace("%power%", StringUtils.format(faction.getPower())).replace("%faction%", faction.getName()));
        }


    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire((Player) sender);

        if (!optionalEmpire.isPresent()) return null;

        Empire empire = optionalEmpire.get();

        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1],
                    empire.getSubFactions().stream().map(Faction::getName).collect(Collectors.toList()),
                    new ArrayList<>());
        }
        return null;
    }

    @Override
    public String getName() {
        return "power";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_POWER_FACTION;
    }
}
