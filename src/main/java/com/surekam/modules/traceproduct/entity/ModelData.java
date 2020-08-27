package com.surekam.modules.traceproduct.entity;
import java.util.ArrayList;
import java.util.List;

import com.surekam.common.persistence.Page;
import com.surekam.modules.tracemodel.entity.TraceModelDataGroup;


public class ModelData {

    private int total;
    private String modelName;
    private String modelShowType;
    private List<Rows> rows;
    private List<TraceModelDataGroup> groupList = new ArrayList<TraceModelDataGroup>();
    public void setTotal(int total) {
         this.total = total;
     }
     public int getTotal() {
         return total;
     }

    public void setModelName(String modelName) {
         this.modelName = modelName;
     }
     public String getModelName() {
         return modelName;
     }
     
    /**
	 * @return the modelShowType
	 */
	public String getModelShowType() {
		return modelShowType;
	}
	/**
	 * @param modelShowType the modelShowType to set
	 */
	public void setModelShowType(String modelShowType) {
		this.modelShowType = modelShowType;
	}
	public void setRows(List<Rows> rows) {
         this.rows = rows;
     }
     public List<Rows> getRows() {
         return rows;
     }
	public List<TraceModelDataGroup> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<TraceModelDataGroup> groupList) {
		this.groupList = groupList;
	}
     
     

}