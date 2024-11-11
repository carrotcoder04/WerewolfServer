package io;

import java.nio.charset.StandardCharsets;

public class Reader {
    private final byte[] buffer;
    private int position;
    public Reader(byte[] buffer) {
        this.buffer = buffer;
        this.position = 1;
    }
    public byte[] getBuffer() {
        return buffer;
    }
    public byte readTag() {
        return buffer[0];
    }
    public int nextInt() throws ArrayIndexOutOfBoundsException {
        if (position + 3 >= buffer.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return (buffer[position++] & 0xFF) | ((buffer[position++] & 0xFF) << 8) | ((buffer[position++] & 0xFF) << 16)  | ((buffer[position++] & 0xFF) << 24);
    }
    public byte nextByte() throws ArrayIndexOutOfBoundsException {
        if (position >= buffer.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return buffer[position++];
    }
    public String nextString() throws ArrayIndexOutOfBoundsException {
        int length = nextByte();
        if(position + length > buffer.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        String utf8String = new String(buffer, position, length, StandardCharsets.UTF_8);
        position += length;
        return utf8String;
    }
}
