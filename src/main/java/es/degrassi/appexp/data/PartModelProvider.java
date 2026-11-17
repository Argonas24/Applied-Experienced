package es.degrassi.appexp.data;

import appeng.core.AppEng;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.definition.AExpParts;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;

public class PartModelProvider extends ModelProvider<BlockModelBuilder> {
  public PartModelProvider(PackOutput packOutput, ExistingFileHelper helper) {
    super(packOutput, AppliedExperienced.MODID, "part", BlockModelBuilder::new, helper);
  }

  @Override
  protected void registerModels() {
    List.of("on", "off").forEach(variant -> {
      withExistingParent("part/%s_%s".formatted(AExpParts.EXPERIENCE_ACCESS_TERMINAL.id().getPath(), variant), AppEng.makeId("part/crafting_terminal_%s".formatted(variant)))
          .texture("lightsBright", AExpParts.EXPERIENCE_ACCESS_TERMINAL.id().withPrefix("part/").withSuffix("_bright"))
          .texture("lightsMedium", AExpParts.EXPERIENCE_ACCESS_TERMINAL.id().withPrefix("part/").withSuffix("_medium"))
          .texture("lightsDark", AExpParts.EXPERIENCE_ACCESS_TERMINAL.id().withPrefix("part/").withSuffix("_dark"));
    });
  }

  @Override
  public String getName() {
    return "Part Models: " + modid;
  }
}
