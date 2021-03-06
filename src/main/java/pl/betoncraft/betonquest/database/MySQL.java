package pl.betoncraft.betonquest.database;

import org.bukkit.plugin.Plugin;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;

/**
 * Connects to and uses a MySQL database
 */
public class MySQL extends Database {
    private final String user;
    private final String database;
    private final String password;
    private final String port;
    private final String hostname;

    /**
     * Creates a new MySQL instance
     *
     * @param plugin   Plugin instance
     * @param hostname Name of the host
     * @param port     Port number
     * @param database Database name
     * @param username Username
     * @param password Password
     */
    public MySQL(final Plugin plugin, final String hostname, final String port, final String database, final String username, final String password) {
        super(plugin);
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    @Override
    public Connection openConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?&useSSL=false", this.user, this.password);
        } catch (Exception e) {
            LogUtils.getLogger().log(Level.WARNING, "MySQL says: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
        return connection;
    }
}
