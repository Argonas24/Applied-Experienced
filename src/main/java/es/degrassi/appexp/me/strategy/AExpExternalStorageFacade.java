package es.degrassi.appexp.me.strategy;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.me.storage.ExternalStorageFacade;
import com.google.common.primitives.Ints;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.appexp.me.key.ExperienceKeyType;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@UtilityClass
public final class AExpExternalStorageFacade {
  public static ExternalStorageFacade of(IExperienceHandler handler) {
    return new ExperienceHandlerFacade(handler);
  }

  @RequiredArgsConstructor
  private static class ExperienceHandlerFacade extends ExternalStorageFacade {
    private final IExperienceHandler handler;

    protected @Nullable Long toResource(AEKey key, long amount) {
      return (key instanceof AEExperienceKey && amount > 0) ? amount : null;
    }

    @Override
    public int getSlots() {
      return handler.getTanks();
    }

    @Override
    public @Nullable GenericStack getStackInSlot(int slot) {
      var amount = handler.getExperience();
      return amount == 0 ?  null : new GenericStack(AEExperienceKey.KEY, amount);
    }

    @Override
    public AEKeyType getKeyType() {
      return ExperienceKeyType.TYPE;
    }

    @Override
    protected int insertExternal(AEKey what, int amount, Actionable mode) {
      var resource = toResource(what, amount);
      if (resource == null) return 0;
      int total = 0;
      for (int i = 0; i < handler.getTanks() && amount > 0; i++) {
        var insert = Ints.saturatedCast(handler.receiveExperienceRecipe(i, amount, mode.isSimulate()));
        total += insert;
        amount -= insert;
      }
      return total;
    }

    @Override
    protected int extractExternal(AEKey what, int amount, Actionable mode) {
      var resource = toResource(what, amount);
      if (resource == null) return 0;
      int total = 0;
      for (int i = 0; i < handler.getTanks() && amount > 0; i++) {
        var extract = Ints.saturatedCast(handler.extractExperienceRecipe(i, amount, mode.isSimulate()));
        total += extract;
        amount -= extract;
      }
      return total;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
      for (int i = 0; i < handler.getTanks(); i++) {
        // Skip resources that cannot be extracted if that filter is enabled
        var stack = handler.extractExperienceRecipe(i, Long.MAX_VALUE, true);
        if (stack == 0) continue;

        if (extractableOnly) {
          // Try to determine whether the resource is extractable
          var extracted = handler.extractExperienceRecipe(i, 1, true);
          // Try again in case the handler only allows extracting the resource in its entirety (i.e.
          // cauldrons)
          if (extracted == 0) {
            extracted = handler.extractExperienceRecipe(i, 1, true);
          }
          if (extracted == 0) continue; // Skip unextractable slots
        }

        out.add(AEExperienceKey.KEY, stack);
      }
    }

    @Override
    public boolean containsAnyFuzzy(Set<AEKey> keys) {
      for (int i = 0; i < handler.getTanks(); i++) {
        var what = handler.extractExperienceRecipe(i, Long.MAX_VALUE, true) > 0 ? AEExperienceKey.KEY : null;
        if (what != null) {
          if (keys.contains(what.dropSecondary())) {
            return true;
          }
        }
      }
      return false;
    }
  }
}
