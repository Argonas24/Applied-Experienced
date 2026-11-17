package es.degrassi.appexp.util.inv;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import com.google.common.primitives.Ints;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.experiencelib.util.ExperienceUtils;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.player.Player;

@UtilityClass
public final class MEExperienceUtils {
  public static void addLevelToPlayer(MEStorage storage, int levelDiff, Player player) {
    int requestedLevel = player.experienceLevel + levelDiff;
    requestedLevel = Math.max(requestedLevel, 0);
    long playerXp = ExperienceUtils.getPlayerTotalXp(player);
    long requestedXp = ExperienceUtils.getXpFromLevel(requestedLevel) - playerXp;
    long awardXp = levelDiff > 0
        ? Math.min(storage.getAvailableStacks().get(AEExperienceKey.KEY), requestedXp)
        : -Math.min(Math.abs(requestedXp), storage.insert(AEExperienceKey.KEY, Math.abs(requestedXp), Actionable.SIMULATE, IActionSource.ofPlayer(player)));
    if (awardXp == 0) return;
    awardXP(storage, Ints.saturatedCast(awardXp), player);
  }

  public static void addAllLevelToPlayer(MEStorage storage, boolean give, Player player) {
    long awardXp;
    if (give) {
      awardXp = storage.extract(AEExperienceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE, IActionSource.ofPlayer(player));
    } else {
      awardXp = -Math.min(ExperienceUtils.getPlayerTotalXp(player), storage.insert(AEExperienceKey.KEY, Long.MAX_VALUE, Actionable.SIMULATE, IActionSource.ofPlayer(player)));
    }
    if (awardXp == 0) return;
    awardXP(storage, Ints.saturatedCast(awardXp), player);
  }

  public static void awardXP(MEStorage handler, int xpOrigin, Player player) {
    if (xpOrigin > 0) {
      handler.extract(AEExperienceKey.KEY, xpOrigin, Actionable.MODULATE, IActionSource.ofPlayer(player));
    } else {
      handler.insert(AEExperienceKey.KEY, Math.abs(xpOrigin), Actionable.MODULATE, IActionSource.ofPlayer(player));
    }
    player.giveExperiencePoints(xpOrigin);
  }
}
