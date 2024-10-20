package message.data;
import message.io.MessageReader;
import message.io.MessageWriter;

public abstract class Message<T > {
    private byte tag;
    private T data;
    public Message(byte tag,T data,MessageReader reader) {
        if(reader == null) {
            this.tag = tag;
            this.data = data;
        }
        else {
            serialize(reader);
        }
    }
    protected abstract void serialize(MessageReader reader);
    public abstract MessageWriter getWriter();
    public T getData() {
        return data;
    }
    protected void setData(T data) {
        this.data = data;
    }
    public byte getTag() {
        return tag;
    }
    protected void setTag(byte tag) {
        this.tag = tag;
    }
}
