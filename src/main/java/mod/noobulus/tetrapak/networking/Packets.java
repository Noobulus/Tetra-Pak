package mod.noobulus.tetrapak.networking;

import mod.noobulus.tetrapak.TetraPak;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;

public enum Packets {

	ENTITY_FLOAT_PARTICLE(EntityFloatParticlePacket.class, EntityFloatParticlePacket::new, PLAY_TO_CLIENT);

	public static final ResourceLocation CHANNEL_NAME = TetraPak.asId("network");
	public static final String NETWORK_VERSION = TetraPak.asId("1").toString();
	public static final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
		.serverAcceptedVersions(NETWORK_VERSION::equals)
		.clientAcceptedVersions(NETWORK_VERSION::equals)
		.networkProtocolVersion(() -> NETWORK_VERSION)
		.simpleChannel();

	private final LoadedPacket<?> packet;

	<T extends ISimplePacket> Packets(Class<T> packetClass, Function<PacketBuffer, T> factory, NetworkDirection direction) {
		packet = new LoadedPacket<>(packetClass, factory, direction);
	}

	public static void registerPackets() {
		for (Packets packet : values())
			packet.packet.register();
	}

	private static class LoadedPacket<T extends ISimplePacket> {
		private static int index = 0;
		BiConsumer<T, PacketBuffer> encoder;
		Function<PacketBuffer, T> decoder;
		BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
		Class<T> type;
		NetworkDirection direction;

		private LoadedPacket(Class<T> type, Function<PacketBuffer, T> factory, NetworkDirection direction) {
			encoder = T::writePacketData;
			decoder = factory;
			handler = T::handle;
			this.type = type;
			this.direction = direction;
		}

		private void register() {
			channel.messageBuilder(type, index++, direction)
				.encoder(encoder)
				.decoder(decoder)
				.consumer(handler)
				.add();
		}
	}
}
