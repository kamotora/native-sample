package ru.kamotora.graal.impl;

public class NativeLibException extends RuntimeException {
    public NativeLibException() {
        super("Oshibochka...");
    }
    public int getErrorCode(){
        return -1;
    }
}
