package com.surekam.modules.sys.utils;

import com.surekam.common.utils.ResultEnum;

/**
 * 溯源码生成工具类
 * @author 腾农科技
 *
 */
public class TracingSourceCode{
	/**
	 * 
	 * @param qybh 企业编号
	 * @param jgscdbh  加工生产点编号
	 * @param sjbh  时间编码
	 * @param cpzlbh  产品种类编码
	 * @param bzzlbh  包装种类编码
	 * @param xlh  序列
	 * @return
	 */
	public static String GeneratingTraceableCode(String qybh,String jgscdbh,String sjbh,String cpzlbh,String bzzlbh,String xlh) {
		if(qybh.length() !=3) {
			return ResultEnum.COMPANY_CODE_WRONG.getCode()+"_"+ResultEnum.COMPANY_CODE_WRONG.getMessage();
		}
		if(jgscdbh.length() !=3) {
			return ResultEnum.BASE_CODE_WRONG.getCode()+"_"+ResultEnum.BASE_CODE_WRONG.getMessage();
		}
		if(sjbh.length() !=8) {
			return ResultEnum.TIME_CODE_WRONG.getCode()+"_"+ResultEnum.TIME_CODE_WRONG.getMessage();
		}
		if(cpzlbh.length() !=1) {
			return ResultEnum.PRODUCT_CODE_WRONG.getCode()+"_"+ResultEnum.PRODUCT_CODE_WRONG.getMessage();
		}
		if(bzzlbh.length() !=1) {
			return ResultEnum.PACKING_CODE_WRONG.getCode()+"_"+ResultEnum.PACKING_CODE_WRONG.getMessage();
		}
		if(xlh.length() !=8) {
			return ResultEnum.SUMBER_CODE_WRONG.getCode()+"_"+ResultEnum.SUMBER_CODE_WRONG.getMessage();
		}
		String str = qybh + jgscdbh + sjbh + cpzlbh + bzzlbh + xlh;
		return CheckCode(str);
	}
	/**
	 *   代码位置序号是指包括校验码在内的，由右至左的顺序号（校验码的代码位置序号为1）
	 *   校验码的计算步骤如下：
	 *   a) 从代码位置序号2开始，所有偶数位的数字代码求和。
	 *   b) 将步骤a)的和乘以3。
	 *   c) 从代码位置序号3开始，所有奇数位的数字代码求和。
	 *   d) 将步骤b)与步骤c)的结果相加。
	 *   e) 用10减去步骤d)所得结果的个位数作为校验码（个位数为0，校验码为0）。
	 * 001001201906121100000012
	 * @param str
	 * @return
	 */
	public static String CheckCode(String str) {
		//字符串顺序反转
		String str2 = new StringBuilder(str).reverse().toString();
		
		char[] chars = str2.toCharArray();
		int num0 = 0;
		int num1 = 0; 
		for (int i =0; i < chars.length;  i++) {
			//传入进来的值是不带校验码的,故字符串反转后的奇数位就是算法规则中的偶数位
			if(i%2 == 0) {
				num1 += Integer.parseInt(String.valueOf(chars[i]));
			}else {
				num0 +=Integer.parseInt(String.valueOf(chars[i]));
			}
		}
		int num = 10 - ( (num1 * 3 + num0) % 10);
		if(num == 10){
			num = 0;
		}
		return str + String.valueOf(num);
	}
	
	public static void main(String[] args) {
		System.out.println(CheckCode("001001201906121100000012"));
	}

}
