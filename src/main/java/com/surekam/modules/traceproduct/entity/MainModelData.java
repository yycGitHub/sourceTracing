package com.surekam.modules.traceproduct.entity;

import com.surekam.modules.productbatch.entity.ProductBatch;

public class MainModelData {

	private String identityCode;// 身份编号
	private String dateOfManufacture;// 生产日期
	private String qualityGuaranteePeriod;// 保质期
	private String queryCount;// 查询次数
	private String restOfDays;
	private String producePlace;// 生产场地
	private TraceProduct product;// 溯源产品
	private ProductBatch productBatch;// 溯源批次

	public ProductBatch getProductBatch() {
		return productBatch;
	}

	public void setProductBatch(ProductBatch productBatch) {
		this.productBatch = productBatch;
	}

	private String skinId;// 皮肤id

	public void setIdentityCode(String identityCode) {
		this.identityCode = identityCode;
	}

	public String getIdentityCode() {
		return identityCode;
	}

	public void setDateOfManufacture(String dateOfManufacture) {
		this.dateOfManufacture = dateOfManufacture;
	}

	public String getDateOfManufacture() {
		return dateOfManufacture;
	}

	public void setQualityGuaranteePeriod(String qualityGuaranteePeriod) {
		this.qualityGuaranteePeriod = qualityGuaranteePeriod;
	}

	public String getQualityGuaranteePeriod() {
		return qualityGuaranteePeriod;
	}

	public void setQueryCount(String queryCount) {
		this.queryCount = queryCount;
	}

	public String getQueryCount() {
		return queryCount;
	}

	/**
	 * @return the restOfDays
	 */
	// public String getRestOfDays() {
	// if(this.getDateOfManufacture() != null){
	// SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd");
	// try {
	// Date date1 = formatter.parse(this.getDateOfManufacture());
	// String date2String = formatter.format(new Date());
	// Date date2 = formatter.parse(date2String);
	// int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
	// int qualityGuaranteePeriodInt =
	// this.getQualityGuaranteePeriod()==null?0:Integer.parseInt(this.getQualityGuaranteePeriod());
	// int restDay = qualityGuaranteePeriodInt - days ;
	// restOfDays = String.valueOf(restDay);
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return restOfDays;
	// }
	/**
	 * @param restOfDays
	 *            the restOfDays to set
	 */
	public void setRestOfDays(String restOfDays) {
		this.restOfDays = restOfDays;
	}

	public String getProducePlace() {
		return producePlace;
	}

	public void setProducePlace(String producePlace) {
		this.producePlace = producePlace;
	}

	public TraceProduct getProduct() {
		return product;
	}

	public void setProduct(TraceProduct product) {
		this.product = product;
	}

	/**
	 * @return the skinId
	 */
	public String getSkinId() {
		return skinId;
	}

	/**
	 * @param skinId
	 *            the skinId to set
	 */
	public void setSkinId(String skinId) {
		this.skinId = skinId;
	}

}