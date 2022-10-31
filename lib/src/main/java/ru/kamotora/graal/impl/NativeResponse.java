package ru.kamotora.graal.impl;

import org.graalvm.nativeimage.c.struct.RawField;
import org.graalvm.nativeimage.c.struct.RawStructure;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.word.PointerBase;

/**
 * todo непонятно, как определяется порядок полей (похоже, что по алфавиту...)
 */
@RawStructure
//@CContext(JNIHeaderDirectives.class)
public interface NativeResponse extends PointerBase {

    @RawField
    long getCode();

    @RawField
    void setCode(long code);

    @RawField
    CCharPointer getResult();

    @RawField
    void setResult(CCharPointer result);

    @RawField
    CCharPointer getErrorMessage();

    @RawField
    void setErrorMessage(CCharPointer errorMessagePtr);
}
