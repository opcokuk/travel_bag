package ovh.corail.travel_bag.network;

public interface IProxy {
    void updateConfigIfDirty();
    void markConfigDirty();
}
