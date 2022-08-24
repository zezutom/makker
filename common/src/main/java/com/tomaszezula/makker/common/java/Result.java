package com.tomaszezula.makker.common.java;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

public class Result<T> {

    private final CompletableFuture<T> future;

    public Result(CompletableFuture<T> future) {
        this.future = future;
    }

    public <U> Result<U> then(Function<T, Result<U>> block) {
        return new Result<>(this.future.thenCompose(t -> block.apply(t).future));
    }

    public Result<T> onSuccess(Consumer<T> block) {
        return new Result<>(this.future.whenComplete((t, throwable) -> {
            if (throwable == null) {
                block.accept(t);
            }
        }));
    }

    public Result<T> onFailure(Consumer<Throwable> block) {
        return new Result<>(this.future.whenComplete((t, throwable) -> {
            if (throwable != null) {
                block.accept(throwable);
            }
        }));
    }

    public Result<T> logOnSuccess(Consumer<T> block) {
        return onSuccess(block);
    }

    public Result<T> logOnFailure(Consumer<Throwable> block) {
        return onFailure(block);
    }

    public T getOrNull() {
        try {
            return this.future.get();
        } catch (InterruptedException | ExecutionException ex) {
            return null;
        }
    }

    public T getOrThrow() {
        try {
            T t = this.future.get();
            if (t == null) {
                throw new RuntimeException("The value is null!");
            } else return t;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }
}
