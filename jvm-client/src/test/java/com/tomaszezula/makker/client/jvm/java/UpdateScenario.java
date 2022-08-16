package com.tomaszezula.makker.client.jvm.java;

import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import static com.tomaszezula.makker.client.jvm.java.Config.*;

public class UpdateScenario {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        fromBlueprint();
        fromEncodedBlueprint();
        fromFile();
    }

    private static void fromBlueprint() throws ExecutionException, InterruptedException {
        makeClient.updateScenario(
                473703,
                updatedBlueprint
        ).whenComplete((scenario, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(scenario);
            }
        }).get();
    }

    private static void fromEncodedBlueprint() throws ExecutionException, InterruptedException {
        makeClient.updateScenarioEncoded(
                473703,
                Base64.getEncoder().encodeToString(updatedBlueprint.getBytes())
        ).whenComplete((scenario, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(scenario);
            }
        }).get();
    }

    private static void fromFile() throws ExecutionException, InterruptedException {
        makeClient.updateScenario(
                473703,
                Path.of(getResource("blueprint-updated.json"))
        ).whenComplete((scenario, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(scenario);
            }
        }).get();
    }
}
