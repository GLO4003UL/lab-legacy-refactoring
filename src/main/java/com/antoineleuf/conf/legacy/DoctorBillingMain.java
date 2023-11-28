package com.antoineleuf.conf.legacy;

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

import com.antoineleuf.conf.legacy.ws.domain.md.BillingService;
import com.antoineleuf.conf.legacy.ws.resources.BillingResource;

import jakarta.ws.rs.core.Application;

/**
 * RESTApi setup without using DI or spring
 */
@SuppressWarnings("all")
public class DoctorBillingMain {

  public static final String DB_URL =
                                    "jdbc:postgresql://db-conf-legacy-do-user-1743345-0.b.db.ondigitalocean.com:25060/doctordb?sslmode=require";
  private static final Logger LOGGER = LoggerFactory.getLogger(DoctorBillingMain.class);

  public static boolean isDev = true; // Would be a JVM argument or in a .property file

  public static void main(String[] args) throws Exception {
    LOGGER.info("Setup resources (API)");
    BillingService billingService = createBillingService();
    BillingResource billingResource = new BillingResource(billingService);

    LOGGER.info("Setup API context (JERSEY + JETTY)");
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/api");
    ResourceConfig resourceConfig = ResourceConfig.forApplication(new Application() {
      @Override
      public Map<String, Object> getProperties() {
        return Map.of("jersey.config.server.wadl.disableWadl", "true");
      }

      @Override
      public Set<Object> getSingletons() {
        HashSet<Object> resources = new HashSet<>();
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
    // Setup resources' dependencies (DOMAIN + INFRASTRUCTURE)
    BillingService contactService = new BillingService();

    return contactService;
  }

}
