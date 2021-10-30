package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.utils.FontMetrics;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionMapSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionMapSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 1) {
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

        int map_width = main.getConfig().getInt("f-map.width");
        int map_height = main.getConfig().getInt("f-map.height");

        final Chunk center = player.getLocation().getChunk();

        final int topLeftX = center.getX() - (map_width / 2);
        final int topLeftZ = center.getZ() - (map_height / 2);
        final int bottomRightX = center.getX() + (map_width / 2);
        final int bottomRightZ = center.getZ() + (map_height / 2);

        final ChatColor centerColor = ChatColor.valueOf(main.getConfig().getString("f-map.color.center"));
        final ChatColor ownedColor = ChatColor.valueOf(main.getConfig().getString("f-map.color.owned"));
        final ChatColor enemyColor = ChatColor.valueOf(main.getConfig().getString("f-map.color.enemy"));
        final ChatColor neutralColor = ChatColor.valueOf(main.getConfig().getString("f-map.color.neutral"));
        final ChatColor defaultColor = ChatColor.valueOf(main.getConfig().getString("f-map.color.default"));

        final boolean hasFaction = optionalFaction.isPresent();

        final HashMap<UUID, Integer> printedHolders = new HashMap<>();
        final HashMap<UUID, String> colorMap = new HashMap<>();

        player.sendMessage(FontMetrics.obtainCenteredMessage("&fNorth"));

        String key = main.getConfig().getString("f-map.key");

        for (int z = topLeftZ; z <= bottomRightZ; z++) {
            final StringBuilder line = new StringBuilder();
            for (int x = topLeftX; x <= bottomRightX; x++) {

                Chunk chunk = center.getWorld().getChunkAt(x, z);

                Optional<Faction> claimedFaction = main.getEmpireManager().isClaimed(chunk);

                if (claimedFaction.isPresent()) {
                    Faction faction = claimedFaction.get();

                    printedHolders.put(faction.getUniqueId(), printedHolders.getOrDefault(faction.getUniqueId(), 0) + 1);

                    if (hasFaction) {
                        Faction ownedFaction = optionalFaction.get();

                        ChatColor color;
                        if (chunk.equals(center)) {
                            color = centerColor;
                            printedHolders.put(faction.getUniqueId(), printedHolders.get(faction.getUniqueId()) - 1);
                        } else if (faction.getUniqueId().toString().equals(ownedFaction.getUniqueId().toString())) {
                            color = ownedColor;
                        } else {
                            color = enemyColor;
                            colorMap.put(faction.getUniqueId(), String.valueOf(defaultColor.getChar()));
                        }
                        line.append(color);
                    } else {
                        line.append(ChatColor.RED);
                    }
                    line.append(key);
                } else {
                    if (chunk.equals(center)) {
                        line.append(centerColor).append("+");
                    } else {
                        line.append(neutralColor).append("-");
                    }
                }
            }
            StringUtils.sendMessage(player, line.toString());

        }
        StringUtils.sendMessage(player, centerColor + "+&7 = You");

        final List<String> added = new ArrayList<>();
        int index = 0;

        for (UUID printedHolder : printedHolders.keySet()) {
            if (!(printedHolders.get(printedHolder) <= 0)) {
                String line;
                String factionName = main.getEmpireManager().getFactionByUUID(printedHolder).get().getNameColored();
                try {
                    if (hasFaction && printedHolder.toString().equals(optionalFaction.get().getUniqueId().toString())) {
                        line = ownedColor + "+&7 = " + factionName;
                    } else {
                        if (hasFaction) {
                            line =  enemyColor + key + "&7 = " + factionName;
                        } else {
                            line = enemyColor.getChar() + key + "&7 = " + factionName;
                        }
                    }
                } catch (IndexOutOfBoundsException ex) {
                    line = "&7§ = " + factionName;
                }
                added.add(line);
            }
            index++;
        }

        if (!added.isEmpty()) {
            StringUtils.sendMessage(player, " " + String.join(", ", added));
        }
    }

    @Override
    public String getName() {
        return "map";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_MAP;
    }
}
