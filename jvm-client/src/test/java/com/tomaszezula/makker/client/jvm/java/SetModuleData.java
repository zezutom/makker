package com.tomaszezula.makker.client.jvm.java;

import com.tomaszezula.makker.client.jvm.java.model.ModuleUpdate;

import java.util.Arrays;

import static com.tomaszezula.makker.client.jvm.java.Config.makeClient;

public class SetModuleData {
    public static void main(String[] args) {
        singleModule();
        multipleModules();
    }

    private static void singleModule() {
        makeClient.setModuleData(
                        471310,
                        9,
                        "value",
                        "{{5.greeting}}"
                )
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }

    private static void multipleModules() {
        makeClient.setModuleData(
                        471310,
                        Arrays.asList(
                                new ModuleUpdate(9, "value", "{{5.greeting}}"),
                                new ModuleUpdate(12, "value", "{{5.greeting}}"),
                                new ModuleUpdate(13, "json", "{{5.greeting}}")
                        )
                )
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace);
    }
}
