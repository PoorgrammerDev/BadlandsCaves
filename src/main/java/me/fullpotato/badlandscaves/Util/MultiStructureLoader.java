package me.fullpotato.badlandscaves.Util;

import org.bukkit.Location;

public class MultiStructureLoader {
    private final StructureTrack[] structures;

    public MultiStructureLoader(StructureTrack... structures) {
        this.structures = structures;
    }

    public void loadAll(Location origin, boolean keepStructureBlocks) {
        for (StructureTrack structure : structures) {
            structure.load(origin, keepStructureBlocks);
        }
    }

    public void loadAll(Location origin) {
        for (StructureTrack structure : structures) {
            structure.load(origin);
        }
    }

    public void loadAll() {
        for (StructureTrack structure : structures) {
            structure.load();
        }
    }
}
