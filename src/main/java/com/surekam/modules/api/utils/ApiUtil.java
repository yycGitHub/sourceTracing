package com.surekam.modules.api.utils;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class ApiUtil {
	
	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",  
        "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",  
        "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",  
        "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",  
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",  
        "W", "X", "Y", "Z"};  


	public static String generateShortUuid() {  
		StringBuffer shortBuffer = new StringBuffer();  
		String uuid = UUID.randomUUID().toString().replace("-", "");  
		for (int i = 0; i < 8; i++) {  
			String str = uuid.substring(i * 4, i * 4 + 4);  
			int x = Integer.parseInt(str, 16);
	    	shortBuffer.append(chars[x % 0x3E]);  
		}
		return shortBuffer.toString(); 
	}
	
	public static String generateToken() {  
		String token = UUID.randomUUID().toString().replace("-", "");  
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("md5");
			byte[] md5=md.digest(token.getBytes());
			Base64 encoder= new Base64();
			String code = URLEncoder.encode(encoder.encodeAsString(md5), "UTF-8");
			return code;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getJhqjm(String[] jhm, int minValue, int maxValue) {
		String result = "";
		if(jhm.length>0){
			if(jhm.length==1){
				if(Integer.parseInt(jhm[0])==minValue){
					result+= (minValue+1)+"-"+maxValue;
				}else if(Integer.parseInt(jhm[0])>minValue && Integer.parseInt(jhm[0])<maxValue){
					result+= minValue+"-"+(Integer.parseInt(jhm[0])-1)+","+(Integer.parseInt(jhm[0])+1)+"-"+maxValue;
				}else{
					result+= minValue+"-"+(maxValue-1);
				}
			}else{
				int i = 0;
				for(int j=0;j<jhm.length;j++){
					int p = Integer.parseInt(jhm[j]);
					if(j==0){
						if(p>minValue){
							if(minValue == (p-1)){
								result+= minValue+",";
							}else{
								result+= minValue + "-"+(p-1)+",";
							}
						}
					}else if(j>0 && j<jhm.length-1){
						if(p>i+1){
							if((p-1) == (i+1)){
								result+= (i+1)+",";
							}else{
								result+= (i+1) + "-" +(p-1)+",";
							}
						}
					}else if(j>0 && j==jhm.length-1){
						if(p>i+1){
							if((p-1) == (i+1)){
								result+= (i+1)+",";
							}else{
								result+= (i+1) + "-" +(p-1)+",";
							}
						}
						if(p<maxValue){
							if(p==maxValue-1){
								result+= maxValue; 
							}else{
								result+= (p+1) + "-" + maxValue;
							}
						}else{
							result = result.substring(0, result.length()-1);
						}
					}
					i = p ;
				}
			}
			
		}else{
			result = minValue + "-" + maxValue;
		}
		return result;
	}
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		
		System.out.println(getQjjhm(list));
	}
	
	public static String getQjjhm(List<Integer> jhm) {
		String result = "";
		if(jhm.size()==1){
			result = jhm.get(0).toString()+",";
		}else if(jhm.size()>1){
			int i = 0;
			for(int j = 0;j<jhm.size();j++){
				if(j == 0){
					i = jhm.get(0);
					result = i+",";
				}else{
					int p = Integer.parseInt(jhm.get(j).toString());
					if(p == i+1){
						result+= i+"-"+p+",";
					}else{
						result+= p+",";
					}
					i = p ;
				}
			}
		}
		if(result.length()>0){
			result = result.substring(0, result.length()-1);
		}
		System.out.println(result);
		String re = "";
		if(result.contains(",")){
			String s = "";
			String[] str = result.split(","); 
			for(String sss : str){
				if(StringUtils.isNotBlank(s)){
					if(sss.contains("-") && s.contains("-")){
						if(sss.split("-")[0].equals(s.split("-")[1])){
							if(StringUtils.isNotBlank(re)){
								re = re.substring(0, re.length()-s.length()-1);
							}
							re+=s.split("-")[0]+"-"+sss.split("-")[1]+",";
							s = s.split("-")[0]+"-"+sss.split("-")[1];
						}
					}else if(sss.contains("-") && !s.contains("-")){
						if(sss.split("-")[0].equals(s)){
							if(StringUtils.isNotBlank(re)){
								re = re.substring(0, re.length()-s.length()-1);
							}
							re+=sss+",";
							s = sss;
						}
					}else{
						re+=sss+",";
						s = sss;
					}
				}else{
					s = sss;
					re=sss+",";
				}
			}
			if(re.length()>0){
				re = re.substring(0, re.length()-1);
			}
		}else{
			re = result;
		}
		return re;
	}
	
}
