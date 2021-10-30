package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.data.PlayerData;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class FactionPlayerSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionPlayerSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 2) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            StringUtils.sendMessage(sender, Messager.INVALID_TARGET);
            return;
        }

        Player player = (Player) sender;

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();

        Optional<Faction> optionalFaction = empire.getFaction(target);

        if (!optionalFaction.isPresent()) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_FACTION);
            return;
        }

        Faction faction = optionalFaction.get();
        PlayerData playerData = PlayerData.get(target.getUniqueId());

        for (String s : Messager.PLAYER_INFO) {
            StringUtils.sendMessage(player, s
                    .replace("%player%", player.getName())
                    .replace("%display_name%", target.getDisplayName())
                    .replace("%faction%", faction.getNameColored())
                    .replace("%power%", StringUtils.format(playerData.getPower()))
            );
        }


    }

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_PLAYER_INFO;
    }
}
