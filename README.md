# BadlandsCaves

![Project Status](https://img.shields.io/badge/Project%20Status-Playable-brightgreen)
![Available for](https://img.shields.io/badge/Available%20for-Minecraft%201.16.2-blue)

## Description
BadlandsCaves is a Spigot plugin offering a challenging custom survival experience for Minecraft 1.16.2.<br>
The player is thrown into a hostile underground cave world with limited resources.<br>
The oceans are riddled with lethal toxin, but water is required to live.<br>
Dabble in magic or tinker with technology to overcome the perils ahead!

## Gallery

Images coming soon!

## Features
- Challenging subterranean environment
- Explore custom generated dungeons for loot
- Fight custom mini-bosses in late-game
- High risk, high reward "void" mining at the bottom of the world
- Volatile surface littered with enemies and acid rain
- Tech class
    - Throw together makeshift weapons early-game
    - Build indestructible Starlight gear late-game
- Magic class
    - Endure otherworldy trials to unlock supernatural abilities
    - Quickly adapt to and traverse terrain
    - Gain the upper hand in battles and during mining
    - Build Voidmatter gear as an extension of your abilities late-game
- Fight a stronger version of the Wither in a custom stage
- Explore hazardous alternate dimensions

## Installing

### Using Precompiled Jar (Recommended)
1. Download `BadlandsCaves.jar`, `BadlandsCaves-Map.zip`, and `BadlandsCaves-Resource-Pack.zip` from the Github Releases tab.
2. Create a Spigot server jar for 1.16.2 using [BuildTools](https://www.spigotmc.org/wiki/buildtools/).
3. Move the completed server jar from BuildTools into its own directory.
4. Create a `plugins` folder in the same directory as the server jar.
5. Move `BadlandsCaves.jar` into the `plugins` folder.
6. Unpack the `BadlandsCaves-Map.zip` archive and put the `world` folder in the same directory as the server jar.
7. Run the server.
8. Have each player install the resource pack (`BadlandsCaves-Resource-Pack.zip`). Alternatively, you can set the server resource pack in `server.properties`.

### Compiling Jar from Source
1. Create a Spigot server jar for 1.16.2 using [BuildTools](https://www.spigotmc.org/wiki/buildtools/). This is a prerequisite for <ins>Step 9</ins>.
2. Install [JetBrains IntelliJ IDEA Community](https://www.jetbrains.com/idea/).
3. Install IntelliJ plugin [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development).
4. Clone this repository.
5. Open the cloned folder `BadlandsCaves` as a project in IntelliJ.
6. Go to `Build > Build Artifacts > BadlandsCaves:jar > Edit...`.
7. Set the desired output directory under `Output Directory`.
8. Go to `Build > Build Artifacts > BadlandsCaves:jar > Build`.
9. Continue to "**Using Precompiled Jar**", using your compiled `BadlandsCaves.jar` instead of the pre-compiled jar of the same name. In <ins>Step 1</ins>, skip downloading the jar file, but still download the other two files. Additionally, skip <ins>Step 2</ins> entirely.

## Credits

Artist: Harry Zhou

Programmers: Thomas Tran, Aaron Tam


<!--
## Gameplay / Progression

- The gameplay is split into two halves: Pre-hardmode and Hardmode
- The Wither Boss Fight acts as the bridge between these halves
- Pre-hardmode can be split into two halves as well
    - The first half is characterized by building a stable settlement with food, clean water, and decent gear to fend off monsters
    - The second half is characterized by exploring and powering up for the Wither fight and Hardmode
        - By the end of this, the player should be at or near max vanilla gear (diamond/netherite, fully enchanted)
        - Undergoing trials and collecting Runes for magic class
- 
-->
