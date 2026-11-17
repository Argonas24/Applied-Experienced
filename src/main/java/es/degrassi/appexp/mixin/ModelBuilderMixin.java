package es.degrassi.appexp.mixin;

import com.google.common.base.Preconditions;
import es.degrassi.appexp.data.AppexModelBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ModelBuilder.class)
public abstract class ModelBuilderMixin<T extends ModelBuilder<T>> extends ModelFile implements AppexModelBuilder<T> {
  @Shadow
  @Final
  protected Map<String, String> textures;
  @Shadow
  protected abstract T self();

  protected ModelBuilderMixin(ResourceLocation location) {
    super(location);
  }

  @Override
  public T uncheckedTexture(String key, ResourceLocation texture) {
    Preconditions.checkNotNull(key, "Key must not be null");
    Preconditions.checkNotNull(texture, "Texture must not be null");
    textures.put(key, texture.toString());
    return this.self();
  }
}
