package jclre.cache.field;

public class FieldDefinition {

    private String name;
    private Class type;
    private Object defaultValue;

    public FieldDefinition( String name, Class type, Object defaultValue ) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
