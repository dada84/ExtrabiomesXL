package extrabiomes;

import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.MinecraftForgeClient;
import net.minecraft.src.forge.NetworkMod;
import extrabiomes.api.ExtrabiomesBlock;
import extrabiomes.api.ExtrabiomesItem;
import extrabiomes.api.IPlugin;
import extrabiomes.api.PluginManager;
import extrabiomes.api.TerrainGenManager;
import extrabiomes.biomes.CustomBiomeManager;
import extrabiomes.blocks.BlockCustomFlower;
import extrabiomes.config.AchievementManager;
import extrabiomes.config.Config;
import extrabiomes.config.ConfigureCustomBiomes;
import extrabiomes.config.ConfigureVanillaBiomes;
import extrabiomes.terrain.TerrainGenerator;

public class Extrabiomes {

	private static final String VERSION = "2.2.0";
	private static final String PRIORITIES = "";

	public static int addFuel(int id, int damage) {
		if (id == ExtrabiomesBlock.sapling.blockID)
			return 100;
		return 0;
	}

	public static void addRenderer(Map map) {
		if (ExtrabiomesItem.scarecrow != null)
			map.put(EntityScarecrow.class, new RenderScarecrow(
					new ModelScarecrow(), 0.4F));
	}

	public static void generateSurface(World world, Random random, int x, int z) {
		TerrainGenerator.generateSurface(world, random, x, z);
	}

	public static String getPriorities() {
		return PRIORITIES;
	}

	public static String getVersion() {
		return VERSION;
	}

	private static void injectPlugins() {
		for (IPlugin plugin : PluginManager.plugins)
			if (plugin != null && plugin.isEnabled()) {
				Log.write("Injecting the " + plugin.getName()
						+ " plugin into ExtrabiomesXL.");
				plugin.inject();
			}
	}

	public static void load() {
		MinecraftForge.versionDetect("Extrabiomes XL", 3, 2, 5);
		if (ForgeHooks.getBuildVersion() < 126)
			Log.write("IMPORTANT: Due to FML bugs, you must use a forge build of 126 or greater.");
		preloadTexture("/extrabiomes/extrabiomes.png");

		Config.load();
	}

	public static void modsLoaded(NetworkMod mod) {
		Config.modsLoaded();

		if (ExtrabiomesItem.scarecrow != null) {
			Proxy.addRecipe(new ItemStack(ExtrabiomesItem.scarecrow, 1),
					new Object[] { " a ", "cbc", " c ", Character.valueOf('a'),
							Block.pumpkin, Character.valueOf('b'), Block.melon,
							Character.valueOf('c'), Item.stick });
		}

		if (ExtrabiomesBlock.redRock != null)
			Proxy.addShapelessRecipe(new ItemStack(Item.clay, 4), new Object[] {
					new ItemStack(ExtrabiomesBlock.redRock),
					new ItemStack(Item.bucketWater),
					new ItemStack(Item.bucketWater),
					new ItemStack(Item.bucketWater) });

		if (ExtrabiomesBlock.crackedSand != null)
			Proxy.addShapelessRecipe(new ItemStack(Block.sand), new Object[] {
					new ItemStack(ExtrabiomesBlock.crackedSand),
					new ItemStack(Item.bucketWater) });

		if (ExtrabiomesBlock.flower != null) {
			ModLoader.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 12),
					new Object[] { new ItemStack(ExtrabiomesBlock.flower, 1,
							BlockCustomFlower.metaHydrangea) });
			ModLoader.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 14),
					new Object[] { new ItemStack(ExtrabiomesBlock.flower, 1,
							BlockCustomFlower.metaOrange) });
			ModLoader.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 13),
					new Object[] { new ItemStack(ExtrabiomesBlock.flower, 1,
							BlockCustomFlower.metaPurple) });
			ModLoader.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 7),
					new Object[] { new ItemStack(ExtrabiomesBlock.flower, 1,
							BlockCustomFlower.metaWhite) });
		}

		Config.addNames();

		if (ExtrabiomesItem.scarecrow != null) {
			ExtrabiomesEntity.scarecrow = 127;
			Proxy.registerEntityID(EntityScarecrow.class, "scarecrow",
					ExtrabiomesEntity.scarecrow);
			Proxy.registerEntity(EntityScarecrow.class, mod,
					ExtrabiomesEntity.scarecrow);
		}

		ConfigureVanillaBiomes.disableVanillaBiomes();

		if (TerrainGenManager.blockWasteland != null)
			CustomBiomeManager.wasteland.topBlock = CustomBiomeManager.wasteland.fillerBlock = (byte) TerrainGenManager.blockWasteland.blockID;
		if (TerrainGenManager.blockWasteland != null)
			CustomBiomeManager.mountainRidge.topBlock = CustomBiomeManager.mountainRidge.fillerBlock = (byte) TerrainGenManager.blockMountainRidge.blockID;

		ConfigureCustomBiomes.enableCustomBiomes();

		injectPlugins();
	}

	public static void preloadTexture(String filename) {
		MinecraftForgeClient.preloadTexture(filename);
	}

	public static void takenFromCrafting(EntityPlayer player,
			ItemStack itemstack, IInventory var3) {
		AchievementManager.craftingAchievement(player, itemstack);
	}

	public static boolean onTickInGame(float var1, Minecraft var2) {
		// For future expansion (returning false removes this hook from the
		// loop)
		return false;
	}
}