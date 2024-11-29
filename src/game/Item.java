package game;

import io.Reader;
import io.Writer;
import serialization.Serializable;

public class Item implements Serializable<Item> {
     private ItemType type;
     private String imagePath;
     public Item(Reader reader) {
          deserialize(reader);
     }

     public ItemType getType() {
          return type;
     }
     @Override
     public void deserialize(Reader reader) {
          this.type = ItemType.values()[reader.nextByte()];
          this.imagePath = reader.nextString();
     }
     @Override
     public Writer serialize() {
          Writer writer = new Writer(100);
          byte type = (byte) this.type.ordinal();
          writer.writeByte(type);
          writer.writeString(this.imagePath);
          return writer;
     }
}