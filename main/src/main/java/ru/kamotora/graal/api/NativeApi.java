package ru.kamotora.graal.api;

import jdk.incubator.foreign.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

/**
 * https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/foreign/package-summary.html
 */
@Slf4j
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
        var cStringPointer = NativeUtils.toCStringPointer(email);
        test(isolateThreadId, cStringPointer);
    }

    // todo проверить на утечку памяти
    public static byte[] reverseBytes(byte[] bytes) {
        //Convert Java String to C *char
        var isolateThreadId = createIsolate();
        var bytesSegment = NativeUtils.toBytesSegment(bytes);
        var bytesPointer = NativeUtils.address(bytesSegment);
        var reversedBytesPointer = testBytes(isolateThreadId, bytesPointer, bytes.length);
        return NativeUtils.fromBytesPointer(reversedBytesPointer, bytes.length);
    }

    // todo проверить на утечку памяти
    public static byte[] generateRandomArray() {
        //Convert Java String to C *char
        var isolateThreadId = createIsolate();
        try (var sharedScope = ResourceScope.newSharedScope()) {
            var segment = SegmentAllocator.ofScope(sharedScope).allocate(MemoryLayouts.JAVA_INT);
            var resultSizePointer = NativeUtils.address(segment);
            var randomByteArrayPointer = randomByteArray(isolateThreadId, resultSizePointer);
            var resultSize = segment.toIntArray()[0];
            assert resultSize > 0 && resultSize <= 5;
            return NativeUtils.fromBytesPointer(randomByteArrayPointer, resultSize);
        }
    }

    public static void generateRandomPoint() {
        int x = RandomUtils.nextInt();
        int y = RandomUtils.nextInt();
        log.debug("call method 'createPoint' with x: {}, y: {}", x, y);
        var pointPointer = createPoint(createIsolate(), x, y);
        try (var sharedScope = ResourceScope.newSharedScope()) {
            var address = MemoryAddress.ofLong(pointPointer);
            MemorySegment.allocateNative(Point.NATIVE_LAYOUT, sharedScope);
            var segment = address
                    .asSegment(Point.NATIVE_LAYOUT.byteSize(), sharedScope);
            var point = Point.read(segment);
            log.debug("created point with x: {}, y: {}", point.x(), point.y());
            // после выхода из try point удалится, т.к. sharedScope
        }
    }


    private static native int test(long isolateThreadId, long emailPointer);

    private static native long testBytes(long isolateThreadId, long bytesArrayPointer, int size);

    private static native long randomByteArray(long isolateThreadId, long resultSizePointer);

    private static native int add(long isolateThreadId, int a, int b);

    private static native long createPoint(long isolateThreadId, int x, int y);

    private static native long createIsolate();
}
