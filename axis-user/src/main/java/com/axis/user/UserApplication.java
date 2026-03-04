package com.axis.user;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class UserApplication {

    public static void main(String... args) {
        Quarkus.run(args);
    }
}