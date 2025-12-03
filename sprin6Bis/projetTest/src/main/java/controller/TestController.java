package controller;

import annotation.AnnotationController;
import annotation.AnnotationUrl;
import annotation.RequestParam;
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
    public String testArg(@RequestParam(name="nom") String anarana) {
        return "<h2>Bonjour " + anarana + "</h2>";
    }
}
