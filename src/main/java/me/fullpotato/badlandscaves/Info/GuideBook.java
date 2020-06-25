package me.fullpotato.badlandscaves.Info;

import me.fullpotato.badlandscaves.BadlandsCaves;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;

public class GuideBook {
    public static ItemStack getGuideBook (BadlandsCaves plugin) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("Guide");
        meta.setAuthor("FullPotato");
        meta.setGeneration(BookMeta.Generation.ORIGINAL);

        String version = plugin.getDescription().getVersion();
        meta.addPage("\n" + "§0\n" + "§0\n" + "§6§lBadlandsCaves§0\n" + "§8§lby FullPotato§0\n" + "§0\n" + "§0\n" + "§0\n" + "§8ver. " + version);

        TextComponent quickLinks = new TextComponent("§lExternal Links\n");
        TextComponent craftGuideLink = new TextComponent("§9Crafting Guide");
        TextComponent[] craftHover = new TextComponent[1];
        craftHover[0] = new TextComponent("Click to go to the Crafting Guide.");
        craftGuideLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, craftHover));
        craftGuideLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/craftguide"));
        TextComponent[] craftGuidePage = {quickLinks, craftGuideLink};
        meta.spigot().addPage(craftGuidePage);


        final String[] entries_page_1 = {
                "§0Introduction§0\n",
                "§0Getting Started§0\n",
                "§9Thirst§0\n",
                "§2Toxicity§0\n",
                "§6Deaths§0\n",
                "§3Purifying Water§0\n",
                "§1Fishing§0\n",
                "§0Classes§0\n",
                "§3Puresoul§0\n",
                "§5Heretic§0\n",
                "§2Dungeons §0\n",
                "§4Chaos§0\n",
                "§cHallowed Chambers§0",
        };
        final String[] pages_page_1 = {
                "4",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "16",
                "21",
                "22",
                "23",
        };
        meta.spigot().addPage(getTOCEntries(entries_page_1, pages_page_1, false));


        final String[] entries_page_2 = {"§4Hardmode§0\n", "§9Augmented Mobs"};
        final String[] pages_page_2 = {"26", "28"};
        meta.spigot().addPage(getTOCEntries(entries_page_2, pages_page_2, true));

        meta.addPage("§lIntroduction§0\n" + "§0You awaken in a mysterious underground cave that seems to never stop. There are no signs of life around you, and all you have are tools to make a tree.");
        meta.addPage("§lIntroduction (cont.)§0\n" + "§0All the water around you is highly poisonous, and your body would not last more than a few seconds in it. However, you need to drink water to survive. Dying only makes things worse.");
        meta.addPage("§lGetting Started§0\n" + "§0You can get wood by using the items you spawn with. With that, tools and weapons are now within reach. Sand for bottles can be made by combining dirt and terracotta, and fishing can be a good early-game food source.");
        meta.addPage("§9§lThirst§0\n" + "§0As you move around, you get thirsty. You must drink water, but the water around you is toxic. Drinking this will increase your Toxicity. To safely drink water, purify it. As your Thirst decreases, you get worse and worse effects until you die at 0%.");
        meta.addPage("§2§lToxicity§0\n" + "§0The reason you die in water is because it rapidly increases your Toxicity. Drinking toxic water also yields the same result, albeit slower. As your toxicity increases, you get worse and worse effects until you die at 100%.");
        meta.addPage("§6§lDeaths§0\n" + "§0You initially spawn with a few buffs. If you keep dying, these buffs are removed and replaced with worse and worse debuffs. To reverse deaths, craft and eat Blessed Apples.");
        meta.addPage("§3§lPurifying Water§0\n" + "§0To purify water, you need to light a fire underneath a cauldron, fill it with water, and open it by right-clicking. Then, add a glass bottle and blaze powder.");
        meta.addPage("§1§lFishing§0\n" + "§0Fishing is a great way to get food early on in the game. String can be harvested from cobwebs or spiders to make the fishing rod. You can fish up Fishing Crates that contain a decent amount of fish or even rare treasure.");
        meta.addPage("§lClasses§0\n" + "§0As you progress, you can either become a Heretic or stay a Puresoul. A Heretic is involved with black magic, whereas a Puresoul can invest into technology.");
        meta.addPage("§3§lPuresoul§0\n" + "§0As a puresoul, you can invest into tech, starting with first a modified sword all the way to power armor, laser sabers, and laser guns in Hardmode. The tech-enhanced player is more durable than a magic user, but less agile.");
        meta.addPage("§3§lPuresoul (cont.)§0\n" + "§0You can modify swords to be Serrated, Voltshock, or Corrosive. Arrows can also be modified to be Voltshock or Corrosive. Serrated inflicts slow-acting DoT, Voltshock increases damage and stuns, and Corrosive inflicts fast-acting true DoT.");
        meta.addPage("§3§lPuresoul (cont.)§0\n" + "§0You can also craft shields from different materials like Cobblestone, Iron, and Diamond. These shields are more protective and durable than a regular shield, but also slow you down more.");
        meta.addPage("§5§lHeretic§0\n" + "§0A heretic uses black magic to weave between enemies and obstacles. Their powers work together to make a menacing force. The Heretic is less durable, but more agile.");
        meta.addPage("§5§lHeretic (cont.)§0\n" + "§0To access black magic, you must collect souls from mobs. Combine the souls of a zombie, zombified piglin, creeper, witch, ghast, silverfish, skeleton, spider, and phantom to make a Merged Soul.");
        meta.addPage("§5§lHeretic (cont.)§0\n" + "§0Then, infuse that merged soul into a diamond. The result is a Soul Crystal, but it is incomplete. You must sacrifice one more soul - your own. Right-click to enter the World of Reflection and harvest your own soul.");
        meta.addPage("§5§lHeretic (cont.)§0\n" + "§0After that, the Soul Crystal is complete. Again, right-click to enter Descension. Here, you must sneak past Lost Souls to capture four towers in a large ring. With every tower you capture, you gain more powers to use.");
        meta.addPage("§5§lHeretic (cont.)§0\n" + "§0If you fail, all collected powers are lost. After you capture all four towers, you can walk along the beam to the center, and exit. You come back with only level 1 Displace. To unlock more powers, you need to find Runes.");
        meta.addPage("§2§lDungeons§0\n" + "§0Finding dungeons and breaking spawners gives players loot, but is vital to a Heretic because it is the only source of Runes. Everytime a spawner is broken, a new dungeon spawns elsewhere with the same spawner.");
        meta.addPage("§4§lChaos§0\n" + "§0Breaking spawners to get loot causes consequences. Everytime a spawner is broken, Chaos increases in the world. Higher chaos causes enemies to become stronger, but also causes more loot, too.");
        meta.addPage("§c§lHallowed Chambers§0\n" + "§0To progress to Hardmode, the latter half of the game, players must travel to The Hallowed Chambers to fight The Wither. To open a portal, simply build a Wither as normal. This will open a portal, and destroy everything around it.");
        meta.addPage("§c§lHallowed Chambers (cont.)§0\n" + "§0This is not an easy fight. You should be in fully enchanted diamond gear with plenty of enchanted golden apples before attempting this.");
        meta.addPage("§c§lHallowed Chambers (cont.)§0\n" + "§0After everyone has entered, right click the red barrier twice to start. After going down the hallway, kill mobs in each of the 3 dungeons. One mob in each dungeon holds a key. Put these keys in the center platform.");
        meta.addPage("§4§lHardmode§0\n" + "§0After defeating The Wither for the first time, the world is irreversibly put into Hardmode. All mobs are significantly buffed. Thirst drains quicker, water doesn't quench as much, more materials are needed to purify, among other things.");
        meta.addPage("§4§lHardmode (cont.)§0\n" + "§0However, you now get access to crazier items, like Power Suits, Sabers, and Modules for the Puresouls, and Voidmatter Armor, Tools, and Artifacts for the Heretics.");
        meta.addPage("§9§lAugmented Mobs§0\n" + "§0In Hardmode, there is a small chance of an Augmented Mob to spawn in the place of a regular mob. This chance is increased with Chaos. These mobs are VERY powerful and could be considered mini-bosses.");


        book.setItemMeta(meta);
        return book;
    }

    public static TextComponent[] getTOCEntries(String[] entries, String[] pages, boolean cont) {
        ArrayList<TextComponent> tableComponents = new ArrayList<>();
        tableComponents.add(new TextComponent(cont ? "§lTable of Contents (cont.)§0\n" : "§lTable of Contents§0\n"));

        for (int i = 0; i < entries.length; i++) {
            TextComponent component = new TextComponent(entries[i]);
            component.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, pages[i]));

            TextComponent[] hover = new TextComponent[1];
            hover[0] = new TextComponent("Click to skip to " + ChatColor.stripColor(entries[i]));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));

            tableComponents.add(component);
        }

        TextComponent[] a = new TextComponent[0];
        return tableComponents.toArray(a);
    }
}
