package controller;

import  mg.itu.prom16.*;

@AnnotedController()
public class Controller2 {
    @AnnotedMth("mth2")
    public String methode2(){
        return "yesyes";
    }

    @AnnotedMth("model2")
    public ModelView exampleModelViewMethod() {
        ModelView mv = new ModelView("/views/test.jsp");
        mv.addObject("message", "mia");
        return mv;
    }
}

