package me.matzhilven.empirefactions.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.data.PlayerData;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.core.Core;
import me.matzhilven.empirefactions.empire.core.CoreType;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.menu.ChooseEmpireMenu;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import me.matzhilven.empirefactions.utils.Vector3D;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
            if (player.hasPermission("empire.choose")) {
                main.getServer().getScheduler().runTaskLater(main, () -> new ChooseEmpireMenu(player).open(), 10L);
                return;
            }

            main.getEmpireManager().getEmpireWithLeastPlayers().addMember(player.getUniqueId());
        }

        main.getDb().addPlayer(player.getUniqueId());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        main.getDb().savePlayer(e.getPlayer());
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
    private void onEnderCrystalDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof EnderCrystal) {
            EnderCrystal crystal = (EnderCrystal) e.getEntity();
            if (!crystal.hasMetadata("empire")) return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerPunchEnderCrystal(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction() != Action.LEFT_CLICK_AIR) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock() == null) return;

                Block block = e.getClickedBlock();

                Optional<Faction> optionalClaimedFaction = main.getChunkManager().getChunkOwner(block.getChunk());
                if (!optionalClaimedFaction.isPresent()) return;

                Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

                if (!optionalEmpire.isPresent()) {
                    e.setCancelled(true);
                    return;
                }

                Empire empire = optionalEmpire.get();
                Optional<Faction> optionalFaction = empire.getFaction(player);

                if (!optionalFaction.isPresent()) {
                    e.setCancelled(true);
                    return;
                }

                if (block.getType().toString().equals("LEVER") || block.getType().toString().contains("BUTTON") || block.getType().toString().contains("DOOR")) {
                    Faction claimedFaction = optionalClaimedFaction.get();
                    Faction faction = optionalFaction.get();

                    if (claimedFaction.isRaidable() || claimedFaction.isAlly(faction)) return;
                    if (claimedFaction != faction) e.setCancelled(true);
                }
                return;
            }
            for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
                if (isLookingAt(player, entity)) {
                    EnderCrystal crystal = (EnderCrystal) entity;
                    if (!crystal.hasMetadata("empire")) return;
                    e.setCancelled(true);
                    Location location = crystal.getLocation();

                    Optional<Empire> optionalPlayerEmpire = main.getEmpireManager().getEmpire(player);
                    if (!optionalPlayerEmpire.isPresent()) return;

                    Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(UUID.fromString(crystal.getMetadata("empire").get(0).asString()));
                    if (!optionalEmpire.isPresent()) return;

                    Empire empire = optionalEmpire.get();
                    Empire playerEmpire = optionalPlayerEmpire.get();
                    if (empire == playerEmpire) {
                        StringUtils.sendMessage(player, Messager.OWN_CORE);
                        return;
                    }

                    Core core = empire.removeCore(location);

                    if (core == null) return;
                    crystal.remove();

                    location.getWorld().playEffect(location, Effect.EXPLOSION_HUGE, 0);
                    location.getWorld().playSound(location, Sound.EXPLODE, 1f, 1f);

                    StringUtils.sendMessage(player, Messager.BROKEN_CORE.replace("%empire%", empire.getName()).replace("%type%", core.getCoreType().getName()));
                    return;
                }
            }
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        Optional<Faction> optionalClaimedFaction = main.getChunkManager().getChunkOwner(block.getChunk());
        if (!optionalClaimedFaction.isPresent()) return;

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) {
            e.setCancelled(true);
            return;
        }

        Empire empire = optionalEmpire.get();
        Optional<Faction> optionalFaction = empire.getFaction(player);

        if (!optionalFaction.isPresent()) {
            e.setCancelled(true);
            return;
        }

        if (block.getType().toString().equals("LEVER") || block.getType().toString().contains("BUTTON") || block.getType().toString().contains("DOOR")) {
            Faction claimedFaction = optionalClaimedFaction.get();
            Faction faction = optionalFaction.get();
            if (claimedFaction.isRaidable() || claimedFaction.isAllowedAlly(faction)) return;
            if (claimedFaction != faction) e.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        Optional<Faction> optionalClaimedFaction = main.getChunkManager().getChunkOwner(block.getChunk());
        if (!optionalClaimedFaction.isPresent()) return;

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) {
            e.setCancelled(true);
            return;
        }

        Empire empire = optionalEmpire.get();
        Optional<Faction> optionalFaction = empire.getFaction(player);

        if (!optionalFaction.isPresent()) {
            e.setCancelled(true);
            return;
        }

        Faction claimedFaction = optionalClaimedFaction.get();
        Faction faction = optionalFaction.get();
        if (claimedFaction.isRaidable() || claimedFaction.isAllowedAlly(faction)) return;

        if (claimedFaction != faction) e.setCancelled(true);
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);
        if (!optionalEmpire.isPresent()) return;

        Empire empire = optionalEmpire.get();

        Optional<Faction> optionalFaction = empire.getFaction(player);
        if (!optionalFaction.isPresent()) return;

        Faction faction = optionalFaction.get();
        if (!faction.isInChat(player)) return;
        e.setCancelled(true);
        if (!faction.sendMessage(player, e.getMessage())) {
            StringUtils.sendMessage(player, Messager.CHAT_MUTED);
        }
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        PlayerData.get(player.getUniqueId()).removePower(main.getConfig().getInt("power-loss-per-death"));

        Player killer = player.getKiller();
        if (killer != null) {
            Optional<Empire> optionalKillerEmpire = main.getEmpireManager().getEmpire(killer);
            if (!optionalKillerEmpire.isPresent()) return;

            Optional<Faction> optionalKillerFaction = optionalKillerEmpire.get().getFaction(killer);
            if (!optionalKillerFaction.isPresent()) return;

            optionalKillerFaction.get().addKill();
        }

        Optional<Empire> optionalPlayerEmpire = main.getEmpireManager().getEmpire(player);
        if (!optionalPlayerEmpire.isPresent()) return;

        Optional<Faction> optionalPlayerFaction = optionalPlayerEmpire.get().getFaction(player);
        if (!optionalPlayerFaction.isPresent()) return;

        optionalPlayerFaction.get().addDeath();
    }

    private boolean isLookingAt(Player player, Entity entity) {
        Location playerPos = player.getEyeLocation();
        Vector3D playerDir = new Vector3D(playerPos.getDirection());
        Vector3D playerStart = new Vector3D(playerPos);
        Vector3D playerEnd = playerStart.add(playerDir.multiply(100));

        Vector3D targetPos = new Vector3D(entity.getLocation());
        Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
        Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

        return hasIntersection(playerStart, playerEnd, minimum, maximum);
    }

    private boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
        final double epsilon = 0.0001f;
        Vector3D d = p2.subtract(p1).multiply(0.5);
        Vector3D e = max.subtract(min).multiply(0.5);
        Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        Vector3D ad = d.abs();

        if (Math.abs(c.x) > e.x + ad.x) return false;
        if (Math.abs(c.y) > e.y + ad.y) return false;
        if (Math.abs(c.z) > e.z + ad.z) return false;

        if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon) return false;
        if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon) return false;
        return !(Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon);
    }
}