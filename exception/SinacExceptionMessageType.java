package es.mjusticia.sinac.core.business.exception;

/*-
 * #%L
 * sinac-core
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

public enum SinacExceptionMessageType {

  MESSAGE_1("El informe MJU del expediente {} no tiene registro de antecedentes o son nulos.", null),
  MESSAGE_2("El informe CNI del expediente {} no tiene registro de antecedentes o son nulos.", null),
  MESSAGE_3("El informe MDE del expediente {} no tiene registro de antecedentes o son nulos.", null),
  MESSAGE_4("Error, el sha256 no es correcto", null), MESSAGE_5("Error, no se ha encontrado el sha256 ", null),
  MESSAGE_6("Error, no se ha encontrado el contenido del documento ", null),
  MESSAGE_7("El interesado no puede ser igual que el representante legal 1", null),
  MESSAGE_8("El interesado no puede ser igual que el representante legal 2", null),
  MESSAGE_9("El interesado no puede ser igual que el representante mandato", null),
  MESSAGE_10("El representante legal 1 no puede ser igual que el representante legal 2", null),
  MESSAGE_11("El representante legal 1 no puede ser igual que el representante mandato", null),
  MESSAGE_12("El representante legal 2 no puede ser igual que el representante mandato", null),
  MESSAGE_13("Se produjo un NullPointerException en la agrupación",
      "No se han podido recuperar ProcedimientosPorLdvMaestra"),
  MESSAGE_14("El expediente, el aviso o el usuario no fueron encontrados.", null),
  MESSAGE_15("Se produjo un NullPointerException en la agrupación", "No se han podido recuperar los avisos"),
  MESSAGE_16("No se encontró el expediente con ID: {0}, al traer los avisos expediente",
      "Se ha producido un error al identificar el expediente."),
  MESSAGE_17("Se ha producido un error por un valor nulo inesperado en los datos de los avisos.", null),
  MESSAGE_18("Ocurrió un error inesperado al obtener los avisos del expediente con ID: {0}",
      "Se produjo un error inesperado al recuperar los avisos expediente."),
  MESSAGE_19("Se ha producido un error al procesar los avisos del expediente encontrando un valor nulo.", null),
  MESSAGE_20("Ocurrió un error inesperado al recuperar los avisos del expediente.",
      "No se han podido recuperar los avisos del expediente debido a un error inesperado."),
  MESSAGE_21("NullPointerException detectado durante el cambio de estado del aviso expediente con ID: {0}",
      "Se ha producido un error al recuperar el expediente aviso, por algún valor nulo inesperado"),
  MESSAGE_22("Ocurrió un error inesperado al cambiar el estado del aviso expediente con ID: {0}",
      "Error inesperado al cambiar el estado del aviso."),
  MESSAGE_23("Error: El servicio de firma devolvió un resultado nulo al firmar el índice electronico.",
      "No se ha podido generar el expediente ENI porque ha ocurrido un error en la firma del índice del expediente."),
  MESSAGE_24("No se ha podido generar la plantilla porque el documento odt está mal formado", null),
  MESSAGE_25("El id del Documento es nulo ", null), MESSAGE_26("El id de Expediente es nulo", null),
  MESSAGE_27("Error genérico: no se ha obtenido resultado de Notifica", null),
  MESSAGE_28("Se produjo un error inesperado al procesar los avisos de expedientes.", null),
  MESSAGE_29("No se pudo completar la búsqueda de avisos de expedientes, debido a un error inesperado", null),
  MESSAGE_30("No se ha podido guardar la solicitud", null), MESSAGE_31("El id de la notificación de SUN es nulo", null),
  MESSAGE_32("El nif  es nulo ", null), MESSAGE_33("Error de formato de fechas", null),
  MESSAGE_34("Los parámetros no pueden ser nulos", null),
  MESSAGE_35("No se pudo recuperar el documento del Gestor Documental.", null),
  MESSAGE_36("Error al recuperar el documento ENI.", null),
  MESSAGE_37("No se ha guardado en Base de Datos el informe DGP ", null),
  MESSAGE_38("No se ha guardado en Base de Datos la renovacion dni ", null),
  MESSAGE_39("No se ha guardado en Base de Datos el trámite del informe DGP ", null),
  MESSAGE_40("El informe ya se encuentra en estado pendiente.", null),
  MESSAGE_41("El informe ya se encuentra en estado solicitado.", null),
  MESSAGE_42("No se puede vincular el expediente de origen o uno ya vinculado", null),
  MESSAGE_43("No se ha guardado en Base de Datos la secuencia del expediente ", null),
  MESSAGE_44("Error al recuperar los últimos avisos: posible valor nulo inesperado", null),
  MESSAGE_45("Error de argumentos inválidos al procesar los últimos avisos", null),
  MESSAGE_46("No se han podido recuperar los expedientes avisos por un error inesperado.", null),
  MESSAGE_47("No se han podido recuperar el interesado", null), MESSAGE_48("", null),
  MESSAGE_49("El informe de la DGP no ha podido guardarse correctamente por que su contenido es nulo", null),
  MESSAGE_50("Se ha producido un error al actualizar el informe con el nuevo documento", null),
  MESSAGE_51("Error en la remisión a Justicia en INSIDE", null),
  MESSAGE_52("Error en la consulta de remisión a Justicia en INSIDE", null),
  MESSAGE_53("No se ha podido actualizar la validación.", null),
  MESSAGE_54("Ha habido un error al recuperar la lista de los informes para el expediente.", null),
  MESSAGE_55("No se ha podido recuperar la lista de tareas planificadas", null),
  MESSAGE_56("No se ha podido ejecutar la tarea planificada", null),
  MESSAGE_57("No se han podido recuperar las observaciones", null),
  MESSAGE_58("No se ha podido recuperar el id de la plantilla", null),
  MESSAGE_59("Se ha producido un error al no poder identificar el procedimiento aviso.", null),
  MESSAGE_60("Se produjo un error inesperado al procesar la solicitud, debido a un valor nulo.", null),
  MESSAGE_61("No se pudo completar la actualización debido a un error insesperado.", null),
  MESSAGE_62("Error al recuperar los procedimientos validados o al realizar su tratamiento", null),
  MESSAGE_63("Error durante el proceso de generación del Requerimiento en Estado \"Borrador\".", null),
  MESSAGE_67("Error al enviar el correo", null),
  MESSAGE_68(
      "PortafirmasConnectorImpl.createRequest - Error: La Petición no ha podido ser creada en el Servidor de Portafirmas porque ha habido un error durante el proceso de creación de la Petición.",
      null),
  MESSAGE_69(
      "PortafirmasConnectorImpl.sendRequest - Error: La Petición no ha podido ser enviada a Portafirmas porque ha habido un error durante el proceso de envío de la Petición.",
      null),
  MESSAGE_70("No se ha podido generar el registro ", null),
  MESSAGE_71("No se ha podido caducar el requerimiento porque el plazo no tiene asociado un requerimiento", null),
  MESSAGE_72("Error obteniendo Fecha de Subsanación.", null),
  MESSAGE_73("Se ha producido un error al recibir la respuesta del informe", null),
  MESSAGE_74("Error al registrar las comunicacion externa", null), MESSAGE_75("Error solicitando informe CNI", null),
  MESSAGE_76("Error construyendo DocumentoTipoEntitySpecification", null),
  MESSAGE_79("Error transformando estado en JSON", null), MESSAGE_80("No se ha podido generar el token WOPI", null),
  MESSAGE_81("Documento WOPI no coincide", null), MESSAGE_82("Token WOPI expirado", null),
  MESSAGE_83("No se ha podido verificar el token WOPI", null),
  MESSAGE_88("Error leyendo fichero de recepción en NFS", null),
  MESSAGE_89("Error leyendo fichero de solicitud en NFS", null),
  MESSAGE_90("Se ha producido un error inesperado al obtener los ajustes de los avisos de procedimientos.", null),
  MESSAGE_92("Error.", null), MESSAGE_93("Test Exception", null), MESSAGE_94("Error", null),
  MESSAGE_95("test message", null), MESSAGE_96("Procedimiento no encontrado", null),
  MESSAGE_97("Plazo no encontrado", null), MESSAGE_98("No se han podido añadir las validaciones al expediente 1", null),
  MESSAGE_99("Error con el formato de las fechas", null), MESSAGE_100("Error al generar el índice", null),
  MESSAGE_101("Error simulado", null),
  MESSAGE_102("Se ha producido un error al realizar la petición de alta de filiación", null),
  MESSAGE_103(
      "Error al invocar al listado de la Dgp. El listado devuelve un error por algún dato erróneo en la petición. Respuesta: {0}",
      null),
  MESSAGE_104(
      "Se ha producido un error intentar actualizar las validacion del Informe {0} del semaforo, para el expediente {1}",
      null),
  MESSAGE_105("No es posible sincronizar el envío de la notificación {0} porque el estado recibido está vacío", null),
  MESSAGE_106("Se ha producido un error al intentar guardar el documento de acuse y la notificación", null),
  MESSAGE_107("Se ha producido un error al intentar abrir el expediente en GD", null),
  MESSAGE_108("Se ha producido un error al intentar solicitar los informes disponibles", null),
  MESSAGE_109("Se ha producido un error al intentar generar el documento de DGP", null),
  MESSAGE_110("Se ha producido un error al obtener el documento para guardar", null),
  MESSAGE_111("No se encontró el expediente aviso con ID: {0}", "No se pudo identificar el expediente aviso"),
  MESSAGE_112("Error recuperando catalogo {0}", null),
  MESSAGE_113("Error recuperando catalogo {0} para el codigo {1}", null),
  MESSAGE_114(
      "No se ha encontrado en Base de Datos el Tipo de Documento asociado al Identificador de Tipo de Documento \"{0}\"",
      null),
  MESSAGE_115("Error al recuperar el Tipo de Documento asociado al Identificador de Tipo de Documento \"{0}\"", null),
  MESSAGE_116(
      "No se ha encontrado en Base de Datos la Solicitud del Documento asociada al Identificador de Tipo de Documento \"{0}\"",
      null),
  MESSAGE_117("Error al recuperar la Solicitud del Documento asociada al Identificador de Tipo de Documento \"{0}\"",
      null),
  MESSAGE_118("Error no se ha encontrado el fichero con nombre '{0}' en la ruta '{1}'", null),
  MESSAGE_119("Error, el fichero {0} no tiene extension odt", null),
  MESSAGE_120("Error en la composicion del nombre del fichero {0}, no tiene punto", null),
  MESSAGE_121("No se ha guardado en Base de Datos el documento {0}", null),
  MESSAGE_122("No se ha encontrado en Base de Datos la Plantilla asociada al Identificador de la Plantilla \"{0}\"",
      null),
  MESSAGE_123("Error al recuperar la Plantilla asociada al Identificador de la Plantilla \"{0}\"", null),
  MESSAGE_124("No se ha encontrado la plantilla '{0}' en NFS para el expediente {1}", null),
  MESSAGE_125(
      "Se ha producido el siguiente error generando el documento en NFS a partir de la plantilla {0} del expediente {1}",
      "Se ha producido un error generando el documento en NFS a partir de la plantilla: {0}"),
  MESSAGE_126(
      "Error: No se ha podido generar el índice del expediente {0} por no encontrarse la plantilla con código: {1}",
      null),
  MESSAGE_127("Error inesperado durante la generación del índice electrónico del expediente {0}",
      "No se ha podido descargar el índice del expediente por un error inesperado."),
  MESSAGE_128("Error al insertar valores en la plantilla {0} del expediente {1}",
      "Ha ocurrido un error inesperado al recuperar el índice del expediente"),
  MESSAGE_129("Error al crear el documento de la plantilla {0} de índice del expediente {1}",
      "Ha ocurrido un error inesperado al recuperar el índice del expediente"),
  MESSAGE_130(
      "No se ha encontrado en Base de Datos el Tipo de Documento asociado al Identificador de LdvMaestra de gestor documental \"{0}\" y LdvMaestra de registro \"{1}\"",
      null),
  MESSAGE_131("Error al obtener el valor del campo {0}", null), MESSAGE_132("Documento {0} no encontrado", null),
  MESSAGE_133("Se ha producido un error", null),
  MESSAGE_134("No se han recuperado los ProcedimientosDocumentosTipoEnity del procedimiento {0}", null),
  MESSAGE_135("No se ha podido recuperar el Documento con Identificador: {0}", null),
  MESSAGE_136("No se ha podido recuperar el Documento {0} con nombre: {1} del Gestor Documental.", null),
  MESSAGE_137("No se ha podido recuperar el Documento {0} de la NAS.", null),
  MESSAGE_138("No se ha encontrado el Documento con Identificador: {0}", null),
  MESSAGE_139("Error obteniendo fichero temporal para el documento {0}",
      "No se ha podido recuperar el documento indicado"),
  MESSAGE_140("Error recuperando la copia auténtica del documento {0}",
      "No se ha podido recuperar el documento indicado"),
  MESSAGE_141(
      "No se puede recuperar la copia auténtica del documento {0} ya que no se encuentra en el Gestor Documental",
      null),
  MESSAGE_142("Error al recuperar el Documento asociado al Identificador de Documento \"{0}\"", null),
  MESSAGE_143("No se ha podido recuperar los documentos del informe con id {}", null),
  MESSAGE_144("El Documento \"{0}\" no ha sido borrado en NFS porque ha habido un error durante el proceso.", null),
  MESSAGE_145(
      "El Documento \"{0}\" no ha sido guardado en el Gestor Documental porque ha habido un error durante el Proceso de guardado del Documento.",
      null),
  MESSAGE_146("Error al guardar el documento {0} en el Gestor Documental", null),
  MESSAGE_147("Error al eliminar el Registro con número {0}", null),
  MESSAGE_148("Error al insertar el Registro {0} con los Datos del Apunte Registral.", null),
  MESSAGE_149("No se ha podido generar el registro en el documento {0}", null),
  MESSAGE_150("Error durante la firma del documento '{0}': ",
      "No se ha podido generar el expediente ENI porque ha ocurrido un error en la firma del índice del expediente."),
  MESSAGE_151("Error: Respuesta inválida del servicio de firma para el documento: {0}",
      "No se ha podido generar el expediente ENI porque ha ocurrido un error en la firma del índice del expediente."),
  MESSAGE_152("Error: El servicio de firma del documento '{0}' devolvió un resultado nulo.",
      "No se ha podido generar el expediente ENI porque ha ocurrido un error en la firma del índice del expediente."),
  MESSAGE_153("No se ha podido firmar el documento {0}", null),
  MESSAGE_154("Error al insertar el Registro con los Datos del Documento {0} enviado o recibido de Portafirmas.", null),
  MESSAGE_155(
      "Error al recuperar la Lista de Firmantes del Documento asociado al Identificador de Procedimiento \"{0}\" y al Identificador de Tipo de Documento \"{1}\"",
      null),
  MESSAGE_156("No se ha encontrado el expediente informe con id: {0}", null),
  MESSAGE_157("No se puede cambiar el estado a favorable de un informe que ya tiene ese estado", null),
  MESSAGE_158("No se puede cambiar el estado a desfavorable de un informe que ya tiene ese estado", null),
  MESSAGE_159("No se ha encontrado el ldvMaestraEntity con cod: {0}", null),
  MESSAGE_160("El usuario no puede asignar el expediente",
      "Error: no se ha encontrado el expediente {0}, por lo que no se ha podido asignar al usuario {1}"),
  MESSAGE_161("No se puede desasignar el usuario del expediente",
      "Error: no se ha encontrado el expediente {0} debido a que no se ha encontrado o porque el usuario {1} no está asignado a él"),
  MESSAGE_162("No se ha encontrado la plantilla seleccionada.", null),
  MESSAGE_163("No se encontró el Documento con el ID: {0}", null),
  MESSAGE_164("No se encontró el expediente con el ID: {0}", null),
  MESSAGE_165(
      "No se ha podido enviar el correo ya que el documento {0} no se encuentra en Gestor Documental y no se ha podido adjuntar",
      "El documento {1} no se encuentra en Gestor Documental, por lo que no se ha podido adjuntar al correo su copia auténtica para el expediente {2}"),
  MESSAGE_166("El ExpedientePersona de nif {0} y del expediente {1} no ha sido encontrado", null),
  MESSAGE_167(
      "El expediente {0} no se ha encontrado en BBDD, por lo que no se ha podido recibir su informe de {1} encontrado en el listado",
      null),
  MESSAGE_168(
      "No se puede solicitar el informe DGP para el expediente {0} porque el código de procedimiento indicado no está contemplado entre las opciones",
      null),
  MESSAGE_169(
      "ExpedientesServiceImpl.saveDocumentoInterno Ha habido un error en el proceso de generación de la plantilla por lo que no se puede recupera el documento para guardarlo en el gestor documental con el expediente: {0}",
      null),
  MESSAGE_170("Se ha superado el número de reintentos para convertir el documento {0} a pdf", null),
  MESSAGE_171("Error en la simulación de portafirmas ", null),

  SINAC_MESSAGE_1("No se ha encontrado el expediente documento con idDocExp={0}", null),
  SINAC_MESSAGE_2("La operación indicada ({0}) no es correcta o no esta contemplada", null),
  SINAC_MESSAGE_3("No se ha encontrado el ldvMaestraEntity con cod: {0}", null),
  SINAC_MESSAGE_4("Error al extraer el tipo mime del archivo {0}", null),
  SINAC_MESSAGE_5("Error al enviar el documento {0} a GEISER: {1}", null),
  SINAC_MESSAGE_6("Error al descargar el justificante de GEISER: {0}", null),
  SINAC_MESSAGE_7("Error al actualizar el estado para el asiento  %d", null),
  SINAC_MESSAGE_8("No se ha encontrado en Base de Datos el informe asociado al Identificador del informe \"{0}\"",
      null),
  SINAC_MESSAGE_9("Error al recuperar el informe asociado al Identificador del informe \"{0}\"", null),
  SINAC_MESSAGE_10("Error no se ha encontrado el fichero con nombre '{0}' en la ruta '{1}'", null),
  SINAC_MESSAGE_11("Error, el fichero no tiene extension odt ('{0}')", null),
  SINAC_MESSAGE_12("Error en la composicion del nombre del fichero, no tiene punto ('{0}')", null),
  SINAC_MESSAGE_13("No se ha encontrado el Registro con los Datos del Documento asociado al Identificador \"{0}\"",
      null),
  SINAC_MESSAGE_14(
      "Error al actualizar a no vigente el Registro con los Datos del Documento asociado al Identificador \"{0}\"",
      null),
  SINAC_MESSAGE_15("No se ha encontrado la Información del Documento asociado al Identificador de Documento \"{0}\"",
      null),
  SINAC_MESSAGE_16("Error al recuperar la Información del Documento asociado al Identificador de Documento \"{0}\"",
      null),
  SINAC_MESSAGE_17("No se ha encontrado el Documento asociado al Identificador de Documento \"{0}\"", null),
  SINAC_MESSAGE_18("Error al actualizar el Documento asociado al Identificador de Documento \"{0}\"", null),
  SINAC_MESSAGE_19(
      "No se ha encontrado el Registro con los Datos del Documento enviado a Portafirmas asociado al Identificador de la Solicitud de Firma \"{0}\"",
      null),
  SINAC_MESSAGE_20(
      "Error al recuperar el Registro con los Datos del Documento enviado a Portafirmas asociado al Identificador de la Solicitud de Firma \"{0}\"",
      null),
  SINAC_MESSAGE_21("No se ha encontrado el Documento asociado al Identificador de Documento \"{0}\"", null),
  SINAC_MESSAGE_22("Error al actualizar el Estado del Documento asociado al Identificador de Documento \"{0}\"", null),
  SINAC_MESSAGE_23("No se puede validar un documento validado", null),
  SINAC_MESSAGE_24("No se puede rechazar un documento rechazado", null),
  SINAC_MESSAGE_25("No se ha encontrado el ldvMaestraEntity con cod: {0}", null),
  SINAC_MESSAGE_26("Error en el proceso de registro del documento {0}", null),
  SINAC_MESSAGE_27("Error en el proceso de firma del documento {0}", null),
  SINAC_MESSAGE_28("Error en el proceso de guardado del documento {0} en el Gestor Documental", null),
  SINAC_MESSAGE_29("Error, El documento {0} no tiene código de gestor documental", null),
  SINAC_MESSAGE_30(
      "El documento {0} no se ha podido notificar porque la persona {1}  tiene una identificación principal distinta a DNI o NIE  en el expediente {2} - {3}",
      "No se ha podido notificar el documento porque el destinatario indicado tiene una identificación principal distinta a DNI o NIE"),
  SINAC_MESSAGE_31(
      "El documento {0} no se ha podido notificar porque el interesado tiene una identificación principal distinta a NIE en el expediente {1} - {2}",
      "No se ha podido notificar el documento porque el interesado tiene una identificación principal distinta a NIE"),
  SINAC_MESSAGE_32(
      "El documento {0} no se encuentra en Gestor Documental, por lo que no se ha podido notificar su copia auténtica para el expediente {1}",
      "No se ha podido notificar el documento porque no se encuentra guardado en Gestor documental y no se ha podido generar su copia auténtica"),
  SINAC_MESSAGE_33("No se ha podido desactivar los documentos con id {0}", null),

  SINAC_EXPEDIENTES_1("No se ha guardado en Base de Datos el expediente para la solicitud {0}", null),
  SINAC_EXPEDIENTES_2("No se ha guardado en Base de Datos el valor del formulario del expediente {0}", null),
  SINAC_EXPEDIENTES_3("No se ha guardado en Base de Datos las personas del expediente {0}", null),
  SINAC_EXPEDIENTES_4(
      "No se ha encontrado en Base de Datos el Expediente asociado al  Identificador del Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_5("Error al recuperar el Expediente asociado al Identificador del Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_6(
      "No se ha encontrado en Base de Datos el Expediente asociado al Identificador del Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_7("Error al recuperar el Expediente asociado al Identificador del Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_8("No se ha encontrado en Base de Datos el Expediente asociado al código del Expediente \"{0}\"",
      null),
  SINAC_EXPEDIENTES_9("Error al recuperar el Expediente asociado al código del Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_10("No se ha encontrado el Expediente asociado al número del Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_11("Error al recuperar el Expediente asociado al número del Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_12(
      "Error: no se ha encontrado el expediente {0}, por lo que no se ha podido asignar al usuario {1}",
      "El usuario no puede asignar el expediente"),
  SINAC_EXPEDIENTES_13("No se ha podido asignar el expediente {0} al usuario {1}", null),
  SINAC_EXPEDIENTES_14(
      "Error: no se ha encontrado el expediente {0} debido a que no se ha encontrado o porque el usuario {1} no está asignado a él",
      "No se puede desasignar el usuario del expediente"),
  SINAC_EXPEDIENTES_15("No se ha podido desasignar el expediente {0} del usuario {1}", null),
  SINAC_EXPEDIENTES_16("No se ha podido recuperar el expediente {0}", null),
  SINAC_EXPEDIENTES_17("Se produjo un error inesperado al procesar los avisos de expedientes.", null),
  SINAC_EXPEDIENTES_18("No se pudo completar la búsqueda de avisos de expedientes, debido a un error inesperado", null),
  SINAC_EXPEDIENTES_19("No se ha podido actualizar el expediente {0}", null),
  SINAC_EXPEDIENTES_20("No se ha podido desactivar la persona del expediente {0}", null),
  SINAC_EXPEDIENTES_21("No se han podido actualizar los datos del expediente {0}", null),
  SINAC_EXPEDIENTES_22("No se ha podido descativar el expediente VAL del expediente {0}", null),
  SINAC_EXPEDIENTES_23("No se ha podido guardar el expediente VAL del expediente {0}", null),
  SINAC_EXPEDIENTES_24("No se ha podido guardar la solicitud", null),
  SINAC_EXPEDIENTES_25("No se ha podido guardar las personas del expediente {0}", null),
  SINAC_EXPEDIENTES_26("No se ha podido guardar las personas del expediente {0}", null),
  SINAC_EXPEDIENTES_27("No se ha guardado en Base de Datos la comunicación externa del expdiente {0}", null),
  SINAC_EXPEDIENTES_28("Ha habido un error al intentar solicitar el informe", null),
  SINAC_EXPEDIENTES_29("Ha habido un error al intentar solicitar el informe", null),
  SINAC_EXPEDIENTES_30("{mensajeError}", null),
  SINAC_EXPEDIENTES_31("No se ha encontrado el ldvMaestraEntity con cod: {0}", null),
  SINAC_EXPEDIENTES_32("No se ha guardado en Base de Datos la Notificación del documento {0}", null),
  SINAC_EXPEDIENTES_33("La notificación con id {0} no ha sido encontrada", null),
  SINAC_EXPEDIENTES_34("El id de la notificación de SUN es nulo", null),
  SINAC_EXPEDIENTES_35("Se ha producido un error: {0}", null),
  SINAC_EXPEDIENTES_36("No se encontró el Documento con el ID: {0}", null),
  SINAC_EXPEDIENTES_37("No se encontró el expediente con el ID: {0}", null),
  SINAC_EXPEDIENTES_38(
      "El documento {0} no se encuentra en Gestor Documental, por lo que no se ha podido adjuntar al correo su copia auténtica para el expediente {1}",
      "No se ha podido enviar el correo ya que el documento {0} no se encuentra en Gestor Documental y no se ha podido adjuntar"),
  SINAC_EXPEDIENTES_39("El ExpedientePersona de nif {0} y del expediente {1} no ha sido encontrado", null),
  SINAC_EXPEDIENTES_40("El nif  es nulo", null), SINAC_EXPEDIENTES_41("El id de Expediente es nulo", null),
  SINAC_EXPEDIENTES_42("Se ha producido un error: {0}", null),
  SINAC_EXPEDIENTES_43("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_EXPEDIENTES_44("Error de formato de fechas", null),
  SINAC_EXPEDIENTES_45("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_EXPEDIENTES_46("Error de formato de fechas", null),
  SINAC_EXPEDIENTES_47("Ha habido un error al intentar solicitar el informe", null),
  SINAC_EXPEDIENTES_48("Se ha producido un error al encontrar la carta de apoyo del expediente {0}", null),
  SINAC_EXPEDIENTES_49("Se ha producido un error recuperando los datos resumidos del expediente {0}", null),
  SINAC_EXPEDIENTES_50("Los parámetros no pueden ser nulos", null),
  SINAC_EXPEDIENTES_51("No se encontró el expediente con ID: {0}", null),
  SINAC_EXPEDIENTES_52("{e.getMessage()}", null),
  SINAC_EXPEDIENTES_53("Se ha producido un error al insertar las fechas cierre/archivo en el expediente {0}", null),
  SINAC_EXPEDIENTES_54("No se encontró el expediente con el ID: {0}", null),
  SINAC_EXPEDIENTES_55("No se ha encontrado la secuencia asociado al Identificador de Procedimiento \"{0}\"", null),
  SINAC_EXPEDIENTES_56("Error al recuperar la secuencia asociado al Identificador de Procedimiento \"{0}\"", null),
  SINAC_EXPEDIENTES_57("No se ha podido generar el registro para el documento {0}", null),
  SINAC_EXPEDIENTES_58("No se ha podido subir el documento al gestor documental {0}", null),
  SINAC_EXPEDIENTES_59("No se pudo recuperar el documento del Gestor Documental.", null),
  SINAC_EXPEDIENTES_60("Error al recuperar el documento ENI.", null),
  SINAC_EXPEDIENTES_61("No se ha encontrado la fecha de registro asociado al Identificador de Expediente \"{0}\"",
      null),
  SINAC_EXPEDIENTES_62("Error al recuperar la fecha de registro asociado al Identificador de Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_63("Error al actualizar fecha de efectos asociado al Identificador de Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_64(
      "El expediente {0} no se ha encontrado en BBDD, por lo que no se ha podido recibir su informe de {1} encontrado en el listado",
      null),
  SINAC_EXPEDIENTES_65("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_EXPEDIENTES_66("Error de formato de fechas", null),
  SINAC_EXPEDIENTES_67("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_EXPEDIENTES_68("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_EXPEDIENTES_69("No se ha guardado en Base de Datos el informe DGP ", null),
  SINAC_EXPEDIENTES_70("No se ha guardado en Base de Datos la renovacion dni ", null),
  SINAC_EXPEDIENTES_71("No se ha guardado en Base de Datos el trámite del informe DGP ", null),
  SINAC_EXPEDIENTES_72("Error de formato de fechas", null),
  SINAC_EXPEDIENTES_73("Ha habido un error al intentar solicitar el informe", null),
  SINAC_EXPEDIENTES_74("El informe ya se encuentra en estado pendiente.",
      "El informe ya se encuentra en estado pendiente."),
  SINAC_EXPEDIENTES_75("El informe ya se encuentra en estado solicitado.",
      "El informe ya se encuentra en estado solicitado."),
  SINAC_EXPEDIENTES_76("No se ha podido recuperar la información del informe con id : {0}", null),
  SINAC_EXPEDIENTES_77(
      "No se puede solicitar el informe DGP para el expediente {0} porque el código de procedimiento indicado no está contemplado entre las opciones",
      null),
  SINAC_EXPEDIENTES_78("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_EXPEDIENTES_79("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_EXPEDIENTES_80("{0}{1}", null),
  SINAC_EXPEDIENTES_81("No se puede vincular el expediente de origen o uno ya vinculado", null),
  SINAC_EXPEDIENTES_82("Error al procesar comunicaciones externas por tipo de informe \n{0}",
      "Error al procesar comunicaciones externas"),
  SINAC_EXPEDIENTES_83("No se ha guardado en Base de Datos la secuencia del expediente ", null),
  SINAC_EXPEDIENTES_84("Error al recuperar los últimos avisos: posible valor nulo inesperado", null),
  SINAC_EXPEDIENTES_85("Error de argumentos inválidos al procesar los últimos avisos", null),
  SINAC_EXPEDIENTES_86("No se han podido recuperar los expedientes avisos por un error inesperado.", null),
  SINAC_EXPEDIENTES_87("No se han podido recuperar los avisos del expediente {0}", null),
  SINAC_EXPEDIENTES_88("No se han podido recuperar los avisos del expediente {0}", null),
  SINAC_EXPEDIENTES_89("Se ha producido un error al validar los documentos de entrada del expediente {0}", null),
  SINAC_EXPEDIENTES_90("No se ha encontrado estados relaccionados con la acción {1}", null),
  SINAC_EXPEDIENTES_91("No se ha encontrado el codigo de procedimiento origen guardado en el expediente {}", null),
  SINAC_EXPEDIENTES_92("No se han podido recuperar el interesado", null),
  SINAC_EXPEDIENTES_93(
      "Ha habido un error en el proceso de generación de la plantilla por lo que no se puede recupera el documento para guardarlo en el gestor documental con el expediente: {0}",
      null),
  SINAC_EXPEDIENTES_94("", null),
  SINAC_EXPEDIENTES_95("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_EXPEDIENTES_96("El informe de la DGP no ha podido guardarse correctamente por que su contenido es nulo", null),
  SINAC_EXPEDIENTES_97("No se ha podido recuperar la lista de informes", null),
  SINAC_EXPEDIENTES_98("Error al guardar el informe en la base de datos",
      "Error al guardar el informe en la base de datos"),
  SINAC_EXPEDIENTES_99("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_EXPEDIENTES_100("Se ha producido un error al actualizar el informe con el nuevo documento", null),
  SINAC_EXPEDIENTES_101("Se ha producido un error al intentar vincular el documento al informe", null),
  SINAC_EXPEDIENTES_102(
      "No se ha encontrado en Base de Datos el Expediente asociado al Identificador del Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_103("Error al recuperar el Expediente asociado al Identificador del Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_104("No se han encontrado Informes del Expediente asociados al Identificador de Expediente \"{0}\"",
      null),
  SINAC_EXPEDIENTES_105(
      "Error al recuperar los Informes del Expediente asociados al Identificador de Expediente \"{0}\"", null),
  SINAC_EXPEDIENTES_106("No se ha encontrado el Informe asociado al Identificador de Informe \"{0}\"", null),
  SINAC_EXPEDIENTES_107("Error al actualizar el Estado del Informe asociado al Identificador de Informe \"{0}\"", null),
  SINAC_EXPEDIENTES_108("No se ha encontrado el documento asociado al Identificador de documento\"{0}\"", null),
  SINAC_EXPEDIENTES_109("Error en la remisión a Justicia en INSIDE", null),
  SINAC_EXPEDIENTES_110("Error en la consulta de remisión a Justicia en INSIDE", null),
  SINAC_EXPEDIENTES_111("Se ha producido un error al realizar la petición de consulta de nie de filiación.",
      "{e.getMessage()}"),
  SINAC_EXPEDIENTES_112("Se ha producido un error al formatear la fecha del titular", null),
  SINAC_EXPEDIENTES_113("Se ha producido un error al formatear la fecha de la referencia", null),
  SINAC_EXPEDIENTES_114("Se ha producido un error al formatear la fecha de nacimiento", null),
  SINAC_EXPEDIENTES_115("Se ha producido un error al realizar la petición de consulta de referencia de filiación.",
      null),
  SINAC_EXPEDIENTES_116("Se ha producido un error al formatear la fecha del titular", null),
  SINAC_EXPEDIENTES_117("Se ha producido un error al formatear la fecha de la referencia", null),
  SINAC_EXPEDIENTES_118("Se ha producido un error al formatear la fecha de nacimiento", null),
  SINAC_EXPEDIENTES_119("Se ha producido un error al formatear la fecha de nacimiento", null),
  SINAC_EXPEDIENTES_120("No se han podido añadir las validaciones al expediente {0}", null),
  SINAC_EXPEDIENTES_121("No se ha podido actualizar la validación.", null),
  SINAC_EXPEDIENTES_122("Ha habido un error al recuperar la lista de los informes para el expediente.", null),
  SINAC_EXPEDIENTES_123("No se encuentra ldvMaestra con id: {0}", null),
  SINAC_EXPEDIENTES_124("No se ha encontrado el expediente informe con id: {0}", null),
  SINAC_EXPEDIENTES_125("No se ha encontrado el expediente con id: {0}", null),
  SINAC_EXPEDIENTES_126("No se ha encontrado el expediente documento con id: {0}", null),
  SINAC_EXPEDIENTES_127(
      "La lista de expedientes a acumular contiene varios expedientes. La lista contiene {} elementos", null),

  SINAC_OBSERVACIONES_1("Error al intentar obtener la lista de observaciones", null),

  SINAC_PERSONAS_1("No se ha podido guardar la persona", null),
  SINAC_PERSONAS_2("No se ha encontrado el Número de Acreditación asociado al Identificador de Persona \"{0}\"", null),
  SINAC_PERSONAS_3("Error al recuperar el Número de Acreditación asociado al Identificador de Persona \"{0}\"", null),

  SINAC_PLAZOS_1("{}", null),
  SINAC_PLAZOS_2("No se ha encontrado la configuración asociada al Tipo de Acción con Identificador \"{0}\"", null),
  SINAC_PLAZOS_3("Error al recuperar la configuración asociada al Tipo de Acción con Identificador \"{0}\"", null),
  SINAC_PLAZOS_4(
      "No se ha encontrado la configuración asociada al Tipo de Acción con Identificador \"{0}\" y al Estado \"{1}\"",
      null),
  SINAC_PLAZOS_5(
      "Error al recuperar la configuración asociada al Tipo de Acción con Identificador \"{0}\" y al Estado \"{1}\"",
      null),
  SINAC_PLAZOS_6(
      "No se ha encontrado el Plazo Vigente asociado al Identificador de Expediente \"{0}\" y al Identificador de Plazo \"{1}\"",
      null),
  SINAC_PLAZOS_7(
      "Error al recuperar el Plazo Vigente asociado al Identificador de Expediente \"{0}\" y al Identificador de Plazo \"{1}\"",
      null),
  SINAC_PLAZOS_8(
      "No se ha encontrado el Plazo Vigente asociado al Identificador de Expediente \"{0}\" y al Identificador de Plazo \"{1}\" en Estado \"{2}\"",
      null),
  SINAC_PLAZOS_9(
      "Error al recuperar el Plazo Vigente asociado al Identificador de Expediente \"{0}\" y al Identificador de Plazo \"{1}\" en Estado \"{2}\"",
      null),
  SINAC_PLAZOS_10(
      "No se ha encontrado el Plazo Vigente asociado al Identificador de Expediente \"{0}\", al Identificador de Plazo \"{1}\" y al Identificador de Requerimiento \"{2}\"",
      null),
  SINAC_PLAZOS_11(
      "Error al recuperar el Plazo Vigente asociado al Identificador de Expediente \"{0}\", al Identificador de Plazo \"{1}\" y al Identificador de Requerimiento \"{2}\"",
      null),
  SINAC_PLAZOS_12(
      "No se ha encontrado el Plazo Vigente asociado al Identificador de Expediente \"{0}\", al Identificador de Plazo \"{1}\" y al Identificador de Requerimiento \"{2}\"",
      null),
  SINAC_PLAZOS_13(
      "Error al recuperar el Plazo Vigente asociado al Identificador de Expediente \"{0}\", al Identificador de Plazo \"{1}\" y al Identificador de Requerimiento \"{2}\"",
      null),
  SINAC_PLAZOS_14(
      "No se ha encontrado el Plazo Vigente asociado al Identificador de Expediente \"{0}\", al Identificador de Plazo \"{1}\" y al Identificador de Requerimiento \"{2}\" en Estado \"{3}\"",
      null),
  SINAC_PLAZOS_15(
      "Error al recuperar el Plazo Vigente asociado al Identificador de Expediente \"{0}\", al Identificador de Plazo \"{1}\" y al Identificador de Requerimiento \"{2}\" en Estado \"{3}\"",
      null),
  SINAC_PLAZOS_16("No se han encontrado Plazos del Expediente asociados al Identificador de Expediente \"{0}\"", null),
  SINAC_PLAZOS_17("Error al recuperar los Plazos del Expediente asociados al Identificador de Expediente \"{0}\"",
      null),
  SINAC_PLAZOS_18("No se han encontrado Plazos del Expediente asociados al Identificador de Expediente \"{0}\"", null),
  SINAC_PLAZOS_19(
      "Error al comprobar si existen Plazos del Expediente en curso asociados al Identificador de Expediente \"{0}\"",
      null),
  SINAC_PLAZOS_20(
      "No se ha encontrado el Histórico del Plazo asociado al Identificador de Expediente \"{0}\" y al Identificador de Plazo \"{1}\"",
      null),
  SINAC_PLAZOS_21(
      "Error al recuperar el Histórico del Plazo asociado al Identificador de Expediente \"{0}\" y al Identificador de Plazo \"{1}\"",
      null),
  SINAC_PLAZOS_22(
      "No se ha encontrado el Histórico del Plazo del Expediente asociado al Identificador de Expediente \"{0}\", al Identificador de Plazo \"{1}\" y al Identificador de Requerimiento \"{2}\"",
      null),
  SINAC_PLAZOS_23(
      "Error al recuperar el Histórico del Plazo del Expediente asociado al Identificador de Expediente \"{0}\", al Identificador de Plazo \"{1}\" y al Identificador de Requerimiento \"{2}\"",
      null),
  SINAC_PLAZOS_24("No se ha encontrado el Plazo del Expediente asociado al Identificador \"{0}\"", null),
  SINAC_PLAZOS_25("Error al actualizar a no vigente el Plazo del Expediente asociado al Identificador \"{0}\"", null),
  SINAC_PLAZOS_26("Error al crear el Plazo del Expediente \"{0}\"", null),
  SINAC_PLAZOS_27(
      "No se ha encontrado el Plazo asociado al Identificador de Procedimiento \"{0}\" y al Código de Tipo de Plazo \"{1}\"",
      null),
  SINAC_PLAZOS_28(
      "Error al recuperar el Plazo asociado al Identificador de Procedimiento \"{0}\" y al Código de Tipo de Plazo \"{1}\"",
      null),
  SINAC_PLAZOS_29(
      "No se ha encontrado el Informe en Estado \"Solicitado\" asociado al Identificador de Expediente \"{0}\" y al Código de Tipo de Informe \"{1}\"",
      null),
  SINAC_PLAZOS_30(
      "Error al recuperar el Informe en Estado \"Solicitado\" asociado al Identificador de Expediente \"{0}\" y al Código de Tipo de Informe \"{1}\"",
      null),
  SINAC_PLAZOS_31("No se han encontrado Plazos Vigentes Vencidos en Estado \"{0}\"", null),
  SINAC_PLAZOS_32("Error al recuperar los Plazos Vigentes Vencidos en Estado \"{0}\"", null),
  SINAC_PLAZOS_33("No se ha encontrado el Plazo de Resolución Vigente asociado al Identificador de Expediente \"{0}\"",
      null),
  SINAC_PLAZOS_34("Error al recuperar el Plazo de Resolución Vigente asociado al Identificador de Expediente \"{0}\"",
      null),

  SINAC_PROCEDIMIENTOS_1("No se ha encontrado el Código SIA asociado al Identificador de Procedimiento \"{0}\"", null),
  SINAC_PROCEDIMIENTOS_2("Error al recuperar el Código SIA asociado al Identificador de Procedimiento \"{0}\"", null),
  SINAC_PROCEDIMIENTOS_3(
      "No se ha encontrado en Base de Datos el procedimientosFasesTramitesOperacionesAcciones asociado al Identificador \"{0}\" y código de acción \"{1}\"",
      null),
  SINAC_PROCEDIMIENTOS_4(
      "Error al recuperar el procedimientosFasesTramitesOperacionesAcciones asociado al Identificador \"{0}\" y código de acción \"{1}\"",
      null),
  SINAC_PROCEDIMIENTOS_5(
      "No se ha encontrado en Base de Datos el procedimientosFasesTramitesOperaciones asociado al Identificador \"{0}\"",
      null),
  SINAC_PROCEDIMIENTOS_6(
      "Error al recuperar el procedimientosFasesTramitesOperaciones asociado al Identificador \"{0}\"", null),
  SINAC_PROCEDIMIENTOS_7(
      "No se ha encontrado la Información del Procedimiento, Fase, Trámite y Operación asociada al Identificador \"{0}\"",
      null),
  SINAC_PROCEDIMIENTOS_8(
      "Error al recuperar la Información del Procedimiento, Fase, Trámite y Operación asociada al Identificador \"{0}\"",
      null),
  SINAC_PROCEDIMIENTOS_9(
      "No se ha encontrado la Información del Procedimiento, Fase, Trámite, Operación y Acción asociada al IdProFaseTraOpe \"{0}\" y al Código de la Acción \"{1}\"",
      null),
  SINAC_PROCEDIMIENTOS_10(
      "Error al recuperar la Información del Procedimiento, Fase, Trámite, Operación y Acción asociada al IdProFaseTraOpe \"{0}\" y al Código de la Acción \"{1}\"",
      null),
  SINAC_PROCEDIMIENTOS_11(
      "No se ha encontrado el IdProFaseTraOpeAcc para Código de Procedimiento \"{0}\", Código de Trámite \"{1}\", Código de Operación \"{2}\" y Código de Acción \"{3}\"",
      null),
  SINAC_PROCEDIMIENTOS_12(
      "Error al recuperar el IdProFaseTraOpeAcc para Código de Procedimiento \"{0}\", Código de Trámite \"{1}\", Código de Operación \"{2}\" y Código de Acción \"{3}\"",
      null),
  SINAC_PROCEDIMIENTOS_13("No se ha encontrado procedimiento para el expediente {0}", null),

  SINAC_REQ_AUDIENCIAS_1(
      "No se han encontrado Tipos de Oficios y sus Documentos a requerir en el Procedimiento con Identificador \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_2(
      "Error al recuperar los Tipos de Oficios y sus Documentos a requerir en el Procedimiento con Identificador \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_3("No se han encontrado Documentos a requerir para el Procedimiento con Identificador \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_4("Error al recuperar los Documentos a requerir para el Procedimiento con Identificador \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_5("No se han encontrado Requerimientos asociados al Identificador de Expediente \"{0}\"", null),
  SINAC_REQ_AUDIENCIAS_6("Error al recuperar los Requerimientos asociados al Identificador de Expediente \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_7(
      "No se han encontrado Tipos de Documentos asociados a los Identificadores de Tipos de Documentos \"{0}\"", null),
  SINAC_REQ_AUDIENCIAS_8(
      "Error al recuperar los Tipos de Documentos asociados a los Identificadores de Tipos de Documentos \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_10("No se ha encontrado el Requerimiento asociado al Identificador de Documento \"{0}\"", null),
  SINAC_REQ_AUDIENCIAS_11("Error al recuperar el Requerimiento asociado al Identificador de Documento \"{0}\"", null),
  SINAC_REQ_AUDIENCIAS_12("No se ha encontrado el Requerimiento asociado al Identificador de Requerimiento \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_13(
      "Error al actualizar el Estado del Requerimiento asociado al Identificador de Requerimiento \"{0}\"", null),
  SINAC_REQ_AUDIENCIAS_14("No se ha encontrado el Requerimiento asociado al Identificador de Requerimiento \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_15(
      "Error al actualizar el Estado y la Fecha de Finalización del Requerimiento asociado al Identificador de Requerimiento \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_16("No se han encontrado Requerimientos asociados al Expediente \"{0}\" en Estado \"{1}\"",
      null),
  SINAC_REQ_AUDIENCIAS_17("Error al recuperar los Requerimientos asociados al Expediente \"{0}\" en Estado \"{1}\"",
      null),
  SINAC_REQ_AUDIENCIAS_18(
      "No se ha encontrado el Identificador de la Plantilla asociado al Identificador de Expediente \"{0}\" y al Identificador de Requerimiento \"{1}\"",
      null),
  SINAC_REQ_AUDIENCIAS_19(
      "Error al recuperar el Identificador de la Plantilla asociado al Identificador de Expediente \"{0}\" y al Identificador de Requerimiento \"{1}\"",
      null),
  SINAC_REQ_AUDIENCIAS_20("No se ha encontrado el Requerimiento asociado al Identificador de Requerimiento \"{0}\"",
      null),
  SINAC_REQ_AUDIENCIAS_21("Error al recuperar el Requerimiento asociado al Identificador de Requerimiento \"{0}\"",
      null),

  SINAC_SOLICITUDES_1("No se ha podido guardar la solicitud {0}", null),
  SINAC_SOLICITUDES_2("No se ha podido guardar el formulario VAL de la solicitud {0}", null),
  SINAC_SOLICITUDES_3("No se ha podido descativar el formulario VAL de la solicitud {0}", null),
  SINAC_SOLICITUDES_4("No se ha podido recuperar las personas de la solicitud {0}", null),
  SINAC_SOLICITUDES_5("No se ha podido recuperar la solicitud {0}", null),
  SINAC_SOLICITUDES_6("No se ha podido guardar las personas de la solicitud {0}", null),
  SINAC_SOLICITUDES_7("No se ha podido eliminar las personas de la solicitud {0}", null),
  SINAC_SOLICITUDES_8("No se ha encontrado la fecha de registro asociado al Identificador de Solicitud \"{0}\"", null),
  SINAC_SOLICITUDES_9("Error al recuperar la fecha de registro asociado al Identificador de Solicitud \"{0}\"", null),

  SINAC_USUARIOS_1("No se ha encontrado la Información del Usuario asociado al Usuario de Justicia \"{0}\"", null),
  SINAC_USUARIOS_2("Error al recuperar la Información del Usuario asociado al Usuario de Justicia \"{0}\"", null),

  SINAC_MOTOR_1("No se ha podido instanciar el Unmarshaller del motor de condicines", null),
  SINAC_MOTOR_2("Error recuperando condicion: {0} en el expediente {1}", null),
  SINAC_MOTOR_3("La lista de elementos de la condición en el exp {0}no puede ser nula ni estar vacía", null),
  SINAC_MOTOR_4("La lista de elementos de la condición en el exp {0} no puede tener mas de un elemento padre", null),
  SINAC_MOTOR_5("La lista de valores de la condición en el exp {0} no puede ser nula ni estar vacía", null),
  SINAC_MOTOR_6(
      "El elemento padre no tiene un tipo valido, debe ser de un tipo que develva un Boolean: AND, OR, EXISTE, NOEXISTE, INCLUIDO, NOINCLUIDO, MAYOR, MENOR, MAYORIGUAL, MENORIGUAL, NO, DISTINTO, IGUAL",
      null),
  SINAC_MOTOR_7(
      "El expediente {0} no está asignado a ninguna persona o la persona asignada no coincide con la de la sesión actual",
      "No tienes asignado el expediente actual."),
  SINAC_MOTOR_8("Error añadiendo fecha", null), SINAC_MOTOR_9("Error añadiendo fecha", null),
  SINAC_MOTOR_10("La acción {0} no está disponible para ejecutar en el expediente {1}", null),
  SINAC_MOTOR_11("El usuario {0} no tiene permisos para ejecutar la acción (pftoa): {1}",
      "El usuario no tiene permisos para ejecutar la acción"),
  SINAC_MOTOR_12("La acción con código {0} no existe", "No se puede ejecutar la acción"),
  SINAC_MOTOR_13("Se ha producido un error recuperando las acciones para el expediente {0}", null),

  SINAC_CLIFIRMASRV_1(
      "Error: No ha sido posible firmar el documento {0} porque ha habido un error durante el proceso de firma del documento.",
      null),

  SINAC_COPIAAUT_1("No se ha obtenido resultado al recuperar la copia auténtica del documento", null),
  SINAC_COPIAAUT_2("Error en la llamada al servicio de copias auténticas",
      "Error obteniendo la copia auténtica del documento"),

  SINAC_GESTDOC_1(
      "GestorDocumentalConnectorImpl.capturarDocumento - Error: El documento {0} no ha sido guardado en el Gestor Documental porque ha habido un error durante el proceso de guardado del documento.",
      null),
  SINAC_GESTDOC_2("No se ha obtenido el documento {0} de ECService, respuesta vacía",
      "No se ha podido recuperar el documento solicitado"),
  SINAC_GESTDOC_3(
      "GestorDocumentalConnectorImpl.obtenerDocumento - Error: El documento con identificador {0} no ha podido ser obtenido del Gestor Documental porque ha habido un error durante el proceso de obtención del documento.",
      "No se ha podido recuperar el documento solicitado"),

  SINAC_NOTIFICA_1(
      "No se ha podido realizar la entrega postal en el expediente {0} porque no hay domicilio de notificación",
      "El domicilio de notificación es obligatorio para realizar una entrega postal"),

  SINAC_ACC_CADI_1(
      "Se ha producido un error intentar actualizar las validacion del Informe {0} del semaforo, para el expediente {1}",
      null),
  SINAC_ACC_CEXP_1("Error calculando la fecha de cierre y archivo", null),
  SINAC_ACC_CREE_1("Error guardando la solicitud {0}", null), SINAC_ACC_EPFI_1("{0}", null),
  SINAC_ACC_NDOC_1("No se ha podido enviar la notificación del documento {0}", "Error al enviar la notificación"),
  SINAC_ACC_EBOE_1("Se ha producido un error al realizar la petición al BOE, expediente : {0}, error: {1}",
      "Se ha producido un error al realizar la petición de envío. Contacte con el administrador del sistema. "),
  SINAC_ACC_FSEL_1(
      "Error: No ha sido posible firmar el documentoporque ha habido un error durante el proceso de firma del documento.",
      null),
  SINAC_ACC_GEND_1("No se ha podido guardar el documento {0} en el expediente {1}", null),
  SINAC_ACC_GEND_2("{0}", "No se ha podido generar el/los documento/s."),
  SINAC_ACC_GEND_3("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_ACC_REXO_1("No se reabrir el expediente origen {0} el mensaje de error es: {1}", null),
  SINAC_ACC_RPFI_1("No se puede procesar la Petición '{0}' porque el contenido del Documento es nulo.", null),
  SINAC_ACC_RPFI_2("RecibirDePortaFirmasAccion.ejecutaAccion - Estado de la Petición: \'{0}\'", null),
  SINAC_ACC_RDGP_1("Error en formateo de la fecha {0}", null),
  SINAC_ACC_REXP_1("Error al procesar los bytes del certificado de la resolución",
      "No se ha podido recuperar el certificado"),
  SINAC_ACC_SDGP_1("Error solicitando informe {0} DGP para el expediente {1}", "Error solicitando informe DGP"),
  SINAC_ACC_SDGP_2("Error solicitando informe DGP para el expediente con id: {0}", null),
  SINAC_ACC_VALD_1("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo", null),
  SINAC_ACC_COME_1("Ha habido un error al intentar completar el expediente", null),

  SINAC_UTILS_1("Error al intentar convertir \n {0}", null), SINAC_UTILS_2("No se ha podido formatear la fecha", null),
  SINAC_UTILS_3("Error de formato numérico con las fechas de solicitud desde/hasta", null),
  SINAC_UTILS_4("Error parseando las fechas de solicitud desde/hasta", null),
  SINAC_UTILS_5("Error inicializando Hibernate", null),

  SINAC_EXPE_WC_1("Error al procesar el archivo ZIP (expediente ENI) para el expediente.", null),
  SINAC_EXPE_WC_2("No se ha podido obtener las listas para rellenar los combo", null),
  SINAC_EXPE_WC_3("Se ha producido un error inesperado. Inténtelo de nuevo más tarde o contacte con un administrador.",
      null),
  SINAC_EXPE_WC_4("El expediente {0} no ha podido ser resuelto",
      "No se ha podido ejecutar la acción para resolver el expediente"),
  SINAC_EXPE_WC_5("Se ha producido un error al formatear la fecha de nacimiento", null),
  SINAC_EXPE_WC_6(
      "Se ha producido un error, el interesado debe tener NIE o pasaporte para poder realizar la consulta. Identificadores del interesado: {0}",
      "Se ha producido un error, el interesado debe tener NIE o pasaporte para poder realizar la consulta"),
  SINAC_EXPE_WC_7("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo: {0}", null),
  SINAC_EXPE_WC_8("Ha habido un error al intentar cambiar el estado de la validacion en el semaforo: {0}", null),
  SINAC_EXPE_WC_9(
      "Se ha producido un error, el interesado debe tener NIE o pasaporte para poder realizar la consulta. Identificadores del interesado: {0}",
      "Se ha producido un error, el interesado debe tener NIE o pasaporte para poder realizar la consulta"),
  SINAC_EXPE_WC_10("El expediente {0} no ha podido ser resuelto",
      "No se ha podido ejecutar la acción para resolver el expediente"),
  SINAC_EXPE_WC_11("Se ha producido un error al formatear la fecha de nacimiento", null),
  SINAC_EXPE_WC_12("No se ha podido guardar el estado de la validación en el semaforo para el expediente: {0}", null),

  SINAC_SINAC_WC_1("El rol {0} no está disponible para el usuario de la sesión", null),
  SINAC_SOLI_WC_1("Se ha producido un error al obtener cookie", null),
  SINAC_SOLI_WC_2(
      "Se ha producido un error obteniendo procedimientos fases tramites operaciones acciones dtoByCodigos: INI, INI, SOL, CREE",
      "No se ha podido crear el expediente"),
  SINAC_SOLI_WC_3("Para crear un expediente deben adjuntarse todos los documentos obligatorios", null),

  SINAC_CCTR_HL_1("No se ha podido generar el documento en el expediente {0} para la plantilla {1}",
      "No se ha podido generar el documento indicado: {0}"),
  SINAC_CCTR_HL_2("No se ha podido enviar a firma el documento {0} en el expediente {1}",
      "No se ha podido enviar a firma el documento indicado: {0}"),
  SINAC_CCTR_HL_3("No se ha podido comunicar el documento {0} en el expediente {1}",
      "No se ha podido comunicar el documento indicado: {0}"),
  SINAC_CCTR_HL_4("No se ha podido comunicar la comunicación de concesión al Registro Civil en el expediente {0}",
      "No se ha podido comunicar el documento indicado al Registro Civil: {0}"),
  SINAC_RCTR_HL_1("No se ha podido generar el documento en el expediente {0} para la plantilla {1}",
      "No se ha podido generar el documento indicado: {0}"),
  SINAC_RCTR_HL_2("No se ha podido enviar a firma el documento {0} en el expediente {1}",
      "No se ha podido enviar a firma el documento indicado: {0}"),
  SINAC_RCTR_HL_3("No se ha podido comunicar el documento {0}  en el expediente {1}",
      "No se ha podido comunicar el documento indicado: {0}"),
  SINAC_RCTR_HL_4("No se ha podido comunicar la comunicación de concesión al Registro Civil en el expediente {0}",
      "No se ha podido comunicar el documento indicado al Registro Civil: {0}"),
  SINAC_TRAM_HL_1("No se ha encontrado Helper para el tramite {0}", null),

  SINAC_DOC_VAL_1("Error al recuperar el documento {0}", null),
  SINAC_DOC_VAL_2("Error parseando fecha de registro {0}", null),
  SINAC_SOL_VAL_1(
      "Para poder hacer una búsqueda de rastreo para el {0} hay que rellenar alguno de los siguientes campos: Nº de identificador, Nombre, Primer Apellido, Segundo Apellido{1}",
      null),
  SINAC_SOL_VAL_2("Error al recuperar el documento {0}", null),

  SINAC_NTIF_WS_1("Error en la sincronización del envío para la notificación {0}", null),
  SINAC_PFIR_WS_1("El estado de la Request no es valido: {0}", null), CUSTOM_MESSAGE("{0}", null),

  SINAC_JOB_RECIBIR_1("Error al guardar el informe de la dgp recibido con número de expediente {0} y fecha alta {1}",
      null),
  SINAC_JOB_RECIBIR_2("Error al recuperar el informe de la dgp con número de expediente {0} y fecha alta {1}", null),
  SINAC_JOB_RECIBIR_3(
      "Error al actualizar el estado del informe de la dgp con número de expediente {0} y fecha alta {1}", null),

  SINAC_JOBS_TGPAUSAR("No se ha podido pausar el trigger del job {}", null),
  SINAC_JOBS_TGREANUDAR("No se ha podido reanudar el trigger del job {}", null),
  SINAC_JOBS_TGESTADO("No se ha podido recuperar el estado del trigger para el job {0}", null),

  // Mensajes para alta de expediente desde SEDE
  SINAC_SEDE_1("Los datos de la petición son obligatorios para crear el expediente - numeroIdentificador={0}",
      "Los datos de la petición son obligatorios para crear el expediente"),
  SINAC_SEDE_2("Los datos del interesado son obligatorios para procesar la petición = {0}",
      "Los datos del interesado son obligatorios para procesar la petición"),
  SINAC_SEDE_3("El motivo de solicitud es obligatorio, no puede ser {0}", "El motivo de solicitud es obligatorio"),
  SINAC_SEDE_4("Motivo de solicitud no válido: {0}",
      "Motivo de solicitud no válido. Verifique el código proporcionado"),
  SINAC_SEDE_5("Procedimiento no encontrado: {0}", "Procedimiento no encontrado. Verifique el código proporcionado"),
  SINAC_SEDE_6("Catálogo no encontrado: {0}", "Catálogo no encontrado. Verifique el código proporcionado"),
  SINAC_SEDE_7("Error al convertir datos de solicitud, motivoSolicitud= {0}",
      "Error al procesar los datos de la solicitud. Verifique la información proporcionada"),
  SINAC_SEDE_8("Error al convertir datos del interesado - numeroIdentificador={0}: {1}",
      "Error al procesar los datos del interesado. Verifique la información proporcionada"),
  SINAC_SEDE_9("Error de validación de solicitud - numeroIdentificador={0}: {1}",
      "Error de validación. Verifique que todos los campos obligatorios estén completos y sean correctos"),
  SINAC_SEDE_10("No se pudo crear la solicitud - numeroIdentificador={0}, motivoSolicitud={1}: {2}",
      "No se pudo crear la solicitud. Inténtelo nuevamente o contacte con el administrador"),
  SINAC_SEDE_11("No se pudo crear el expediente - idSolicitud={0}: {1}",
      "No se pudo crear el expediente. Inténtelo nuevamente o contacte con el administrador"),
  SINAC_SEDE_12("Error inesperado al crear expediente desde SEDE - numeroIdentificador={0}: {1}",
      "Error inesperado al procesar la solicitud. Contacte con el administrador del sistema"),

  // Validación de campos específicos de procedimiento
  CAMPO_NOMBRECAMPO_VACIO("El nombre del campo no puede estar vacío", "El nombre del campo no puede estar vacío"),
  CAMPO_DUPLICADO("Campo {0} duplicado en el request", "Campo duplicado en el request"),
  CAMPO_NO_EXISTE_PROCEDIMIENTO("El campo {0} no existe en el procedimiento", "El campo no existe en el procedimiento"),
  CAMPO_OBLIGATORIO_VACIO("Campo obligatorio está vacío", "Campo obligatorio está vacío"),
  CAMPO_OBLIGATORIO_FALTANTE("Falta campo obligatorio: {0}", "Falta campo obligatorio"),
  CAMPO_LONGITUD_EXCEDIDA("El campo {0} excede la longitud máxima de {1}", "El campo excede la longitud máxima"),
  CAMPO_FORMATO_INVALIDO("El formato del campo es inválido", "El formato del campo es inválido"),
  CAMPOS_NO_PERMITIDOS_PROCEDIMIENTO("El procedimiento {0} no acepta campos específicos",
      "El procedimiento no acepta campos específicos"),

  // CU071 - Adjuntar documentos a expediente desde SEDE
  SINAC_EXPEDIENTE_NO_ACTIVO("Expediente {0} no está activo",
      "El expediente no está activo y no se pueden adjuntar documentos"),
  SINAC_EXPEDIENTE_ESTADO_FINAL("Expediente {0} en estado final no permite adjuntar documentos",
      "El expediente se encuentra en un estado que no permite adjuntar documentos"),
  SINAC_TIPO_DOCUMENTO_NO_ENCONTRADO("Tipo de documento {0} no encontrado en catálogos", "Tipo de documento no válido"),
  SINAC_ERROR_MAPEO_DOCUMENTO("Error mapeando documento {0}", "Error procesando el documento"),
  SINAC_ERROR_OBTENER_ESTADO_DOCUMENTO(
      "Error al obtener estado documento para procedimiento {0} y tipo documento {1}: {2}",
      "No se pudo determinar el estado del documento"),
  SINAC_TIPO_DOCUMENTO_PROCEDIMIENTO("Tipo documento no existe para para procedimiento de expediente {0}",
      "Tipo documento no válido para el procedimiento del expediente"),
  SINAC_JOB_RECUPERAR_ITEMS("No se han podido recuperar los items {} ", "Error recuperando los items");

  private String logMessage;

  private String userMessage;

  SinacExceptionMessageType(String logMessage, String userMessage) {
    this.logMessage = logMessage;
    this.userMessage = userMessage;
  }

  public String getLogMessage() {
    return logMessage;
  }

  public String getUserMessage() {
    return userMessage;
  }

}
