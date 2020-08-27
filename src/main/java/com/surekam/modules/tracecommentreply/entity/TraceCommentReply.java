package com.surekam.modules.tracecommentreply.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;

import java.lang.String;

/**
 * 评论回复管理Entity
 * @author xy
 * @version 2019-01-29
 */
@Entity
@Table(name = "t_trace_comment_reply")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceCommentReply extends XGXTEntity<TraceCommentReply> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String commentId;//评论id
	private String replyContent;//回复内容


	public TraceCommentReply() {
		super();
	}
	
	@PrePersist
	public void prePersist(){
		this.id = IdGen.uuid();
	}
		
	@Id
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCommentId() {
		return this.commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	
	public String getReplyContent() {
		return this.replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	
}


