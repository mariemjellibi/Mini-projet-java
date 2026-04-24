package Database;

public class Config {

    public static final String NOM_Drive = "org.postgresql.Driver";

    public static final String IPserveur =
            "ep-misty-haze-amfo6zv3-pooler.c-5.us-east-1.aws.neon.tech";

    public static final String PORT = "5432";

    public static final String NOM_DB = "neondb";

    public static final String URL_DB =
            "jdbc:postgresql://" + IPserveur + ":" + PORT + "/" + NOM_DB
                    + "?sslmode=require&channel_binding=require";

    public static final String USERNAME = "neondb_owner";

    public static final String PASSWORD = "npg_Mz29ALuIfPra";
}