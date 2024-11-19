package mg.itu.prom16;

import java.util.Objects;

public class VerbAction {
    private String action;
    private String verb;
    private Class<?>[] parameterTypes;

    public VerbAction(String action, String verb, Class<?>[] parameterTypes) {
        this.action = action;
        this.verb = verb;
        this.parameterTypes = parameterTypes != null ? parameterTypes : new Class<?>[0];
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes != null ? parameterTypes : new Class<?>[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerbAction that = (VerbAction) o;
        return action.equals(that.action) && verb.equals(that.verb);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(action, verb);
        return result;
    }
}
