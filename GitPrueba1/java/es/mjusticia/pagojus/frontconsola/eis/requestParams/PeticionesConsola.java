package es.mjusticia.pagojus.frontconsola.eis.requestParams;

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

import java.util.Map;

public interface PeticionesConsola {

  public static final String ORDEN_TYPE = "ORDEN";
  public static final String COL_ORDEN = "COL_ORDEN";
  public static final String N_PAGINA = "N_PAGINA";
  public static final String TAMANIO_PAGINA = "TAMANIO_PAGINA";
  public static final String ORDER_ASC = "ASC";
  public static final String ORDER_DESC = "DESC";

  public static final int TAMANIO_PAGINA_DEFAULT = 10;
  public static final int N_PAGINA_DEFAULT = 1;
  public static final String ORDER_DEFAULT = ORDER_ASC;
  public static final int MIN_VALUE_PAGINACION = 1;
  public static final int MAX_VALUE_PAGINACION = 1000;

  Map<String, Object> getPaginadoYOrden();

}
