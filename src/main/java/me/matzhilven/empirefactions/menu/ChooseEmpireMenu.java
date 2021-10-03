package me.matzhilven.empirefactions.menu;

import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.ItemBuilder;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.List;

public class ChooseEmpireMenu extends Menu {

    private final FileConfiguration guiConfig = main.getGuiConfig();
    private final HashMap<Integer, Empire> slots;

    public ChooseEmpireMenu(Player p) {
        super(p);
        this.slots = new HashMap<>();
    }

    @Override
    public String getMenuName() {
        return guiConfig.getString("main.title");
    }

    @Override
    public int getSlots() {
        return guiConfig.getInt("main.slots");
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (!slots.containsKey(e.getSlot())) return;

        Empire empire = slots.get(e.getSlot());
        empire.addMember(p.getUniqueId());
        p.closeInventory();
    }

    @Override
    public void handleClose(InventoryCloseEvent e) {
        if (main.getEmpireManager().isInEmpire(p)) return;
        main.getServer().getScheduler().runTaskLater(main, this::open, 2L);
    }

    @Override
    public void setMenuItems() {
        int slot = -1;
        List<Empire> empires = main.getEmpireManager().getList();
        for (String item : guiConfig.getConfigurationSection("main.items").getKeys(false)) {
            if (slot + 1 == empires.size()) break;
            Empire empire = main.getEmpireManager().getList().get(++slot);
            if (empire == null) continue;
            inventory.setItem(guiConfig.getInt("main.items." + item + ".slot"),
                    new ItemBuilder(Material.matchMaterial(guiConfig.getString("main.items." + item + ".material")))
                            .setName(empire.getName())
                            .setLore(guiConfig.getStringList("main.items." + item + ".lore"))
                            .replace("%name%", StringUtils.colorize(empire.getName()))
                            .replace("%players%", String.valueOf(empire.getAll().size()))
                            .toItemStack());
            slots.put(guiConfig.getInt("main.items." + item + ".slot"), empire);
        }
    }
}
