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

public class FactionSetDescriptionSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionSetDescriptionSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        Player player = (Player) sender;

        if (main.getAdminManager().isIn(player)) {
            if (args.length != 3) {
                StringUtils.sendMessage(sender, Messager.HELP_ADMIN);
                return;
            }

            Faction faction = null;

            for (Empire loopEmpire : main.getEmpireManager().getEmpires()) {
                Optional<Faction> optionalFaction = loopEmpire.getFaction(args[1]);
                if (optionalFaction.isPresent()) {
                    faction = optionalFaction.get();
                    break;
                }
            }

            if (faction == null) {
                StringUtils.sendMessage(sender, Messager.INVALID_FACTION);
                return;
            }

            String description = String.join(" ", args).replace(args[0], "").replace(args[1], "").replaceFirst("  ", "");

            faction.setDescription(description);

            StringUtils.sendMessage(sender, Messager.SET_DESCRIPTION.replace("%description%", description));
            return;
        }

        if (args.length < 2) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender,Messager.NOT_IN_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();

        Optional<Faction> optionalFaction = empire.getFaction(player);

        if (!optionalFaction.isPresent()) {
            StringUtils.sendMessage(sender,Messager.NOT_IN_FACTION);
            return;
        }

        Faction faction = optionalFaction.get();

        if (!faction.isLeader(player)) {
            StringUtils.sendMessage(sender,Messager.INVALID_PERMISSION);
            return;
        }

        String description = String.join(" ", args).replace(args[0], "").replaceFirst("  ", "");

        faction.setDescription(description);

        StringUtils.sendMessage(sender, Messager.SET_DESCRIPTION.replace("%description%", description));
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1],
                    main.getEmpireManager().getEmpires().stream().map(Empire::getName).collect(Collectors.toList()),
                    new ArrayList<>());
        }
        return null;
    }

    @Override
    public String getName() {
        return "setdescription";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_SET_DESCRIPTION_FACTION;
    }
}
