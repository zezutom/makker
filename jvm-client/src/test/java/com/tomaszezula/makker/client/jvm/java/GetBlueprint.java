package com.tomaszezula.makker.client.jvm.java;

import java.util.Arrays;

import static com.tomaszezula.makker.client.jvm.java.Config.makeClient;

public class GetBlueprint {
    public static void main(String[] args) {
        single();
        multiple();
    }

    private static void single() {
        makeClient.getBlueprint(471310)
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }

    private static void multiple() {
        makeClient.getBlueprints(Arrays.asList(473703, 471310))
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }
}
