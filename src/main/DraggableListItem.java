package main;

public class DraggableListItem {
    private Object itemObject;
    private String itemString;

    public DraggableListItem(Object itemObject, String itemString) {
        this.itemObject = itemObject;
        this.itemString = itemString;
    }

    public Object getItemObject() {
        return itemObject;
    }

    public void setItemObject(Object itemObject) {
        this.itemObject = itemObject;
    }

    public String getItemString() {
        return itemString;
    }

    public void setItemString(String itemString) {
        this.itemString = itemString;
    }
}
