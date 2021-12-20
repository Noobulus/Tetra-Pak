package mod.noobulus.tetrapak.networking;


import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityFloatParticlePacket implements ISimplePacket {
	private final int entityId;

	public EntityFloatParticlePacket(Entity entity) {
		entityId = entity.getId();
	}

	public EntityFloatParticlePacket(FriendlyByteBuf buffer) {
		entityId = buffer.readInt();
	}

	@Override
	public void writePacketData(FriendlyByteBuf buffer) {
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
				Vec3 pos = entity.position();
				Vec3 ppos = VecHelper.offsetRandomly(pos, entity.level.random, .5f);
				entity.level.addParticle(ParticleTypes.END_ROD, ppos.x, pos.y, ppos.z, 0, -.1f, 0);
			}
		});
		ctx.setPacketHandled(true);
	}
}
