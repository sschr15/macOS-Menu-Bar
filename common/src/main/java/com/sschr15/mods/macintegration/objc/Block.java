/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.objc;

import com.sun.jna.*;

/**
 * Horrible glue to get Objective-C blocks right - because lambdas can't just be lambdas
 */
public class Block {
    /**
     * Convert a callback into a pointer for a block.
     * @param callback The callback to convert.
     * @see com.sschr15.mods.macintegration.inputs.objc.PressureHandler A sample use case.
     * @return a pointer to be used where the block is expected.
     */
    public static Structure createBlockForCallback(Callback callback) {
        BlockLiteral blockLiteral = new BlockLiteral();
        blockLiteral.isa = GLOBAL_BLOCK_ISA;
        blockLiteral.flags = BLOCK_IS_GLOBAL | BLOCK_HAS_DESCRIPTOR;
        blockLiteral.reserved = 0;
        blockLiteral.invoke = CallbackReference.getFunctionPointer(callback);

        BlockDescriptor descriptor = new BlockDescriptor();
        descriptor.reserved = 0;
        descriptor.size = blockLiteral.size();
        descriptor.write();
        blockLiteral.descriptor = descriptor.getPointer();

        blockLiteral.write();
        return blockLiteral;
    }

    private static final Pointer GLOBAL_BLOCK_ISA = NativeLibrary.getInstance("System").getGlobalVariableAddress("_NSConcreteGlobalBlock");
    private static final int
            BLOCK_IS_GLOBAL = 1 << 28,
            BLOCK_HAS_DESCRIPTOR = 1 << 29;

    @Structure.FieldOrder({"reserved", "size"})
    public static class BlockDescriptor extends Structure {
        public long reserved, size;
    }

    @Structure.FieldOrder({"isa", "flags", "reserved", "invoke", "descriptor"})
    public static class BlockLiteral extends Structure {
        public Pointer isa;
        public int flags, reserved;
        public Pointer invoke, descriptor;
    }
}
