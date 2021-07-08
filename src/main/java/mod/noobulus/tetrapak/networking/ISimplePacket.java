package mod.noobulus.tetrapak.networking;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface ISimplePacket {
	void writePacketData(PacketBuffer buffer);

	void handle(Supplier<NetworkEvent.Context> contextSupplier);
}
