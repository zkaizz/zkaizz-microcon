package me.kafeitu.demo.activiti.entity.oa;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity(name = "EN_BLOODCULTURE")
public class BloodCultureJpaEntity implements Serializable {

    private Long id;
    private String processInstanceId;
    private String userId;
    private String code; //条码
    private String sampleType; //标本
    
    /**
     * 标本信息
     */
    private String patientName;
    private String patientGender;
    private String patientAge;
    private String patientCondition;
    private Date sampleRevTime;
    private Date registerTime;
    
	private String ifQualified; //是否合格
    private String reason; //不合格原因
    
    private Date peiyangTime;
    private Date pingbanTime;
    
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "PROCESS_INSTANCE_ID")
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Column(name = "REGISTER_TIME")
    public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}
	
    @Column(name = "REASON")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(name = "USER_ID")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "CODE")
    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "SAMPLE_TYPE")
	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	@Column(name = "PATIENT_NAME")
	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	@Column(name = "PATIENT_GENDER")
	public String getPatientGender() {
		return patientGender;
	}

	public void setPatientGender(String patientGender) {
		this.patientGender = patientGender;
	}

	@Column(name = "PATIENT_AGE")
	public String getPatientAge() {
		return patientAge;
	}

	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}

	@Column(name = "PATIENT_CONDITIONE")
	public String getPatientCondition() {
		return patientCondition;
	}

	public void setPatientCondition(String patientCondition) {
		this.patientCondition = patientCondition;
	}

	@Column(name = "SAMPLE_REVTIME")
	public Date getSampleRevTime() {
		return sampleRevTime;
	}

	public void setSampleRevTime(Date sampleRevTime) {
		this.sampleRevTime = sampleRevTime;
	}

	@Column(name = "IF_QUALIFIED")
	public String getIfQualified() {
		return ifQualified;
	}

	public void setIfQualified(String ifQualified) {
		this.ifQualified = ifQualified;
	}

	@Column(name = "PEIYANG_TIME")
	public Date getPeiyangTime() {
		return peiyangTime;
	}

	public void setPeiyangTime(Date peiyangTime) {
		this.peiyangTime = peiyangTime;
	}

	public Date getPingbanTime() {
		return pingbanTime;
	}

	public void setPingbanTime(Date pingbanTime) {
		this.pingbanTime = pingbanTime;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
