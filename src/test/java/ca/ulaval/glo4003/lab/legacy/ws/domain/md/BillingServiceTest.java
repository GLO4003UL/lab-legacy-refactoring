package ca.ulaval.glo4003.lab.legacy.ws.domain.md;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import ca.ulaval.glo4003.lab.legacy.DoctorBillingMain;

public class BillingServiceTest {

  private static final int PROCEDURE_ID_FOR_TEST = 9900;

  private static Connection connection;

  private BillingService service = new BillingService();

  @BeforeAll
  public static void before() throws SQLException {
    connection = DriverManager.getConnection(DoctorBillingMain.DB_URL);
  }

  @AfterEach
  public void cleanUp() throws SQLException {
    Statement deleteDataStatement = connection.createStatement();
    deleteDataStatement.execute(String.format("delete from procedures where p_id='%s'",
                                              PROCEDURE_ID_FOR_TEST));
  }

  @AfterAll
  public static void kill() throws SQLException {
    connection.close();
  }

  @Test
  public void given_when_then() throws SQLException {
    // given

    // when
    service.dailyTotalOf("A_DOCTOR", LocalDate.now());

    // then
    Truth.assertThat(false).isFalse();
  }
}
