package growthcraft.bees;

import growthcraft.api.bees.BeesFluidTag;
import growthcraft.api.bees.BeesRegistry;
import growthcraft.api.bees.user.UserBeesConfig;
import growthcraft.api.bees.user.UserFlowerEntry;
import growthcraft.api.bees.user.UserFlowersConfig;
import growthcraft.api.core.fluids.TaggedFluidStacks;
import growthcraft.api.core.item.recipes.ShapelessMultiRecipe;
import growthcraft.api.core.log.GrcLogger;
import growthcraft.api.core.log.ILogger;
import growthcraft.api.core.module.ModuleContainer;
import growthcraft.bees.common.CommonProxy;
import growthcraft.bees.creativetab.CreativeTabsGrowthcraftBees;
import growthcraft.bees.init.GrcBeesFluids;
import growthcraft.bees.init.GrcBeesItems;
import growthcraft.bees.init.GrcBeesRecipes;
import growthcraft.cellar.GrowthCraftCellar;
import growthcraft.core.common.definition.BlockDefinition;
import growthcraft.core.GrcGuiProvider;
import growthcraft.core.integration.bop.BopPlatform;
import growthcraft.core.util.MapGenHelper;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(
	modid = GrowthCraftBees.MOD_ID,
	name = GrowthCraftBees.MOD_NAME,
	version = GrowthCraftBees.MOD_VERSION,
	dependencies = "required-after:Growthcraft@@VERSION@;required-after:Growthcraft|Cellar@@VERSION@;after:Forestry"
)
public class GrowthCraftBees
{
	public static final String MOD_ID = "Growthcraft|Bees";
	public static final String MOD_NAME = "Growthcraft Bees";
	public static final String MOD_VERSION = "@VERSION@";

	@Instance(MOD_ID)
	public static GrowthCraftBees instance;
	public static CreativeTabs tab;
	public static final GrcBeesItems items = new GrcBeesItems();
	public static final GrcBeesFluids fluids = new GrcBeesFluids();

	private final ILogger logger = new GrcLogger(MOD_ID);
	private final GrcBeesConfig config = new GrcBeesConfig();
	private final ModuleContainer modules = new ModuleContainer();
	private final UserBeesConfig userBeesConfig = new UserBeesConfig();
	private final UserFlowersConfig userFlowersConfig = new UserFlowersConfig();
	private final GrcBeesRecipes recipes = new GrcBeesRecipes();

	public static UserBeesConfig getUserBeesConfig()
	{
		return instance.userBeesConfig;
	}

	/**
	 * Only use this logger for logging GrowthCraftBees related items
	 */
	public static ILogger getLogger()
	{
		return instance.logger;
	}

	public static GrcBeesConfig getConfig()
	{
		return instance.config;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		config.setLogger(logger);
		config.load(event.getModConfigurationDirectory(), "growthcraft/bees.conf");
		modules.add(items);
		modules.add(fluids);
		modules.add(recipes);

		userBeesConfig.setConfigFile(event.getModConfigurationDirectory(), "growthcraft/bees/bees.json");
		modules.add(userBeesConfig);

		userFlowersConfig.setConfigFile(event.getModConfigurationDirectory(), "growthcraft/bees/flowers.json");
		modules.add(userFlowersConfig);

		//userHoneyConfig.setConfigFile(event.getModConfigurationDirectory(), "growthcraft/bees/honey.json");
		//modules.add(userHoneyConfig);

		if (config.enableThaumcraftIntegration) modules.add(new growthcraft.bees.integration.ThaumcraftModule());
		modules.add(CommonProxy.instance);
		if (config.debugEnabled)
		{
			BeesRegistry.instance().setLogger(logger);
			modules.setLogger(logger);
		}
		modules.freeze();
		tab = new CreativeTabsGrowthcraftBees("creative_tab_grcbees");

		modules.preInit();
		register();
	}

	private void register()
	{
		// TileEntities
		modules.register();
	}


	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		modules.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		userBeesConfig.loadUserConfig();
		userFlowersConfig.loadUserConfig();

		modules.postInit();
	}
}
