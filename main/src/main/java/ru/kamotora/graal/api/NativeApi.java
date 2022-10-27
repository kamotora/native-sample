package ru.kamotora.graal.api;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

import java.nio.ByteBuffer;


/**
 * https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/foreign/package-summary.html
 */
public class NativeApi {

    static {
        System.load(System.getProperty("user.dir") + "/lib/target/lib.so");
    }

    public static void add(int a, int b) {
        var isolateThreadId = createIsolate();
        add(isolateThreadId, 5, 5);
    }

    /**
     * https://graalvm.slack.com/archives/CN9KSFB40/p1629811417043700
     *
     * @param email
     */
    public static void print(String email) {
        //Convert Java String to C *char
        var isolateThreadId = createIsolate();
        add(isolateThreadId, 5, 5);
        var cStringPointer = toCStringPointer(email);
        test(isolateThreadId, cStringPointer);
    }

    // todo проверить на утечку памяти
    public static byte[] reverseBytes(byte[] bytes) {
        //Convert Java String to C *char
        var isolateThreadId = createIsolate();
        var bytesSegment = toBytesSegment(bytes);
        var bytesPointer = address(bytesSegment);
        var reversedBytesPointer = testBytes(isolateThreadId, bytesPointer, bytes.length);
        return fromBytesPointer(reversedBytesPointer, bytes.length);
    }

    private static native int test(long isolateThreadId, long emailPointer);

    private static native long testBytes(long isolateThreadId, long bytesArrayPointer, int size);

    private static native int add(long isolateThreadId, int a, int b);

    private static native long createIsolate();

    static long toCStringPointer(String str) {
        return address(CLinker.toCString(str, ResourceScope.newImplicitScope()));
    }

    static long toBytesPointer(byte[] bytes) {
        return address(toBytesSegment(bytes));
    }

    static MemorySegment toBytesSegment(byte[] bytes) {
        ByteBuffer heapOffBuffer = ByteBuffer
                .allocateDirect(bytes.length)
                .put(bytes);
        heapOffBuffer.position(0);
        return MemorySegment.ofByteBuffer(heapOffBuffer);
    }

    static byte[] fromBytesPointer(long bytesPointer, int size) {
        var address = MemoryAddress.ofLong(bytesPointer);
        return address
                .asSegment(size, ResourceScope.newImplicitScope())
                .toByteArray();
    }

    static long address(MemorySegment memorySegment) {
        return memorySegment.address().toRawLongValue();
    }
}
