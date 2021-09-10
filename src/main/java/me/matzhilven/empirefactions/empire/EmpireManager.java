package me.matzhilven.empirefactions.empire;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EmpireManager {

    private final EmpireFactions main;
    private final Set<Empire> empires;

    public EmpireManager(EmpireFactions main) {
        this.main = main;
        this.empires = new HashSet<>();

        loadEmpires();
    }

    private void loadEmpires() {

    }

    public Optional<Empire> getEmpire(Player player) {
        return empires.stream().filter(empire -> empire.isIn(player)).findFirst();
    }

    public boolean isInEmpire(Player player) {
        return getEmpire(player).isPresent();
    }

    public Set<Empire> getEmpires() {
        return empires;
    }

    public Optional<Empire> byName(String name) {
        return empires.stream().filter(empire -> StringUtils.removeColor(empire.getName()).equals(name)).findAny();
    }

    public void addEmpire(Empire empire) {
        empires.add(empire);
    }
}
