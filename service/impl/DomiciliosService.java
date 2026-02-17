package es.mjusticia.sinac.core.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import es.mjusticia.sinac.core.business.service.LocalidadesService;
import es.mjusticia.sinac.core.business.service.PaisesService;
import es.mjusticia.sinac.core.business.service.ProvinciasService;
import es.mjusticia.sinac.core.business.service.TipoViaService;
import es.mjusticia.sinac.core.model.dto.PersonaDomicilioDto;
import es.mjusticia.sinac.core.utils.Constantes;

@Service
public class DomiciliosService {

  @Autowired
  PaisesService paisesService;

  @Autowired
  TipoViaService tipoViaService;

  @Autowired
  ProvinciasService provinciasService;

  @Autowired
  LocalidadesService localidadesService;

  public PersonaDomicilioDto formarDireccionNoConsta() {
    PersonaDomicilioDto personaDomicilio = new PersonaDomicilioDto();
    personaDomicilio.setTipoDomicilio("1");
    personaDomicilio.setNomVia(Constantes.ConstantesDireccion.NO_CONSTA);

    personaDomicilio.setPoblacion(Constantes.ConstantesDireccion.NO_CONSTA);

    personaDomicilio.setNumVia(Constantes.ConstantesDireccion.NO);

    personaDomicilio.setCodigoPostal("00000");

    personaDomicilio.setPaisDto(paisesService.getPaisPorCodTresIso(Constantes.ConstantesDireccion.ESP));

    personaDomicilio.setTipoVia(tipoViaService.getTipoViaPorCodDgp("OT"));

    personaDomicilio.setProvinciaDto(provinciasService.getProvinciaByCodProv("OTR"));

    personaDomicilio.setLocalidadDto(localidadesService.getLocalidadByCodProvAndCodMun("OTR", "OTR"));

    return personaDomicilio;
  }

}
