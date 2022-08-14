package com.tomaszezula.makker.client.jvm.java;

import com.tomaszezula.makker.client.jvm.java.model.ModuleUpdate;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.tomaszezula.makker.client.jvm.java.Config.makeClient;

public class SetModuleData {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        singleModule();
        multipleModules();
    }

    private static void singleModule() throws ExecutionException, InterruptedException {
        makeClient.setModuleData(
                471310L,
                9L,
                "value",
                "{{5.greeting}}"
        ).whenComplete((blueprint, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(blueprint);
            }
        }).get();
    }

    private static void multipleModules() throws ExecutionException, InterruptedException {
        makeClient.setModuleData(
                471310L,
                Arrays.asList(
                        new ModuleUpdate(9L, "value", "{{5.greeting}}"),
                        new ModuleUpdate(12L, "value", "{{5.greeting}}"),
                        new ModuleUpdate(13L, "json", "{{5.greeting}}")
                )
        ).whenComplete((blueprint, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.println(blueprint);
            }
        }).get();
    }
}
