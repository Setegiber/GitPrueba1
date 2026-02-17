package es.mjusticia.sinac.core.business.service.impl;

import java.util.ArrayList;
import java.util.List;
/*-
 * #%L
 * sinac-core
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
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.model.mapper.UsuarioMapper;
import es.mjusticia.sinac.core.persistence.UsuarioDao;

/**
 * Clase de Implementación de {@link UsuariosService}.
 *
 * @author NTT Data.
 */
@Component
public class UsuariosServiceImpl implements UsuariosService {

  private static final Logger LOG = LoggerFactory.getLogger(UsuariosServiceImpl.class);

  @Autowired
  private UsuarioDao usuarioDao;

  @Autowired
  private UsuarioMapper usuarioMapper;

  private UsuarioDto usuarioDto = null;

  @Override
  public UsuarioDto getUsuarioByUsuarioJusticia(final String usuarioJusticia) throws SinacException {
    LOG.debug("UsuariosServiceImpl.getUsuarioByUsuarioJusticia - Init");

    UsuarioDto usuarioDto = null;

    try {
      usuarioDto = usuarioMapper.toDto(usuarioDao.getUsuarioByUsuarioJusticia(usuarioJusticia).orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_USUARIOS_1)
          .logMessageParams(usuarioJusticia).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_USUARIOS_2).logMessageParams(usuarioJusticia)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("UsuariosServiceImpl.getUsuarioByUsuarioJusticia - End");

    return usuarioDto;
  }

  @Override
  public List<UsuarioDto> getAllUsuarios() throws SinacException {
    LOG.debug("UsuariosServiceImpl.getAllUsuarios - Init");
    List<UsuarioDto> listaUsuarioDtos = new ArrayList<>();
    usuarioDao.findAll().forEach(usuario -> {
      usuarioDto = usuarioMapper.toDto(usuario);
      listaUsuarioDtos.add(usuarioDto);
    });
    LOG.debug("UsuariosServiceImpl.getAllUsuarios - End");
    return listaUsuarioDtos;
  }
  
  @Override
  public List<String> getEmailsUsuariosByRol(String rol) throws SinacException {
    LOG.debug("UsuariosServiceImpl.getEmailsUsuariosByRol - Init");
    List<String> listaUsuarioDtos = usuarioDao.getEmailsUsuariosByRol(rol);
    LOG.debug("UsuariosServiceImpl.getEmailsUsuariosByRol - End");
    return listaUsuarioDtos;
  }

  @Override
  public UsuarioDto getUsuario(Integer idUsuario) throws SinacException {
    return usuarioMapper.toDto(usuarioDao.findById(idUsuario).orElseThrow());
  }

}
