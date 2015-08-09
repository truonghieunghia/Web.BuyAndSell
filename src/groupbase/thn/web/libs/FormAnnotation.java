package groupbase.thn.web.libs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormAnnotation {
	public String Name() default "";

    public Class<?> FieldType() default Object.class;
}
