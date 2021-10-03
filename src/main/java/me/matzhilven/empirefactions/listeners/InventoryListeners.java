package me.matzhilven.empirefactions.listeners;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.menu.Menu;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListeners implements Listener {

    private final EmpireFactions main;

    public InventoryListeners(EmpireFactions main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof Menu) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            e.setCancelled(true);
            ((Menu) holder).handleClick(e);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof Menu) {
            ((Menu) holder).handleClose(e);
        }
    }
}
