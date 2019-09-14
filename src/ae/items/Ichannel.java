/*
 * Copyright (c) 2019. Aleksey Eremin
 * 12.09.19 11:25
 *
 */

/*
  Элемент "канал (измерения)"
   <measuringchannel code="04" desc="счетчик, реакт. отдача">
        <period start="0000" end="0030">
          <value>0</value>
        </period>
        ...
    </measuringchannel>

 */
package ae.items;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;

public class Ichannel {

  private int       count = 0;  // кол-во записанных получасовок
  private String    code;   // атрибут "код канала"
  private String    desc;   // атрибут "описание"
  private Double[]  periods = new Double[48];  // периоды

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public Double[] getPeriods() {
    return periods;
  }

  public void setPeriods(Double[] periods) {
    this.periods = periods;
    this.count = 0;
  }

  /**
   * Подсчитать кол-во получасовок в канале измерения
   * @return  кол-во измерений
   */
  public int getCount()
  {
    if(this.count == 0) {
      int c = 0;
      for (Double d : periods) {
        if (d != null)
          c++;
      }
      this.count = c;
    }
    return count;
  }

  private static final String
      SUBITEM    = "period",
      SUBVALUE   = "value",
      ATTR_CODE  = "code",
      ATTR_DESC  = "desc",
      ATTR_START = "start",
      ATTR_END   = "end";

  /**
   * Читаем канал учета, открытого в вышестоящей точке учета
   * @param eventReader     читатель событий XML
   * @param startElementTag прочитанный стартовый тэг канала
   */
  public void read(XMLEventReader eventReader, StartElement startElementTag)
  {
    try {
      String tagName = startElementTag.getName().getLocalPart(); // measuringchannel
      //
      // We read the attributes from this tag and add the s_date
      // attribute to our object
      // читаем атрибуты
      Iterator<Attribute> attributesTag = startElementTag.getAttributes();
      while (attributesTag.hasNext()) {
        Attribute attribute = attributesTag.next();
        if (attribute.getName().toString().equals(ATTR_CODE)) {
          this.code = attribute.getValue();
        }
        if (attribute.getName().toString().equals(ATTR_DESC)) {
          this.desc = attribute.getValue();
        }
      }
      //
      boolean bitem  = false; // флаг "нашли тэг <period>"
      String  pstart = "";    // время начала периода
      String  pend   = "";    // время конца периода
      String  pvalue = "";    // значение периода
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
          StartElement startElement = event.asStartElement();
          String startTag = startElement.getName().getLocalPart();
          // If we have an item element, we create a new item
          // нашли период учета
          if (startTag.equals(SUBITEM)) {
            bitem = true;
            pvalue = "0";
            // <period start="0000" end="0030">
            Iterator<Attribute> attributes = startElement.getAttributes();
            while (attributes.hasNext()) {
              Attribute attribute = attributes.next();
              if (attribute.getName().toString().equals(ATTR_START)) {
                pstart = attribute.getValue();
              }
              if (attribute.getName().toString().equals(ATTR_END)) {
                pend = attribute.getValue();
              }
            }
            continue;
          }
          if(bitem && startTag.equals(SUBVALUE)) {
            event = eventReader.nextEvent();
            pvalue = event.asCharacters().getData();  // значение value
          }
        }
        // If we reach the end of an item element, we add it to the list
        if (event.isEndElement()) {
          EndElement endElement = event.asEndElement();
          String endTag = endElement.getName().getLocalPart();
          if(bitem && endTag.equals(SUBITEM)) {
            // конец тэга </period>
            bitem = false;
            setPeriod(pstart, pend, pvalue);
          }
          if (endTag.equals(tagName)) {
            return;
          }
        }
      }
    } catch (Exception e) {
      System.out.println("?-error-ошибка чтения точки учета: " + e.getMessage());
      // e.printStackTrace();
    }
  }

  /**
   * Задать значение периода по времени
   * @param start начало времени
   * @param end   конец времени (не включая)
   * @param value значение
   */
  private void setPeriod(String start, String end, String value) {

    int i1 = indexOfTime(start);  // начало
    int i2 = indexOfTime(end);    // конец (не включая)
    if(i2 == 0) i2 = 48;
    double val = Double.parseDouble(value); // значение
    for(int i = i1; i < i2; i++) {
      this.periods[i] = val;
    }
    this.count = 0;   // сбросим кол-во записанных получасовок, их потом пресчитаем
  }

  /**
   * Индекс [0-47] из строки времени "0230" - 4 символа
   * @param stm  строка времени
   * @return  индекс
   */
  private int indexOfTime(String stm)
  {
    int idx = 0;
    try {
      String sh = stm.substring(0, 2);  // час
      String sm = stm.substring(2, 4);  // минута
      int ih = Integer.parseInt(sh);  // номер часа
      int im = Integer.parseInt(sm);  // номер минуты
      if(ih > 23 || ih < 0)
        throw new Exception("неправильный час");
      int m  = (im < 30)? 0: 1;
      idx = (ih * 2) + m;
    } catch (Exception e) {
      System.err.println("?-error-строка времени неверная: " + stm + ". " + e.getMessage());
    }
    return idx;
  }

  /**
   * Выдать измерения канала учета в виде строки для SQL
   * code,count,h1,h2,...h48
   * @return строка
   */
  public String toStr(String prefix,String postfix)
  {
    StringBuilder strbuf = new StringBuilder();
    strbuf.append(prefix);  // префикс
    strbuf.append(getCode()).append("', ");
    strbuf.append(getCount());  //кол-во измерений
    int Np = this.periods.length;
    for(int i = 0; i < Np; i++) {
      strbuf.append(",");
      if(periods[i] != null)
        strbuf.append(periods[i]);
      else
        strbuf.append("NULL");
    }
    strbuf.append(postfix);
    return strbuf.toString();
  }

}  // end of class
