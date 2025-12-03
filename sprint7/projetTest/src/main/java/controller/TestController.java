package controller;

import annotation.AnnotationController;
import annotation.GetMapping;
import annotation.PostMapping;
import model.ModelVue;

@AnnotationController(annotationName = "/test")
public class TestController {


     @GetMapping(url = "/{id}")
    public String getById(int id) {
        return "<h2>GET TestController : ID reçu = " + id + "</h2>";
    }

    @PostMapping(url = "/postForm")
    public String handleForm(String nom) {
        return "<h2>POST TestController :</h2>" +
                "<p>Nom = " + nom + "</p>";
    }

    @GetMapping(url = "/vue")
    public ModelVue showvue() {
        ModelVue mv = new ModelVue();
        mv.setVue("formulaire.jsp");
        mv.addData("message", "Bonjour depuis VueController !");
        return mv;

    }
}
