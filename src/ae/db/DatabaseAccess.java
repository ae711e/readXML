/*
 * Copyright (c) 2017. Aleksey Eremin
 * 28.01.17 21:26
 */

package ae.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by ae on 16.03.2017.
 * База данных MS Access
 *  Чтение данных из Access 2010
 *  Использует библиотеку  UCanAccess http://ucanaccess.sourceforge.net/site.html
 *  эта библиотека требует еще 5 бибилиотек - они есть в дистрибутиве UCanAccess подкаталог lib:
 *  UCanAccess (ucanaccess-x.x.x.jar)
 *  HSQLDB (hsqldb.jar, version 2.2.5 or newer)
 *  Jackcess (jackcess-2.x.x.jar)
 *  commons-lang (commons-lang-2.6.jar, or newer 2.x version)
 *  commons-logging (commons-logging-1.1.1.jar, or newer 1.x version)
 */

public class DatabaseAccess extends Database
{
    private String f_databaseName;  // имя файлы базы данных
    
    /**
     * Конструктор
     * в нем формируется имя базы данных
     */
    public DatabaseAccess(String dbName)
    {
        f_databaseName = dbName;
    }
    
    /**
     * Возвращает соединение к базе данных MS Access
     * @return соединение к БД
     */
    @Override
    public synchronized Connection getDbConnection()
    {
        if(f_connection == null) {
            try {
                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver"); /* often not required for Java 6 and later (JDBC 4.x) */
                f_connection = DriverManager.getConnection("jdbc:ucanaccess://"+f_databaseName);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return f_connection;
    }

} // end of class
