package jclre.tool.helper;

import javassist.CtField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldsCompareResult {

    private List<CtField> addedFields = new ArrayList<CtField>();
    private List<CtField> removedFields = new ArrayList<CtField>();

    public void addedField( CtField addedField ) {
        addedFields.add( addedField );
    }

    public void removedField( CtField removedField ) {
        removedFields.add( removedField );
    }

    public List<CtField> getAddedFields() {
        return Collections.unmodifiableList( addedFields );
    }

    public List<CtField> getRemovedFields() {
        return Collections.unmodifiableList( removedFields );
    }
}
