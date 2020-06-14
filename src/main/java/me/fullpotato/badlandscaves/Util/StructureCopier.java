package me.fullpotato.badlandscaves.Util;

import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class StructureCopier {
    private final static String namespace = "badlandscaves";

    public static void copyStructures(World origin, World target, String folderName) {
        File originFolder = new File(origin.getName() + "/generated/" + namespace + "/structures/" + folderName);
        if (originFolder.isDirectory()) {
            File targetFolder = new File(target.getName() + "/generated/" + namespace + "/structures/");
            targetFolder.mkdirs();

            for (File structureFile : originFolder.listFiles()) {
                File targetFile = new File(target.getName() + "/generated/" + namespace + "/structures/" + structureFile.getName());
                try {
                    Files.copy(structureFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException ignored) {
                }
            }
        }
    }
}