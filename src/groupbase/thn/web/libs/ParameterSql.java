package groupbase.thn.web.libs;

public class ParameterSql {
	private Class<?> mClassType;
	private Object mValue;

	public ParameterSql(Class<?> classType, Object value) {
		setClassType(classType);
		setValue(value);
	}

	public Class<?> getClassType() {
		return mClassType;
	}

	public void setClassType(Class<?> classType) {
		this.mClassType = classType;
	}

	public Object getValue() {
		return mValue;
	}

	public void setValue(Object value) {
		this.mValue = value;
	}
}
