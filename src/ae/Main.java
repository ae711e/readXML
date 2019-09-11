/*
 * Copyright (c) 2019. Aleksey Eremin
 *  
 */

package ae;
/*
   чтение XML файла с измерениями
 */

import java.util.List;

public class Main {

    public static void main(String[] args) {
	      // write your code here

      StaXParser read = new StaXParser();
      List<Item> readConfig = read.readConfig("a.xml");
      for (Item item : readConfig) {
          System.out.println(item);
      }
              System.out.println("Hello:");
   }
}
