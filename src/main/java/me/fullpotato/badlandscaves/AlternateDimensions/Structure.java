package me.fullpotato.badlandscaves.AlternateDimensions;

import java.util.ArrayList;
import java.util.List;

public enum Structure {
    //surface layer structures ------
    MANABAR(StructureType.SURFACE_STRUCTURE),
    LAB(StructureType.SURFACE_STRUCTURE),
    LAB_ABANDONED(StructureType.SURFACE_STRUCTURE),
    LAB_DESTROYED(StructureType.SURFACE_STRUCTURE),
    JAIL(StructureType.SURFACE_STRUCTURE),
    JAIL_ABANDONED(StructureType.SURFACE_STRUCTURE),
    SHRINE(StructureType.SURFACE_STRUCTURE),
    SHRINE_DESTROYED(StructureType.SURFACE_STRUCTURE),
    CURSED_HOUSE(StructureType.SURFACE_STRUCTURE),
    TENT(StructureType.SURFACE_STRUCTURE),
    HOUSE(StructureType.SURFACE_STRUCTURE),
    HOUSE_ABANDONED(StructureType.SURFACE_STRUCTURE),
    HOUSE_DESTROYED(StructureType.SURFACE_STRUCTURE),
    BUNKER(StructureType.SURFACE_STRUCTURE),
    BUNKER2(StructureType.SURFACE_STRUCTURE),
    BUNKER_AB(StructureType.SURFACE_STRUCTURE),
    CASTLE(StructureType.SURFACE_STRUCTURE),

    //void layer structures -----
    CASTLE_VOID(StructureType.VOID_STRUCTURE),
    HOUSE_VOID(StructureType.VOID_STRUCTURE),
    LAB_VOID(StructureType.VOID_STRUCTURE),
    HOUSE_DESTROYED_VOID(StructureType.VOID_STRUCTURE),
    LAB_DESTROYED_VOID(StructureType.VOID_STRUCTURE),
    SHRINE_VOID(StructureType.VOID_STRUCTURE),

    //void layer decorations -----
    LANTERN_STATUE_GHOUL_1(StructureType.VOID_DECORATION),
    LANTERN_STATUE_GHOUL_2(StructureType.VOID_DECORATION),
    LANTERN_STATUE_GHOUL_3(StructureType.VOID_DECORATION),
    LANTERN_STATUE_GHOUL_4(StructureType.VOID_DECORATION),
    LANTERN_STATUE_LARGE_1(StructureType.VOID_DECORATION),
    LANTERN_STATUE_LARGE_2(StructureType.VOID_DECORATION),
    STATUE_HOUND_1(StructureType.VOID_DECORATION),



    
    ;
   
    private StructureType type; 

    private Structure(StructureType type) {
        this.type = type;
    }

    public StructureType getType() {
        return this.type;
    }

    public static List<Structure> GetStructuresByType(StructureType type) {
        final List<Structure> ret = new ArrayList<>();

        for (Structure st : Structure.values()) {
            if (st.getType() == type) {
                ret.add(st);
            }
        }
        
        return ret;
    }

}