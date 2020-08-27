package com.surekam.modules.tracecomment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;

import java.lang.String;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 溯源产品评论Entity
 * 
 * @author huangrd
 * @version 2019-01-08
 */
@Entity
@Table(name = "t_trace_comment")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceComment extends XGXTEntity<TraceComment> {

	private static final long serialVersionUID = 1L;
	private String id;//
	private String productId;// 产品ID
	private String officeId;// 企业ID
	private String batchCode;// 批次号
	private String contentId;// 评论内容的编号
	private String productName;// 产品名称
	private String score;// 评分（1差评 2差评 3一般 4好评 5好评）
	private String content;// 评论内容
	private String nickname;// 用户昵称
	private String headImg;// 用户头像
	private String commentPic;// 产品图片
	private String auditUserid;// 审核人
	private String auditDate;// 审核时间
	private String auditFlag;// 审核状态（1待审核 2审核通过 2审核未通过）
	private String openId;// 用户openid

	private List<String> commentPicUrlList = new ArrayList<String>();// 产品图片绝对路径

	private String replyContent;
	private Date replyCreatTime;
	// 公司名称
	private String corpName;

	public TraceComment() {
		super();
	}

	@Transient
	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	@Transient
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES)
	@DateBridge(resolution = Resolution.DAY)
	public Date getReplyCreatTime() {
		return replyCreatTime;
	}

	public void setReplyCreatTime(Date replyCreatTime) {
		this.replyCreatTime = replyCreatTime;
	}

	@PrePersist
	public void prePersist() {
		this.id = IdGen.uuid();
	}

	@Id
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "PRODUCT_ID")
	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name = "OFFICE_ID")
	public String getOfficeId() {
		return this.officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getBatchCode() {
		return this.batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	@Column(name = "CONTENT_ID")
	public String getContentId() {
		return this.contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getScore() {
		return this.score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getCommentPic() {
		return commentPic;
	}

	public void setCommentPic(String commentPic) {
		this.commentPic = commentPic;
	}

	@Transient
	public List<String> getCommentPicUrlList() {
		return commentPicUrlList;
	}

	public void setCommentPicUrlList(List<String> commentPicUrlList) {
		this.commentPicUrlList = commentPicUrlList;
	}

	public String getAuditUserid() {
		return auditUserid;
	}

	public void setAuditUserid(String auditUserid) {
		this.auditUserid = auditUserid;
	}

	public String getAuditDate() {
		return this.auditDate;
	}

	public void setAuditDate(String auditDate) {
		this.auditDate = auditDate;
	}

	public String getAuditFlag() {
		return this.auditFlag;
	}

	public void setAuditFlag(String auditFlag) {
		this.auditFlag = auditFlag;
	}

	@Transient
	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

}
