package com.tomaszezula.makker.client.jvm.java;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Config {
    public static Logger logger = LoggerFactory.getLogger("Main");

    public static final MakeClient makeClient = MakeClient.Companion.eu("REPLACE-WITH-YOUR-API-KEY");

    public static final String blueprint = readResource("blueprint.json");
    public static final String updatedBlueprint = readResource("blueprint-updated.json");

    static URI getResource(String name) {
        try {
            return Objects.requireNonNull(Config.class.getClassLoader().getResource(name)).toURI();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String readResource(String name) {
        try {
            return Files.readString(Path.of(getResource(name)));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
