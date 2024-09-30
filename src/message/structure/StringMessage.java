package message.structure;

import message.io.MessageReader;
import message.io.MessageWriter;

public class StringMessage implements MessageStructure{
    private String message;
    public StringMessage(String message) {
        this.message = message;
    }
    public StringMessage(MessageReader reader) {
        this.message = reader.nextString();
    }
    @Override
    public MessageWriter getWriter() {
        MessageWriter writer = new MessageWriter();
        writer.writeString(message);
        return writer;
    }
}
