package pl.betoncraft.betonquest.database;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.database.Connector.UpdateType;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Saves the data to the database asynchronously
 */
public class Saver extends Thread implements Listener {

    private final Connector con;
    private final ConcurrentLinkedQueue<Record> queue;
    private boolean run;
    private boolean active;

    /**
     * Creates new database saver thread
     */
    public Saver() {
        super();
        this.con = new Connector();
        this.queue = new ConcurrentLinkedQueue<>();
        this.run = true;
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void run() {
        while (true) {
            while (queue.isEmpty()) {
                if (!run) {
                    return;
                }
                synchronized (this) {
                    try {
                        active = false;
                        wait();
                    } catch (InterruptedException e) {
                        LogUtils.getLogger().log(Level.SEVERE, "There was a exception with SQL");
                        LogUtils.logThrowable(e);
                    }
                }
            }
            if (!active) {
                con.refresh();
                active = true;
            }
            final Record rec = queue.poll();
            con.updateSQL(rec.getType(), rec.getArgs());
        }
    }

    /**
     * Adds new record to the queue, where it will be saved to the database.
     *
     * @param rec Record to save
     */
    public synchronized void add(final Record rec) {
        queue.add(rec);
        notify();
    }

    /**
     * Ends this saver's job, letting it save all remaining data.
     */
    public synchronized void end() {
        run = false;
        notify();
    }

    /**
     * Holds the data and the method of saving them to the database
     */
    public static class Record {

        private final UpdateType type;
        private final String[] args;

        /**
         * Creates new Record, which can be saved to the database using
         * Saver.add()
         *
         * @param type method used for saving the data
         * @param args list of Strings which will be saved to the database
         */
        public Record(final UpdateType type, final String[] args) {
            this.type = type;
            this.args = args == null ? null : Arrays.copyOf(args, args.length);
        }

        private UpdateType getType() {
            return type;
        }

        private String[] getArgs() {
            return args;
        }
    }

}
