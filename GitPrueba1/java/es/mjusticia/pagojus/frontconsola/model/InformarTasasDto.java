package es.mjusticia.pagojus.frontconsola.model;

/*-
 * #%L
 * pagojus-restws
 * %%
 * Copyright (C) 2023 - 2025 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
 * %%
 * Licencia con arreglo a la EUPL, Versión 1.2 o –en cuanto
 *  sean aprobadas por la Comisión Europea– versiones
 *  posteriores de la EUPL (la «Licencia»)
 *  Solo podrá usarse esta obra si se respeta la Licencia.
 *  Puede obtenerse una copia de la Licencia en:
 * 
 *  https://joinup.ec.europa.eu/software/page/eupl
 * 
 *  Salvo cuando lo exija la legislación aplicable o se acuerde
 *  por escrito, el programa distribuido con arreglo a la
 *  Licencia se distribuye «TAL CUAL»,
 *  SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas
 *  ni implícitas.
 *  Véase la Licencia en el idioma concreto que rige
 *  los permisos y limitaciones que establece la Licencia
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonIgnore;

public class InformarTasasDto {

  private String codTasa;
  private String descTasa;

  @JsonIgnore
  private boolean flgBorrado;

  // Constructor
  public InformarTasasDto(String codTasa, String descTasa, boolean flgBorrado) {
    this.codTasa = codTasa;
    this.descTasa = descTasa;
    this.flgBorrado = flgBorrado;
  }

  // Getters y Setters
  public String getCodTasa() {
    return codTasa;
  }

  public void setCodTasa(String codTasa) {
    this.codTasa = codTasa;
  }

  public String getDescTasa() {
    return descTasa;
  }

  public void setDescTasa(String descTasa) {
    this.descTasa = descTasa;
  }

  public boolean isFlgBorrado() {
    return flgBorrado;
  }

  public void setFlgBorrado(boolean flgBorrado) {
    this.flgBorrado = flgBorrado;
  }

}
