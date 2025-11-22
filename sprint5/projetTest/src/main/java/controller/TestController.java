package controller;

import annotation.AnnotationController;
import annotation.AnnotationUrl;
import entities.Personne; 
import model.ModelVue;

@AnnotationController(annotationName =  "/products")
public class TestController {

    @AnnotationUrl(url ="/test/hello")
    public String hello() {
        return "<h1>Hello depuis TestController !</h1>";
    }

    @AnnotationUrl(url ="/test/vue")
    public ModelVue voirVue() {
        ModelVue mv = new ModelVue();
        mv.setVue("test.jsp");
        mv.addData("message", "Bonjour depuis VueController !");
        return mv;
    }

    @AnnotationUrl(url ="/test/objet")
    public Object getObject() {
        Personne p = new Personne("Lalaina", 19);
        return p;
    }
}
