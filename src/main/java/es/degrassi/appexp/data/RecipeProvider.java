package es.degrassi.appexp.data;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.items.storage.StorageTier;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import de.mari_023.ae2wtlib.wut.WTDefinitions;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.data.ae2wtlib.CombineBuilder;
import es.degrassi.appexp.data.ae2wtlib.UpgradeBuilder;
import es.degrassi.appexp.definition.AExpBlocks;
import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.definition.AExpParts;
import es.degrassi.appexp.weat.WeatDefinitions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

  public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, lookupProvider);
  }

  @Override
  protected void buildRecipes(@NotNull RecipeOutput output) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AExpItems.EXPERIENCE_CELL_HOUSING)
        .pattern("QRQ")
        .pattern("R R")
        .pattern("OOO")
        .define('Q', AEBlocks.QUARTZ_GLASS)
        .define('R', Tags.Items.DUSTS_REDSTONE)
        .define('O', Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_dusts/redstone", has(Tags.Items.DUSTS_REDSTONE))
        .save(output, AppliedExperienced.id("experience_cell_housing"));

    var housing = AExpItems.EXPERIENCE_CELL_HOUSING.get();

    AExpItems.getCells().forEach(cell -> {
      var component = cellComponent(cell.get().getTier());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, cell::get)
          .requires(housing)
          .requires(component)
          .unlockedBy("has_cell_component_" + cell.get().getTier().namePrefix(), has(cell))
          .unlockedBy("has_experience_housing", has(housing))
          .save(output);
      output.accept(
          cell.id().withSuffix("_disassembly"),
          new StorageCellDisassemblyRecipe(
              cell.asItem(),
              List.of(component.getDefaultInstance(), housing.getDefaultInstance())),
          null);
    });

    AExpItems.getPortables().forEach(portable -> {
      var component = cellComponent(portable.get().getTier());
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, portable::get)
          .requires(AEBlocks.ME_CHEST)
          .requires(component)
          .requires(AEBlocks.ENERGY_CELL)
          .requires(housing)
          .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(housing).getPath(), has(housing))
          .unlockedBy("has_energy_cell", has(AEBlocks.ENERGY_CELL))
          .save(output);
      output.accept(
          portable.id().withSuffix("_disassembly"),
          new StorageCellDisassemblyRecipe(
              portable.asItem(),
              List.of(
                  component.getDefaultInstance(),
                  housing.getDefaultInstance(),
                  AEBlocks.ME_CHEST.stack(),
                  AEBlocks.ENERGY_CELL.stack())),
          null);
    });

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AExpBlocks.EXPERIENCE_CONVERTER)
        .pattern("ppp")
        .pattern("ede")
        .pattern("ccc")
        .define('p', TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.withDefaultNamespace("planks")))
        .define('d', AEItems.CELL_COMPONENT_64K)
        .define('c', TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:glass_panes")))
        .define('e', Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_experience_bottle", has(Items.EXPERIENCE_BOTTLE))
        .save(output, AppliedExperienced.id("experience_converter"));

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AExpBlocks.EXPERIENCE_ACCEPTOR)
        .pattern("ede")
        .pattern("dad")
        .pattern("ede")
        .define('a', AEBlocks.ENERGY_ACCEPTOR)
        .define('d', AEItems.CELL_COMPONENT_1K)
        .define('e', Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_experience_bottle", has(Items.EXPERIENCE_BOTTLE))
        .save(output, AppliedExperienced.id("experience_acceptor_1"));

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AExpBlocks.EXPERIENCE_ACCEPTOR)
        .pattern("ded")
        .pattern("eae")
        .pattern("ded")
        .define('a', AEBlocks.ENERGY_ACCEPTOR)
        .define('d', AEItems.CELL_COMPONENT_1K)
        .define('e', Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_experience_bottle", has(Items.EXPERIENCE_BOTTLE))
        .save(output, AppliedExperienced.id("experience_acceptor_2"));

    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AExpParts.EXPERIENCE_ACCEPTOR)
        .requires(AExpBlocks.EXPERIENCE_ACCEPTOR)
        .unlockedBy("has_experience_acceptor", has(AExpBlocks.EXPERIENCE_ACCEPTOR))
        .save(output, AppliedExperienced.id("cable_experience_acceptor"));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AExpBlocks.EXPERIENCE_ACCEPTOR)
        .requires(AExpParts.EXPERIENCE_ACCEPTOR)
        .unlockedBy("has_experience_acceptor", has(AExpBlocks.EXPERIENCE_ACCEPTOR))
        .save(output, AppliedExperienced.id("experience_acceptor_from_part"));

    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AExpParts.EXPERIENCE_ACCESS_TERMINAL)
        .requires(ConventionTags.ILLUMINATED_PANEL)
        .requires(AEItems.ENGINEERING_PROCESSOR)
        .requires(Items.EXPERIENCE_BOTTLE)
        .unlockedBy("has_experience_bottle", has(Items.EXPERIENCE_BOTTLE))
        .save(output, AppliedExperienced.id("network/parts/terminals_experience_access"));

    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AExpItems.EXPERIENCE_TERMINAL)
        .pattern("a")
        .pattern("b")
        .pattern("c")
        .define('a', AEItems.WIRELESS_RECEIVER)
        .define('b', AExpParts.EXPERIENCE_ACCESS_TERMINAL)
        .define('c', AEBlocks.DENSE_ENERGY_CELL.asItem())
        .unlockedBy("has_wt", has(AEItems.WIRELESS_TERMINAL))
        .unlockedBy("has_expt", has(AExpParts.EXPERIENCE_ACCESS_TERMINAL))
        .save(output);

    List.of(
        WTDefinitions.PATTERN_ACCESS,
        WTDefinitions.CRAFTING,
        WTDefinitions.PATTERN_ENCODING
    ).forEach(wt -> CombineBuilder.of(wt, WeatDefinitions.EXPERIENCE)
        .unlockedBy("has_" + wt.terminalName(), has(wt.item()))
        .unlockedBy("has_xp_terminal", has(AExpItems.EXPERIENCE_TERMINAL))
        .save(
            output,
            AppliedExperienced.id(wt.terminalName().replace("pattern_", "").charAt(0) + "exp")
              .withPrefix("wireless_universal_terminal/")
        )
    );
    UpgradeBuilder.of(WeatDefinitions.EXPERIENCE)
        .unlockedBy("has_weat", has(AExpItems.EXPERIENCE_TERMINAL))
        .save(output, AppliedExperienced.id("upgrade_experience_access").withPrefix("wireless_universal_terminal/"));
  }

  private static Item cellComponent(StorageTier tier) {
    return tier.componentSupplier().get();
  }
}
