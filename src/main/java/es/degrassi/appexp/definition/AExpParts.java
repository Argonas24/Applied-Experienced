package es.degrassi.appexp.definition;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.part.ExperienceAcceptorPart;
import es.degrassi.appexp.part.ExperienceP2PTunnelPart;
import es.degrassi.appexp.part.reporting.ExperienceAccessTerminalPart;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

import static es.degrassi.appexp.definition.AExpItems.item;

@UtilityClass
public final class AExpParts {
  public static final ItemDefinition<PartItem<ExperienceAcceptorPart>> EXPERIENCE_ACCEPTOR = createPart(
      "ME Cable Experience Acceptor",
      AppliedExperienced.id("cable_experience_acceptor"),
      ExperienceAcceptorPart.class,
      ExperienceAcceptorPart::new
  );

  public static final ItemDefinition<PartItem<ExperienceP2PTunnelPart>> EXPERIENCE_P2P_TUNNEL = createPart(
      "Experience P2P Tunnel",
      AppliedExperienced.id("experience_p2p_tunnel"),
      ExperienceP2PTunnelPart.class,
      ExperienceP2PTunnelPart::new
  );

  public static final ItemDefinition<PartItem<ExperienceAccessTerminalPart>> EXPERIENCE_ACCESS_TERMINAL = createPart(
      "ME Experience Access Terminal",
      AppliedExperienced.id("experience_access_terminal"),
      ExperienceAccessTerminalPart.class,
      ExperienceAccessTerminalPart::new
  );

  private static <T extends IPart> ItemDefinition<PartItem<T>> createPart(
      String englishName,
      ResourceLocation id,
      Class<T> partClass,
      Function<IPartItem<T>, T> factory) {
    PartModels.registerModels(PartModelsHelper.createModels(partClass));
    return item(englishName, id, props -> new PartItem<>(props, partClass, factory));
  }

  public static void init() {}
}
