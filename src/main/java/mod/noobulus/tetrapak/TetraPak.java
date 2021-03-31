package mod.noobulus.tetrapak;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("tetrapak")
public class TetraPak {
    public TetraPak() {
        MinecraftForge.EVENT_BUS.register(this);
        //MinecraftForge.EVENT_BUS.register(new DeforestEffect());
        MinecraftForge.EVENT_BUS.register(DeforestEffect.class);
        MinecraftForge.EVENT_BUS.register(VoidingEffect.class);
    }
}