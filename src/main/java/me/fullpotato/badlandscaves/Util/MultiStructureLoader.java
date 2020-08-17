package me.fullpotato.badlandscaves.Util;

import org.bukkit.Location;
import org.bukkit.block.Structure;

import java.util.HashSet;
import java.util.Set;

public class MultiStructureLoader {
    private final StructureTrack[] structures;

    public MultiStructureLoader(StructureTrack... structures) {
        this.structures = structures;
    }

    public Set<Structure> loadAll(Location origin, boolean keepStructureBlocks) {
        final Set<Structure> structures = new HashSet<>();
        for (StructureTrack structure : this.structures) {
            structures.add(structure.load(origin, keepStructureBlocks));
        }
        return structures;
    }

    public Set<Structure> loadAll(Location origin) {
        final Set<Structure> structures = new HashSet<>();
        for (StructureTrack structure : this.structures) {
            structures.add(structure.load(origin));
        }
        return structures;
    }

    public Set<Structure> loadAll() {
        final Set<Structure> structures = new HashSet<>();
        for (StructureTrack structure : this.structures) {
            structures.add(structure.load());
        }
        return structures;
    }
}
