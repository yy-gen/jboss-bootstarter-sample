package egovframework.example.sample.service;

import org.egovframe.rte.ptl.reactive.validation.EgovNullCheck;

/**
 * @Class Name : BoardVO.java
 * @Description : BoardVO Class
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2026-03-24   author   최초생성
 *
 * @author author
 * @since 2026-03-24
 * @version 1.0.0
 * @see
 *
 *  Copyright (C) All right reserved.
 */
public class BoardVO extends BoardDefaultVO {

	private static final long serialVersionUID = 1L;

	/** id */
	private java.lang.Integer id;

	/** title */
	// @EgovNullCheck(message="title is required") // egovframe-Todo: Null Check 적용시 좌측 주석 코드 참고
	private java.lang.String title;

	/** content */
	// @EgovNullCheck(message="content is required") // egovframe-Todo: Null Check 적용시 좌측 주석 코드 참고
	private java.lang.String content;

	/** author */
	// @EgovNullCheck(message="author is required") // egovframe-Todo: Null Check 적용시 좌측 주석 코드 참고
	private java.lang.String author;

	/** view_count */
	// @EgovNullCheck(message="viewCount is required") // egovframe-Todo: Null Check 적용시 좌측 주석 코드 참고
	private java.lang.Integer viewCount;

	/** created_at */
	// @EgovNullCheck(message="createdAt is required") // egovframe-Todo: Null Check 적용시 좌측 주석 코드 참고
	private java.sql.Timestamp createdAt;

	/** updated_at */
	// @EgovNullCheck(message="updatedAt is required") // egovframe-Todo: Null Check 적용시 좌측 주석 코드 참고
	private java.sql.Timestamp updatedAt;

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.String getTitle() {
		return title;
	}

	public void setTitle(java.lang.String title) {
		this.title = title;
	}

	public java.lang.String getContent() {
		return content;
	}

	public void setContent(java.lang.String content) {
		this.content = content;
	}

	public java.lang.String getAuthor() {
		return author;
	}

	public void setAuthor(java.lang.String author) {
		this.author = author;
	}

	public java.lang.Integer getViewCount() {
		return viewCount;
	}

	public void setViewCount(java.lang.Integer viewCount) {
		this.viewCount = viewCount;
	}

	public java.sql.Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.sql.Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public java.sql.Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(java.sql.Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}
