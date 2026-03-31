package egovframework.example.sample.service.impl;

import java.util.List;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import egovframework.example.sample.service.BoardService;
import egovframework.example.sample.service.BoardVO;
import jakarta.annotation.Resource;

/**
 * @Class Name : BoardServiceImpl.java
 * @Description : Board Business Implement Class
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
@Service("boardService")
public class BoardServiceImpl extends EgovAbstractServiceImpl implements BoardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BoardServiceImpl.class);

	/** BoardMapper */
	@Resource(name = "boardMapper")
	private BoardMapper boardMapper;

	/** ID Generation */
	@Resource(name = "egovIdGnrService")
	private EgovIdGnrService egovIdGnrService;

	/**
	 * 글을 등록한다.
	 * @param vo - 등록할 정보가 담긴 BoardVO
	 * @exception Exception
	 */
	@Override
	public void insertBoard(BoardVO vo) throws Exception {
		LOGGER.debug(vo.toString());

		/** ID Generation Service */
		String id = egovIdGnrService.getNextStringId();
		LOGGER.debug(vo.toString());

		boardMapper.insertBoard(vo);
	}

	/**
	 * 글을 수정한다.
	 * @param vo - 수정할 정보가 담긴 BoardVO
	 * @exception Exception
	 */
	@Override
	public void updateBoard(BoardVO vo) throws Exception {
		boardMapper.updateBoard(vo);
	}

	/**
	 * 글을 삭제한다.
	 * @param vo - 삭제할 정보가 담긴 BoardVO
	 * @exception Exception
	 */
	@Override
	public void deleteBoard(BoardVO vo) throws Exception {
		boardMapper.deleteBoard(vo);
	}

	/**
	 * 글을 조회한다.
	 * @param vo - 조회할 정보가 담긴 BoardVO
	 * @return 조회한 글
	 * @exception Exception
	 */
	@Override
	public BoardVO selectBoard(BoardVO vo) throws Exception {
		BoardVO resultVO = boardMapper.selectBoard(vo);
		// if (resultVO == null)
		//  	throw processException("info.nodata.msg");
		return resultVO;
	}

	/**
	 * 글 목록을 조회한다.
	 * @param vo - 조회할 정보가 담긴 VO
	 * @return 글 목록
	 * @exception Exception
	 */
	@Override
	public List<?> selectBoardList(BoardVO vo) throws Exception {
		return boardMapper.selectBoardList(vo);
	}

	/**
	 * 글 총 갯수를 조회한다.
	 * @param vo - 조회할 정보가 담긴 VO
	 * @return 글 총 갯수
	 * @exception
	 */
	@Override
	public int selectBoardListTotCnt(BoardVO vo) {
		return boardMapper.selectBoardListTotCnt(vo);
	}

}
