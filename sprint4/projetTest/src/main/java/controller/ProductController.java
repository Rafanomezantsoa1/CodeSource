package controller;
import annotation.AnnotationController;
import annotation.AnnotationUrl;

@AnnotationController(annotationName =  "/products")
public class ProductController {
    @AnnotationUrl(url = "/list")
    public String listProducts() {
        return "<h1>Liste des produits</h1><p>Produit A, Produit B, Produit C</p>";
    }

    @AnnotationUrl(url = "/favorites")
    public void favoritesProducts() {
        // Logic to list favorite products
    }

    
    
}
