package jclre.cache.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassFieldDefinitions {

    private List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();

    public void add( FieldDefinition fieldDefinition ) {
        fieldDefinitions.add( fieldDefinition );
    }

    public void remove( FieldDefinition fieldDefinition ) {
        fieldDefinitions.remove( fieldDefinition );
    }

    public List<FieldDefinition> getAll() {
        return Collections.unmodifiableList( fieldDefinitions );
    }

}
