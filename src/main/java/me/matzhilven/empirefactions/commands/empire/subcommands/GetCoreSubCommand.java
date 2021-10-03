package me.matzhilven.empirefactions.commands.empire.subcommands;

import de.tr7zw.nbtapi.NBTItem;
import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.core.CoreType;
import me.matzhilven.empirefactions.utils.ItemBuilder;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class GetCoreSubCommand implements SubCommand {

    private final EmpireFactions main;

    public GetCoreSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 3) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Optional<Empire> optionalEmpire = main.getEmpireManager().byName(args[1]);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.INVALID_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();
        CoreType coreType;

        try {
            coreType = CoreType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            StringUtils.sendMessage(sender, Messager.INVALID_CORE_TYPE);
            return;
        }

        ItemStack coreItem = new ItemBuilder(Material.ENDER_PORTAL_FRAME).setName(empire.getName()).setLore("", "&7Place this end crystal down",
                "&7 to add a core to this empire", "", "&7Type: " + coreType.getName()).toItemStack();

        NBTItem nbtItem = new NBTItem(coreItem);
        nbtItem.setString("empire", empire.getUniqueId().toString());
        nbtItem.setString("coreType", coreType.getName().toUpperCase());

        ((Player) sender).getInventory().addItem(nbtItem.getItem());

        StringUtils.sendMessage(sender, Messager.RECEIVED_CORE.replace("%empire%", empire.getName()).replace("%type%", coreType.getName()));

    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return StringUtil.copyPartialMatches(args[1],
                        main.getEmpireManager().getEmpires().stream().map(Empire::getName).collect(Collectors.toList()),
                        new ArrayList<>());
            case 3:
                return StringUtil.copyPartialMatches(args[2],
                        Arrays.stream(CoreType.values()).map(CoreType::getName).collect(Collectors.toList()),
                        new ArrayList<>());
        }


        return null;
    }

    @Override
    public String getName() {
        return "getcore";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_GET_CORE;
    }

    @Override
    public String getPermission() {
        return "empire.getcore";
    }
}
