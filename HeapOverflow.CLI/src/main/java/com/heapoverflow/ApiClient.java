package com.heapoverflow;

import java.io.IOException;

import com.heapoverflow.app.App;

public class ApiClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        App.init();
        App.run();
        App.cleanUp();
    }
}
