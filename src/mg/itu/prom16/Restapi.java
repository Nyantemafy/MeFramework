package mg.itu.prom16;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Restapi {
    String value() default "";
}
