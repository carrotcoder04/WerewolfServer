package game;

import io.Reader;
import io.Writer;
import serialization.Serializable;

import java.util.TreeMap;

public class Avatar implements Serializable<Avatar> {
     private final TreeMap<ItemType, Item> items;
     public Avatar(Reader reader) {
          items = new TreeMap<>();
          deserialize(reader);
     }
     public Avatar(TreeMap<ItemType,Item> items) {
          this.items = items;
     }
     public void setItem(Item item) {
          items.put(item.getType(), item);
     }

     @Override
     public void deserialize(Reader reader) {
          for (int i = 0; i < ItemType.values().length; i++) {
               Item item = new Item(reader);
               items.put(item.getType(), item);
          }
     }
     @Override
     public Writer serialize() {
          Writer writer = new Writer(1024);
          for(Item item : items.values()) {
               writer.write(item);
          }
          System.out.println(writer.getBuffer().length);
          return writer;
     }
}