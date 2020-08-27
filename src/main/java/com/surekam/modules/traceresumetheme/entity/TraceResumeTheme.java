package com.surekam.modules.traceresumetheme.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.PrePersist;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;
import java.lang.String;
import java.util.Date;

/**
 * 溯源产品主题管理Entity
 * @author liw
 * @version 2018-08-23
 */
@Entity
@Table(name = "t_trace_resume_theme")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceResumeTheme extends XGXTEntity<TraceResumeTheme> {
	
	private static final long serialVersionUID = 1L;
	private String id;//
	private String themeCode;//主题编号
	private String officeId;//企业编号
	private String corpName;//企业名称
	private String themeName;//主题名称
	private String themeType;//模板类型 :  1.设计模板    2.自定义模板
	private String themeTypeName;//模板类型名称
	private String skinId;//模板皮肤编号
	private String themeImg;//主题图片
	private String themeImgUrl;//主题图片绝对路径
	private String headUrl;//头部图片
	private String tailUrl;//尾部图片
	private String pageUrl;//主题页面url
	private String backgroundColor;//背景色
	private String status;//是否启用
	private String statusName;//是否启用名称
	private String creatUserName;//创建人
	private String templet_product_id;//模板产品id
	private String productName;//产品名称
	public TraceResumeTheme() {
		super();
	}
	
	@PrePersist
	public void prePersist(){
		this.id = IdGen.uuid();
	}
		
	@Id
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getThemeCode() {
		return this.themeCode;
	}
	public void setThemeCode(String themeCode) {
		this.themeCode = themeCode;
	}
	
	
	
	public String getThemeName() {
		return this.themeName;
	}
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	
	public String getThemeType() {
		return this.themeType;
	}
	public void setThemeType(String themeType) {
		this.themeType = themeType;
	}
	
	
	
	/**
	 * @return the officeId
	 */
	public String getOfficeId() {
		return officeId;
	}

	/**
	 * @param officeId the officeId to set
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	/**
	 * @return the skinId
	 */
	public String getSkinId() {
		return skinId;
	}

	/**
	 * @param skinId the skinId to set
	 */
	public void setSkinId(String skinId) {
		this.skinId = skinId;
	}

	public String getHeadUrl() {
		return this.headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	
	public String getTailUrl() {
		return this.tailUrl;
	}
	public void setTailUrl(String tailUrl) {
		this.tailUrl = tailUrl;
	}
	
	public String getBackgroundColor() {
		return this.backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	@Transient
	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	@Transient
	public String getThemeTypeName() {
		return themeTypeName;
	}

	public void setThemeTypeName(String themeTypeName) {
		this.themeTypeName = themeTypeName;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Transient
	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	@Transient
	public String getCreatUserName() {
		return creatUserName;
	}

	public void setCreatUserName(String creatUserName) {
		this.creatUserName = creatUserName;
	}

	public String getThemeImg() {
		return themeImg;
	}

	public void setThemeImg(String themeImg) {
		this.themeImg = themeImg;
	}
	
	@Transient
	public String getThemeImgUrl() {
		return themeImgUrl;
	}

	public void setThemeImgUrl(String themeImgUrl) {
		this.themeImgUrl = themeImgUrl;
	}

	
	
	public String getTemplet_product_id() {
		return templet_product_id;
	}

	public void setTemplet_product_id(String templet_product_id) {
		this.templet_product_id = templet_product_id;
	}

	@Transient
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	
	
	
}


