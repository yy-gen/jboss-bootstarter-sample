package egovframework.example.sample.service;

import java.util.List;

/**
 * @Class Name : BoardService.java
 * @Description : BoardService Class
 * @Modification Information
 * @
 * @ 수정일 수정자 수정내용
 * @ --------- --------- -------------------------------
 * @ 2026-03-24 author 최초생성
 *
 * @author author
 * @since 2026-03-24
 * @version 1.0.0
 * @see
 *
 * Copyright (C) All right reserved.
 */
public interface BoardService {

	/**
	 * 글을 등록한다.
	 * @param vo - 등록할 정보가 담긴 BoardVO
	 * @exception Exception
	 */
	void insertBoard(BoardVO vo) throws Exception;

	/**
	 * 글을 수정한다.
	 * @param vo - 수정할 정보가 담긴 BoardVO
	 * @exception Exception
	 */
	void updateBoard(BoardVO vo) throws Exception;

	/**
	 * 글을 삭제한다.
	 * @param vo - 삭제할 정보가 담긴 BoardVO
	 * @exception Exception
	 */
	void deleteBoard(BoardVO vo) throws Exception;

	/**
	 * 글을 조회한다.
	 * @param vo - 조회할 정보가 담긴 BoardVO
	 * @return 조회한 글
	 * @exception Exception
	 */
	BoardVO selectBoard(BoardVO vo) throws Exception;

	/**
	 * 글 목록을 조회한다.
	 * @param vo - 조회할 정보가 담긴 BoardVO
	 * @return 글 목록
	 * @exception Exception
	 */
	List<?> selectBoardList(BoardVO vo) throws Exception;

	/**
	 * 글 총 갯수를 조회한다.
	 * @param vo - 조회할 정보가 담긴 BoardVO
	 * @return 글 총 갯수
	 * @exception
	 */
	int selectBoardListTotCnt(BoardVO vo);

}
