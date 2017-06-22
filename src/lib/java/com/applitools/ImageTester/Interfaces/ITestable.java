package com.applitools.ImageTester.Interfaces;

import com.applitools.eyes.Eyes;

import java.io.IOException;

public interface ITestable {
    void run(Eyes eyes) throws IOException;

    String name();
}
