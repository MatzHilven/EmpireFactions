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

import java.util.Optional;

public class FactionDepositSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionDepositSubCommand(EmpireFactions main) {
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

        Empire empire = optionalEmpire.get();

        Optional<Faction> optionalFaction = empire.getFaction(player);

        if (!optionalFaction.isPresent()) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_FACTION);
            return;
        }

        Faction faction = optionalFaction.get();

        if (!faction.isLeader(player)) {
            StringUtils.sendMessage(sender, Messager.INVALID_PERMISSION);
            return;
        }

        long amount;

        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            StringUtils.sendMessage(sender, Messager.INVALID_NUMBER);
            return;
        }

        if (main.getEcon().getBalance(player) < amount) {
            StringUtils.sendMessage(sender, Messager.INSUFFICIENT_FUNDS);
            return;
        }

        main.getEcon().withdrawPlayer(player, amount);

        faction.addMoney(amount);

        StringUtils.sendMessage(sender, Messager.DEPOSITED.replace("%amount%", StringUtils.format(amount)));
    }

    @Override
    public String getName() {
        return "deposit";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_DEPOSIT;
    }


}
