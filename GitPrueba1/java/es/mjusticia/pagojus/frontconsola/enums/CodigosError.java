package es.mjusticia.pagojus.frontconsola.enums;

import java.text.MessageFormat;

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

public enum CodigosError {
  CODE_200("OK"), CODE_400_1("Los campos obligatorios {0} no están informados."),

  CODE_400_2("Los campos {0} no cumplen con la longitud permitida."),
  CODE_400_3("No se cumple el formato del campo {0}."),

  CODE_401("Aplicacion no autorizada a consumir el servicio."),

  CODE_409_1("Se ha producido un error en {0}."),
  CODE_409_2("No existe ninguna tasa valida en nuestro sistema para el código de tasa {0} adjuntada en la petición."),
  CODE_409_3("El NRC adjuntado a la petición no existe en nuestro sistema."),
  CODE_409_4("El documento de identidad adjuntado a la petición no tiene relación con el NRC."),
  CODE_409_5("El NRC no puede ser utilizado en este trámite."),
  CODE_409_6("El importe del NRC no corresponde con el importe actual de la tasa"),
  CODE_409_7("La tasa ya se encuentra bloqueada."),
  CODE_409_8("Las tasas recibidas no pertenecen a la misma aplicación."),
  CODE_409_9("Alguna de las tasas recibidas no puede pagarse por el metodo de pago indicado en la petición."),
  CODE_409_10("El localizador adjuntado a la petición no existe en nuestro sistema."),
  CODE_409_11("El localizador adjuntado a la petición no tiene relación con el documento de identidad."),
  CODE_409_12(
      "No se ha encontrado ninguna aplicacion asociada a los codigos de Tasa y metodo de pago enviados con la peticion."),
  CODE_409_13("El metodo de pago adjuntado en la peticion no es aplicable."),
  CODE_409_14("No se ha encontrado ninguna aplicación asociada a todos los códigos de Tasa enviados con la petición."),
  CODE_409_15(
      "No se ha encontrado ningun documento identificador del pagador relacionado con el localizador adjuntado a la petición."),
  CODE_409_16("No se ha encontrado ninguna tasa vigente."), CODE_200_20("{0}"),
  CODE_409_17("No se ha encontrado ningun metodo de pago activo"),
  CODE_409_18("No se ha encontrado ninguna entidad financiera"),
  CODE_200_21("Pago incompleto, alguna de las tasas enviadas en la solicitud no ha podido ser pagada"),
  CODE_409_22(
      "No se permite pagar ninguna de las tasas enviadas en la solicitud. El estado del pago debe ser 'PAGADOR_INFORMADO'. "),
  CODE_500_23("Error en ePago. Código interno ePago: {0}. Descripción: {1}"),
  CODE_500_24("Error en RedSys. Código interno RedSys: {0}. Descripción: {1}"),
  CODE_409_40(
      "Posible incoherencia en la base de datos. No se ha podido encontrar una traducción para {0}, en el idioma '{1}'."),
  CODE_409_50("Operacion no permitida, el Estado del pago {0} es incorrecto; los estados permitidos son: {1}. "),
  CODE_409_60("No se ha encontrado ninguna tasa asociada a la aplicacion."),
  CODE_409_61("No se han encontrado ninguna tasa asociada al pago."),
  CODE_409_62("La tasa asociada al pago no pertenece a la aplicacion."),
  CODE_409_63("Incoherencia de resultados en la busqueda."),
  CODE_409_64("No existe ninguna tasa en nuestro sistema para el código de tasa adjuntada en la petición."),
  CODE_409_403("El usuario no tiene permisos para realizar esta accion"), CODE_500(""),

  CODE_MOCK_EPAGO_01("Número de cuenta invalido"), CODE_MOCK_EPAGO_02("Número de tarjeta invalido"),

  WARNING_IDIOMA(
      "No existe una traduccion para el idioma especificado en la peticion, {0}. Se utilizara el idioma por defecto.");

  private String mensajeRespuesta;

  CodigosError(String mensajeRespuesta) {
    this.mensajeRespuesta = mensajeRespuesta;
  }

  public String getMensajeRespuesta() {
    return mensajeRespuesta;
  }

  public String getMensajeRespuesta(String... params) {
    String errorMsg = this.getMensajeRespuesta();
    if (params != null) {
      errorMsg = MessageFormat.format(errorMsg, (Object[]) params);
    }

    return errorMsg;
  }
}
