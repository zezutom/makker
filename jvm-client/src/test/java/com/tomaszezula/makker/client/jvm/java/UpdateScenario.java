package com.tomaszezula.makker.client.jvm.java;

import java.nio.file.Path;
import java.util.Base64;

import static com.tomaszezula.makker.client.jvm.java.Config.*;

public class UpdateScenario {
    public static void main(String[] args) {
        fromBlueprint();
        fromEncodedBlueprint();
        fromFile();
    }

    private static void fromBlueprint() {
        makeClient.updateScenario(
                        473703,
                        updatedBlueprint
                )
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }

    private static void fromEncodedBlueprint() {
        makeClient.updateScenarioEncoded(
                        473703,
                        Base64.getEncoder().encodeToString(updatedBlueprint.getBytes())
                )
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }

    private static void fromFile() {
        makeClient.updateScenario(
                        473703,
                        Path.of(getResource("blueprint-updated.json"))
                )
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }
}
