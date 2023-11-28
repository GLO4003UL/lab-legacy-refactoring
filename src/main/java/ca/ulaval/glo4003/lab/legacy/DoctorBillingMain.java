package ca.ulaval.glo4003.lab.legacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.lab.legacy.ws.domain.md.BillingService;
import ca.ulaval.glo4003.lab.legacy.ws.resources.BillingResource;
import jakarta.ws.rs.core.Application;

/**
 * RESTApi setup without using DI or spring
 */
@SuppressWarnings("all")
public class DoctorBillingMain {

  public static final String DB_URL =
                                    String.format("jdbc:sqlite:%s/legacy.db",
                                                  System.getProperty("user.dir"));
  private static final Logger LOGGER =
                                     LoggerFactory.getLogger(DoctorBillingMain.class);

  public static boolean isDev = true; // Would be a JVM argument or in a .property file

  public static void main(String[] args) throws Exception {
    LOGGER.info("Setup resources (API)");
    feedDB();
    BillingService billingService = createBillingService();
    BillingResource billingResource = new BillingResource(billingService);

    LOGGER.info("Setup API context (JERSEY + JETTY)");
    ServletContextHandler context =
                                  new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/api");
    ResourceConfig resourceConfig =
                                  ResourceConfig.forApplication(new Application()
                                  {
                                    @Override
                                    public Map<String, Object> getProperties() {
                                      return Map.of("jersey.config.server.wadl.disableWadl",
                                                    "true");
                                    }

                                    @Override
                                    public Set<Object> getSingletons() {
                                      HashSet<Object> resources =
                                                                new HashSet<>();
                                      LOGGER.info("Add resources to context");
                                      return Set.of(billingResource);
                                    }
                                  });

    ServletContainer servletContainer = new ServletContainer(resourceConfig);
    ServletHolder servletHolder = new ServletHolder(servletContainer);
    context.addServlet(servletHolder, "/*");

    LOGGER.info("Setup http server");
    ContextHandlerCollection contexts = new ContextHandlerCollection();
    contexts.setHandlers(new Handler[] { context });
    Server server = new Server(8080);
    server.setHandler(contexts);
    try {
      LOGGER.info("Staring server");
      server.start();
      server.join();
    } catch (Exception e) {
      LOGGER.error("Error during server start", e);
      server.stop();
      server.destroy();
    }
  }

  private static BillingService createBillingService() {
    BillingService contactService = new BillingService();

    return contactService;
  }

  private static void feedDB() throws SQLException {
    Connection connection =
                          DriverManager.getConnection(DoctorBillingMain.DB_URL);
    Statement feedData = connection.createStatement();

    feedData.execute("drop table if exists PROCEDURES");
    feedData.executeUpdate("create TABLE PROCEDURES(P_ID varchar(255), D_ID varchar(255), H_NAME varchar(255), S_TIME varchar(255), E_TIME varchar(255), P_DATE varchar(255));");
    String today = LocalDate.now().toString();
    feedData.executeUpdate(String.format("INSERT INTO procedures values ('45', 'aDoc93', 'ENFANT-JESUS', '10:25:00', '12:25:00', '%s');",
                                         today));
    feedData.executeUpdate(String.format("INSERT INTO procedures values ('46', 'aDoc93', 'ENFANT-JESUS', '08:00:00', '10:00:00', '%s');",
                                         today));
    feedData.executeUpdate(String.format("INSERT INTO procedures values ('47', 'aDoc93', 'ENFANT-JESUS', '13:00:00', '17:00:00', '%s');",
                                         today));
    feedData.close();
    connection.close();
  }

}
