package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public enum PlayerScore {
    DEATHS,
    THIRST(PersistentDataType.DOUBLE, 100),
    TOXICITY,
    THIRST_SYS_VAR,
    TOX_NAT_DECR_VAR,
    TOX_SLOW_INCR_VAR,
    OPENED_CAULDRON(PersistentDataType.BYTE),
    HAS_SUPERNATURAL_POWERS(PersistentDataType.BYTE),
    IN_DESCENSION,
    IN_REFLECTION(PersistentDataType.BYTE),
    REFLECTION_ZOMBIE,
    REFL_RESPAWN_INV(PersistentDataType.BYTE),
    DESCENSION_DETECT,
    DESCENSION_DETECT_COOLDOWN,
    DESCENSION_TIMER,
    DESCENSION_SHRINES_CAPPED,
    MANA(PersistentDataType.DOUBLE, 100),
    MAX_MANA(PersistentDataType.DOUBLE, 100),
    MANA_REGEN_DELAY_TIMER,
    MANA_BAR_ACTIVE_TIMER,
    MANA_BAR_MESSAGE_TIMER,
    SWAP_SLOT,
    SWAP_DOUBLESHIFT_WINDOW,
    SWAP_WINDOW,
    SWAP_COOLDOWN,
    SWAP_NAME_TIMER,
    SPELL_COOLDOWN(PersistentDataType.BYTE),
    DISPLACE_LEVEL,
    HAS_DISPLACE_MARKER(PersistentDataType.BYTE),
    DISPLACE_X,
    DISPLACE_Y,
    DISPLACE_Z,
    WITHDRAW_LEVEL,
    WITHDRAW_X,
    WITHDRAW_Y,
    WITHDRAW_Z,
    WITHDRAW_CHUNK_X,
    WITHDRAW_CHUNK_Z,
    WITHDRAW_TIMER,
    EYES_LEVEL,
    USING_EYES(PersistentDataType.BYTE),
    POSSESS_LEVEL,
    IN_POSSESSION(PersistentDataType.BYTE),
    POSSESSED_ENTITY,
    POSSESS_ORIG_WORLD,
    POSSESS_ORIG_X,
    POSSESS_ORIG_Y,
    POSSESS_ORIG_Z,
    ENDURANCE_LEVEL,
    AGILITY_LEVEL,
    AGILITY_BUFF_SPEED_LVL,
    HAS_SEEN_BACKROOMS(PersistentDataType.BYTE),
    BACKROOMS_TIMER,
    BLEEDING_DEBUFF(PersistentDataType.BYTE);

    private final PersistentDataType<?, ?> type;
    private final Object defaultScore;

    PlayerScore() {
        type = PersistentDataType.DOUBLE;
        defaultScore = 0;
    }

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
        return player.getPersistentDataContainer().get(new NamespacedKey(plugin, this.name()), this.getType());
    }

    public void setScore(BadlandsCaves plugin, Player player, Object score) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (this.getType().getPrimitiveType().equals(Double.class) && score instanceof Double) {
            container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.DOUBLE, (Double) score);
        }
        else if (this.getType().getPrimitiveType().equals(Byte.class) && score instanceof Byte) {
            container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.BYTE, (Byte) score);
        }
        else if (this.getType().getPrimitiveType().equals(Integer.class) && score instanceof Integer) {
            container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.INTEGER, (Integer) score);
        }
        else if (this.getType().getPrimitiveType().equals(Short.class) && score instanceof Short) {
            container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.SHORT, (Short) score);
        }
        else if (this.getType().getPrimitiveType().equals(String.class) && score instanceof String) {
            container.set(new NamespacedKey(plugin, this.name()), PersistentDataType.STRING, (String) score);
        }
    }

    public boolean hasScore(BadlandsCaves plugin, Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();

        if (this.getType().getPrimitiveType().equals(Double.class) && this.getDefaultScore() instanceof Double) {
            return container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.DOUBLE);
        }
        else if (this.getType().getPrimitiveType().equals(Byte.class) && this.getDefaultScore() instanceof Byte) {
            return container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.BYTE);
        }
        else if (this.getType().getPrimitiveType().equals(Integer.class) && this.getDefaultScore() instanceof Integer) {
            container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.INTEGER);
        }
        else if (this.getType().getPrimitiveType().equals(Short.class) && this.getDefaultScore() instanceof Short) {
            return container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.SHORT);
        }
        else if (this.getType().getPrimitiveType().equals(String.class) && this.getDefaultScore() instanceof String) {
            return container.has(new NamespacedKey(plugin, this.name()), PersistentDataType.STRING);
        }
        return false;
    }
}
