package controller;
import annotation.AnnotationController;
import annotation.AnnotationUrl;
import model.ModelVue;

@AnnotationController(annotationName =  "/products")
public class ProductController {
    @AnnotationUrl(url = "/list")
    public String listProducts() {
        return "<h1>Liste des produits</h1><p>Produit A, Produit B, Produit C</p>";
    }

    @AnnotationUrl(url = "/favorites")
    public void favoritesProducts() {
    }

    @AnnotationUrl(url = "/listes")
    public ModelVue listProduit() {
        ModelVue mv = new ModelVue("listProducts.jsp");
        mv.addData("title", "Liste des Produits");
        mv.addData("products", new String[]{"vary", "riz", "rice"});
        return mv;
    }
}
