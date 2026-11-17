package es.degrassi.appexp.data.ae2wtlib;

import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.wut.recipe.Upgrade;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpgradeBuilder implements RecipeBuilder {
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
  private final RecipeCategory category = RecipeCategory.MISC;

  private final WTDefinition terminal;

  public static UpgradeBuilder of(WTDefinition terminal) {
    return new UpgradeBuilder(terminal);
  }

  @Override
  public UpgradeBuilder unlockedBy(String name, Criterion<?> criterion) {
    this.criteria.put(name, criterion);
    return this;
  }

  @Override
  public UpgradeBuilder group(@Nullable String s) {
    return this;
  }

  @Override
  public Item getResult() {
    return AE2wtlibItems.UNIVERSAL_TERMINAL;
  }

  @Override
  public void save(RecipeOutput recipeOutput, ResourceLocation id) {
    this.ensureValid(id);
    Advancement.Builder advancement$builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
    Objects.requireNonNull(advancement$builder);
    criteria.forEach(advancement$builder::addCriterion);

    var recipe = new Upgrade(Ingredient.of(terminal.item()), terminal);

    recipeOutput.accept(id, recipe, advancement$builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
  }

  private void ensureValid(ResourceLocation id) {
    if (this.criteria.isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + id);
    }
  }
}
