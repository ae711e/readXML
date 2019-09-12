/*
 * Copyright (c) 2019. Aleksey Eremin
 * 12.09.19 11:06
 *
 */

/*
  Чтение файла XML с макетом 800200
 */
package ae;

import ae.items.Idate;
import ae.items.Ipoint;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class o80020 {

  ArrayList<Ipoint> ipoints = new ArrayList<>();

  private static final String
      ITEM      = "measuringpoint";

  public void start(String fileName) {
    Idate id = new Idate();
    id.readDate(fileName);
    System.out.println("Прочитали дату: " + id.getDate());
    //
    try {
      // First, create a new XMLInputFactory
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      // Setup a new eventReader
      InputStream in = new FileInputStream(fileName);
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      // read the XML document

      int cnt = 0;
      String prefix  = "('"+id.getDate("yyyy-MM-dd")+"',";
      String postfix = ");\r\n";
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
          StartElement startElement = event.asStartElement();
          // If we have an item element, we create a new item
          if (startElement.getName().getLocalPart().equals(ITEM)) {
            Ipoint ipoint = new Ipoint(id.getDate());
            ipoint.read(eventReader, startElement);
            ipoints.add(ipoint);
            cnt++;
            System.out.println(cnt + ") прочитали точку учета: " + ipoint.getCode() + " " + ipoint.getName());
            String s;
            s = ipoint.toStr(prefix, postfix);
            System.out.println(s);
          }
        }
      }
      //
    } catch (Exception e) {
      System.err.println("?-error-ошибка чтения макета 80020: " + e.getMessage());
    }
  }

}  // end of class

