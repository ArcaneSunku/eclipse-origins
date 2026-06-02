package dev.atomixsoft.solar_eclipse.core.utils;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public final class CodecUtils {

    private CodecUtils() {}

    public static void writeString(ByteBuf out, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    public static String readString(ByteBuf in) {
        if (in.readableBytes() < 4) return null;

        in.markReaderIndex();
        int length = in.readInt();

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return null;
        }

        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

}
