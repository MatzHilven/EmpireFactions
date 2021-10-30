package me.matzhilven.empirefactions.utils;

import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.core.Core;
import me.matzhilven.empirefactions.empire.faction.Faction;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class StringUtils {

    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> colorize(List<String> s) {
        return s.stream().map(StringUtils::colorize).collect(Collectors.toList());
    }

    public static String removeColorCodes(String s) {
        return ChatColor.stripColor(colorize(s));
    }

    public static String decolorize(String s) {
        return ChatColor.stripColor(s);
    }

    public static List<String> decolorize(List<String> s) {
        return s.stream().map(ChatColor::stripColor).collect(Collectors.toList());
    }

    public static void sendMessage(CommandSender sender, String m) {
        sender.sendMessage(colorize(m));
    }

    public static void sendMessage(CommandSender sender, List<String> m) {
        m.forEach(msg -> sendMessage(sender,msg));
    }

    public static void sendClickableMessage(Player player, String message, String command) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(colorize(message)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        player.spigot().sendMessage(component);
    }

    public static String format(int c) {
        return NumberFormat.getNumberInstance(Locale.US).format(c);
    }

    public static String format(long c) {
        return NumberFormat.getNumberInstance(Locale.US).format(c);
    }

    public static String getFormattedList(List<UUID> members) {
        StringBuilder result = new StringBuilder();
        for (UUID member : members) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(member);
            result.append(player.isOnline() ? "&a" + player.getName() : "&c" + player.getName());
            result.append("&7, ");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 2) : "N/A";
    }

    public static String getFormattedAllies(Empire empire, List<UUID> allies) {
        StringBuilder result = new StringBuilder();
        for (UUID ally : allies) {
            Optional<Faction> faction = empire.byFactionUUID(ally);
            if (!faction.isPresent()) continue;
            result.append(faction.get().getName());
            result.append("&7, ");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 2) : "N/A";
    }

    public static String getFormattedCores(Empire empire) {
        List<Core> cores = empire.getCores();

        StringBuilder result = new StringBuilder();
        for (Core core : cores) {
            result.append(core.isAlive() ? "&a" + core.getCoreType().getName() : "&c" + core.getCoreType().getName());
            result.append("&7, ");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 2) : "N/A";
    }
}
