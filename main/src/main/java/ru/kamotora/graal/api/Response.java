package ru.kamotora.graal.api;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayouts;
import jdk.incubator.foreign.MemorySegment;

import java.lang.invoke.VarHandle;

import static jdk.incubator.foreign.MemoryLayout.PathElement.groupElement;

record Response(long code, long resultPtr, String errorMessage) {
    static final MemoryLayout NATIVE_LAYOUT = MemoryLayout.structLayout(
            MemoryLayouts.JAVA_LONG.withName("code"),
            MemoryLayouts.JAVA_LONG.withName("errorMessage"),
            MemoryLayouts.JAVA_LONG.withName("result")
    );

    static final VarHandle CODE_HANDLE = NATIVE_LAYOUT.varHandle(long.class, groupElement("code"));
    static final VarHandle ERROR_MESSAGE_HANDLE = NATIVE_LAYOUT.varHandle(long.class, groupElement("errorMessage"));
    static final VarHandle RESULT_HANDLE = NATIVE_LAYOUT.varHandle(long.class, groupElement("result"));

    public static Response read(MemorySegment segment) {
        long code = (long) CODE_HANDLE.get(segment);
        long resultPtr = 0;
        String errorMessage = null;
        if (code == 0) {
            resultPtr = (long) RESULT_HANDLE.get(segment);
        } else {
            errorMessage = NativeUtils.fromCStringPointer((long) ERROR_MESSAGE_HANDLE.get(segment));
        }
        return new Response(code, resultPtr, errorMessage);
    }
}