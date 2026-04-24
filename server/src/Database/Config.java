package Database;

public class Config {
    public static final String NOM_Drive = "org.postgresql.Driver";
    public static final String IPserveur = "ep-billowing-flower-aiac79vo-pooler.c-4.us-east-1.aws.neon.tech";
    public static final String PORT = "5432";
    public static final String NOM_DB = "neondb";
    public static final String URL_DB = "jdbc:postgresql://" + IPserveur + ":" + PORT + "/" + NOM_DB
            + "?sslmode=require&currentSchema=public";
    public static final String USERNAME = "neondb_owner";
    public static final String PASSWORD = "npg_cC9qzVvaY7PX";   // Use the password from that connection string
}