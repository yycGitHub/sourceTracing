package com.surekam.modules.api.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.TraceDeliveryAddress.entity.TraceDeliveryAddress;
import com.surekam.modules.TraceDeliveryAddress.service.TraceDeliveryAddressService;
import com.surekam.modules.api.service.ApiUserService;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author wangyuewen
 * 收货地址管理
 */

@Api(value="溯源收货地址管理接口Controller", description="溯源收货地址管理的相关数据接口")
@Controller
@RequestMapping(value = "api/traceDeliveryAddress")
public class TraceDeliveryAddressController extends BaseController {
	
	@Autowired
	private ApiUserService apiUserService;
	
	@Autowired
	private TraceDeliveryAddressService traceDeliveryAddressService;
	
	@Autowired
	private OfficeService officeService;
	
	/**
	 * 收货地址列表
	 * @author wangyuewen
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "list",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "收货地址列表", httpMethod = "GET", 
	notes = "收货地址列表",	consumes="application/x-www-form-urlencoded")
	public String list(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String receiver,
			@RequestParam(required = false) String corpCode,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String, Object> map = new HashMap<String, Object>();
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceDeliveryAddress> page = new Page<TraceDeliveryAddress>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		if (token != null) {
			TraceDeliveryAddress traceDeliveryAddress = new TraceDeliveryAddress();
			User user = apiUserService.getUserByToken(token);
			List<String> findChildrenOffice = new ArrayList<String>();
			if(!StringUtils.isNotBlank(corpCode)) {
				findChildrenOffice = officeService.findChildrenOffice(user.getCompany().getId());
			}
			String admin = "";
			// roleType 1: 非管路员 0 ：则管路员
			if (!user.isAdmin()) {
				map.put("roleType", "0");
				admin = user.getCompany().getId();
				traceDeliveryAddress.setCreatUserid(user.getId());
			} else {
				map.put("roleType", "0");
			}
			traceDeliveryAddress.setReceiver(receiver);
			page = traceDeliveryAddressService.find(page, traceDeliveryAddress,findChildrenOffice,corpCode,admin,user);
			List<TraceDeliveryAddress> list = page.getList();
			if (null != list && 0 != list.size()) {
				for (Iterator<TraceDeliveryAddress> iterator = list.iterator(); iterator.hasNext();) {
					TraceDeliveryAddress deliveryAddress = (TraceDeliveryAddress) iterator.next();
					if(!deliveryAddress.getOfficeId().equals("") && deliveryAddress.getOfficeId() != null) {
						Office office = officeService.get(deliveryAddress.getOfficeId());
						deliveryAddress.setOfficeName(office.getName());
					}else {
						deliveryAddress.setOfficeName("");
					}
					
					StringBuffer str = new StringBuffer();
					if(StringUtils.isNotBlank(deliveryAddress.getReceiver())) {
						str.append(deliveryAddress.getReceiver() + '\u0009');
					}
					if(StringUtils.isNotBlank(deliveryAddress.getPhoneNum())) {
						str.append(deliveryAddress.getPhoneNum() + '\u0009');
					}
					if(StringUtils.isNotBlank(deliveryAddress.getProvince())) {
						str.append(deliveryAddress.getProvince());
					}
					if(StringUtils.isNotBlank(deliveryAddress.getCity())) {
						str.append(deliveryAddress.getCity());
					}
					if(StringUtils.isNotBlank(deliveryAddress.getArea())) {
						str.append(deliveryAddress.getArea());
					}
					if(StringUtils.isNotBlank(deliveryAddress.getStreet())) {
						str.append(deliveryAddress.getStreet());
					}
					if(StringUtils.isNotBlank(deliveryAddress.getDetailAddress())) {
						str.append(deliveryAddress.getDetailAddress());
					}
					deliveryAddress.setAddressMosaicing(str.toString());
				}
			}
			map.put("page", page);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
	
	/**
	 * 收货地址导出数据，不分页
	 * @author wangyuewen
	 * @param request
	 * @param response
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "listExport",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "收货地址导出数据，不分页", httpMethod = "GET", 
	notes = "收货地址导出数据，不分页",	consumes="application/x-www-form-urlencoded")
	public String listExport(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(required = false) String receiver,
			@RequestParam(required = false) String corpCode,
			@RequestParam Integer pageno,
			@RequestParam Integer pagesize) {
		response.setContentType("application/json; charset=UTF-8");
		Map<String, Object> map = new HashMap<String, Object>();
		int pageNo = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int pageSize = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;
		Page<TraceDeliveryAddress> page = new Page<TraceDeliveryAddress>(pageNo, pageSize);
		String token = request.getHeader("X-Token");
		if (token != null) {
			TraceDeliveryAddress traceDeliveryAddress = new TraceDeliveryAddress();
			User user = apiUserService.getUserByToken(token);
			List<String> findChildrenOffice = new ArrayList<String>();
			if(!StringUtils.isNotBlank(corpCode)) {
				findChildrenOffice = officeService.findChildrenOffice(user.getCompany().getId());
			}
			String admin = "";
			// roleType 1: 非管路员 0 ：则管路员
			if (!user.isAdmin()) {
				map.put("roleType", "0");
				admin = user.getCompany().getId();
				traceDeliveryAddress.setCreatUserid(user.getId());
			} else {
				map.put("roleType", "0");
			}
			traceDeliveryAddress.setReceiver(receiver);
			List<TraceDeliveryAddress> list = traceDeliveryAddressService.findListExport(traceDeliveryAddress,findChildrenOffice,corpCode,admin,user);
			if (null != list && 0 != list.size()) {
				for (Iterator<TraceDeliveryAddress> iterator = list.iterator(); iterator.hasNext();) {
					TraceDeliveryAddress deliveryAddress = (TraceDeliveryAddress) iterator.next();
					if(!deliveryAddress.getOfficeId().equals("") && deliveryAddress.getOfficeId() != null) {
						Office office = officeService.get(deliveryAddress.getOfficeId());
						deliveryAddress.setOfficeName(office.getName());
					}else {
						deliveryAddress.setOfficeName("");
					}
					StringBuffer str = new StringBuffer();
					if(StringUtils.isNotBlank(deliveryAddress.getReceiver())) {
						str.append(deliveryAddress.getReceiver() + '\u0009');
					}
					if(StringUtils.isNotBlank(deliveryAddress.getPhoneNum())) {
						str.append(deliveryAddress.getPhoneNum() + '\u0009');
					}
					if(StringUtils.isNotBlank(deliveryAddress.getProvince())) {
						str.append(deliveryAddress.getProvince());
					}
					if(StringUtils.isNotBlank(deliveryAddress.getCity())) {
						str.append(deliveryAddress.getCity());
					}
					if(StringUtils.isNotBlank(deliveryAddress.getArea())) {
						str.append(deliveryAddress.getArea());
					}
					if(StringUtils.isNotBlank(deliveryAddress.getStreet())) {
						str.append(deliveryAddress.getStreet());
					}
					if(StringUtils.isNotBlank(deliveryAddress.getDetailAddress())) {
						str.append(deliveryAddress.getDetailAddress());
					}
					deliveryAddress.setAddressMosaicing(str.toString());
				}
			}
			page.setList(list);
			map.put("page", page);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(map));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
			
	/**
	 * @author wangyuewen
	 * 获取默认收货地址
	 * @return
	 */
	@RequestMapping(value = "getDefaultAddress",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "获取默认收货地址", httpMethod = "GET", 
	notes = "获取默认收货地址",	consumes="application/x-www-form-urlencoded")
	public String getDefaultAddress(HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		if(token != null) {
			User user = apiUserService.getUserByToken(token);
			TraceDeliveryAddress traceDeliveryAddress = traceDeliveryAddressService.getDefaultAddress(user.getId());
			StringBuffer str = new StringBuffer();
			if(StringUtils.isNotBlank(traceDeliveryAddress.getId())) {
				if(StringUtils.isNotBlank(traceDeliveryAddress.getReceiver())) {
					str.append(traceDeliveryAddress.getReceiver() + '\u0009');
				}
				if(StringUtils.isNotBlank(traceDeliveryAddress.getPhoneNum())) {
					str.append(traceDeliveryAddress.getPhoneNum() + '\u0009');
				}
				if(StringUtils.isNotBlank(traceDeliveryAddress.getProvince())) {
					str.append(traceDeliveryAddress.getProvince());
				}
				if(StringUtils.isNotBlank(traceDeliveryAddress.getCity())) {
					str.append(traceDeliveryAddress.getCity());
				}
				if(StringUtils.isNotBlank(traceDeliveryAddress.getArea())) {
					str.append(traceDeliveryAddress.getArea());
				}
				if(StringUtils.isNotBlank(traceDeliveryAddress.getStreet())) {
					str.append(traceDeliveryAddress.getStreet());
				}
				if(StringUtils.isNotBlank(traceDeliveryAddress.getDetailAddress())) {
					str.append(traceDeliveryAddress.getDetailAddress());
				}
				traceDeliveryAddress.setAddressMosaicing(str.toString());
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceDeliveryAddress));
			}else {
				traceDeliveryAddress.setAddressMosaicing("");
				return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(traceDeliveryAddress));
			}
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
			
	/**
	 * @author wangyuewen
	 * 通过id获取收货地址详情
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "getAdreessDetailById",produces="application/json;charset=UTF-8", method = {RequestMethod.GET})
	@ResponseBody
	@ApiOperation(value = "通过id获取收货地址详情", httpMethod = "GET", 
	notes = "通过id获取收货地址详情",	consumes="application/x-www-form-urlencoded")
	public String getAdreessDetailById(HttpServletRequest request,
			@RequestParam String id) {
		if(StringUtils.isNotBlank(id)){
			TraceDeliveryAddress address = traceDeliveryAddressService.get(id);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(address));
		}else{
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage()));
		}
	}

	/**
	 * @author wangyuewen
	 * 保存收货地址
	 * @param request
	 * @param traceProduct
	 * @return
	 */
	@RequestMapping(value = "saveAddress",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "保存收货地址", httpMethod = "POST", 
	notes = "保存收货地址",	consumes="application/json")
	public String saveAddress(HttpServletRequest request,
			@RequestBody @ApiParam(name="收货地址对象",value="传入json格式",required=true) TraceDeliveryAddress address) {
		String token = request.getHeader("X-Token");
		if(StringUtils.isNotBlank(token)){
			User user = apiUserService.getUserByToken(token);
			address.setCreatUserid(user.getId());
			address.setOfficeId(user.getCompany().getId());
			traceDeliveryAddressService.save(address);
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(address));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	
	/**
	 * @author xy
	  * 申请标签窗口保存并使用地址
	 * @param request
	 * @param traceProduct
	 * @return
	 */
	@RequestMapping(value = "saveAndUseAddress",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "申请标签窗口保存并使用地址", httpMethod = "POST", notes = "申请标签窗口保存并使用地址",	consumes="application/json")
	public String saveAndUseAddress(HttpServletRequest request,
			@RequestBody @ApiParam(name="收货地址对象",value="传入json格式",required=true) TraceDeliveryAddress address) {
		String token = request.getHeader("X-Token");
		if(StringUtils.isNotBlank(token)){
			User user = apiUserService.getUserByToken(token);
			address.setCreatUserid(user.getId());
			traceDeliveryAddressService.save(address);
			address = traceDeliveryAddressService.getAddress(user.getId());
			StringBuffer str = new StringBuffer();
			if(StringUtils.isNotBlank(address.getReceiver())) {
				str.append(address.getReceiver() + '\u0009');
			}
			if(StringUtils.isNotBlank(address.getPhoneNum())) {
				str.append(address.getPhoneNum() + '\u0009');
			}
			if(StringUtils.isNotBlank(address.getProvince())) {
				str.append(address.getProvince());
			}
			if(StringUtils.isNotBlank(address.getCity())) {
				str.append(address.getCity());
			}
			if(StringUtils.isNotBlank(address.getArea())) {
				str.append(address.getArea());
			}
			if(StringUtils.isNotBlank(address.getStreet())) {
				str.append(address.getStreet());
			}
			if(StringUtils.isNotBlank(address.getDetailAddress())) {
				str.append(address.getDetailAddress());
			}
			address.setAddressMosaicing(str.toString());//在后台拼接地址返回给前台
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(address));
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
	
	/**
	 * 设置默认收货地址
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "setDefaultAddress",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "设置默认收货地址", httpMethod = "POST", 
	notes = "设置默认收货地址",	consumes="application/x-www-form-urlencoded")
	public String setDefaultAddress(HttpServletRequest request,@RequestParam String id){
		String token = request.getHeader("X-Token");
		if(StringUtils.isNotBlank(token)){
			User user = apiUserService.getUserByToken(token);
			traceDeliveryAddressService.setDefaultAddress(id, user.getId());
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
	
	/**
	 * 取消默认
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "canceDefaultAddress",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "取消默认收货地址", httpMethod = "POST", notes = "取消默认收货地址",	consumes="application/x-www-form-urlencoded")
	public String canceDefaultAddress(HttpServletRequest request,@RequestParam String id) {
		String token = request.getHeader("X-Token");
		if(StringUtils.isNotBlank(token)){
			User user = apiUserService.getUserByToken(token);
			traceDeliveryAddressService.canceDefaultAddress(id, user.getId());
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}

	/**
	 * 根据id删除收货地址
	 * @author wangyuewen
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "deleteById",produces="application/json;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "根据id删除收货地址", httpMethod = "POST", 
	notes = "根据id删除收货地址",	consumes="application/x-www-form-urlencoded")
	public String deleteById(HttpServletRequest request,@RequestParam String id){
		String token = request.getHeader("X-Token");
		if(StringUtils.isNotBlank(token)){
			User user = apiUserService.getUserByToken(token);
			traceDeliveryAddressService.delete(id, user.getId());
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success());
		} else {
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.TOKEN_IS_NULL.getCode(), ResultEnum.TOKEN_IS_NULL.getMessage()));
		}
	}
	
	/**
	 * 收货地址编辑保存
	 * @param request
	 * @param id
	 * @param receiver
	 * @param phoneNum
	 * @param province
	 * @param city
	 * @param area
	 * @param street
	 * @param detailAddress
	 * @param zipCode
	 * @return
	 */
	@RequestMapping(value = "updateAddressSave",produces="text/plain;charset=UTF-8", method = {RequestMethod.POST})
	@ResponseBody
	@ApiOperation(value = "收货地址修改保存", httpMethod = "POST",   notes = "收货地址修改保存",	consumes="application/json")
	public String updateAddressSave(HttpServletRequest request,
			@RequestParam String id,
			@RequestParam String receiver,
			@RequestParam String phoneNum,
			@RequestParam String province,
			@RequestParam String city,
			@RequestParam String area,
			@RequestParam(required = false) String street,
			@RequestParam String detailAddress,
			@RequestParam String zipCode,
			@RequestParam String officeId) {
		
		String token = request.getHeader("X-Token");
		TraceDeliveryAddress add = new TraceDeliveryAddress();
		if(StringUtils.isNotBlank(id)) {
			add = traceDeliveryAddressService.get(id);
		}
		
		if(StringUtils.isNotBlank(receiver)) {
			add.setReceiver(receiver);
		}
	    if(StringUtils.isNotBlank(phoneNum)) {
			add.setPhoneNum(phoneNum);
		}
	    if(StringUtils.isNotBlank(province)) {
			add.setProvince(province);
		}
		if(StringUtils.isNotBlank(city)) {
			add.setCity(city);
		}
		if(StringUtils.isNotBlank(area)) {
			add.setArea(area);
		}
		if(StringUtils.isNotBlank(street)) {
			add.setStreet(street);
		}
		if(StringUtils.isNotBlank(detailAddress)) {
			add.setDetailAddress(detailAddress);
		}
		if(StringUtils.isNotBlank(zipCode)) {
			add.setZipCode(zipCode);
		}
		if(StringUtils.isNotBlank(officeId)) {
			add.setOfficeId(officeId);
		}
		
		User user = apiUserService.getUserByToken(token);
		add.setUpdateUserid(user.getId());
		add.setUpdateTime(new Date());	
		
		try{
			traceDeliveryAddressService.save(add);
		}catch(Exception e){
			e.printStackTrace();
			return JsonMapper.nonDefaultMapper().toJson(ResultUtil.error(ResultEnum.SYSTEM_ERROR.getCode(),ResultEnum.SYSTEM_ERROR.getMessage()));
		}
		return JsonMapper.nonDefaultMapper().toJson(ResultUtil.success(add));
	}
	
}
