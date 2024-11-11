package game;

import io.Reader;
import io.Writer;
import serialization.Serializable;

public class Item implements Serializable<Item> {
     private ItemType type;
     private int indexOfAsset;
     public Item(Reader reader) {
          deserialize(reader);
     }

     public ItemType getType() {
          return type;
     }
     @Override
     public void deserialize(Reader reader) {
          this.type = ItemType.values()[reader.nextByte()];
          this.indexOfAsset = reader.nextInt();
     }
     @Override
     public Writer serialize() {
          Writer writer = new Writer(6);
          byte type = (byte) this.type.ordinal();
          writer.writeByte(type);
          writer.writeInt(indexOfAsset);
          return writer;
     }
}