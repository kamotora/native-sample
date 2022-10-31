package ru.kamotora.graal.api;

import jdk.incubator.foreign.*;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.function.Function;

public class NativeUtils {

    private NativeUtils() {
    }

    public static long toCStringPointer(String str) {
        return address(CLinker.toCString(str, ResourceScope.newImplicitScope()));
    }

    public static boolean isNull(long pointer) {
        return isNull(MemoryAddress.ofLong(pointer));
    }

    public static boolean isNull(MemoryAddress address) {
        return MemoryAddress.NULL.equals(address.address());
    }

    @Nullable
    public static String fromCStringPointer(long stringPtr) {
        MemoryAddress addr = MemoryAddress.ofLong(stringPtr);
        if (MemoryAddress.NULL.equals(addr.address())) {
            return null;
        } else {
            return CLinker.toJavaString(addr);
        }
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

    public static <T> T fromObjectPointer(long pointer,
                                          MemoryLayout memoryLayout,
                                          Function<MemorySegment, T> readFunction) {
        try (var sharedScope = ResourceScope.newSharedScope()) {
            var address = MemoryAddress.ofLong(pointer);
            var segment = address
                    .asSegment(memoryLayout.byteSize(), sharedScope);
            return readFunction.apply(segment);
        }
    }
}
