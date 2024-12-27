package mg.itu.prom16;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private String value;
    private List<String> errors;

    public ValidationResult(String value, String initialError) {
        this.value = value;
        this.errors = new ArrayList<>();
        if (initialError != null) {
            this.errors.add(initialError);
        }
    }

    public String getValue() {
        return value;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
    public String toString() {
        if (hasErrors()) {
            return "ValidationResult{" +
                   "value='" + value + '\'' +
                   ", errors=" + errors +
                   '}';
        } else {
            return "ValidationResult{value='" + value + "', pas d'erreurs}";
        }
    }
}
