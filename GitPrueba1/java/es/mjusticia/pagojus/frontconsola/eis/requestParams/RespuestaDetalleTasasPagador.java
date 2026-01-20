package es.mjusticia.pagojus.frontconsola.eis.requestParams;

/*-
 * #%L
 * pagojus-restws
 * %%
 * Copyright (C) 2023 - 2024 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.mjusticia.pagojus.frontconsola.model.TasasBusquedaConsolaDto;
import java.util.List;

public class RespuestaDetalleTasasPagador {

  @JsonProperty("Detallepagotasa")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<TasasBusquedaConsolaDto> resultados;

  @JsonProperty("Total Resultados")
  private long totalResultados;

  public List<TasasBusquedaConsolaDto> getResultados() {
    return resultados;
  }

  public void setResultados(List<TasasBusquedaConsolaDto> resultados) {
    this.resultados = resultados;
  }

  public long getTotalResultados() {
    return totalResultados;
  }

  public void setTotalResultados(long totalResultados) {
    this.totalResultados = totalResultados;
  }

}
