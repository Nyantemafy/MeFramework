package mg.itu.prom16;

public class Mapping {
    String nameClass;
    String nameMethod; 
    String annotationType;

    public void add(String n1, String n2, String n3) {
        this.nameClass = n1;
        this.nameMethod = n2;
        this.annotationType = n3;
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
}
