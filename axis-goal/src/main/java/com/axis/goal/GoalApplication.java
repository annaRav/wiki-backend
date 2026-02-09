package com.axis.goal;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class GoalApplication {

    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
