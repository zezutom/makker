package com.tomaszezula.makker.client.jvm.java;

import static com.tomaszezula.makker.client.jvm.java.Config.*;

public class Main {
    public static void main(String[] args) {
        makeClient.createScenario(
                        55228,
                        22143,
                        blueprint
                ).logOnSuccess(scenario ->
                        logger.info("Successfully created a new scenario: " + scenario)
                )
                .logOnFailure(throwable ->
                        logger.warn("Failed to create a new scenario", throwable)
                ).then(scenario ->
                        makeClient.getBlueprint(scenario.getId())
                                .logOnSuccess(blueprint ->
                                        logger.info("Received the scenario's blueprint" + blueprint)
                                )
                                .logOnFailure(throwable ->
                                        logger.warn("Failed to receive the scenario's blueprint", throwable)
                                )
                ).onSuccess(blueprint ->
                        System.out.println("Do stuff with " + blueprint)
                ).onFailure(throwable ->
                        System.out.println("You got an error. Handle it: " + throwable.getMessage())
                ).getOrThrow();
    }
}
