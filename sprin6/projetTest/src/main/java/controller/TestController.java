package controller;

import annotation.AnnotationController;
import annotation.AnnotationUrl;
import model.ModelVue;

@AnnotationController(annotationName = "/test")
public class TestController {

    @AnnotationUrl(url = "/formulaire")
    public ModelVue testFormulaire() {
        ModelVue mv = new ModelVue();
        mv.setVue("formulaire.jsp");
        mv.addData("message", "Bonjour depuis VueController !");
        return mv;

    }

    @AnnotationUrl(url = "/arg")
    public String testArg(String nom) {
        return "<h2>Bonjour " + nom + "</h2>";
    }
}
