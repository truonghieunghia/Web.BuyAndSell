package groupbase.thn.web.libs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnNameAnnotation {
	public String ColumnName() default "";

    public Class<?> FieldType() default Object.class;
    
    public int MaxLength() default 0;
    
    public boolean isPrimary() default false;
    
    public boolean isAuto_increment() default false;
}
