package mg.itu.prom16;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER}) 
public @interface Param {
    String name();
}
