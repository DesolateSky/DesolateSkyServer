package com.fisherl.desolatesky.util;

import org.jetbrains.annotations.Nullable;

public interface Result {

    Result SUCCESS = new Result() {
        @Override
        public boolean succeeded() {
            return true;
        }

        @Override
        public @Nullable Reason reason() {
            return null;
        }
    };

    static <T> Result.Typed<T> ofSuccess(@Nullable T value) {
        return new Result.Typed<>() {
            @Override
            public boolean succeeded() {
                return true;
            }

            @Override
            public @Nullable T value() {
                return value;
            }

            @Override
            public @Nullable Reason reason() {
                return null;
            }
        };
    }

    boolean succeeded();

    @Nullable Reason reason();

    interface Typed<T> extends Result {

        @Nullable T value();

    }

    interface Reason {

    }
}
