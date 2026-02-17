package es.mjusticia.sinac.core.business.facade;

import java.math.BigInteger;
import java.util.List;
import es.mjusticia.sinac.core.business.exception.SinacException;
/*-
 * #%L
 * sinac-core
 * %%
 * Copyright (C) 2022 - 2023 Ministerio de Justicia
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
import es.mjusticia.sinac.core.model.dto.EstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;

public interface ProcedimientosFacade {

  public List<ProcedimientoDto> getProcedimientos() throws SinacException;

  public List<EstadoDto> getEstados() throws SinacException;

  public List<EstadoDto> getEstadosByProcedimiento(Short idProcedimiento) throws SinacException;

  public ProcedimientoDto getProcedimientoCompleto(short idPro) throws SinacException;

  public List<ExpedienteDocumentoDto> cargarDocumentosEntrada(ExpedienteDto expediente, ProcedimientoDto procedimiento);

  public List<ExpedienteDocumentoDto> cargarDocumentosEntradaOtros(ExpedienteDto expediente,
      ProcedimientoDto procedimiento);

  public List<ExpedienteDocumentoDto> cargarDocumentosGenerados(ExpedienteDto expediente,
      ProcedimientoDto procedimiento);

  public List<ExpedienteDocumentoDto> cargarDocumentosSalida(ExpedienteDto expediente, ProcedimientoDto procedimiento);

  public List<ExpedienteDocumentoDto> filtrarDocumentosRequeridos(BigInteger idExp) throws SinacException;

  public ProcedimientoDto getProcedimientoByCodPro(String codPro) throws SinacException;

}
