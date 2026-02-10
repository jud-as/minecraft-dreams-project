package com.project.dreams;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@EventBusSubscriber(modid = Dreams.MOD_ID, value = Dist.CLIENT)
public class DreamsClient {
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Dreams.LOGGER.info("Dreams mod client setup complete.");
    }

}
