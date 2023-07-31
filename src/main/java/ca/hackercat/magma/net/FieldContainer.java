package ca.hackercat.magma.net;

public class FieldContainer {

    public int objectSlot;
    public String fieldName;
    public Object value;

    public FieldContainer() {}

    public FieldContainer(int objectSlot, String fieldName, Object value) {
        this.objectSlot = objectSlot;
        this.fieldName = fieldName;
        this.value = value;
    }
}
