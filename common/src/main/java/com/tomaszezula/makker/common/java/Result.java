package com.tomaszezula.makker.common.java;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

public class Result<A> {

    private final CompletableFuture<A> future;

    public Result(CompletableFuture<A> future) {
        this.future = future;
    }

    public <B> Result<B> then(Function<A, CompletableFuture<B>> block) {
        return new Result<>(this.future.thenCompose(block));
    }

    public Result<A> onSuccess(Consumer<A> block) {
        return new Result<>(this.future.whenComplete((a, throwable) -> {
            if (throwable == null) {
                block.accept(a);
            }
        }));
    }

    public Result<A> onFailure(Consumer<Throwable> block) {
        return new Result<>(this.future.whenComplete((a, throwable) -> {
            if (throwable != null) {
                block.accept(throwable);
            }
        }));
    }

    public Result<A> logOnSuccess(Consumer<A> block) {
        return onSuccess(block);
    }

    public Result<A> logOnFailure(Consumer<Throwable> block) {
        return onFailure(block);
    }

    public A getOrNull() {
        try {
            return this.future.get();
        } catch (InterruptedException | ExecutionException ex) {
            return null;
        }
    }

    public A getOrThrow() {
        try {
            A a = this.future.get();
            if (a == null) {
                throw new RuntimeException("The value is null!");
            } else return a;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }
}
