/*
 * Copyright (c) 2019. Aleksey Eremin
 * 12.09.19 11:22
 *
 */

/*
  Чтение точки измерения из файла
  Элемент "точка (измерения)
  <measuringpoint code="523070005107201" name="ВЛ-101 ПС Луч">
      <measuringchannel code="01" desc="счетчик, акт. прием">
        <period start="0000" end="0030">
        ...
    </measuringpoint>
 */

package ae.items;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class Ipoint {
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private String  code; // атрибут "код" т.и.
  private String  name; // атрибут имя т.и.
  private ArrayList<Ichannel> ichannels = new ArrayList<>();  // массив точек учета

  private static final String
      SUBITEM   = "measuringchannel",
      ATTR_1 = "code",
      ATTR_2 = "name";

  /**
   * Чтение точки измерения из читателя событий XML, мы стоим на этом тэге
   * @param eventReader     читатель событий XML
   * @param startElementTag прочитанный стартовый тэг точки учета
   */
  public void read(XMLEventReader eventReader, StartElement startElementTag)
  {
    try {
      String tagName = startElementTag.getName().getLocalPart();
      //
      // We read the attributes from this tag and add the s_date
      // attribute to our object
      // читаем атрибуты
      Iterator<Attribute> attributes = startElementTag.getAttributes();
      while (attributes.hasNext()) {
        Attribute attribute = attributes.next();
        if (attribute.getName().toString().equals(ATTR_1)) {
          this.code = attribute.getValue();
        }
        if (attribute.getName().toString().equals(ATTR_2)) {
          this.name = attribute.getValue();
        }
      }
      //
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
          StartElement startElement = event.asStartElement();
          // If we have an item element, we create a new item
          // нашли канал учёта, проверим начался ли канал
          if (startElement.getName().getLocalPart().equals(SUBITEM)) {
            Ichannel ichannel = new Ichannel();
            ichannel.read(eventReader, startElement);
            ichannels.add(ichannel);    // запомним канал учета
          }
        }
        // If we reach the end of an item element, we add it to the list
        if (event.isEndElement()) {
          EndElement endElement = event.asEndElement();
          if (endElement.getName().getLocalPart().equals(tagName)) {
            return;
          }
        }
      }
    } catch (Exception e) {
      System.out.println("?-error-ошибка чтения точки учета: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Выдать измерения точки учета в виде строк для SQL
   * code1_ch,count,h1,h2,...h48
   * code2_ch,count,h1,h2,...h48
   * @return строка
   */
  public String toStr(String prefix, String postfix)
  {
    StringBuilder strbuf = new StringBuilder();
    String spref = prefix + "'" + getCode() + "_";
    for (Ichannel ich: this.ichannels) {
      String s = ich.toStr(spref, postfix);
      strbuf.append(s);
    }
    return strbuf.toString();
  }

} // end of class
