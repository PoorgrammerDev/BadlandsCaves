package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.ManaBarManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.IllegalFormatException;

public class SwapPowers implements Listener {
    private final BadlandsCaves plugin;
    private final ManaBarManager manaBarManager;

    public SwapPowers(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.manaBarManager = new ManaBarManager(plugin);
    }

    @EventHandler
    public void doubleShift (PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        ManaBarManager bar = new ManaBarManager(plugin);

        boolean doubleShiftOption = (byte) PlayerScore.SWAP_DOUBLESHIFT_OPTION.getScore(plugin, player) == 1;
        if (doubleShiftOption) {
            if (player.isSneaking()) {
                PlayerScore.SWAP_WINDOW.setScore(plugin, player, 0);
                if ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) <= 0) {
                    bar.clearMessage(player);
                }
            }
            else {
                final boolean doubleshift_window = (PlayerScore.SWAP_DOUBLESHIFT_WINDOW.hasScore(plugin, player)) && ((byte) PlayerScore.SWAP_DOUBLESHIFT_WINDOW.getScore(plugin, player) == 1);
                if (doubleshift_window) {
                    PlayerScore.SWAP_WINDOW.setScore(plugin, player, 1);

                    if ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) <= 0) {
                        bar.displayMessage(player, "§3Scroll to Access Abilities", 2, false);
                    }
                }
                else {
                    PlayerScore.SWAP_DOUBLESHIFT_WINDOW.setScore(plugin, player, 1);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            PlayerScore.SWAP_DOUBLESHIFT_WINDOW.setScore(plugin, player, 0);
                        }
                    }.runTaskLaterAsynchronously(plugin, 20);
                }
            }
        }
        else {
            if (player.isSneaking()) {
                PlayerScore.SWAP_WINDOW.setScore(plugin, player, 0);
                if ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) <= 0) {
                    bar.clearMessage(player);
                }
            }
            else {
                PlayerScore.SWAP_WINDOW.setScore(plugin, player, 1);
                bar.displayMessage(player, "§3Scroll to Access Abilities", 2, false);
            }
        }
    }


    @EventHandler
    public void swap_to_powers (PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 0) return;
        if (!player.isSneaking()) return;
        if (!PlayerScore.SWAP_WINDOW.hasScore(plugin, player) || (byte) PlayerScore.SWAP_WINDOW.getScore(plugin, player) == 0) return;
        if ((int) PlayerScore.SWAP_COOLDOWN.getScore(plugin, player) > 0) return;

        final ItemStack offhandItem = player.getInventory().getItemInOffHand();
        final ActivePowers[] order = getSwapOrder(player);
        int swap_slot = ((int) PlayerScore.SWAP_SLOT.getScore(plugin, player));
        event.setCancelled(true);

        for (int tries = 0; tries < order.length * 2; tries++) {
            swap_slot = ++swap_slot >= order.length ? -1 : swap_slot;

            //Switching back to default offhand
            if (swap_slot == -1) {
                final ItemStack orig_item = plugin.getSystemConfig().getItemStack("player_info." + player.getUniqueId() + ".saved_offhand_item");
                player.getInventory().setItemInOffHand(orig_item);

                plugin.getSystemConfig().set("player_info." + player.getUniqueId() + ".saved_offhand_item", null);
                plugin.saveSystemConfig();

                if ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) <= 0) {
                    manaBarManager.clearMessage(player);
                }
                successfulSwap(player, swap_slot);
                return;
            }
            else {
                //If the next spell in the list is unlocked
                if ((int) order[swap_slot].getLevelScore().getScore(plugin, player) > 0) {

                    //Checks if current offhand item is spell
                    boolean offHandIsSpell = false;
                    for (ActivePowers check : ActivePowers.values()) {
                        if (offhandItem.isSimilar(check.getItem().getItem())) {
                            offHandIsSpell = true;
                            break;
                        }
                    }

                    //Saves item if not spell
                    if (!offHandIsSpell) {
                        plugin.getSystemConfig().set("player_info." + player.getUniqueId() + ".saved_offhand_item", offhandItem);
                        plugin.saveSystemConfig();
                    }

                    player.getInventory().setItemInOffHand(order[swap_slot].getItem().getItem());

                    if ((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) <= 0) {
                        manaBarManager.displayMessage(player, order[swap_slot].getDisplayName(), 2, true);
                    }
                    successfulSwap(player, swap_slot);
                    return;
                }
            }
        }
    }

    public void successfulSwap (Player player, int swap_slot) {
        PlayerScore.SWAP_SLOT.setScore(plugin, player, swap_slot);
        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        if ((byte) PlayerScore.SWAP_COOLDOWN_OPTION.getScore(plugin, player) == 1) PlayerScore.SWAP_COOLDOWN.setScore(plugin, player, 5);
        new BukkitRunnable() {
            @Override
            public void run() {
                int swapCooldown = ((int) PlayerScore.SWAP_COOLDOWN.getScore(plugin, player));
                if (swapCooldown > 0) {
                    PlayerScore.SWAP_COOLDOWN.setScore(plugin, player, --swapCooldown);
                }
                else {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin,0,0);
    }

    public ActivePowers[] getSwapOrder (Player player) {
        final String raw = (String) PlayerScore.SWAP_ORDER.getScore(plugin, player);
        final String[] split = raw.split(",");

        final ActivePowers[] output = new ActivePowers[split.length];

        for (int i = 0; i < split.length; i++) {
            try {
                output[i] = ActivePowers.valueOf(split[i]);
            }
            catch (IllegalFormatException ignored) {
            }
        }
        return output;
    }

    public void setSwapOrder (Player player, ActivePowers... powers) {
        final StringBuilder builder = new StringBuilder();
        for (ActivePowers power : powers) {
            builder.append(power.name()).append(",");
        }
        PlayerScore.SWAP_ORDER.setScore(plugin, player, builder.toString());
    }
}
