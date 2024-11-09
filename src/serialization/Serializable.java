package serialization;

import io.Reader;
import io.Writer;

public interface Serializable<T> {
     void deserialize(Reader reader);
     Writer serialize();
}
