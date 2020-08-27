package com.surekam.modules.TraceDeliveryAddress.service;

import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.TraceDeliveryAddress.dao.TraceDeliveryAddressDao;
import com.surekam.modules.TraceDeliveryAddress.entity.TraceDeliveryAddress;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.traceproduct.entity.TraceProduct;

/**
 * 收货地址管理
 * @author wangyuewen
 *
 */
@Component
@Transactional(readOnly = true)
public class TraceDeliveryAddressService extends BaseService {

	@Autowired
	private TraceDeliveryAddressDao TraceDeliveryAddressDao;
	
	@Autowired
	private UserDao userDao;
	
	
	public TraceDeliveryAddress get(String id) {
		return TraceDeliveryAddressDao.get(id);
	}
	
	public Page<TraceDeliveryAddress> find(Page<TraceDeliveryAddress> page, TraceDeliveryAddress address,List<String> findChildrenOffice,String corpCode,String admin) {
		DetachedCriteria dc = TraceDeliveryAddressDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}else {
				dc.add(Restrictions.in("officeId", findChildrenOffice));
			}
		}else {
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}
		}
		if (address != null && address.getReceiver() != null && !"".equals(address.getReceiver())) {
			dc.add(Restrictions.like("receiver", address.getReceiver(), MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.desc("creatTime"));
		Page<TraceDeliveryAddress> page2 = new Page<TraceDeliveryAddress>();
		page2 = TraceDeliveryAddressDao.find(page, dc);
		return page2;
	}
	
	// 负责人只看自己批次的评论
	public Page<TraceDeliveryAddress> find(Page<TraceDeliveryAddress> page, TraceDeliveryAddress address,List<String> findChildrenOffice,String corpCode,String admin,User user) {
		DetachedCriteria dc = TraceDeliveryAddressDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}else {
				dc.add(Restrictions.in("officeId", findChildrenOffice));
			}
		}else {
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}
		}
		if (address != null && address.getReceiver() != null && !"".equals(address.getReceiver())) {
			dc.add(Restrictions.like("receiver", address.getReceiver(), MatchMode.ANYWHERE));
		}
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					dc.add(Restrictions.eq("creatUserid", user.getId()));
				}
			}
			
		}
		dc.addOrder(Order.desc("creatTime"));
		Page<TraceDeliveryAddress> page2 = new Page<TraceDeliveryAddress>();
		page2 = TraceDeliveryAddressDao.find(page, dc);
		return page2;
	}
	
	public List<TraceDeliveryAddress> findTraceDeliveryAddressByProductId(TraceDeliveryAddress address, String officeId) {
		DetachedCriteria dc = TraceDeliveryAddressDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(officeId)) {//非管理员进入
			dc.add(Restrictions.eq("officeId", officeId));
		}
		dc.add(Restrictions.ne("id", "1"));
		if (address != null && address.getReceiver() != null && !"".equals(address.getReceiver())) {
			dc.add(Restrictions.like("receiver", address.getReceiver(), MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.desc("creatTime"));
		List<TraceDeliveryAddress> list = TraceDeliveryAddressDao.find(dc);
		return list;
	}
	
	public List<TraceDeliveryAddress> findListExport(TraceDeliveryAddress address,List<String> findChildrenOffice,String corpCode,String admin) {
		DetachedCriteria dc = TraceDeliveryAddressDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}else {
				dc.add(Restrictions.in("officeId", findChildrenOffice));
			}
		}else {
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}
		}
		if (address != null && address.getReceiver() != null && !"".equals(address.getReceiver())) {
			dc.add(Restrictions.like("receiver", address.getReceiver(), MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.desc("creatTime"));
		List<TraceDeliveryAddress> list = TraceDeliveryAddressDao.find(dc);
		return list;
	}
	// 右上角收货地址，生产负责人只导出自己的数据
	public List<TraceDeliveryAddress> findListExport(TraceDeliveryAddress address,List<String> findChildrenOffice,String corpCode,String admin,User user) {
		DetachedCriteria dc = TraceDeliveryAddressDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}else {
				dc.add(Restrictions.in("officeId", findChildrenOffice));
			}
		}else {
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}
		}
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					dc.add(Restrictions.eq("creatUserid", user.getId()));
				}
			}
			
		}
		if (address != null && address.getReceiver() != null && !"".equals(address.getReceiver())) {
			dc.add(Restrictions.like("receiver", address.getReceiver(), MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.desc("creatTime"));
		List<TraceDeliveryAddress> list = TraceDeliveryAddressDao.find(dc);
		return list;
	}
	/**
	 * 获取默认地址
	 * @return
	 */
	public TraceDeliveryAddress getDefaultAddress(String userId) {
		Page<TraceDeliveryAddress> page = new Page<TraceDeliveryAddress>();
		DetachedCriteria dc = TraceDeliveryAddressDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceDeliveryAddress.FIELD_DEL_FLAG_XGXT, TraceDeliveryAddress.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("creatUserid", userId));
		dc.add(Restrictions.eq("isDefault", "1"));
		page = TraceDeliveryAddressDao.find(page, dc);
		List<TraceDeliveryAddress> addressList = page.getList();
		if(addressList != null && addressList.size() > 0) {
			return addressList.get(0);
		}else{
			return new TraceDeliveryAddress();
		}
	}
	
	/**
	 * 获取默认地址
	 * @return
	 */
	public TraceDeliveryAddress getAddressByOffice(Office office) {
		TraceDeliveryAddress traceDeliveryAddress = new TraceDeliveryAddress();
		String sql = "select a.* from sys_user a, sys_user_role b where a.office_id = :p1 and a.del_flag='0' and a.id = b.user_id and b.role_id='1852c8e247744ff184e8c162eff44f4c'";
		List<User> list = userDao.findBySql(sql, new Parameter(office.getId()),User.class);
		if(list!=null && list.size()>0){
			User user = list.get(0);
			traceDeliveryAddress.setReceiver(user.getName());
			traceDeliveryAddress.setPhoneNum(user.getPhone());
		}
		traceDeliveryAddress.setOfficeId(office.getId());
		traceDeliveryAddress.setOfficeName(office.getName());
		return traceDeliveryAddress;
	}
	
	/**
	 * 新增和编辑的保存
	 * @param TraceDeliveryAddress
	 */
	@Transactional(readOnly = false)
	public void save(TraceDeliveryAddress TraceDeliveryAddress) {
		if(StringUtils.isNotBlank(TraceDeliveryAddress.getId())){
			TraceDeliveryAddress.setStates("U");
		}else {
			if(TraceDeliveryAddress.getIsDefault() != null && TraceDeliveryAddress.getIsDefault().equals("1")) {
				TraceDeliveryAddress defaultAdress = this.getDefaultAddress(TraceDeliveryAddress.getCreatUserid());
				if(defaultAdress != null) {
					defaultAdress.setIsDefault("0");
					TraceDeliveryAddressDao.save(defaultAdress);
				}
			}
		}
		TraceDeliveryAddressDao.save(TraceDeliveryAddress);
	}
	
	@Transactional(readOnly = false)
	public void setDefaultAddress(String id, String userId) {
		TraceDeliveryAddress defaultAdress = this.getDefaultAddress(userId);
		if(defaultAdress != null && defaultAdress.getId() != null && !defaultAdress.getId().equals("")) {
			defaultAdress.setIsDefault("0");
			TraceDeliveryAddressDao.save(defaultAdress);
		}
		TraceDeliveryAddress address = this.getAddress(id, userId);
		if(address != null) {
			address.setIsDefault("1");
			TraceDeliveryAddressDao.save(address);
		}
	}
	
	@Transactional(readOnly = false)
	public void canceDefaultAddress(String id, String userId) {
		TraceDeliveryAddress address = this.getAddress(id, userId);
		if(address != null) {
			address.setIsDefault("0");
			TraceDeliveryAddressDao.save(address);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(String id, String userId) {
		TraceDeliveryAddress address = this.getAddress(id, userId);
		if(address != null) {
			TraceDeliveryAddressDao.deleteByXGXTId(address.getId());
		}
	}
	
	public  TraceDeliveryAddress getAddress(String id, String userId) {
		DetachedCriteria dc = TraceDeliveryAddressDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceDeliveryAddress.FIELD_DEL_FLAG_XGXT, TraceDeliveryAddress.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("id", id));
		//dc.add(Restrictions.eq("creatUserid", userId));
		List<TraceDeliveryAddress> list = TraceDeliveryAddressDao.find(dc);
		if(list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	public  TraceDeliveryAddress getAddress(String userId) {
		DetachedCriteria dc = TraceDeliveryAddressDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceDeliveryAddress.FIELD_DEL_FLAG_XGXT, TraceDeliveryAddress.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(userId)) {
			dc.add(Restrictions.eq("creatUserid", userId));
		}
		dc.addOrder(Order.desc("creatTime"));
		List<TraceDeliveryAddress> list = TraceDeliveryAddressDao.find(dc);
		if(list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return new TraceDeliveryAddress();
		}
	}
}
