package es.degrassi.appexp.data;

import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.data.event.TranslationLangEvent;
import es.degrassi.appexp.data.lang.LocalizationProvider;
import net.minecraft.Util;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AppliedExperienced.MODID)
public class AppliedExperiencedDataGenerators {
  @SubscribeEvent
  public static void onGatherData(GatherDataEvent event) {
    var pack = event.getGenerator().getVanillaPack(true);
    var existing = event.getExistingFileHelper();

    var localizations = LocalizationProvider.create(event.getGenerator());

    pack.addProvider(output -> new ItemModelProvider(output, existing));
    pack.addProvider(output -> new BlockStateModelProvider(output, existing));
    pack.addProvider(o -> new PartModelProvider(o, existing));

    var registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
    var blockTagsProvider = pack.addProvider(output -> new BlockTagsProvider(output, registries, existing));
    pack.addProvider(output -> new FluidTagsProvider(output, registries, existing));
    pack.addProvider(output -> new ItemTagsProvider(output, registries, blockTagsProvider.contentsGetter(), existing));
    pack.addProvider(output -> new RecipeProvider(output, registries));

    var blockDrops = new LootTableProvider.SubProviderEntry(LootProvider::new, LootContextParamSets.BLOCK);
    pack.addProvider(output -> new LootTableProvider(output, Set.of(), List.of(blockDrops), registries));
    var translationEvent = new TranslationLangEvent();
    ModLoader.postEventWrapContainerInModOrder(translationEvent);
    // Must run last
    for (var local : localizations) {
      pack.addProvider(o -> local.withTranslations(translationEvent.get(local.getLang())));
    }
  }
}
