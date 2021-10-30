package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.rank.EmpireRank;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class RenameSubCommand implements SubCommand {

    private final EmpireFactions main;

    public RenameSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        Player player = (Player) sender;

        if (main.getAdminManager().isIn(player)) {
            if (args.length != 3) {
                StringUtils.sendMessage(sender, getUsage());
                return;
            }

            Empire empire = null;

            for (Empire loopEmpire : main.getEmpireManager().getEmpires()) {
                if (loopEmpire.getName().equals(args[1])) {
                    empire = loopEmpire;
                }
            }

            if (empire == null) {
                StringUtils.sendMessage(sender, Messager.INVALID_EMPIRE);
                return;
            }

            empire.setName(args[2]);

            StringUtils.sendMessage(sender, Messager.RENAMED.replace("%name%", args[2]));
            return;
        }

        if (args.length != 2) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();

        if (!(empire.getRank(player) == EmpireRank.LEADER || empire.getRank(player) == EmpireRank.ADMIN)) {
            StringUtils.sendMessage(sender, Messager.INVALID_PERMISSION);
            return;
        }

        empire.setName(args[1]);

        StringUtils.sendMessage(sender, Messager.RENAMED.replace("%name%", args[1]));

    }

    @Override
    public String getName() {
        return "rename";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_RENAME;
    }
}
