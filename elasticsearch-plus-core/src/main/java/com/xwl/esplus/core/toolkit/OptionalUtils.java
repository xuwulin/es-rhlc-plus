package com.xwl.esplus.core.toolkit;

import java.util.Objects;
import java.util.function.Function;

/**
 * 对JDK提供的Optional的自定义,个人认为其高频api ifPresent没有返回用起来不方便
 * 可避免写过多if-else 提升代码优雅
 *
 * @author xwl
 * @since 2022/3/12 17:09
 */
public final class OptionalUtils<T> {
    private static final OptionalUtils<?> EMPTY = new OptionalUtils<>();

    private final T value;

    private OptionalUtils() {
        this.value = null;
    }

    public static <T> OptionalUtils<T> empty() {
        @SuppressWarnings("unchecked")
        OptionalUtils<T> t = (OptionalUtils<T>) EMPTY;
        return t;
    }

    private OptionalUtils(T value) {
        this.value = Objects.requireNonNull(value);
    }

    public static <T> OptionalUtils<T> of(T value) {
        return new OptionalUtils<>(value);
    }

    public static <T> OptionalUtils<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public boolean isPresent() {
        return value != null;
    }


    public <U> OptionalUtils<U> ifPresent(Function<? super T, ? extends U> present, T otherValue) {
        Objects.requireNonNull(present);
        if (isPresent()) {
            return OptionalUtils.ofNullable(present.apply(value));
        } else {
            return OptionalUtils.ofNullable(present.apply(otherValue));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof OptionalUtils)) {
            return false;
        }

        OptionalUtils<?> other = (OptionalUtils<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value != null
                ? String.format("MyOptional[%s]", value)
                : "MyOptional.empty";
    }
}
