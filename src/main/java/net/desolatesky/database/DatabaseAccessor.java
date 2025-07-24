package net.desolatesky.database;

import org.jetbrains.annotations.Nullable;

public interface DatabaseAccessor<I, O, ID> {

    void save(ID id, I data);

    void delete(ID id, I data);

    @Nullable O load(ID identifier);

}
