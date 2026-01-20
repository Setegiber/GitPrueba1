package es.mjusticia.pagojus.frontconsola.enums;

import es.mjusticia.pagojus.frontconsola.utils.Utils;

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

public enum Estados {

  NO_INICIADO(""), INICIADO("Iniciado"), PAGADOR_INFORMADO("Pago pendiente"), PENDIENTE_PAGO(""),
  PAGADO(Utils.PAGO_REALIZADO_DESC), NO_PAGADO(""), UTILIZADO(""), PARCIALMENTE_PAGADO(Utils.PAGO_REALIZADO_DESC),
  PARCIALMENTE_UTILIZADO(Utils.PAGO_REALIZADO_DESC), FINALIZADO(Utils.PAGO_REALIZADO_DESC), ERROR_ENVIO(""),
  ENVIADO(""), DEVUELTO("Devuelto"),
  // NO_ESTADO. Sirve para no devolver nulos.
  // ademas isEstado("NO_ESTADO") devuelve false.
  NO_ESTADO("");

  private final String descripcion;

  Estados(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public static boolean isEstado(String test) {
    test = test.toUpperCase();
    for (Estados estado : Estados.values()) {
      if (estado.name().equals(test) && !estado.equals(NO_ESTADO)) {
        return true;
      }
    }
    return false;
  }

  public static Estados getEstado(String test) {
    test = test.toUpperCase();
    for (Estados estado : Estados.values()) {
      if (estado.name().equals(test)) {
        return estado;
      }
    }
    return NO_ESTADO;
  }
}
