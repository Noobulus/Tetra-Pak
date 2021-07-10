package mod.noobulus.tetrapak.networking;


import com.simibubi.create.foundation.utility.VecHelper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityFloatParticlePacket implements ISimplePacket {
	private final int entityId;

	public EntityFloatParticlePacket(Entity entity) {
		entityId = entity.getId();
	}

	public EntityFloatParticlePacket(PacketBuffer buffer) {
		entityId = buffer.readInt();
	}

	@Override
	public void writePacketData(PacketBuffer buffer) {
		buffer.writeInt(this.entityId);
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context ctx = contextSupplier.get();

		ctx.enqueueWork(() -> {
			if (Minecraft.getInstance().level == null)
				return;
			Entity entity = Minecraft.getInstance().level.getEntity(entityId);
			if (entity != null && entity.level != null) {
				Vector3d pos = entity.position();
				Vector3d ppos = VecHelper.offsetRandomly(pos, entity.level.random, .5f);
				entity.level.addParticle(ParticleTypes.END_ROD, ppos.x, pos.y, ppos.z, 0, -.1f, 0);
			}
		});
		ctx.setPacketHandled(true);
	}
}
