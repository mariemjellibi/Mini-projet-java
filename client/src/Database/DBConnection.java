package Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection makeConnection() {
        try {
            Class.forName(Config.nomDriver);

            Connection conn = DriverManager.getConnection(
                    Config.URL_DB,
                    Config.USERNAME,
                    Config.PASSWORD
            );

            System.out.println("DB Connected ✔");
            return conn;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DATABASE CONNECTION FAILED ❌", e);
        }
    }

    public static void main(String[] args) {
        try {
            DBConnection.makeConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
