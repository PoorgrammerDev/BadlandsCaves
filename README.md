
# BadlandsCaves

![Game Status](https://img.shields.io/badge/Game%20Status-Playable-brightgreen)
![Available for](https://img.shields.io/badge/Available%20for-Minecraft%201.16.5-blue)
![Project Status](https://img.shields.io/badge/Project%20Status-Incomplete,%20Development%20Stopped-lightgray)

## Description
BadlandsCaves is a Spigot plugin offering a challenging custom survival experience for Minecraft 1.16.5.<br>
The player is thrown into a hostile underground cave world with limited resources.<br>
The oceans are riddled with lethal toxin, but water is required to live.<br>
Explore dungeons, fight bosses, open portals to raid other worlds, and dabble in magic or tinker with technology to overcome the perils ahead!

## Gallery

Images coming soon!

## Features
- Challenging subterranean environment
- Explore custom generated dungeons for loot
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
- Fight custom mini-bosses in late-game
- Invade hazardous alternate dimensions
	- Pilfer and raid structures and enemies
	- Succumb to the unforgiving Void Layer

## Installing

### <ins>Using Precompiled Jar (Recommended)</ins>
1. Download `BadlandsCaves.jar`, `BadlandsCaves-Map.zip`, and `BadlandsCaves-Resource-Pack.zip` from the Github Releases tab.
2. Create a Spigot server jar for 1.16.5 using [BuildTools](https://www.spigotmc.org/wiki/buildtools/).
3. Move the completed server jar from BuildTools into its own directory.
4. Create a `plugins` folder in the same directory as the server jar.
5. Move `BadlandsCaves.jar` into the `plugins` folder.
6. Unpack the `BadlandsCaves-Map.zip` archive and put the `world` folder in the same directory as the server jar.
7. Run the server.
8. Have each player install the resource pack (`BadlandsCaves-Resource-Pack.zip`). Alternatively, you can set the server resource pack in `server.properties`.


### Compiling Jar from Source using Maven (Command Line)
1. Create a Spigot server jar for 1.16.5 using [BuildTools](https://www.spigotmc.org/wiki/buildtools/). This is a prerequisite for <ins>Step 4</ins>.
2. Download and install [Maven](https://maven.apache.org/).
3. Clone this repository.
4. Run `mvn` in the BadlandsCaves folder.
5. Inside the `target` directory, the file `BadlandsCaves-BETA 1.2.jar` file is the compiled jar file.
7. Continue to "**Using Precompiled Jar**", using your compiled jar file instead of the pre-compiled jar. In <ins>Step 1</ins>, skip downloading the jar file, but still download the other two files. Additionally, skip <ins>Step 2</ins> entirely.


### Compiling Jar from Source using IntelliJ (GUI)
1. Create a Spigot server jar for 1.16.5 using [BuildTools](https://www.spigotmc.org/wiki/buildtools/). This is a prerequisite for <ins>Step 9</ins>.
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

## About
This section doesn't pertain to the plugin's features or setting it up at all. This section is about why I made this project, what it means to me, etc.

I started this project in my senior year of high school as I was getting into programming. It wasn't for a class or for a club, just something I wanted to work on in my free time.

At the time, I was learning Java in my coding class. I knew Minecraft was coded in Java and knew how some of the technical things worked in the game, so I decided to take a shot at making some kind of gameplay experience. While this is technically a Spigot plugin, I tried to design the experience to be more akin to a Forge modpack. The only reason I didn't go with Forge is because I had wanted the game to be playable without installing any mods.

I used this project not only as a means to express my ideas, but also as a way to better learn programming. Much of the code here is prime evidence of a learning process. It's poorly optimized, poorly designed, and insanely repetitive, as I had written this before I learned any concept of time complexity, data structures, design patterns, etc. Despite that, I believe the practice/experience I built from working on this (and other personal projects) has served me quite well.

I had stopped development in August 2020 and moved onto other things for a while, but it always bothered me that I simply left the project to die. So, this year, I returned and tied up some loose ends, fixed some bugs, and wrote this readme. (And yes, it was painful working with my old code.) It's not completely finished, but it's at a point where I can be happy about leaving it.

At the time of writing this, I'm currently a college student and looking to get into game development. One day, I want to make a better version of this project as a standalone game. If that ever happens, I will update this readme with a link to it.
