package com.acmerobotics.robomatic.hardware;

public class CachingSensor<T> implements CachingHardwareDevice{

    public interface UpdateCall<T> {
        T call();
    }

    private UpdateCall<T> call;
    private T value;

    public CachingSensor (UpdateCall<T> call) {
        this.call = call;
    }

    @Override
    public void update () {
        value = call.call();
    }

    public T getValue () {
        return value;
    }
}
