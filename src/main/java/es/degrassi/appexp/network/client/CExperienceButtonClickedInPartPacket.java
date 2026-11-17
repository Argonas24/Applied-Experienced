package es.degrassi.appexp.network.client;

import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.client.Amount;
import es.degrassi.appexp.client.container.ExperienceAccessTermMenu;
import es.degrassi.appexp.util.inv.MEExperienceUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record CExperienceButtonClickedInPartPacket(int containerId, Amount amount, boolean extraction) implements CustomPacketPayload {
  public static final Type<CExperienceButtonClickedInPartPacket> TYPE = new Type<>(AppliedExperienced.id("experience_button_clicked_in_part"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CExperienceButtonClickedInPartPacket> CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      CExperienceButtonClickedInPartPacket::containerId,
      Amount.STREAM_CODEC,
      CExperienceButtonClickedInPartPacket::amount,
      ByteBufCodecs.BOOL,
      CExperienceButtonClickedInPartPacket::extraction,
      CExperienceButtonClickedInPartPacket::new
  );

  @Override
  public @Nonnull Type<CExperienceButtonClickedInPartPacket> type() {
    return TYPE;
  }

  public static void handle(CExperienceButtonClickedInPartPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      if (player.containerMenu.containerId == packet.containerId) {
        if (player.containerMenu instanceof ExperienceAccessTermMenu menu) {
          var delegating = menu.getEntity().getInventory();
          if (packet.amount.isAll()) MEExperienceUtils.addAllLevelToPlayer(delegating, packet.extraction, player);
          else MEExperienceUtils.addLevelToPlayer(delegating, packet.amount.getAmount(packet.extraction), player);
        }
      }
    }
  }
}
