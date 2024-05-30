package com.matthew.plugin.connectfour.apis;

public interface ServerModule {

    /**
     * Allocate necessary resources into memory for the module
     */
    void setup();

    /**
     * Release any allocated resources that are still currently stored in memory
     */
    void teardown();
}
