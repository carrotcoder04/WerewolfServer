package message.io;

import java.nio.charset.StandardCharsets;

public class MessageReader {
    private final byte[] buffer;
    private int position;
    public MessageReader(byte[] buffer) {
        this.buffer = buffer;
        this.position = 0;
    }
    public byte[] getBuffer() {
        return buffer;
    }
    public int nextInt() throws ArrayIndexOutOfBoundsException {
        if (position + 3 >= buffer.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return (buffer[position++] & 0xFF) | ((buffer[position++] << 8) & 0xFF) | ((buffer[position++] << 16) & 0xFF)  | ((buffer[position++] << 24) & 0xFF);
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
