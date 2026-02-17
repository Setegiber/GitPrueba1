package es.mjusticia.sinac.core.business.service.impl;

import java.util.Arrays;

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

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.batch.SinacJobDgpRecibir;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.service.InformesService;
import es.mjusticia.sinac.core.model.dto.InformesDgpRecibidosDto;
import es.mjusticia.sinac.core.model.entity.InformesDgpRecibidosEntity;
import es.mjusticia.sinac.core.model.entity.LdvMaestraEntity;
import es.mjusticia.sinac.core.model.mapper.InformesDgpRecibidosMapper;
import es.mjusticia.sinac.core.persistence.InformesDgpRecibidosDao;
import es.mjusticia.sinac.core.persistence.LdvMaestraDao;

@Component
public class InformesServiceImpl implements InformesService{
    @Autowired
    private InformesDgpRecibidosDao informesDgpRecibidosDao;
    @Autowired
    private LdvMaestraDao ldvMaestraDao;
    @Autowired 
    private InformesDgpRecibidosMapper informesDgpRecibidosMapper;
    private static final String COD_PROCESS = "EST-PROCESS";
    private static final String COD_ERR = "EST-ERR";
    private static final String COD_NO_PROCESS = "EST-NO-PROCESS";
    
    private static final Logger LOG = LoggerFactory.getLogger(InformesServiceImpl.class);

    @Override
    public void saveInformeDgpRecibidoEntity(String numExp, String tipoPeticion, String fechaAlta, String codEstado) {
    	if (informesDgpRecibidosDao.existsByNumExpAndFechaAltaAndEstadoCodLdvMae(numExp, fechaAlta, COD_PROCESS)) {
    		LOG.info("El informe de la dgp recibido con número de expediente {} y fecha de alta {} ya se encuentra procesado en la BD.", numExp, fechaAlta);
    	}else {
    		LOG.info("Guardando nuevo informe de la dgp recibido con número de expediente {}, fecha de alta {} con estado {}.",
                    numExp, fechaAlta, codEstado);
	        try{
	            LdvMaestraEntity estado = ldvMaestraDao.findByCodigo(codEstado);
	            InformesDgpRecibidosEntity entity = new InformesDgpRecibidosEntity();
	            entity.setNumExp(numExp);
	            entity.setFechaAlta(fechaAlta);
	            entity.setTipoPeticion(tipoPeticion);
	            entity.setEstado(estado);
	            informesDgpRecibidosDao.save(entity);
	        }catch (Exception e){
	            throw new SinacException(SinacExceptionMessageType.SINAC_JOB_RECIBIR_1).logMessageParams(numExp,fechaAlta);
	        }
    	}
    }

    @Override
    public List<InformesDgpRecibidosDto> findAllInformesDgpRecibidosEntityNoProcesados() {
        List<InformesDgpRecibidosEntity> entitiesList = informesDgpRecibidosDao.findByEstados(Arrays.asList(COD_ERR, COD_NO_PROCESS));
        return informesDgpRecibidosMapper.toDto(entitiesList);
    }

    @Override
    public void updateEstadoInformeDgpRecibido(InformesDgpRecibidosDto dtoToUpdate, String codEstado) {
    	LOG.info("Actualizando el estado a {} del informe de la dgp recibido con número de expediente {} y fecha de alta {}.",
    			codEstado, dtoToUpdate.getNumExp(), dtoToUpdate.getFechaAlta());
        try{
            InformesDgpRecibidosEntity entityToUpdate = informesDgpRecibidosMapper.toEntity(dtoToUpdate);
            LdvMaestraEntity estado = ldvMaestraDao.findByCodigo(codEstado);
            entityToUpdate.setEstado(estado);
            informesDgpRecibidosDao.save(entityToUpdate);
        }catch (Exception e){
            throw new SinacException(SinacExceptionMessageType.SINAC_JOB_RECIBIR_3).logMessageParams(dtoToUpdate.getNumExp(),dtoToUpdate.getFechaAlta());
        }
    }

    @Override
    public InformesDgpRecibidosDto findByNumExpAndFechaAlta(String numExp, String fechaAlta) {
        Optional<InformesDgpRecibidosEntity> entityOp = informesDgpRecibidosDao.findByNumExpAndFechaAlta(numExp, fechaAlta);
        if(entityOp.isPresent()){
            return informesDgpRecibidosMapper.toDto(entityOp.get());
        }else{
            throw new SinacException(SinacExceptionMessageType.SINAC_JOB_RECIBIR_2).logMessageParams(numExp,fechaAlta);
        }
    }

}
