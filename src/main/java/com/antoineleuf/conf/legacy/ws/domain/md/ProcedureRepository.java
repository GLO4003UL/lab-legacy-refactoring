package com.antoineleuf.conf.legacy.ws.domain.md;

import java.util.List;

public interface ProcedureRepository {

  void add(Procedure procedure);

  List<Procedure> findAll();

}
