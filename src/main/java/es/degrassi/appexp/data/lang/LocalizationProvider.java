package es.degrassi.appexp.data.lang;

import appeng.core.localization.LocalizationEnum;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import es.degrassi.appexp.AppliedExperienced;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LocalizationProvider implements DataProvider {
  public static List<LocalizationProvider> create(DataGenerator generator) {
    return List.of(
      new EnUsLangProvider(generator)
    );
  }
  private final Map<String, String> localizations = new HashMap<>();

  private final DataGenerator generator;
  private boolean wasSaved = false;
  public abstract Lang getLang();

  @Override
  public CompletableFuture<?> run(CachedOutput output) {
    generate();
    return save(output, localizations);
  }

  public LocalizationProvider withTranslations(Map<String, String> translations) {
    if (translations != null && !translations.isEmpty()) localizations.putAll(translations);
    return this;
  }

  @Override
  public String getName() {
    return "Localization (%s)".formatted(getLang().name());
  }
  public <T extends Enum<T> & LocalizationEnum> void addEnum(Class<T> localizedEnum) {
    addEnum(localizedEnum, LocalizationEnum::getEnglishText);
  }

  public <T extends Enum<T> & LocalizationEnum> void addEnum(Class<T> localizedEnum, Function<@NotNull T, String> parser) {
    for (var enumConstant : localizedEnum.getEnumConstants()) {
      var parsedResult = parser.apply(enumConstant);
      if (parsedResult == null) continue;
      add(enumConstant.getTranslationKey(), parsedResult);
    }
  }

  protected final void add(TagKey<?> tagKey, String name) {
    this.add(Tags.getTagTranslationKey(tagKey), name);
  }

  public void add(String key, String text) {
    Preconditions.checkState(!wasSaved, "Cannot add more translations after they were already saved");
    var previous = localizations.put(key, text);
    if (previous != null) {
      throw new IllegalStateException("Localization key " + key + " is already translated to: " + previous);
    }
  }

  protected abstract void generate();
  private CompletableFuture<?> save(CachedOutput cache, Map<String, String> localizations) {
    wasSaved = true;

    var path = this.generator.getPackOutput().getOutputFolder().resolve("assets/%s/lang/%s.json".formatted(AppliedExperienced.MODID, getLang().name()));

    // Dump the translation in ascending order
    var sorted = new TreeMap<>(localizations);
    var jsonLocalization = new JsonObject();
    for (var entry : sorted.entrySet()) {
      jsonLocalization.addProperty(entry.getKey(), entry.getValue());
    }

    return DataProvider.saveStable(cache, jsonLocalization, path);
  }
}
