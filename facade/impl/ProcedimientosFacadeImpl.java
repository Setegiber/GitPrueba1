package es.mjusticia.sinac.core.business.facade.impl;

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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.facade.ProcedimientosFacade;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.ProcedimientosService;
import es.mjusticia.sinac.core.model.dto.EstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;

/**
 * Implementeacion de la fachada de negocio para el acceso a servicios de
 * Procedimiento
 *
 * @author NttData
 */
@Service
@Transactional(readOnly = true)
public class ProcedimientosFacadeImpl implements ProcedimientosFacade {

  private final ProcedimientosService procedimientosService;

  @Autowired
  public ProcedimientosFacadeImpl(ProcedimientosService procedimientosService) {
    this.procedimientosService = procedimientosService;
  }

  @Autowired
  private CatalogosService catalogosService;

  @Override
  public List<ProcedimientoDto> getProcedimientos() throws SinacException {
    return procedimientosService.getProcedimientos();
  }

  @Override
  public List<EstadoDto> getEstados() throws SinacException {
    return procedimientosService.getEstados();
  }

  @Override
  public List<EstadoDto> getEstadosByProcedimiento(Short idProcedimiento) throws SinacException {
    return procedimientosService.getEstadoByProcedimientoId(idProcedimiento);
  }

  @Override
  public ProcedimientoDto getProcedimientoCompleto(short idPro) throws SinacException {
    return procedimientosService.getProcedimientoCompleto(idPro);
  }

  @Override
  public List<ExpedienteDocumentoDto> cargarDocumentosEntrada(ExpedienteDto expediente,
      ProcedimientoDto procedimiento) {

    List<ExpedienteDocumentoDto> documentosEntrada = new ArrayList<>();

    procedimiento.getProcedimientosDocumentosTipoDtos().stream().forEach(proDoc -> {
      if (proDoc.isFlgDocEnt() && (proDoc.getIdDocObligatorioLdv().getCodLdvMae().equals("DOCO-OSA")
          || proDoc.getIdDocObligatorioLdv().getCodLdvMae().equals("DOCO-OEN"))) {
        List<ExpedienteDocumentoDto> listaAuxiliar = expediente.getExpedienteDocumentoDtos().stream()
            .filter(
                expdoinv -> expdoinv.getDocumentoTipoDto().getIdDocTipo() == proDoc.getDocumentosTipo().getIdDocTipo())
            .toList();
        if (listaAuxiliar.isEmpty()) {
          ExpedienteDocumentoDto expDoc = new ExpedienteDocumentoDto();
          expDoc.setDocumentoTipoDto(proDoc.getDocumentosTipo());
          expDoc.setLdvMaestraDtoByIdEstDocLdv(catalogosService.getCatalogoByCod("EDOC-NRE"));
          expDoc.getDocumentoTipoDto().getProcedimientosDocumentosTipoDtos().add(proDoc);
          documentosEntrada.add(expDoc);
        } else {
          List<ExpedienteDocumentoDto> listaAuxiliarMutable = new ArrayList<>(listaAuxiliar);
          listaAuxiliarMutable.sort(Comparator.comparing(ExpedienteDocumentoDto::isFlgActivo).reversed());
          if (listaAuxiliar.size() > 1) {
            listaAuxiliarMutable.get(0).setFlgFlecha(true);
          }
          listaAuxiliarMutable.forEach(expdo -> {
            expdo.setRegistroDtos(expdo.getRegistroDtos().stream()
                .filter(reg -> reg.getLdvMaestraDto().getCodLdvMae().equals("OREG-ENT")).toList());
            documentosEntrada.add(expdo);
          });
        }
      }

    });

    documentosEntrada.sort((d1, d2) -> Integer.compare(
        d1.getDocumentoTipoDto().getProcedimientosDocumentosTipoDtos().get(0).getIdDocObligatorioLdv().getIdLdvMae(),
        d2.getDocumentoTipoDto().getProcedimientosDocumentosTipoDtos().get(0).getIdDocObligatorioLdv().getIdLdvMae()));

    return documentosEntrada;

  }

  @Override
  public List<ExpedienteDocumentoDto> cargarDocumentosEntradaOtros(ExpedienteDto expediente,
      ProcedimientoDto procedimiento) {
    List<ExpedienteDocumentoDto> documentosEntradaOtros = new ArrayList<>();
    procedimiento.getProcedimientosDocumentosTipoDtos().stream().forEach(proDoc -> {
      if (proDoc.isFlgDocEnt() && proDoc.getIdDocObligatorioLdv().getCodLdvMae().equals("DOCO-NOB")) {
        expediente.getExpedienteDocumentoDtos().stream()
            .filter(expdo -> expdo.getDocumentoTipoDto().getIdDocTipo() == proDoc.getDocumentosTipo().getIdDocTipo())
            .forEach(expdo -> {
              expdo.setRegistroDtos(expdo.getRegistroDtos().stream()
                  .filter(reg -> reg.getLdvMaestraDto().getCodLdvMae().equals("OREG-ENT")).toList());
              documentosEntradaOtros.add(expdo);
            });
      }

    });

    return documentosEntradaOtros;

  }

  @Override
  public List<ExpedienteDocumentoDto> cargarDocumentosGenerados(ExpedienteDto expediente,
      ProcedimientoDto procedimiento) {
    List<ExpedienteDocumentoDto> documentosGenerados = new ArrayList<>();
    procedimiento.getProcedimientosDocumentosTipoDtos().stream().forEach(proDoc -> {
      if (!proDoc.isFlgDocEnt()) {
        expediente.getExpedienteDocumentoDtos().stream()
            .filter(expdo -> expdo.getDocumentoTipoDto().getIdDocTipo() == proDoc.getDocumentosTipo().getIdDocTipo()
                && expdo.isFlgActivo())
            .forEach(expdo -> {
              if (expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-BOR")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-ENF")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-FIR")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-FRE")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-VAL")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-EFI")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-ERE")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-EGD")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-NER")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-NIN"))

                documentosGenerados.add(expdo);
            });
      }
    });

    return documentosGenerados;

  }

  @Override
  public List<ExpedienteDocumentoDto> cargarDocumentosSalida(ExpedienteDto expediente, ProcedimientoDto procedimiento) {
    List<ExpedienteDocumentoDto> documentosSalida = new ArrayList<>();
    procedimiento.getProcedimientosDocumentosTipoDtos().stream().forEach(proDoc -> {
      if (!proDoc.isFlgDocEnt()) {
        expediente.getExpedienteDocumentoDtos().stream()
            .filter(expdo -> expdo.getDocumentoTipoDto().getIdDocTipo() == proDoc.getDocumentosTipo().getIdDocTipo()
                && expdo.isFlgActivo())
            .forEach(expdo -> {
              if (expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-ENO")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-ECO")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-NOT")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-COM")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-GEI")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-GEC")
                  || expdo.getLdvMaestraDtoByIdEstDocLdv().getCodLdvMae().equals("EDOC-GER")) {
                expdo.filtrarExpedienteNotificacionActiva();
                documentosSalida.add(expdo);
              }
            });
      }
    });

    return documentosSalida;

  }

  @Override
  public List<ExpedienteDocumentoDto> filtrarDocumentosRequeridos(BigInteger idExp) throws SinacException {
    return procedimientosService.getDocumentosRequeridos(idExp);
  }

  @Override
  public ProcedimientoDto getProcedimientoByCodPro(String codPro) throws SinacException {

    return procedimientosService.getProcedimientoByCodPro(codPro);
  }

}
