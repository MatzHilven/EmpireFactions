package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FactionInfoSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionInfoSubCommand(EmpireFactions main) {
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

        Optional<Faction> optionalFaction = empire.getFaction(player);

        if (!optionalFaction.isPresent()) {
            StringUtils.sendMessage(sender,Messager.NOT_IN_FACTION);
            return;
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
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) return null;

        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1],
                    optionalEmpire.get().getSubFactions().stream().map(Faction::getName).collect(Collectors.toList()),
                    new ArrayList<>());
        }
        return null;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_INFO_FACTION;
    }

    @Override
    public String getPermission() {
        return "faction.info";
    }
}
