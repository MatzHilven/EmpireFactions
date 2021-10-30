package me.matzhilven.empirefactions.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.data.PlayerData;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.OfflinePlayer;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PlaceHolderAPIHook extends PlaceholderExpansion {

    private final EmpireFactions main;

    public PlaceHolderAPIHook(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "empires";
    }

    @Override
    public String getAuthor() {
        return main.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return main.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpireByPlayerUUID(player.getUniqueId());
        if (!optionalEmpire.isPresent()) return null;
        Empire empire = optionalEmpire.get();

        Optional<Faction> optionalFaction = empire.byPlayerUUID(player.getUniqueId());

        switch (identifier) {
            case "%empires_empire_description%":
                return empire.getDescription();

            case "%empires_empire_power%":
                return StringUtils.format(empire.getPower());

            case "%empires_empire_kills%":
                return StringUtils.format(empire.getKills());

            case "%empires_empire_deaths%":
                return StringUtils.format(empire.getDeaths());

            case "%empires_empire_name%":
                return empire.getName();

            case "%empires_empire_color%":
                return String.valueOf(empire.getColor().getChar());


            case "%empires_faction_name%":

                return optionalFaction.map(Faction::getName).orElse(null);

            case "%empires_faction_tag%":
                return optionalFaction.map(Faction::getTag).orElse(null);

            case "%empires_faction_group%":
                return optionalFaction.map(value -> value.getRank(player.getUniqueId())).orElse(null);

            case "%empires_faction_maxpower%":
                // TODO add max power
                return optionalFaction.map(faction -> StringUtils.format(100000)).orElse(null);

            case "%empires_faction_description%":
                return optionalFaction.map(Faction::getDescription).orElse(null);

            case "%empires_faction_power%":
                return optionalFaction.map(faction -> StringUtils.format(faction.getPower())).orElse(null);

            case "%empires_faction_claims%":
                return optionalFaction.map(faction -> StringUtils.format(faction.getAmountClaimed())).orElse(null);

            case "%empires_faction_founded%":
                return optionalFaction.map(faction -> DateTimeFormatter.ofPattern("dd-M-yyyy hh:mm:ss").format(faction.getFounded().toLocalDateTime())).orElse(null);

            case "%empires_faction_kills%":
                return optionalFaction.map(faction -> StringUtils.format(faction.getKills())).orElse(null);

            case "%empires_faction_deaths%":
                return optionalFaction.map(faction -> StringUtils.format(faction.getDeaths())).orElse(null);

            case "%empires_faction_balance%":
                return optionalFaction.map(faction -> StringUtils.format(faction.getBalance())).orElse(null);


            case "%empires_player_power%":
                return StringUtils.format(PlayerData.get(player.getUniqueId()).getPower());
            case "%empires_player_maxpower%":
        }
        return null;
    }
}
