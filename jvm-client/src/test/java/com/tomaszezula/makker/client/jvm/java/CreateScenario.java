package com.tomaszezula.makker.client.jvm.java;

import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import static com.tomaszezula.makker.client.jvm.java.Config.*;

public class CreateScenario {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        fromBlueprint();
        fromEncodedBlueprint();
        fromFile();
    }

    private static void fromBlueprint() throws ExecutionException, InterruptedException {
        makeClient.createScenario(
                55228,
                22143,
                blueprint
        ).whenComplete((scenario, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(scenario);
            }
        }).get();
    }

    private static void fromEncodedBlueprint() throws ExecutionException, InterruptedException {
        makeClient.createScenarioEncoded(
                55228,
                22143,
                Base64.getEncoder().encodeToString(blueprint.getBytes())
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
                55228,
                22143,
                Path.of(getResource("blueprint.json"))
        ).whenComplete((scenario, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(scenario);
            }
        }).get();
    }
}
