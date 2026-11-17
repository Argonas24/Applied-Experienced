package es.degrassi.appexp.weat;

import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class ItemWeat extends ItemWT {

  @Override
  public MenuType<?> getMenuType(ItemMenuHostLocator itemMenuHostLocator, Player player) {
    return WeatMenu.TYPE;
  }
}
