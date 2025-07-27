package net.desolatesky.database;

public interface Saveable<T> {

    T createSnapshot();

}
