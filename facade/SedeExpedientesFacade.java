package es.mjusticia.sinac.core.business.facade;

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

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.AdjuntarDocumentoResultDto;
import es.mjusticia.sinac.core.model.dto.AltaExpedienteSedeResultDto;
import es.mjusticia.sinac.sede.expedientes.AdjuntarDocumentoRequest;
import es.mjusticia.sinac.sede.expedientes.ExpedienteRequest;

/**
 * Fachada de Negocio específica para operaciones de SEDE Electrónica.
 *
 * @author NTT Data
 */
public interface SedeExpedientesFacade {

    /**
     * Crea una solicitud y su expediente desde SEDE Electrónica.
     *
     * @param request Request XML desde SEDE Electrónica con datos del interesado y solicitud
     * @return Resultado de la operación con el expediente creado o información del error
     * @throws SinacException Si ocurre algún error durante la creación
     */
    AltaExpedienteSedeResultDto crearExpedienteSedeElectronica(ExpedienteRequest request) throws SinacException;

    /**
     * Adjunta documentos a un expediente abierto desde SEDE Electrónica.
     *
     * Valida que el expediente existe, está activo, y que el interesado tiene permisos.
     * Procesa los documentos (validación antivirus, tamaño, formato) y los guarda en
     * el Gestor Documental, NFS y Base de Datos.
     *
     * @param request Request SOAP con documentos a adjuntar
     * @return Resultado de la operación con cantidad de documentos adjuntados
     * @throws SinacException Si ocurre algún error durante el proceso
     */
    AdjuntarDocumentoResultDto adjuntarDocumentoExpediente(AdjuntarDocumentoRequest request) throws SinacException;
}
