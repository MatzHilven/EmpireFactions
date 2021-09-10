package me.matzhilven.empirefactions.utils;

import me.matzhilven.empirefactions.empire.Empire;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class StringUtils {

    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> colorize(List<String> s) {
        return s.stream().map(StringUtils::colorize).collect(Collectors.toList());
    }

    public static String removeColor(String s) {
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

    public static String getOnlineMembersFormatted(Empire empire) {
        List<UUID> members = empire.getOnline();

        StringBuilder result = new StringBuilder();
        for (UUID member : members) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(member);
            result.append(player.getName());
            result.append("&7, ");
        }
        return result.length() > 0 ? result.substring(0, result.length() - 2) : "N/A";
    }
}
