package es.degrassi.appexp.client.widgets;

import es.degrassi.appexp.client.Amount;
import net.minecraft.client.gui.components.Button;

public class ExperienceButton extends IconButton {
  public ExperienceButton(boolean extraction, Amount amount, Button.OnPress onPress) {
    super(onPress);
    setIcon(amount.getIcon(extraction));
    setMessage(amount.message(extraction));
  }
}
