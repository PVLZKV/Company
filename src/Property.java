import java.lang.annotation.*;

@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface Property {
    String fieldName();
    String defaultValue() default "";
}
