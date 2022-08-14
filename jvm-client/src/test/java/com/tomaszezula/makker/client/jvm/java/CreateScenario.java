package com.tomaszezula.makker.client.jvm.java;

import com.tomaszezula.makker.common.model.Blueprint;
import com.tomaszezula.makker.common.model.IndefiniteScheduling;

import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import static com.tomaszezula.makker.client.jvm.java.Config.blueprint;
import static com.tomaszezula.makker.client.jvm.java.Config.makeClient;
import static com.tomaszezula.makker.client.jvm.java.Config.getResource;

public class CreateScenario {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        fromBlueprint();
        fromEncodedBlueprint();
        fromFile();
    }

    private static void fromBlueprint() throws ExecutionException, InterruptedException {
        makeClient.createScenario(
                55228L,
                22143L,
                new Blueprint.Json(
                        blueprint,
                        false
                ),
                new IndefiniteScheduling()
        ).whenComplete((scenario, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(scenario);
            }
        }).get();
    }

    private static void fromEncodedBlueprint() throws ExecutionException, InterruptedException {
        makeClient.createScenario(
                55228L,
                22143L,
                new Blueprint.Json(
                        Base64.getEncoder().encodeToString(blueprint.getBytes()),
                        true
                ),
                new IndefiniteScheduling()
        ).whenComplete((scenario, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(scenario);
            }
        }).get();
    }

    private static void fromFile() throws ExecutionException, InterruptedException {
        makeClient.createScenario(
                55228L,
                22143L,
                Path.of(getResource("blueprint.json")),
                new IndefiniteScheduling()
        ).whenComplete((scenario, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(scenario);
            }
        }).get();
    }
}
