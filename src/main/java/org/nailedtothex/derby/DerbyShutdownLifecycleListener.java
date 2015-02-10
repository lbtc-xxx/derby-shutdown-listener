package org.nailedtothex.derby;

import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

// Define this listener inside server element.
// <Listener className="org.nailedtothex.derby.DerbyShutdownLifecycleListener" />
public class DerbyShutdownLifecycleListener implements LifecycleListener {
    private static final Logger log = Logger.getLogger(DerbyShutdownLifecycleListener.class.getName());

    public DerbyShutdownLifecycleListener() {
        log.log(Level.INFO, "{0} instantiated", this.getClass().getName());
    }

    @Override
    public void lifecycleEvent(LifecycleEvent lifecycleEvent) {
        if (lifecycleEvent.getLifecycle().getState() == LifecycleState.DESTROYED) {
            shutdown();
        }
    }

    private static void shutdown() {
        Connection cn = null;
        try {
            cn = DriverManager.getConnection("jdbc:derby:;shutdown=true");
            log.log(Level.SEVERE, "Derby shutdown failed (no exception occurred).");
        } catch (SQLException e) {
            if ("XJ015".equals(e.getSQLState())) {
                log.log(Level.INFO, "Derby shutdown succeeded. SQLState={0}, message={1}",
                        new Object[]{e.getSQLState(), e.getMessage()});
                log.log(Level.FINEST, "Derby shutdown exception", e);
                return;
            }
            log.log(Level.INFO, "Derby shutdown failed or may not yet loaded. message: {0}", e.getMessage());
            log.log(Level.FINER, "Derby shutdown failed", e);
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (Exception e) {
                    log.log(Level.WARNING, "Database closing error", e);
                }
            }
        }
    }

}
