package net.desolatesky.database;

import org.bson.Document;
import org.jetbrains.annotations.UnknownNullability;

public interface MongoCodec<I, O, CONTEXT> {

    /**
     *
     * @param input
     * @param document Document that is used to store the data
     */
    void write(I input, Document document);

    @UnknownNullability
    O read(Document document, @UnknownNullability CONTEXT context);

}
