package es.mjusticia.pagojus.frontconsola.enums;

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

public enum EstadosPermitidos {
  INFORMAR_PAGADOR(Estados.INICIADO + " " + Estados.PAGADOR_INFORMADO), HACER_PAGO(Estados.PAGADOR_INFORMADO.name()),
  DESCARGAR_JUSTIFICANTE(Estados.PAGADOR_INFORMADO + " " + Estados.NO_PAGADO + " " + Estados.PAGADO + " "
      + Estados.PARCIALMENTE_PAGADO + " " + Estados.FINALIZADO + " " + Estados.PARCIALMENTE_UTILIZADO),
  BLOQUEAR_TASAS(Estados.PAGADO + " " + Estados.PARCIALMENTE_PAGADO + " " + Estados.PARCIALMENTE_UTILIZADO),
  CONSOLA_EDITAR_ESTADO_TASA(Estados.PAGADO + " " + Estados.DEVUELTO + " " + Estados.UTILIZADO);
  ;

  private String permitidos;

  EstadosPermitidos(String permitidos) {
    this.permitidos = permitidos;
  }

  public String getPermitidos() {
    return permitidos;
  }

  public static String validarEstado(String estadosPermitidos, Estados estadoActual) {
    return EstadosPermitidos.validarEstado(estadosPermitidos, estadoActual.name());
  }

  public static String validarEstado(String estadosPermitidos, String estadoActual) {
    if (estadosPermitidos == null || estadoActual == null) {
      return "Error en la validacicion de estados permitidos, argumento de entrada nulo";
    }
    String[] estadosPermitidosArray = estadosPermitidos.split(" ");
    for (String estadoPermitido : estadosPermitidosArray) {
      if (estadoPermitido.equalsIgnoreCase(estadoActual)) {
        return null;
      }
    }
    return CodigosError.CODE_409_50.getMensajeRespuesta(estadoActual, estadosPermitidos);
  }
}
