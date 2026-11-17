package es.degrassi.appexp.network;

import es.degrassi.appexp.AppliedExperienced;
import es.degrassi.appexp.network.client.CExperienceButtonClickedInPartPacket;
import es.degrassi.appexp.network.client.CExperienceButtonClickedPacket;
import es.degrassi.appexp.network.client.CInvertExperienceButtonClickedPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = AppliedExperienced.MODID)
public class PacketManager {
  @SubscribeEvent
  public static void register(final RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar registrar = event.registrar(AppliedExperienced.MODID);
    // TO CLIENT

    // TO SERVER
    registrar.playToServer(CExperienceButtonClickedPacket.TYPE, CExperienceButtonClickedPacket.CODEC, CExperienceButtonClickedPacket::handle);
    registrar.playToServer(CExperienceButtonClickedInPartPacket.TYPE, CExperienceButtonClickedInPartPacket.CODEC, CExperienceButtonClickedInPartPacket::handle);
    registrar.playToServer(CInvertExperienceButtonClickedPacket.TYPE, CInvertExperienceButtonClickedPacket.CODEC, CInvertExperienceButtonClickedPacket::handle);
  }
}
