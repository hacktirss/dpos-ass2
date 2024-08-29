package com.ass2.volumetrico.puntoventa.common;

import com.softcoatl.utils.logging.LogManager;

public class OmicromLogManager {

    private OmicromLogManager() {}

    public static void error(String message, Throwable ex) {
        LogManager.info(message);
        LogManager.error(ex);
        LogManager.debug("Trace", ex);
    }

    public static void error(Throwable ex) {
        LogManager.error(ex);
        LogManager.debug("Trace", ex);
    }
}
