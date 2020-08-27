package com.surekam.modules.traceverifycode.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.modules.traceverifycode.entity.TraceVerifyCode;
import com.surekam.modules.traceverifycode.dao.TraceVerifyCodeDao;

/**
 * 溯源码表Service
 * @author liw
 * @version 2019-07-10
 */
@Component
@Transactional(readOnly = true)
public class TraceVerifyCodeService extends BaseService {
	
	//生成验证码
	private static char[] ops = new char[] {'+', '-', 'x'};

	@Autowired
	private TraceVerifyCodeDao traceVerifyCodeDao;
	
	public TraceVerifyCode get(String id) {
		return traceVerifyCodeDao.get(id);
	}
	
	public Page<TraceVerifyCode> find(Page<TraceVerifyCode> page, TraceVerifyCode traceVerifyCode) {
		DetachedCriteria dc = traceVerifyCodeDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceVerifyCode.FIELD_DEL_FLAG, TraceVerifyCode.DEL_FLAG_NORMAL));
		return traceVerifyCodeDao.find(page, dc);
	}
	
	public TraceVerifyCode findTraceVerifyCodeByPhone(String phone) {
		String hql = " from TraceVerifyCode a where a.states<>'D' and a.phone=:p1 ";
		List<TraceVerifyCode> list = traceVerifyCodeDao.find(hql, new Parameter(phone));
		if(list!=null && list.size()>0){
			return list.get(0);
		}else{
			return new TraceVerifyCode();
		}
	}
	
	@Transactional(readOnly = false)
	public void save(String verifyCode, String phone, String rnd) {
		TraceVerifyCode traceVerifyCode = findTraceVerifyCodeByPhone(phone);
		traceVerifyCode.setFormula(verifyCode);
		traceVerifyCode.setAnswer(rnd);
		traceVerifyCode.setPhone(phone);
		traceVerifyCodeDao.save(traceVerifyCode);
	}
	
	public BufferedImage createVerifyCode(String phone) {
		int width = 80;
		int height = 32;
		//create the image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// set the background color
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		// draw the border
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// create a random instance to generate the codes
		Random rdm = new Random();
		// make some confusion
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		// generate a random code
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 20));
		g.drawString(verifyCode, 8, 24);
		g.dispose();
		//把验证码存到redis中
		int rnd = calc(verifyCode);
		save(verifyCode, phone, rnd+"");
		//输出图片	
		return image;
	}
	
	//计算结果
	private int calc(String exp) {
		try {
			exp = exp.replaceAll("X", "*").replaceAll("x", "*");
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			return ((Double)engine.eval(exp)).intValue();
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
 
	private String generateVerifyCode(Random rdm) {
		int num1 = rdm.nextInt(10);
	    int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = ""+ num1 + op1 + num2 + op2 + num3;
		return exp;
	}
	
}
