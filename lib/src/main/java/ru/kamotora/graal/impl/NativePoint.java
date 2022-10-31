package ru.kamotora.graal.impl;

import org.graalvm.nativeimage.c.struct.RawField;
import org.graalvm.nativeimage.c.struct.RawStructure;
import org.graalvm.word.PointerBase;

@RawStructure
public interface NativePoint extends PointerBase {
    @RawField
    int getX();

    @RawField
    void setX(int x);

    @RawField
    int getY();

    @RawField
    void setY(int y);
}
