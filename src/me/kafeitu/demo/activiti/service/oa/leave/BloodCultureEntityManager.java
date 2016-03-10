package me.kafeitu.demo.activiti.service.oa.leave;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import me.kafeitu.demo.activiti.entity.oa.BloodCultureJpaEntity;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BloodCultureEntityManager {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public BloodCultureJpaEntity newBloodCulture(DelegateExecution execution) {
    	BloodCultureJpaEntity entity = new BloodCultureJpaEntity();
    	entity.setProcessInstanceId(execution.getProcessInstanceId());
    	entity.setUserId(execution.getVariable("userId").toString());
    	entity.setCode(execution.getVariable("code").toString());
    	entity.setIfQualified(execution.getVariable("ifQualified").toString());
    	entity.setPatientAge(execution.getVariable("patientAge").toString());
    	entity.setPatientCondition(execution.getVariable("patientCondition").toString());
    	entity.setPatientGender(execution.getVariable("patientGender").toString());
    	entity.setPatientName(execution.getVariable("patientName").toString());
    	entity.setReason(execution.getVariable("reason").toString());
    	entity.setSampleRevTime((Date) execution.getVariable("sampleRevTime"));
    	entity.setSampleType(execution.getVariable("sampleType").toString());
    	entity.setRegisterTime(new Date());
    	entity.setPeiyangTime(new Date());
        entityManager.persist(entity);
        return entity;
    }
    
    @Transactional
    public void updateBloodPingban(DelegateExecution execution) {
    	BloodCultureJpaEntity entity = (BloodCultureJpaEntity)execution.getVariable("bloodCulture");
    	entity = getLeave(entity.getId());
    	entity.setPingbanTime(new Date());
        entityManager.flush();
    }
    

    @Transactional
    public void save(BloodCultureJpaEntity entity) {
        entityManager.persist(entity);
    }

    public BloodCultureJpaEntity getLeave(Long id) {
        return entityManager.find(BloodCultureJpaEntity.class, id);
    }

}