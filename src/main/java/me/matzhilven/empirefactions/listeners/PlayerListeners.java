package me.matzhilven.empirefactions.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.core.Core;
import me.matzhilven.empirefactions.empire.core.CoreType;
import me.matzhilven.empirefactions.menu.ChooseEmpireMenu;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class PlayerListeners implements Listener {

    private final EmpireFactions main;

    public PlayerListeners(EmpireFactions main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!main.getEmpireManager().isInEmpire(player)) {
            if (main.getEmpireManager().getEmpires().size() == 0) return;
            main.getServer().getScheduler().runTaskLater(main, () -> new ChooseEmpireMenu(player).open(), 10L);
        }
    }

    @EventHandler
    private void onBlockPlace(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!e.hasItem() || !e.hasBlock()) return;
        if (e.getItem() == null || e.getClickedBlock() == null) return;


        Player player = e.getPlayer();
        ItemStack hand = e.getItem();

        NBTItem nbtItem = new NBTItem(hand);

        if (!nbtItem.hasKey("empire")) return;

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(UUID.fromString(nbtItem.getString("empire")));

        if (!optionalEmpire.isPresent()) return;

        Empire empire = optionalEmpire.get();
        CoreType coreType = CoreType.valueOf(nbtItem.getString("coreType"));

        empire.addCore(e.getClickedBlock().getLocation().clone().add(0, 1, 0), coreType);
        StringUtils.sendMessage(player, Messager.PLACED_CORE.replace("%empire%", empire.getName()).replace("%type%", coreType.getName()));
    }

    @EventHandler
    private void onEnderCrystalExplode(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof EnderCrystal)) return;
        Location location = e.getLocation();
        EnderCrystal crystal = (EnderCrystal) e.getEntity();

        Player player = null;

        for (Entity entity : crystal.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player) {
                if (player == null) player = (Player) entity;
                if (location.distanceSquared(entity.getLocation()) < location.distanceSquared(player.getLocation()))
                    player = (Player) entity;
            }
        }

        if (player == null) return;

        if (!crystal.hasMetadata("empire")) return;
        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(UUID.fromString(crystal.getMetadata("empire").get(0).asString()));
        if (!optionalEmpire.isPresent()) return;

        Empire empire = optionalEmpire.get();
        Core core = empire.removeCore(location);

        if (core == null) return;

        StringUtils.sendMessage(player, Messager.BROKEN_CORE.replace("%empire%", empire.getName()).replace("%type%", core.getCoreType().getName()));
    }


}
