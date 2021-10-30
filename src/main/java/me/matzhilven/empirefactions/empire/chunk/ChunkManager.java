package me.matzhilven.empirefactions.empire.chunk;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import org.bukkit.Chunk;

import java.util.Optional;

public class ChunkManager {

    private final EmpireFactions main;

    public ChunkManager(EmpireFactions main) {
        this.main = main;
    }

    public Optional<Faction> getChunkOwner(Chunk chunk) {
        for (Empire empire : main.getEmpireManager().getEmpires()) {
            for (Faction faction : empire.getSubFactions()) {
                if (faction.isClaimed(chunk)) return Optional.of(faction);
            }
        }

        return Optional.empty();
    }

    public boolean isClaimed(Chunk chunk) {
        for (Empire empire : main.getEmpireManager().getEmpires()) {
            for (Faction faction : empire.getSubFactions()) {
                if (faction.isClaimed(chunk)) return true;
            }
        }

        return false;
    }
}
