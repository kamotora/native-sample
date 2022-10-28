package ru.kamotora.graal.api;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

import java.nio.ByteBuffer;

public class NativeUtils {

    private NativeUtils() {
    }

    public static long toCStringPointer(String str) {
        return address(CLinker.toCString(str, ResourceScope.newImplicitScope()));
    }

    public static long toBytesPointer(byte[] bytes) {
        return address(toBytesSegment(bytes));
    }

    public static MemorySegment toBytesSegment(byte[] bytes) {
        ByteBuffer heapOffBuffer = ByteBuffer
                .allocateDirect(bytes.length)
                .put(bytes);
        heapOffBuffer.position(0);
        return MemorySegment.ofByteBuffer(heapOffBuffer);
    }

    public static byte[] fromBytesPointer(long bytesPointer, int size) {
        var address = MemoryAddress.ofLong(bytesPointer);
        return address
                .asSegment(size, ResourceScope.newImplicitScope())
                .toByteArray();
    }

    public static long address(MemorySegment memorySegment) {
        return memorySegment.address().toRawLongValue();
    }
}
