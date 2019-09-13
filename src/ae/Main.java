/*
 * Copyright (c) 2019. Aleksey Eremin
 *  
 */

package ae;
/*
   чтение XML файла с измерениями
 */


import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
      ArrayList<String> fileNames = new ArrayList<>();

      boolean f_help = false; // флаг помощи
      // write your code here
      for(int i = 0; i < args.length; i++) {
        switch (args[i]) {
          case "-h":
            // указан ключ подсказки
            f_help = true;
            break;

          default:
            // задан файл рабочей базы данных
            fileNames.add(args[i]); // запомним имя файла
            break;
        }
      }
      if(f_help || fileNames.size() < 2) {
        System.out.println("input arguments: file_XML file_Access");
        return;
      }

      String
          fileXml = fileNames.get(0),
          fileAccess = fileNames.get(1);

//      StaXParser read = new StaXParser();
//      List<Item> readConfig = read.readConfig("a.xml");
//      for (Item item : readConfig) {
//          System.out.println(item);
//      }
//      System.out.println("Hello:");

      //
      o80020 o = new o80020();
      o.start(fileXml, fileAccess);
   }

} // end of class
