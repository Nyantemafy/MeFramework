package sprint2.mg.itu.controller;

import sprint2.mg.itu.annotation.GET;

public class Controller1 {
    @GET("/example")
    public void exampleMethod() {
        // Logique de l'exemple
    }

    @GET("/hello")
    public void helloMethod() {
        // Logique pour l'URL /hello
    }
}
