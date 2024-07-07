package mg.itu.prom16;

public class Mapping {
    String nameClass;
    String nameMethod; 

    public void add(String n1, String n2) {
        this.nameClass = n1;
        this.nameMethod = n2;
    }

    public String getValue() {
        return nameMethod;
    }

    public String getKey(){
        return nameClass;
    }
}
