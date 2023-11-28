package com.antoineleuf.conf.legacy.ws.domain.md;

import java.time.LocalDateTime;

public class Procedure {

  private String hName;
  private LocalDateTime sTime;
  private LocalDateTime eTime;
  private String dId;

  public Procedure(String dId, String hName, LocalDateTime sTime, LocalDateTime eTime) {
    this.dId = dId;
    this.hName = hName;
    this.sTime = sTime;
    this.eTime = eTime;
  }

  public String getHospitalName() {
    return hName;
  }

  public LocalDateTime getEndTime() {
    return eTime;
  }

  public LocalDateTime getStartTime() {
    return sTime;
  }

  public String getDoctorId() {
    return dId;
  }

}
