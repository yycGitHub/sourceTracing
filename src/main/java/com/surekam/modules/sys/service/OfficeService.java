package com.surekam.modules.sys.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.common.service.ServiceException;
import com.surekam.modules.sys.dao.OfficeDao;
import com.surekam.modules.sys.dao.RoleDao;
import com.surekam.modules.sys.entity.CommonUser;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.utils.UserUtils;

/**
 * 机构Service
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends BaseService {

	@Autowired
	private OfficeDao officeDao;
	
	@Autowired
	private RoleDao roleDao;
	
	public Office get(String id) {
		return officeDao.get(id);
	}
	
	public List<Office> findAll(){
		return UserUtils.getOfficeList();
	}
	
	public List<Office> findAllThree(User user){
		DetachedCriteria dc = officeDao.createDetachedCriteria();
		dc.add(dataScopeFilter(user, dc.getAlias(), ""));
		dc.add(Restrictions.eq(Office.FIELD_DEL_FLAG, Office.DEL_FLAG_NORMAL));
		dc.addOrder(Order.asc("name"));
		return officeDao.find(dc);
	}
	
	/**
	 * 传入code（wangcheng或者tulufan）id
	 * @param officeId
	 * @return
	 */
	public List<Office> findAllTwo(String officeId){
		StringBuffer sql = new StringBuffer(200);
		if (StringUtils.isNotBlank(officeId) && !"1".equals(officeId)) {
			sql.append("SELECT * FROM sys_office a WHERE a.DEL_FLAG='0' and (a.id = '" + officeId + "' OR a.PARENT_IDS LIKE '%" + officeId + "%' )");
		}
		if (StringUtils.isNotBlank(officeId) && "1".equals(officeId)) {
			sql.append("SELECT * FROM sys_office a WHERE a.DEL_FLAG='0' and a.PARENT_IDS LIKE '%" + officeId + "%'");
		}
		return officeDao.findBySql(sql.toString(),new Parameter(), Office.class);
	}
	
	/**
	 * 获取一级企业的名称
	 * @param officeId
	 * @return
	 */
	public String getParentOfficeName(User user){
		String parentOfficeName = "";
		if(user.isAdmin()){
			parentOfficeName = "所有公司";
		}else{
			if(user.getOffice().getParentIds().equals("0,1,")){
				parentOfficeName = user.getOffice().getName();
			}else if(user.getOffice().getParent().getParentIds().equals("0,1,")){
				parentOfficeName = user.getOffice().getParent().getName();
			}
		}
		return parentOfficeName;
	}
	
	public List<Office> findAllOffices(CommonUser user){
		DetachedCriteria dc = officeDao.createDetachedCriteria();		
		dc.add(Restrictions.eq(Office.FIELD_DEL_FLAG, Office.DEL_FLAG_NORMAL));
		Office office = new Office();
		office.setId("1");
		dc.add(Restrictions.eq("parent", office));
		dc.addOrder(Order.asc("name"));
		if(!user.getRoleIdList().contains("1")){
			List<String> officeList = getOfficeByRoleId(user.getRoleIdList());
			dc.add(Restrictions.in("id", officeList));
		}
		officeDao.clear();
		return officeDao.find(dc);
	}
	
	/**
	 * 根据用户获取企业列表
	 * @param user
	 * @return
	 */
	public List<Office> findOfficesByUser(User user) {
		List<Office> list = new ArrayList<Office>();
		if(user.isAdmin()) {
			list = findAll();
		} else {
			list.add(user.getCompany());
		}
		return list;
	}
	
	
	@Transactional(readOnly = false)
	public void save(Office office) {
		String oldParentIds = office.getParentIds(); // 获取修改前的parentIds，用于更新子节点的parentIds
		//修改数据为顶点时， 没有parent
		if(null != office.getParent() && StringUtils.isNotBlank(office.getParent().getId())){
			office.setParent(this.get(office.getParent().getId()));
			office.setParentIds(office.getParent().getParentIds()+office.getParent().getId()+",");
		}
		officeDao.clear();
		officeDao.save(office);
		// 更新子节点 parentIds
		List<Office> list = officeDao.findByParentIdsLike("%,"+office.getId()+",%");
		for (Office e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, office.getParentIds()));
		}
		officeDao.save(list);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		officeDao.deleteById(id, "%,"+id+",%");
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

	public List<Office> findZtreeOffices(User user, String officeId) {
		DetachedCriteria dc = officeDao.createDetachedCriteria();		
		if(StringUtils.isNotBlank(officeId)){
			dc.createAlias("parent", "office");
			dc.add(Restrictions.eq("parent.id",officeId));
		}else{
			dc.add(Restrictions.eq("id","1"));
		}
		//dc.add(dataScopeFilter(user, dc.getAlias(), ""));
		dc.add(Restrictions.eq(Office.FIELD_DEL_FLAG, Office.DEL_FLAG_NORMAL));
		dc.addOrder(Order.asc("code"));
		officeDao.clear();
		return officeDao.find(dc);
	}

	public Page<Office> findOffice(Page<Office> page, Office office,User user) {
		DetachedCriteria dc = officeDao.createDetachedCriteria();	
		//dc.add(dataScopeFilter(user, "office", ""));	
		if (StringUtils.isNotEmpty(office.getId())){
			dc.createAlias("parent", "office");
			dc.add(Restrictions.eq("parent.id", office.getId()));
		}
		dc.add(Restrictions.eq(Office.FIELD_DEL_FLAG, office.getDelFlag()));
		dc.addOrder(Order.desc("createDate"));
		officeDao.clear();
        Page<Office> offices = officeDao.find(page, dc);
		return offices;
	}
	
	public List<String> findChildrenOffice(String officeId){
		String sql = "SELECT b.id FROM (SELECT * FROM sys_office a WHERE a.id = '" + officeId + "' AND a.DEL_FLAG='0'"
			+" UNION ALL"
			+" SELECT * FROM sys_office a WHERE a.parent_ids LIKE '%," + officeId + ",%' AND a.DEL_FLAG='0') b";
		return officeDao.findBySql(sql);
	} 

	public List<Office> findChildrenOfficeList(String officeId){
		String sql = "SELECT b.* FROM (SELECT * FROM sys_office a WHERE a.id = '" + officeId + "'"
			+" UNION ALL"
			+" SELECT * FROM sys_office a WHERE a.parent_ids LIKE '%," + officeId + ",%' and a.DEL_FLAG='0') b order by b.name";
		return officeDao.findBySql(sql,null,Office.class);
	}
	
	public List<Map<String,Object>> findChildrenOfficeList1(String officeId){
		String sql = "SELECT b.* FROM (SELECT * FROM sys_office a WHERE a.id = '" + officeId + "'"
			+" UNION ALL"
			+" SELECT * FROM sys_office a WHERE a.parent_ids LIKE '%," + officeId + ",%' and a.DEL_FLAG='0') b order by b.name";
		return officeDao.findBySql(sql,null,Map.class);
	}

	@Transactional(readOnly = false)
	public void rebackOffices(String[] ids) {
		try {
			for(String id:ids){
				officeDao.updateDelFlag(id,Office.DEL_FLAG_NORMAL);
			}
		} catch (Exception e) {
			throw new ServiceException("恢复失败！");
		}
	}
	
	@Transactional(readOnly = false)
	public void rebackOffice(String id) {
		try {
			officeDao.updateDelFlag(id,Office.DEL_FLAG_NORMAL);
		} catch (Exception e) {
			throw new ServiceException("恢复失败！");
		}
	}
	
	public List<String> getOfficeByRoleId(List<String> roleIdList){
		List<String> list = new ArrayList<String>();
		for(int i=0;i<roleIdList.size();i++){
			Role role = roleDao.get(roleIdList.get(i));
			List<Office> officeList = role.getOfficeList();
			for(int j=0;j<officeList.size();j++){
				Office office = officeList.get(j);
				list.add(office.getId());
			}
		}
		return removeDuplicate(list);
	}
	
	private List<String> removeDuplicate(List<String> list) {
	    LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
	    set.addAll(list);
	    list.clear();
	    list.addAll(set);
	    return list;
	}
	
	
    public int getMaxOfficeCode() {
    	String qlString = "select max(code) as code from Office";
    	List<String> list = officeDao.find(qlString);
    	if(list != null && list.size() > 0) {
    		String code = (String) list.get(0);
    		int result = Integer.parseInt(code);
    		return result;
    	}
    	return 100000;
    }
	
    /**
     * 手机端注册--邀请码查询公司
     * @param invitationCode
     * @return
     * 2019-03-29--XY
     */
    public List<Office> findByinvitationCode(String invitationCode){
		DetachedCriteria dc = officeDao.createDetachedCriteria();		
		dc.add(Restrictions.eq(Office.FIELD_DEL_FLAG, Office.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("invitationCode", invitationCode));
		return officeDao.find(dc);
	}
    
    /**
     * 手机端注册--邀请码查询公司
     * @param invitationCode
     * @return
     * 2019-03-29--XY
     */
    public List<Office> findBycompanyName(String companyName){
		DetachedCriteria dc = officeDao.createDetachedCriteria();		
		dc.add(Restrictions.eq(Office.FIELD_DEL_FLAG, Office.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("name", companyName));
		return officeDao.find(dc);
	}
    /**
     * -  企业编号是否存在的验证
     * @param code
     * @return
     */
    public List<Office> findByofficeCode(String code){
		DetachedCriteria dc = officeDao.createDetachedCriteria();		
		dc.add(Restrictions.eq("officeCode", code));
		return officeDao.find(dc);
	}
    
    /**
     * - 所有企业
     * @return
     */
    public List<Office> findAllOffices(){
		DetachedCriteria dc = officeDao.createDetachedCriteria();		
		dc.add(Restrictions.eq(Office.FIELD_DEL_FLAG, Office.DEL_FLAG_NORMAL));
		return officeDao.find(dc);
	}

    public Office getOfficeByKuid(String kuid) {
		List<Office> list = officeDao.find(" from Office a where a.kuid=:p1 and a.delFlag='0'",new Parameter(kuid));
		if(list!=null && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
}
