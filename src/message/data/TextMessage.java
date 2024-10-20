package message.data;
import message.io.MessageReader;
import message.io.MessageWriter;


public class TextMessage extends Message<String> {
    public TextMessage(byte tag, String data, MessageReader reader) {
        super(tag,data,reader);
    }

    @Override
    protected void serialize(MessageReader reader) {
        setTag(reader.readTag());
        setData(reader.nextString());
    }

    @Override
    public MessageWriter getWriter() {
        MessageWriter writer = new MessageWriter();
        writer.writeTag(getTag());
        writer.writeString(getData());
        return writer;
    }
}
