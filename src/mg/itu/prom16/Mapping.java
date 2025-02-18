package mg.itu.prom16;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class Mapping {
    String nameClass;
    Set<VerbAction> verbActions ;
    String annotation;
    String role;
    
    public Mapping(String className, String annot) {
        this.nameClass = className;
        this.verbActions = new HashSet<>();
        this.annotation = annot;
    }

    public void add(String n1) {
        this.nameClass = n1;
        this.verbActions = new HashSet<>(); 
    }

    public String getKey() {
        return nameClass;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role){
        this.role = role;
    }

    public Set<VerbAction> getVerbActions() {
        return verbActions;
    }

    public boolean hasVerbAction(String verb, String action) {
        for (VerbAction v : verbActions) {
            if (v.getVerb().equalsIgnoreCase(verb) && v.getAction().equals(action)) {
                return true;
            }
        }
        return false;
    }

    public void addVerbAction(VerbAction verbAction) throws Exception {
        if (!this.verbActions.add(verbAction)) {
            throw new Exception("Duplicate method and verb combination: " + verbAction.getAction() + " with verb " + verbAction.getVerb());
        }
    }

    public VerbAction getVerbAction(String verb) {
        for (VerbAction verbAction : verbActions) {
            if (verbAction.getVerb().equalsIgnoreCase(verb)) {
                return verbAction;
            }
        }
        return null;
    }

    public Set<String> getAvailableVerbs() {
        return verbActions.stream()
                        .map(VerbAction::getVerb)
                        .collect(Collectors.toSet());
    }  
    
}
