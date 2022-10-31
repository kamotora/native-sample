package ru.kamotora.graal.api;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayouts;
import jdk.incubator.foreign.MemorySegment;

import java.lang.invoke.VarHandle;

record Point(int x, int y) {
    static final MemoryLayout NATIVE_LAYOUT = MemoryLayout.structLayout(
        MemoryLayouts.JAVA_INT.withName("x"),
        MemoryLayouts.JAVA_INT.withName("y")
    );

    static final VarHandle VH_x = NATIVE_LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("x"));
    static final VarHandle VH_y = NATIVE_LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("y"));

    public static Point read(MemorySegment segment) {
        int x = (int) VH_x.get(segment);
        int y = (int) VH_y.get(segment);
        return new Point(x, y);
    }
}