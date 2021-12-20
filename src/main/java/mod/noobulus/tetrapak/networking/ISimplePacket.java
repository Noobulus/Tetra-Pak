package mod.noobulus.tetrapak.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface ISimplePacket {
	void writePacketData(FriendlyByteBuf buffer);

	void handle(Supplier<NetworkEvent.Context> contextSupplier);
}
