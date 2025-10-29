package mg.teste;

import mg.framework.annotations.HandleURL;

public class Teste {
    @HandleURL("/hello")
    public void hello() {}

    @HandleURL("/teste")
    public void about() {}
}