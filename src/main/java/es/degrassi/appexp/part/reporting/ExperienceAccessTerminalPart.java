package es.degrassi.appexp.part.reporting;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.api.storage.SupplierStorage;
import appeng.api.util.IConfigManager;
import appeng.items.parts.PartModels;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractDisplayPart;
import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.api.storage.IExperienceAccessTermMenuHost;
import es.degrassi.appexp.definition.AExpMenus;
import es.degrassi.appexp.me.key.AEExperienceKey;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ExperienceAccessTerminalPart extends AbstractDisplayPart implements IExperienceAccessTermMenuHost {
  @Getter
  private final IConfigManager configManager = IConfigManager.builder(() -> {
        getHost().markForSave();
      })
      .build();
  @PartModels
  public static final ResourceLocation MODEL_OFF = AppliedExperienced.id("part/experience_access_terminal_off");
  @PartModels
  public static final ResourceLocation MODEL_ON = AppliedExperienced.id("part/experience_access_terminal_on");

  public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
  public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
  public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

  public ExperienceAccessTerminalPart(IPartItem<?> partItem) {
    super(partItem, true);
  }

  @Override
  public boolean onUseWithoutItem(Player player, Vec3 pos) {
    if (!super.onUseWithoutItem(player, pos) && !isClientSide()) {
      MenuOpener.open(AExpMenus.EXPERIENCE_ACCESS_TERM_MENU, player, MenuLocators.forPart(this));
    }
    return true;
  }

  @Override
  public IPartModel getStaticModels() {
    return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
  }

  @Override
  public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
    super.writeToNBT(data, registries);
    configManager.writeToNBT(data, registries);
  }

  @Override
  public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
    super.readFromNBT(data, registries);
    configManager.readFromNBT(data, registries);
  }

  @Override
  public MEStorage getInventory() {
    return new SupplierStorage(() -> {
      var grid = getMainNode().getGrid();
      if (grid != null) {
        return grid.getStorageService().getInventory();
      }
      return null;
    });
  }

  @Override
  public ILinkStatus getLinkStatus() {
    return ILinkStatus.ofManagedNode(getMainNode());
  }

  @Override
  public long getExperience() {
    if (!getLinkStatus().connected()) return 0;
    return getInventory()
        .getAvailableStacks()
        .get(AEExperienceKey.KEY);
  }

  @Override
  public long getExperienceCapacity() {
    if (!getLinkStatus().connected()) return 0;
    return getExperience() + getInventory()
        .insert(AEExperienceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE, IActionSource.ofMachine(this));
  }
}
