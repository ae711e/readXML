/*
 * Copyright (c) 2019. Aleksey Eremin
 * 12.09.19 11:20
 *
 */
/*
  Элемент "дата"
  <datetime>
    <timestamp>20180920075901</timestamp>
    <daylightsavingtime>0</daylightsavingtime>
    <day>20180919</day>
  </datetime>

 */

package ae.items;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Idate {

  public LocalDate getDate() {
    return date;
  }

  /**
   * Выдать дату в виде строки
   * @param format формат даты, например yyyy-MM-dd
   * @return строка даты
   */
  public String getDate(String format) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return date.format(formatter);
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
  public void setDate(String date) {
    this.date = fromString(date);
  }

  private LocalDate date; // дата Java

  private static final String ITEM = "datetime",
                              SUBITEM = "day";
  /**
   * Прочитать из XML файла значение атрибута дата
   * @param fileName имя файла
   */
  public void readDate(String fileName) {
    try {
      int abc =0;
      // First, create a new XMLInputFactory
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      // Setup a new eventReader
      InputStream in = new FileInputStream(fileName);
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      boolean bg = false;
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        //
        if (event.isStartElement()) {
          StartElement startElement = event.asStartElement();
          // If we have an item element, we create a new item
          if (startElement.getName().getLocalPart().equals(ITEM)) {
            //item = new Item();
            bg = true;  // начали читать тэг <datetime>
            // We read the attributes from this tag and add the s_date
            // attribute to our object
//            Iterator<Attribute> attributes = startElement.getAttributes();
//            while (attributes.hasNext()) {
//              Attribute attribute = attributes.next();
//              if (attribute.getName().toString().equals("daylightsavingtime")) {
//                //item.setS_date(attribute.getValue());
//                abc++;
//              }
//            }
            continue;
          }
          // прочитать элемент <day> .
          if (bg && event.isStartElement()) {
            if (event.asStartElement().getName().getLocalPart()
                .equals(SUBITEM)) {
              event = eventReader.nextEvent();
              String sd = event.asCharacters().getData();
              setDate(sd);
              continue;
            }
          }

        }
        // If we reach the end of an item element, we add it to the list
        if (event.isEndElement()) {
          EndElement endElement = event.asEndElement();
          if (bg && endElement.getName().getLocalPart().equals(ITEM)) {
            return;
          }
        }
      }
      ////////////////////////////////////////
    } catch (Exception e) {
      System.out.println("?-error-ошибка при определении даты в файле: " + fileName + ". " + e.getMessage());
    }

  }

  /**
   * Установить дату из строки вида YYYYMMDD
   * @param sdate строка с датой
   * @return локальная дата
   */
  private LocalDate fromString(String sdate)
  {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      LocalDate ldat = LocalDate.parse(sdate, formatter);
      return ldat;
    } catch (Exception e) {
      System.err.println("?-error-неправильная строка с датой: " + sdate);
    }
    return null;
  }

} // end of class
