package ca.ulaval.glo4003.lab.legacy.ws.domain.md;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import ca.ulaval.glo4003.lab.legacy.DoctorBillingMain;

public class BillingService {

  public double dailyTotalOf(String id, LocalDate date) throws SQLException {
    Connection c = null;
    PreparedStatement st = null;

    c = DriverManager.getConnection(DoctorBillingMain.DB_URL);

    c.setAutoCommit(false);

    st = c.prepareStatement("Select * from PROCEDURES");
    ResultSet r = st.executeQuery();

    Double l_valeur = 0.0;

    while (r.next()) {

      if (r.getString("d_id").equals(id)) {
        if (LocalDate.parse(r.getString("p_date")).isEqual(date)) {
          LocalDateTime sDT =
                            LocalDateTime.parse(LocalDate.parse(r.getString("p_date"))
                                                + "T"
                                                + LocalTime.parse(r.getString("s_time")));
          LocalDateTime eDT =
                            LocalDateTime.parse(LocalDate.parse(r.getString("p_date"))
                                                + "T"
                                                + LocalTime.parse(r.getString("e_time")));

          Duration duree;
          if (sDT.isAfter(eDT)) {
            duree = Duration.between(sDT, eDT.plusHours(24));
          } else {
            duree = Duration.between(LocalTime.parse(r.getString("s_time")),
                                     LocalTime.parse(r.getString("e_time")));
          }

          l_valeur += 600 * (duree.toHours() / 8.0);
        }
      }
    }

    return l_valeur;
  }

}
