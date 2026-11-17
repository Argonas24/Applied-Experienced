package es.degrassi.appexp.data;

import appeng.core.definitions.ItemDefinition;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.definition.AExpTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {

  public ItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                          CompletableFuture<TagLookup<Block>> blockTagsProvider, ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, blockTagsProvider, AppliedExperienced.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(AExpTags.Items.CURIO)
        .add(AExpItems.EXPERIENCE_TERMINAL.asItem())
        .addAll(AExpItems.getPortables()
            .stream()
            .map(ItemDefinition::holder)
            .map(Holder::unwrapKey)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList()
        );
  }
}
