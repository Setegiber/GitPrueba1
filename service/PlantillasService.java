package es.mjusticia.sinac.core.business.service;

import java.math.BigInteger;
import java.util.List;

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
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.DocumentosTramiteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.PlantillasClasificacionDto;

public interface PlantillasService {

  PlantillaDto getPlantillaPorCod(String codPlantilla) throws SinacException;

  List<PlantillaDto> getAllPlantillasActivas() throws SinacException;

  List<PlantillaDto> getListaPlantillas(BigInteger idExp, String codTramite, String codOpe, String codAccion)
      throws SinacException;

  /**
   * Metodo que devuelve la lista de documentos en funcion del tramite, operacion
   * y accion.
   * 
   * @param idExp
   * @param codTra
   * @param codOpe
   * @param codAcc
   * @return
   * @throws SinacException
   */
  List<DocumentosTramiteDto> getDocumentosTramite(BigInteger idExp, String codTra, String codOpe, String codAcc)
      throws SinacException;

  List<PlantillaDto> getListaPlantillasSinOpe(BigInteger idExp, String codTramite, String codAccion)
      throws SinacException;

  List<DocumentosTramiteDto> getDocumentosTramiteSinOpe(BigInteger idExp, String codTra, String codAcc)
      throws SinacException;

  PlantillaDto getPlantillaPorTipoDocAndPro(short idPro, String codTipo) throws SinacException;

  PlantillaDto selectPlantillaByCod(List<PlantillaDto> listaPlantillas, ExpedienteDto expedienteDto);

  boolean comprobarClasificacionesPlantilla(BigInteger idExp, List<PlantillasClasificacionDto> plantillasClasificacion);

}
