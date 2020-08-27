package com.surekam.common.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.surekam.modules.traceproduct.service.TraceProductService;

@Component
@Lazy(false)
public class UniqueCodeUtil {
	private final static Log log = LogFactory.getLog(UniqueCodeUtil.class);
	private static AtomicInteger atomicInteger;
	private static String formatStr = "yyyyMMdd";
	private static String STR_FORMAT = "0000";
	private final static int atNew = 1;
	
	public static String PRODUCT_PRE = "PRO";
	public static String BATCH_PRE = "BAT";

	@Autowired
	private TraceProductService traceProductService;
	
	private static UniqueCodeUtil uniqueCodeUtil;
	
	public void setTraceProductService(TraceProductService traceProductService) {
		this.traceProductService = traceProductService;
	}

	@PostConstruct  
    public void init() {  
		uniqueCodeUtil = this;  
		uniqueCodeUtil.traceProductService = this.traceProductService;  
    } 

	/**
	 * 获取对应公司的对应对象的唯一编码
	 * @param poName  实体对象
	 * @param poCode  实体编码属性
	 * @param corpCode  公司编码 不是ID
	 * @return
	 */
	public static synchronized String getUniqueCode(String poName, String poCode, String corpCode) {
		log.debug("UniqueCodeUtil begin");
		SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
		String uniqueCode = formatter.format(new Date());
		String maxUniqueCode = uniqueCodeUtil.traceProductService.getMaxNum(poName,poCode,corpCode);
		String number;
		int toInt;
		String dateStr;
		if(null == maxUniqueCode || "".equals(maxUniqueCode)) {
			dateStr = uniqueCode;
			toInt = 0;
		}else {
			number = maxUniqueCode.substring(maxUniqueCode.length()-4, maxUniqueCode.length());
			toInt = Integer.parseInt(number);
			dateStr = maxUniqueCode.substring(maxUniqueCode.length()-12, maxUniqueCode.length()-4);
		}
		if (null == atomicInteger) {
			if(uniqueCode.equals(dateStr)) {
				atomicInteger  = new AtomicInteger(toInt+1);				
			}else {
				atomicInteger  = new AtomicInteger(atNew);
			}
		} else if (null != atomicInteger) {
			if(!uniqueCode.equals(dateStr)) {
				atomicInteger  = new AtomicInteger(atNew);
			}
			if(null == maxUniqueCode || "".equals(maxUniqueCode)){
				atomicInteger  = new AtomicInteger(atNew);
			}else if(uniqueCode.equals(dateStr)) {
				atomicInteger  = new AtomicInteger(toInt+1);				
			}
		}
		DecimalFormat df = new DecimalFormat(STR_FORMAT);
		int	at = atomicInteger.getAndIncrement();
		uniqueCode = uniqueCode + df.format(at);
		log.debug("UniqueCodeUtil end");
		return uniqueCode;
	}
	
}
