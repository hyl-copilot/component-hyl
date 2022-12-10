package com.hyl.component.util.optional;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * 2022-12-10 12:58
 * create by hyl
 * desc:
 */
public final class JoinOptional<T> {

    private String joinKey;

    private final StringBuilder joinStr = new StringBuilder();
    private T value;

    public JoinOptional() {
    }

    private JoinOptional(T value, String joinKey) {
        this.joinKey = joinKey;
        this.value = Objects.requireNonNull(value);
    }

    public static <T> JoinOptional<T> ofValue(T value, String joinKey) {
        return new JoinOptional<>(Objects.requireNonNull(value), joinKey);
    }

    public static <T> JoinOptional<T> ofValue(T value) {
        return ofValue(value, "-");
    }

    @SafeVarargs
    public final <U> JoinOptional<T> join(Function<? super T, ? extends U>... mappers) {
        for (Function<? super T, ? extends U> mapper : mappers) {
            joinStr.append(Optional.ofNullable(joinKey).orElse(""))
                    .append(Optional.ofNullable(mapper).map(m -> m.apply(value).toString()).orElse(""));
        }
        return this;
    }

    public String get() {
        return joinStr.toString().replaceFirst(joinKey, "");
    }

}
