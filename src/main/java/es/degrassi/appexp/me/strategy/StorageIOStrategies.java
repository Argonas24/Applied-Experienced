package es.degrassi.appexp.me.strategy;

import appeng.parts.automation.ForgeExternalStorageStrategy;
import appeng.parts.automation.StorageExportStrategy;
import appeng.parts.automation.StorageImportStrategy;
import es.degrassi.experiencelib.api.capability.ExperienceLibCapabilities;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

@UtilityClass
public final class StorageIOStrategies {
  public static StorageImportStrategy<IExperienceHandler, Long> createImport(ServerLevel level, BlockPos fromPos, Direction fromSide) {
    return new StorageImportStrategy<>(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        AExpHandlerStrategy.EXPERIENCE,
        level,
        fromPos,
        fromSide
    );
  }

  public static StorageExportStrategy<IExperienceHandler, Long> createExport(ServerLevel level, BlockPos fromPos, Direction fromSide) {
    return new StorageExportStrategy<>(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        AExpHandlerStrategy.EXPERIENCE,
        level,
        fromPos,
        fromSide
    );
  }

  public static ForgeExternalStorageStrategy<IExperienceHandler, Long> createExternalStorage(ServerLevel level, BlockPos fromPos, Direction fromSide) {
    return new ForgeExternalStorageStrategy<>(
        ExperienceLibCapabilities.EXPERIENCE.block(),
        AExpHandlerStrategy.EXPERIENCE,
        level,
        fromPos,
        fromSide
    );
  }
}
