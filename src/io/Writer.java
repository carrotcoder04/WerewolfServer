package io;

import config.network.NetworkConfig;
import serialization.Serializable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Writer {
    private final byte[] buffer;
    private int position;
    public Writer() {
        buffer = new byte[NetworkConfig.MAX_BUFFER_WRITER];
        position = 1;
    }
    public Writer(int size) {
        buffer = new byte[size];
        position = 1;
    }
    public void writeTag(byte tag) {
        buffer[0] = tag;
    }
    public void writeInt(int value) throws ArrayIndexOutOfBoundsException {
        if(position + 3 >= buffer.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        buffer[position++] = (byte) (value & 0xFF);
        buffer[position++] = (byte) ((value >> 8) & 0xFF);
        buffer[position++] = (byte) ((value >> 16) & 0xFF);
        buffer[position++] = (byte) ((value >> 24) & 0xFF);
    }
    public void writeByte(byte value) throws ArrayIndexOutOfBoundsException {
        if(position >= buffer.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        buffer[position++] = (byte) (value & 0xFF);
    }
    public void writeString(String value) throws ArrayIndexOutOfBoundsException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        if(position + length >= buffer.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        writeByte((byte)length);
        for(int i = 0; i < length; i++) {
            buffer[position++] = bytes[i];
        }
    }
    public void writeBytes(byte[] bytes) throws ArrayIndexOutOfBoundsException {
        if(position + bytes.length - 1 >= buffer.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        System.arraycopy(bytes, 0, buffer, position, bytes.length);
        position += bytes.length;
    }
    public byte[] getBuffer() {
        return Arrays.copyOfRange(buffer, 0, position);
    }
    private void write(Writer writer) {
        writeBytes(writer.getBufferNoTag());
    }
    public void write(Serializable serializable) {
        write(serializable.serialize());
    }
    private byte[] getBufferNoTag() {
        return Arrays.copyOfRange(buffer, 1, position);
    }
}
