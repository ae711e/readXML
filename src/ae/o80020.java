/*
 * Copyright (c) 2019. Aleksey Eremin
 * 12.09.19 11:06
 *
 */

/*
  Чтение файла XML с макетом 800200 и запись его в базу Access

  https://hr-vector.com/java/osnovy-java-xml

 */
package ae;

import ae.items.*;
import ae.db.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class o80020 {


  private static final String
      ITEM      = "measuringpoint";

  public void start(String fileName, String dbName)
  {
    ArrayList<Ipoint> ipoints = new ArrayList<>();
    Idate id = new Idate();
    id.readDate(fileName);
    System.out.println("Прочитали дату: " + id.getDate());
    //
    Database db = new DatabaseAccess(dbName);
    //
    try {
      // First, create a new XMLInputFactory
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      // Setup a new eventReader
      InputStream in = new FileInputStream(fileName);
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      // read the XML document
      int cnt = 0;
      int a;
      String prefix = "INSERT INTO ASKUE (rDATE,REGID,COUNT,H1,H2,H3,H4,H5,H6,H7,H8,H9,H10,H11,H12,H13,H14,H15,H16,H17,H18,H19,H20,H21,H22,H23,H24,H25,H26,H27,H28,H29,H30,H31,H32,H33,H34,H35,H36,H37,H38,H39,H40,H41,H42,H43,H44,H45,H46,H47,H48) " +
          "VALUES(#" + id.getDate("MM/dd/yyyy") + "#, ";
      String postfix = ");\r\n";
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
          StartElement startElement = event.asStartElement();
          // If we have an item element, we create a new item
          if (startElement.getName().getLocalPart().equals(ITEM)) {
            Ipoint ipoint = new Ipoint();
            ipoint.read(eventReader, startElement);
            ipoints.add(ipoint);
            System.out.print("Точка учета: " + ipoint.getCode() + " \"" + ipoint.getName() + "\"");
            String s;
            s = ipoint.toStr(prefix, postfix);
            a = db.ExecSql(s);
            if(a > 0) {
              // подсчитаем кол-во вхождений слова INSERT
              int n = s.split("INSERT").length - 1;
              System.out.print(" / измерений: " + n);
              cnt = cnt + n;
            }
            System.out.println();
          }
        }
      }
      System.out.println("Записано измерений: " + cnt);
      //
    } catch (Exception e) {
      System.err.println("?-error-ошибка чтения макета 80020: " + e.getMessage());
    }
  }

}  // end of class

