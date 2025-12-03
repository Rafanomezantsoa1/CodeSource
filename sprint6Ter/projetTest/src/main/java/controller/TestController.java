package controller;

import annotation.AnnotationController;
import annotation.AnnotationUrl;

@AnnotationController(annotationName = "/test")
public class TestController {

    @AnnotationUrl(url = "/{id}")
    public String getDept(int id) {
        return "On a reçu l'id : " + id;
    }

    @AnnotationUrl(url = "/{id}/{nom}")
    public String getDept(int id, String nom) {
        return "On a reçu l'id : " + id + "de "+ nom + "\n";
    }
}
