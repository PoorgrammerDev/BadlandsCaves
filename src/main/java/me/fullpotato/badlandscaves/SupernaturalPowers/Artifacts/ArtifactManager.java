package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class ArtifactManager {
    private final BadlandsCaves plugin;

    public ArtifactManager(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void setArtifacts (Player player, Map<ArtifactBaseItem, Artifact> artifactMap) {
        final StringBuilder builder = new StringBuilder();
        artifactMap.forEach((artifactBaseItem, artifact) -> builder.append(artifactBaseItem).append(":").append(artifact).append(";"));
        PlayerScore.ARTIFACTS.setScore(plugin, player, builder.toString());
    }

    public Map<ArtifactBaseItem, Artifact> getArtifacts (Player player) {
        final String raw = (String) PlayerScore.ARTIFACTS.getScore(plugin, player);
        if (raw.isEmpty()) return new HashMap<>();

        final String[] entrySplit = raw.split(";");
        final Map<ArtifactBaseItem, Artifact> map = new HashMap<>();

        for (final String entry : entrySplit) {
            final String[] baseItemArtifactSplit = entry.split(":");
            final String baseItemName = baseItemArtifactSplit[0];
            final String artifactName = baseItemArtifactSplit[1];

            ArtifactBaseItem baseItem;
            Artifact artifact;
            try {
                baseItem = ArtifactBaseItem.valueOf(baseItemName);
                artifact = Artifact.valueOf(artifactName);
            }
            catch (IllegalArgumentException e) {
                continue;
            }

            map.put(baseItem, artifact);
        }
        return map;
    }

    public boolean hasArtifact(Player player, Artifact artifact) {
        for (ArtifactBaseItem artifactBaseItem : artifact.getArtifactBaseItems()) {
            final Artifact test = getArtifacts(player).get(artifactBaseItem);
            if (test != null) {
                return test.equals(artifact);
            }
        }
        return false;
    }

    public boolean isArtifact (ItemStack item) {
        if (item != null) {
            if (item.hasItemMeta()) {
                final ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_artifact"), PersistentDataType.BYTE)) {
                        Byte result = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_artifact"), PersistentDataType.BYTE);
                        if (result != null) {
                            return result == (byte) 1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Artifact toArtifact(ItemStack item) {
        if (isArtifact(item)) {
            for (Artifact value : Artifact.values()) {
                if (plugin.getCustomItemManager().getItem(value.getArtifactItem()).isSimilar(item)) {
                    return value;
                }
            }
        }
        return null;
    }
}
