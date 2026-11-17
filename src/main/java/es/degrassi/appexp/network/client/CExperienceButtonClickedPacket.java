package es.degrassi.appexp.network.client;

import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.block.entity.ExperienceConverterEntity;
import es.degrassi.appexp.client.Amount;
import es.degrassi.experiencelib.util.ExperienceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CExperienceButtonClickedPacket(BlockPos entityPos, Amount amount, boolean extraction) implements CustomPacketPayload {
  public static final Type<CExperienceButtonClickedPacket> TYPE = new Type<>(AppliedExperienced.id("experience_button_clicked"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CExperienceButtonClickedPacket> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      CExperienceButtonClickedPacket::entityPos,
      Amount.STREAM_CODEC,
      CExperienceButtonClickedPacket::amount,
      ByteBufCodecs.BOOL,
      CExperienceButtonClickedPacket::extraction,
      CExperienceButtonClickedPacket::new
  );

  @Override
  public Type<CExperienceButtonClickedPacket> type() {
    return TYPE;
  }

  public static void handle(CExperienceButtonClickedPacket packet, IPayloadContext context) {
    if (context.player() instanceof ServerPlayer player) {
      if (player.level().getBlockEntity(packet.entityPos) instanceof ExperienceConverterEntity entity) {
        if (packet.amount.isAll())
          ExperienceUtils.addAllLevelToPlayer(entity.getExperienceTank(), packet.extraction, player);
        else
          ExperienceUtils.addLevelToPlayer(entity.getExperienceTank(), packet.amount.getAmount(packet.extraction), player);
      }
    }
  }
}
