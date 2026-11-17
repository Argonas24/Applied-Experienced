package es.degrassi.appexp.me.strategy;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.me.storage.ExternalStorageFacade;
import appeng.parts.automation.HandlerStrategy;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class AExpHandlerStrategy {
  public static final HandlerStrategy<IExperienceHandler, Long> EXPERIENCE = new HandlerStrategy<>(ExperienceKeyType.TYPE) {
    @Override
    public boolean isSupported(AEKey what) {
      return AEExperienceKey.is(what);
    }

    @Override
    public ExternalStorageFacade getFacade(IExperienceHandler handler) {
      return AExpExternalStorageFacade.of(handler);
    }

    @Override
    public @Nullable Long getStack(AEKey what, long amount) {
      if (!isSupported(what)) return null;
      return amount;
    }

    @Override
    public long insert(IExperienceHandler handler, AEKey what, long amount, Actionable mode) {
      if (!isSupported(what) || amount <= 0) return 0;
      long total = 0;
      for (int i = 0; i < handler.getTanks() && amount > 0; i++) {
        var insert = handler.receiveExperienceRecipe(i, amount, mode.isSimulate());
        total += insert;
        amount -= insert;
      }
      return total;
    }
  };
}
