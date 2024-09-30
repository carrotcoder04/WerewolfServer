package message.io;

import helper.Constants;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageWriter {
    private final byte[] buffer;
    private int position;
    public MessageWriter() {
        buffer = new byte[Constants.MAX_BUFFER_WRITER];
        position = 0;
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
    public byte[] getBuffer() {
        return Arrays.copyOfRange(buffer, 0, position);
    }
}
