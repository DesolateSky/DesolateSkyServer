package net.desolatesky.config;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.CommentedConfigurationNodeIntermediary;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.ConfigurationVisitor;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.RepresentationHint;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigNode implements CommentedConfigurationNodeIntermediary<ConfigNode> {

    private final CommentedConfigurationNode internalNode;

    public ConfigNode(CommentedConfigurationNode internalNode) {
        this.internalNode = internalNode;
    }

    @Override
    public @Nullable String comment() {
        return this.internalNode.comment();
    }

    @Override
    public ConfigNode comment(@Nullable String comment) {
        return new ConfigNode(this.internalNode.comment(comment));
    }

    @Override
    public ConfigNode commentIfAbsent(String comment) {
        return new ConfigNode(this.internalNode.commentIfAbsent(comment));
    }

    @Override
    public ConfigNode self() {
        return this;
    }

    @Override
    public ConfigNode appendListNode() {
        return new ConfigNode(this.internalNode.appendListNode());
    }

    @Override
    public ConfigNode copy() {
        return new ConfigNode(this.internalNode.copy());
    }

    @Override
    public ConfigNode node(Object... path) {
        return new ConfigNode(this.internalNode.node(path));
    }

    @Override
    public ConfigNode node(Iterable<?> path) {
        return new ConfigNode(this.internalNode.node(path));
    }

    @Override
    public @Nullable ConfigNode parent() {
        final CommentedConfigurationNode parentNode = this.internalNode.parent();
        if (parentNode == null) {
            return null;
        }
        return new ConfigNode(parentNode);
    }

    @Override
    public ConfigNode from(ConfigurationNode other) {
        return new ConfigNode(this.internalNode.from(other));
    }

    @Override
    public ConfigNode mergeFrom(ConfigurationNode other) {
        return new ConfigNode(this.internalNode.mergeFrom(other));
    }

    @Override
    public ConfigNode set(@Nullable Object value) throws SerializationException {
        return new ConfigNode(this.internalNode.set(value));
    }

    @Override
    public ConfigNode raw(@Nullable Object value) {
        return new ConfigNode(this.internalNode.raw(value));
    }

    @Override
    public List<ConfigNode> childrenList() {
        return this.internalNode.childrenList().stream()
                .map(ConfigNode::new)
                .toList();
    }

    @Override
    public Map<Object, ConfigNode> childrenMap() {
        return this.internalNode.childrenMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new ConfigNode(entry.getValue())));
    }

    @Override
    public <V> ConfigNode hint(RepresentationHint<V> hint, @Nullable V value) {
        return new ConfigNode(this.internalNode.hint(hint, value));
    }

    @Override
    public @Nullable Object key() {
        return this.internalNode.key();
    }

    @Override
    public NodePath path() {
        return this.internalNode.path();
    }

    @Override
    public boolean hasChild(Object... path) {
        return this.internalNode.hasChild(path);
    }

    @Override
    public boolean hasChild(Iterable<?> path) {
        return this.internalNode.hasChild(path);
    }

    @Override
    public boolean virtual() {
        return this.internalNode.virtual();
    }

    @Override
    public ConfigurationOptions options() {
        return this.internalNode.options();
    }

    @Override
    public boolean isNull() {
        return this.internalNode.isNull();
    }

    @Override
    public boolean isList() {
        return this.internalNode.isList();
    }

    @Override
    public boolean isMap() {
        return this.internalNode.isMap();
    }

    @Override
    public boolean empty() {
        return this.internalNode.empty();
    }

    @Override
    public @Nullable Object get(Type type) throws SerializationException {
        return this.internalNode.get(type);
    }

    public @Nullable <T> T get(Class<T> typeClass) throws SerializationException {
        final Object object = this.internalNode.get(typeClass);
        if (object == null) {
            return null;
        }
        if (!typeClass.isInstance(object)) {
            throw new SerializationException("Expected type " + typeClass.getName() + " but got " + object.getClass().getName());
        }
        return typeClass.cast(object);
    }

    public <T> T getNonNull(Class<T> typeClass) throws SerializationException {
        final T value = this.get(typeClass);
        if (value == null) {
            throw new SerializationException("Expected non-null value for type " + typeClass.getName());
        }
        return value;
    }

    @Override
    public @Nullable Object raw() {
        return this.internalNode.raw();
    }

    @Override
    public @Nullable Object rawScalar() {
        return this.internalNode.rawScalar();
    }

    @Override
    public boolean removeChild(Object key) {
        return this.internalNode.removeChild(key);
    }

    @Override
    public <S, T, E extends Exception> T visit(ConfigurationVisitor<S, T, E> visitor, S state) throws E {
        return this.internalNode.visit(visitor, state);
    }

    @Override
    public <S, T> T visit(ConfigurationVisitor.Safe<S, T> visitor, S state) {
        return this.internalNode.visit(visitor, state);
    }

    @Override
    public <V> @Nullable V hint(RepresentationHint<V> hint) {
        return this.internalNode.hint(hint);
    }

    @Override
    public <V> @Nullable V ownHint(RepresentationHint<V> hint) {
        return this.internalNode.ownHint(hint);
    }

    @Override
    public Map<RepresentationHint<?>, ?> ownHints() {
        return this.internalNode.ownHints();
    }
}
