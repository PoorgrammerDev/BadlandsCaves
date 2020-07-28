package me.fullpotato.badlandscaves.CustomItems.Using.Voidmatter.Artifacts;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;

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
}
