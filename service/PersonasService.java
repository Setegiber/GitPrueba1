package es.mjusticia.sinac.core.business.service;

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

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.PersonaDomicilioDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PersonaFamDto;
import es.mjusticia.sinac.core.model.entity.PersonaEntity;

@Service
public interface PersonasService {

  PersonaDomicilioDto getPersonaDomicilioById(BigInteger idPer);

  /**
   * Método para guardar la información de una persona y todas sus tablas
   * relacionadas
   * 
   * @param personaDto Persona
   * @return PersonaDto
   * @throws SinacException
   */
  PersonaDto savePersona(PersonaDto personaDto) throws SinacException;

  /**
   * Obtiene el Número de Acreditación asociado al Identificador de Persona
   * establecido como parámetro.
   *
   * @param idPersona Identificador de la Persona.
   * @return Número de Acreditación asociado a la Persona.
   * @throws SinacException Si se produce un error al obtener el Número de
   *                        Acreditación asociado a la Persona.
   */
  String getNumeroAcreditacionByIdPersonaAndFlagPrincipalToTrue(final BigInteger idPersona) throws SinacException;

  public void deletePersona(PersonaEntity persona) throws SinacException;

  void desactivarPersona(PersonaDto personaDto) throws SinacException;

  PersonaFamDto getConyugeByIdPersona(BigInteger idPersona);

  Page<PersonaDto> getPersonasRastreo(String identificador, String nombre, String primerApellido,
      String segundoApellido, Date fechaNacimiento, Pageable pageable, String tipoOrdenacion, String columnaOrdenar);

  PersonaDto getPersonaByIdPer(BigInteger idPer) throws SinacException;

  List<BigInteger> getIdsInteresadosAltaFiliaciones() throws SinacException;

  /**
   * Método para guardar la información solo de la tabla persona
   * 
   * @param personaDto Persona
   * @return PersonaDto
   * @throws SinacException
   */
  PersonaDto saveSoloPersona(PersonaDto personaDto) throws SinacException;

  List<BigInteger> getIdsInteresadosConsultaFiliaciones(String maxItemConsultaFiliaciones) throws SinacException;

  List<PersonaDto> getPersonasRastreoVea(String numAcreditacion);

  /**
   * Busca una persona por su número de identificación y tipo de documento.
   *
   * @param numAcreditacion  Número de identificación
   * @param codTipoDocumento Código del tipo de documento (DID-DNI, DID-NIE, etc)
   * @return PersonaDto encontrada o null
   * @throws SinacException
   */
  PersonaDto buscarPersonaPorIdentificacion(String numAcreditacion, String codTipoDocumento) throws SinacException;

}
