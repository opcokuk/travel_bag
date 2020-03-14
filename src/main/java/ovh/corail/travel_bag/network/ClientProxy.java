package ovh.corail.travel_bag.network;

import com.google.common.reflect.Reflection;
import ovh.corail.travel_bag.event.ClientEventHandler;

public class ClientProxy implements IProxy {
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void preInit() {
        Reflection.initialize(ClientEventHandler.class);
    }

    @Override
    public void markConfigDirty() {
    }
}
