package mod.noobulus.tetrapak.create.recipes;

import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class DeployerStoresLastRecipeOutputBehaviour extends TileEntityBehaviour {
	public static final BehaviourType<DeployerStoresLastRecipeOutputBehaviour> TYPE = new BehaviourType<>();
	public List<Item> lastProduced = new ArrayList<>();

	public DeployerStoresLastRecipeOutputBehaviour(SmartTileEntity te) {
		super(te);
	}

	public void setLastProduced(List<Item> lastProduced) {
		this.lastProduced = lastProduced;
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}
}
