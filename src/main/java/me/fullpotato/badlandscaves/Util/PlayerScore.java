package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public enum PlayerScore {
    INITIALIZED(PersistentDataType.BYTE),
    DEATHS(PersistentDataType.INTEGER),
    THIRST(PersistentDataType.DOUBLE, 100),
    TOXICITY(PersistentDataType.DOUBLE),
    THIRST_SYS_VAR(PersistentDataType.DOUBLE),
    TOX_SLOW_INCR_VAR(PersistentDataType.INTEGER),
    OPENED_CAULDRON(PersistentDataType.BYTE),
    HAS_SUPERNATURAL_POWERS(PersistentDataType.BYTE),
    IN_DESCENSION(PersistentDataType.INTEGER),
    IN_REFLECTION(PersistentDataType.BYTE),
    REFLECTION_ZOMBIE(PersistentDataType.BYTE),
    REFL_RESPAWN_INV(PersistentDataType.BYTE),
    DESCENSION_DETECT(PersistentDataType.DOUBLE),
    DESCENSION_DETECT_COOLDOWN(PersistentDataType.DOUBLE),
    DESCENSION_TIMER(PersistentDataType.INTEGER),
    DESCENSION_SHRINES_CAPPED(PersistentDataType.INTEGER),
    MANA(PersistentDataType.DOUBLE, 100),
    MAX_MANA(PersistentDataType.DOUBLE, 100),
    MANA_REGEN_DELAY_TIMER(PersistentDataType.INTEGER),
    MANA_BAR_ACTIVE_TIMER(PersistentDataType.INTEGER),
    MANA_BAR_MESSAGE_TIMER(PersistentDataType.INTEGER),
    MANA_BAR_COLOR_CHANGE_TIMER(PersistentDataType.INTEGER),
    SWAP_SLOT(PersistentDataType.INTEGER, -1),
    SWAP_DOUBLESHIFT_WINDOW(PersistentDataType.BYTE),
    SWAP_DOUBLESHIFT_OPTION(PersistentDataType.BYTE, 1),
    SWAP_WINDOW(PersistentDataType.BYTE),
    SWAP_COOLDOWN_OPTION(PersistentDataType.BYTE, 1),
    SWAP_COOLDOWN(PersistentDataType.INTEGER),
    SWAP_ORDER(PersistentDataType.STRING, "DISPLACE,ENHANCED_EYES,WITHDRAW,POSSESSION"),
    SPELL_COOLDOWN(PersistentDataType.BYTE),
    SPELLS_SILENCED_TIMER(PersistentDataType.INTEGER),
    DISPLACE_LEVEL(PersistentDataType.INTEGER),
    HAS_DISPLACE_MARKER(PersistentDataType.BYTE),
    DISPLACE_X(PersistentDataType.DOUBLE),
    DISPLACE_Y(PersistentDataType.DOUBLE),
    DISPLACE_Z(PersistentDataType.DOUBLE),
    WITHDRAW_LEVEL(PersistentDataType.INTEGER),
    WITHDRAW_X(PersistentDataType.DOUBLE),
    WITHDRAW_Y(PersistentDataType.DOUBLE),
    WITHDRAW_Z(PersistentDataType.DOUBLE),
    WITHDRAW_CHUNK_X(PersistentDataType.INTEGER),
    WITHDRAW_CHUNK_Z(PersistentDataType.INTEGER),
    WITHDRAW_TIMER(PersistentDataType.INTEGER),
    EYES_LEVEL(PersistentDataType.INTEGER),
    USING_EYES(PersistentDataType.BYTE),
    POSSESS_LEVEL(PersistentDataType.INTEGER),
    IN_POSSESSION(PersistentDataType.BYTE),
    POSSESS_ORIG_WORLD(PersistentDataType.STRING, ""),
    POSSESS_ORIG_X(PersistentDataType.DOUBLE),
    POSSESS_ORIG_Y(PersistentDataType.DOUBLE),
    POSSESS_ORIG_Z(PersistentDataType.DOUBLE),
    ENDURANCE_LEVEL(PersistentDataType.INTEGER),
    AGILITY_LEVEL(PersistentDataType.INTEGER),
    AGILITY_BUFF_SPEED_LVL(PersistentDataType.INTEGER),
    HAS_SEEN_BACKROOMS(PersistentDataType.BYTE),
    BACKROOMS_TIMER(PersistentDataType.INTEGER),
    TEMPERATURE(PersistentDataType.INTEGER, 100),
    USED_PRESERVE_TOTEM(PersistentDataType.BYTE),
    HAS_STARLIGHT_SENTRY(PersistentDataType.BYTE),
    STARLIGHT_SENTRY_UUID(PersistentDataType.STRING),
    PROPULSION_DOUBLESHIFT_WINDOW(PersistentDataType.BYTE),
    PROPULSION_COOLDOWN(PersistentDataType.BYTE),
    COUNTERATTACK_TRACKER(PersistentDataType.BYTE),
    ARTIFACTS(PersistentDataType.STRING, "");

    private final PersistentDataType<?, ?> type;
    private final Object defaultScore;

    PlayerScore(PersistentDataType<?, ?> type) {
        this.type = type;
        this.defaultScore = 0;
    }

    PlayerScore(PersistentDataType<?, ?> type, Object defaultScore) {
        this.type = type;
        this.defaultScore = defaultScore;
    }

    public PersistentDataType<?, ?> getType() {
        return this.type;
    }

    public Object getDefaultScore() {
        return this.defaultScore;
    }

    public Object getScore(BadlandsCaves plugin, Player player) {
        final NamespacedKey key = new NamespacedKey(plugin, this.name());
        if (player.getPersistentDataContainer().has(key, this.getType())) {
            final Object output = player.getPersistentDataContainer().get(key, this.getType());
            if (output != null) return output;
        }
        setScore(plugin, player, this.getDefaultScore());
        return this.getDefaultScore();
    }

    public void setScore(BadlandsCaves plugin, Player player, Object score) {
        PersistentDataContainer container = player.getPersistentDataContainer();

        if (this.getType().getPrimitiveType().equals(Double.class)) {
            try {
                container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.DOUBLE, Double.parseDouble(String.valueOf(score)));
                return;
            }
            catch (NumberFormatException ignored) {
            }
        }
        if (this.getType().getPrimitiveType().equals(Byte.class)) {
            try {
                container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.BYTE, Byte.parseByte(String.valueOf(score)));
                return;
            }
            catch (NumberFormatException ignored) {
            }
        }

        if (this.getType().getPrimitiveType().equals(Integer.class)) {
            try {
                container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.INTEGER, Integer.parseInt(String.valueOf(score)));
                return;
            }
            catch (NumberFormatException ignored) {
            }
        }
        if (this.getType().getPrimitiveType().equals(Short.class)) {
            try {
                container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.SHORT, Short.parseShort(String.valueOf(score)));
                return;
            }
            catch (NumberFormatException ignored) {
            }
        }
        if (this.getType().getPrimitiveType().equals(String.class)) {
            container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.STRING, String.valueOf(score));
        }
    }

    public boolean hasScore(BadlandsCaves plugin, Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        return container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.DOUBLE) ||
                container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.BYTE) ||
                container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.INTEGER) ||
                container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.SHORT) ||
                container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.STRING);
    }
}
