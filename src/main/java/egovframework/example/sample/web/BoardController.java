package egovframework.example.sample.web;

import java.util.List;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import egovframework.example.sample.service.BoardService;
import egovframework.example.sample.service.BoardVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * @Class Name : BoardController.java
 * @Description : Board Controller Class
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
@Controller
public class BoardController {

	/** BoardService */
	@Resource(name = "boardService")
	private BoardService boardService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	/**
	 * Board 목록을 조회한다. (pageing)
	 * @param boardVO - 조회할 정보가 담긴 BoardDefaultVO
	 * @param model
	 * @return "boardList"
	 * @exception Exception
	 */
	@GetMapping("/board/boardList.do")
	public String selectBoardList(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {

		/** EgovPropertyService.sample */
		boardVO.setPageUnit(propertiesService.getInt("pageUnit"));
		boardVO.setPageSize(propertiesService.getInt("pageSize"));

		/** pageing setting */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(boardVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(boardVO.getPageUnit());
		paginationInfo.setPageSize(boardVO.getPageSize());

		boardVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		boardVO.setLastIndex(paginationInfo.getLastRecordIndex());
		boardVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		/** List */
		List<?> boardList = boardService.selectBoardList(boardVO);
		model.addAttribute("resultList", boardList);

		/** Count */
		int totCnt = boardService.selectBoardListTotCnt(boardVO);
		paginationInfo.setTotalRecordCount(totCnt);

		/** Pagination */
		model.addAttribute("paginationInfo", paginationInfo);

		return "board/boardList";
	}

	/**
	 * 글 등록 화면을 조회한다.
	 * @param boardVO - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "boardRegister"
	 * @exception Exception
	 */
	@PostMapping("/board/addBoardView.do")
	public String addBoardView(@ModelAttribute("boardVO") BoardVO boardVO, Model model) throws Exception {

		model.addAttribute("boardVO", boardVO);

		return "board/boardRegister";
	}

	/**
	 * 글을 등록한다.
	 * @param boardVO - 등록할 정보가 담긴 VO
	 * @param bindingResult - @ModelAttrubute에 대한 바인딩 검증 결과
	 * @param model
	 * @param status
	 * @return "redirect:/board/boardList.do"
	 * @exception Exception
	 */
	@PostMapping("/board/addBoard.do")
	public String addBoard(@Valid @ModelAttribute("boardVO") BoardVO boardVO, BindingResult bindingResult, Model model, SessionStatus status) throws Exception {

		// Server-Side Validation
		if (bindingResult.hasErrors()) {
			model.addAttribute("boardVO", boardVO);
			return "board/boardRegister";
		}

		boardService.insertBoard(boardVO);
		status.setComplete();

		return "redirect:/board/boardList.do";
	}

	/**
	 * 글 수정화면을 조회한다.
	 * @param id - 수정할 글의 id
	 * @param model
	 * @return "boardRegister"
	 * @exception Exception
	 */
	@PostMapping("/board/updateBoardView.do")
	public String updateBoardView(@ModelAttribute("boardVO") BoardVO boardVO ,Model model) throws Exception {

		BoardVO detail = boardService.selectBoard(boardVO);
		detail.setSearchCondition(boardVO.getSearchCondition());
		detail.setSearchKeyword(boardVO.getSearchKeyword());
		detail.setPageIndex(boardVO.getPageIndex());

		model.addAttribute("boardVO", detail);
		
		return "board/boardRegister";
	}

	/**
	 * 글을 수정한다.
	 * @param boardVO - 수정할 정보가 담긴 VO
	 * @param bindingResult - @ModelAttrubute에 대한 바인딩 검증 결과
	 * @param model
	 * @param status
	 * @return "redirect:/board/boardList.do"
	 * @exception Exception
	 */
	@PostMapping("/board/updateBoard.do")
	public String updateBoard(@Valid @ModelAttribute("boardVO") BoardVO boardVO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, SessionStatus status) throws Exception {

		// Server-Side Validation
		if (bindingResult.hasErrors()) {
			model.addAttribute("boardVO", boardVO);
			return "board/boardRegister";
		}

		boardService.updateBoard(boardVO);
		status.setComplete();

		redirectAttributes.addAttribute("searchCondition", boardVO.getSearchCondition());
		redirectAttributes.addAttribute("searchKeyword", boardVO.getSearchKeyword());
		redirectAttributes.addAttribute("pageIndex", boardVO.getPageIndex());

		return "redirect:/board/boardList.do";
	}

	/**
	 * 글을 삭제한다.
	 * @param boardVO - 삭제할 정보가 담긴 VO
	 * @param model
	 * @param status
	 * @return "redirect:/board/boardList.do"
	 * @exception Exception
	 */
	@PostMapping("/board/deleteBoard.do")
	public String deleteBoard(@ModelAttribute("boardVO") BoardVO boardVO, RedirectAttributes redirectAttributes, SessionStatus status) throws Exception {
		boardService.deleteBoard(boardVO);
		status.setComplete();

		redirectAttributes.addAttribute("searchCondition", boardVO.getSearchCondition());
		redirectAttributes.addAttribute("searchKeyword", boardVO.getSearchKeyword());
		redirectAttributes.addAttribute("pageIndex", boardVO.getPageIndex());

		return "redirect:/board/boardList.do";
	}

}
