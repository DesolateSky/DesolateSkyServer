package net.desolatesky;

import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        LoggerFactory.getLogger(Main.class).info("Args: {}", Arrays.toString(args));
        DesolateSkyServer.start(args);
    }

}
