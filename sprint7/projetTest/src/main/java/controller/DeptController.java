package controller;

import annotation.AnnotationController;
import annotation.AnnotationUrl;

@AnnotationController(annotationName = "/dept")
public class DeptController {
    @AnnotationUrl(url = "/{id}")
    public String getDept() {
        return "Tongasoa ato amin'ny DeptController";
    }
}
