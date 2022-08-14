package com.tomaszezula.makker.client.jvm.java;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.tomaszezula.makker.client.jvm.java.Config.makeClient;

public class GetBlueprint {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        single();
        multiple();
    }

    private static void single() throws ExecutionException, InterruptedException {
        makeClient.getBlueprint(471310L).whenComplete((blueprint, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(blueprint);
            }
        }).get();
    }

    private static void multiple() throws ExecutionException, InterruptedException {
        makeClient.getBlueprints(Arrays.asList(473703L, 471310L)).whenComplete((blueprints, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(blueprints);
            }
        }).get();
    }
}
