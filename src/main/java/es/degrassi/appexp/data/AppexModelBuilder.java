package es.degrassi.appexp.data;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;

public interface AppexModelBuilder<T extends ModelBuilder<T>> {
  T uncheckedTexture(String key, ResourceLocation loc);
}
