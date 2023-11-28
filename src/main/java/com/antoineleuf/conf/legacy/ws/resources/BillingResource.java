package com.antoineleuf.conf.legacy.ws.resources;

import java.sql.SQLException;
import java.time.LocalDate;

import com.antoineleuf.conf.legacy.ws.domain.md.BillingService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/doctors/{doctorId}/billing")
public class BillingResource {

  private BillingService billingService;

  public BillingResource(BillingService billingService) {
    this.billingService = billingService;
  }

  @GET
  public Response getAllBillingOfToday(@PathParam("doctorId") String medId) {
    try {
      return Response.ok(billingService.dailyTotalOf(medId, LocalDate.now())).build();
    } catch (SQLException e) {
      e.printStackTrace();
      return Response.serverError().build();
    }
  }

}
