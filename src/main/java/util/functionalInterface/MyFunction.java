package util.functionalInterface;

import java.util.function.Function;

/**
 * Represents a function that accepts non argument and produces a
 * result.
 *
 * This is a functional interface whose functional method
 * is {@link #apply()}.
 *
 * @param <R> the type of the result of the function
 *
 * @see Function
 */
@FunctionalInterface
public interface MyFunction<R> {

    /**
     * Applies this function to the given argument.
     *
     * @return the function result
     */
    R apply();
}
