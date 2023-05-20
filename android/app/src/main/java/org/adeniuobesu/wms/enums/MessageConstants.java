package org.adeniuobesu.wms.enums;

// Defines several constants used when transmitting messages between the
// service and the UI.
public enum MessageConstants {
    MESSAGE_READ(0),
    MESSAGE_WRITE(1),
    MESSAGE_TOAST(2);
    private final int value;
    // ... (Add other message types here as needed.)
    MessageConstants(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
