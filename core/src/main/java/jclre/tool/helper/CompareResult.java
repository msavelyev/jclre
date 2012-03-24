package jclre.tool.helper;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompareResult<T> {

    private List<T> addedItems = new ArrayList<T>();
    private List<T> removedItems = new ArrayList<T>();

    public void addedItem( T addedItem ) {
        addedItems.add( addedItem );
    }

    public void removedItem( T removedItem ) {
        removedItems.add( removedItem );
    }

    public List<T> getAddedItems() {
        return Collections.unmodifiableList( addedItems );
    }

    public List<T> getRemovedItems() {
        return Collections.unmodifiableList( removedItems );
    }

    @Override
    public String toString() {
        return "CompareResult{" +
            "addedItems=" + addedItems +
            ", removedItems=" + removedItems +
            '}';
    }
}
