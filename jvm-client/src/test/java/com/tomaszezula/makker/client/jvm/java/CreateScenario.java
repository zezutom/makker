package com.tomaszezula.makker.client.jvm.java;

import java.nio.file.Path;
import java.util.Base64;

import static com.tomaszezula.makker.client.jvm.java.Config.*;

public class CreateScenario {
    public static void main(String[] args) {
        fromBlueprint();
        fromEncodedBlueprint();
        fromFile();
    }

    private static void fromBlueprint() {
        makeClient.createScenario(
                        55228,
                        22143,
                        blueprint
                )
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }

    private static void fromEncodedBlueprint() {
        makeClient.createScenarioEncoded(
                        55228,
                        22143,
                        Base64.getEncoder().encodeToString(blueprint.getBytes())
                )
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }

    private static void fromFile() {
        makeClient.createScenario(
                        55228,
                        22143,
                        Path.of(getResource("blueprint.json"))
                )
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }
}
