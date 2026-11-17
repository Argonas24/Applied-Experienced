package es.degrassi.appexp;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.features.GridLinkables;
import appeng.api.features.P2PTunnelAttunement;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.storage.StorageCells;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;
import appeng.parts.automation.StackWorldBehaviors;
import es.degrassi.appexp.block.entity.ExperienceAcceptorEntity;
import es.degrassi.appexp.block.entity.ExperienceConverterEntity;
import es.degrassi.appexp.definition.AExpBlockEntities;
import es.degrassi.appexp.definition.AExpBlocks;
import es.degrassi.appexp.definition.AExpComponents;
import es.degrassi.appexp.definition.AExpConfig;
import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.definition.AExpMenus;
import es.degrassi.appexp.definition.AExpParts;
import es.degrassi.appexp.definition.AExpText;
import es.degrassi.appexp.me.cell.ExperienceCellHandler;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.appexp.me.misc.GenericStackExperienceStorage;
import es.degrassi.appexp.me.strategy.ExperienceContainerItemStrategy;
import es.degrassi.appexp.me.strategy.StorageIOStrategies;
import es.degrassi.appexp.part.ExperienceAcceptorPart;
import es.degrassi.appexp.part.ExperienceP2PTunnelPart;
import es.degrassi.appexp.weat.WeatMenu;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AppliedExperienced.MODID)
public class AppliedExperienced {
  public static final String MODID = "appex";
  public static final Logger LOGGER = LogManager.getLogger("Applied Experienced");

  public AppliedExperienced(final ModContainer CONTAINER, final IEventBus bus) {
    CONTAINER.registerConfig(ModConfig.Type.COMMON, AExpConfig.getSpec());

    AExpParts.init();
    AExpItems.initialize(bus);
    AExpBlocks.DR.register(bus);
    AExpBlockEntities.DR.register(bus);
    AExpComponents.initialize(bus);
    AExpMenus.initialize(bus);
    bus.addListener(ExperienceKeyType::register);
    bus.addListener(RegisterEvent.class, event -> {
      if (event.getRegistryKey().equals(BuiltInRegistries.MENU)) {
        Registry.register(BuiltInRegistries.MENU, WeatMenu.ID, WeatMenu.TYPE);
      }
    });

    StorageCells.addCellHandler(ExperienceCellHandler.INSTANCE);
    bus.addListener(AExpItems::initCellUpgrades);

    StackWorldBehaviors.registerImportStrategy(ExperienceKeyType.TYPE, StorageIOStrategies::createImport);
    StackWorldBehaviors.registerExportStrategy(ExperienceKeyType.TYPE, StorageIOStrategies::createExport);
    StackWorldBehaviors.registerExternalStorageStrategy(ExperienceKeyType.TYPE, StorageIOStrategies::createExternalStorage);

    ContainerItemStrategy.register(ExperienceKeyType.TYPE, AEExperienceKey.class, new ExperienceContainerItemStrategy());
    GenericSlotCapacities.register(ExperienceKeyType.TYPE, AEExperienceKey.MAX_EXPERIENCE);

    bus.addListener(GenericStackExperienceStorage::registerCapability);
    bus.addListener(ExperienceConverterEntity::registerCapability);
    bus.addListener(ExperienceAcceptorEntity::registerCapability);
    bus.addListener(ExperienceAcceptorPart::registerCapability);
    bus.addListener(RegisterCapabilitiesEvent.class, event -> {
      event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> new PoweredItemCapabilities(
          stack,
          AExpItems.EXPERIENCE_TERMINAL.asItem()
      ), AExpItems.EXPERIENCE_TERMINAL);
    });
    bus.addListener((RegisterPartCapabilitiesEvent event) -> {
      event.register(ExperienceLibCapabilities.EXPERIENCE.block(), (part, context) -> part.getExposedApi(), ExperienceP2PTunnelPart.class);
    });

    bus.addListener((FMLCommonSetupEvent event) -> {
      event.enqueueWork(this::initializeAttunement);
      event.enqueueWork(() -> {
        GridLinkables.register(AExpItems.EXPERIENCE_TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
      });
    });
  }

  private void initializeAttunement() {
    P2PTunnelAttunement.registerAttunementTag(AExpParts.EXPERIENCE_P2P_TUNNEL.get());
    P2PTunnelAttunement.registerAttunementApi(
        AExpParts.EXPERIENCE_P2P_TUNNEL,
        ExperienceLibCapabilities.EXPERIENCE.item(),
        AExpText.P2PAttunementExperience.text()
    );
  }

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }
}
