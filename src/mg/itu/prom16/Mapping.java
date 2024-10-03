package mg.itu.prom16;

public class Mapping {
    String nameClass;
    String nameMethod; 
    String annotationType;
    String verb;

    public void add(String n1, String n2, String n3, String n4) {
        this.nameClass = n1;
        this.nameMethod = n2;
        this.annotationType = n3;
        this.verb = n4;
    }

    public String getValue() {
        return nameMethod;
    }

    public String getKey(){
        return nameClass;
    }

    public String getAnnotationType() {
        return annotationType;
    }
    public String getVerb() {
        return verb;
    }
}
