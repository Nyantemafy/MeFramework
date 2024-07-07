package controller;

import model.Employe;
import mg.itu.prom16.*;

@AnnotedController()
public class Controller1 {
    @AnnotedMth("mth1")
    public int methode1(){
        int val = 1;
        return val ;
    }

    @AnnotedMth("model1")
    public ModelView exampleModelViewMethod() {
        ModelView mv = new ModelView("/views/test.jsp");
        mv.addObject("message", "Hello from ModelView!");
        return mv;
    }
    @AnnotedMth("form1")
    public ModelView fomulaire_controller() {
        ModelView mv = new ModelView("/views/form.jsp");
        mv.addObject("formulaire", "Hello from ModelView!");
        return mv;
    }
    @AnnotedMth("formO1")
    public ModelView fomulaire_controllerO() {
        ModelView mv = new ModelView("/views/formO.jsp");
        mv.addObject("formulaire", "Hello from ModelView!");
        return mv;
    }
    // @AnnotedMth("submitForm")
    // public ModelView submitForm(@Param(name="name") String name, @Param(name="email") String email) {
    //     ModelView mv = new ModelView("/views/result.jsp");
    //     mv.addObject("name", name);
    //     mv.addObject("email", email);
    //     return mv;
    // }
    @AnnotedMth("submitObject")
    public ModelView submitFormObject(@Param(name="emp") Employe emp) {
        ModelView mv = new ModelView("/views/resultObject.jsp");
        mv.addObject("emp", emp);
        return mv;
    }
    @AnnotedMth("submitForm")
    public ModelView submitForm(String name, String email) {
        ModelView mv = new ModelView("/views/result.jsp");
        mv.addObject("name", name);
        mv.addObject("email", email);
        return mv;
    }
}

