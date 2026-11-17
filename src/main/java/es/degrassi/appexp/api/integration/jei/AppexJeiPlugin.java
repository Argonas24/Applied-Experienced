package es.degrassi.appexp.api.integration.jei;

import appeng.api.ids.AEComponents;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.definition.AExpBlocks;
import es.degrassi.appexp.definition.AExpComponents;
import es.degrassi.appexp.definition.AExpItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

@JeiPlugin
public class AppexJeiPlugin implements IModPlugin {

  public AppexJeiPlugin() {
    if (isModLoaded("ae2jeiintegration")) {
      AE2JeiIntegrationHelper.register();
    }
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_1K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_4K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_16K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_64K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpItems.PORTABLE_EXPERIENCE_CELL_256K.asItem(),
        subTypeInterpreterForIngredientList(AEComponents.STORED_ENERGY));
    subtypeRegistry.registerSubtypeInterpreter(AExpBlocks.EXPERIENCE_CONVERTER.asItem(),
        subTypeInterpreterForIngredientList(AExpComponents.EXPERIENCE_AMOUNT));
  }

  private static ISubtypeInterpreter<ItemStack> subTypeInterpreterForIngredientList(DataComponentType<?>... types) {
    return new ISubtypeInterpreter<>() {
      @Override
      public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        if (context == UidContext.Recipe) {
          return null;
        }
        if (types.length == 1) {
          return ingredient.get(types[0]);
        } else {
          var result = new ArrayList<@Nullable Object>(types.length);
          for (var type : types) {
            result.add(ingredient.get(type));
          }
          return result;
        }
      }

      @Override
      public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return "";
      }
    };
  }

  public ResourceLocation getPluginUid() {
    return AppliedExperienced.id("jei_plugin");
  }

  private static boolean isModLoaded(String modId) {
    if (ModList.get() == null) {
      var list = LoadingModList.get().getMods().stream().map(ModInfo::getModId);
      Objects.requireNonNull(modId);
      return list.anyMatch(modId::equals);
    } else {
      return ModList.get().isLoaded(modId);
    }
  }
}
