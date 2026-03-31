/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.example.sample.web;

import java.util.ArrayList;
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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import egovframework.example.sample.service.EgovSampleService;
import egovframework.example.sample.service.SampleVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * @Class Name : EgovSampleController.java
 * @Description : EgovSample Controller Class
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2009.03.16           최초생성
 *
 * @author 개발프레임웍크 실행환경 개발팀
 * @since 2009. 03.16
 * @version 1.0
 * @see
 *
 *  Copyright (C) by MOPAS All right reserved.
 */

@Controller
@Slf4j
public class EgovSampleController {

	/**
	 * EgovSampleService
	 */
	@Resource(name = "sampleService")
	private EgovSampleService sampleService;

	/**
	 * EgovPropertyService
	 */
	@Resource(name = "propertiesService")
	private EgovPropertyService propertiesService;

	@GetMapping("/")
	public String index(@ModelAttribute("sampleVO") SampleVO sampleVO, ModelMap model) throws Exception {
		return this.selectSampleList(sampleVO, model);
	}

	/**
	 * 글 목록을 조회한다. (pageing)
	 *
	 * @param sampleVO - 조회할 정보가 담긴 SampleDefaultVO
	 * @param model
	 * @return "egovSampleList"
	 * @throws Exception
	 */
	@GetMapping("/egovSampleList.do")
	public String selectSampleList(@ModelAttribute("sampleVO") SampleVO sampleVO, ModelMap model) throws Exception {
		sampleVO.setPageUnit(propertiesService.getInt("pageUnit"));
		sampleVO.setPageSize(propertiesService.getInt("pageSize"));

		PaginationInfo paginationInfo = initPaginationInfo(sampleVO);

		List<?> sampleList = sampleService.selectSampleList(sampleVO);
		int totCnt = sampleService.selectSampleListTotCnt(sampleVO);
		paginationInfo.setTotalRecordCount(totCnt);

		model.addAttribute("resultList", sampleList);
		model.addAttribute("paginationInfo", paginationInfo);

		return "sample/egovSampleList";
	}

	/**
	 * 글 등록 화면을 조회한다.
	 *
	 * @param sampleVO - 목록 조회조건 정보가 담긴 VO
	 * @param model
	 * @return "egovSampleRegister"
	 * @throws Exception
	 */
	@PostMapping("/addSampleView.do")
	public String addSampleView(@ModelAttribute("sampleVO") SampleVO sampleVO, Model model) throws Exception {

		model.addAttribute("sampleVO", sampleVO);

		return "sample/egovSampleRegister";
	}

	/**
	 * 글을 등록한다.
	 *
	 * @param sampleVO - 등록할 정보가 담긴 VO
	 * @param status
	 * @return "forward:/egovSampleList.do"
	 * @throws Exception
	 */
	@PostMapping("/addSample.do")
	public String addSample(@Valid @ModelAttribute("sampleVO") SampleVO sampleVO, BindingResult bindingResult, Model model, SessionStatus status) throws Exception {

		if (bindingResult.hasErrors()) {
			model.addAttribute("sampleVO", sampleVO);
			return "sample/egovSampleRegister";
		}

		sampleService.insertSample(sampleVO);
		status.setComplete();

		return "redirect:/egovSampleList.do";
	}

	/**
	 * 글 수정화면을 조회한다.
	 *
	 * @param id    - 수정할 글 id
	 * @param model
	 * @return "egovSampleRegister"
	 * @throws Exception
	 */
	@PostMapping("/updateSampleView.do")
	public String updateSampleView(@ModelAttribute("sampleVO") SampleVO sampleVO, Model model) throws Exception {
		SampleVO detail = sampleService.selectSample(sampleVO);
		setSearchConditions(detail, sampleVO);
		model.addAttribute("sampleVO", detail);

		return "sample/egovSampleRegister";
	}

	/**
	 * 글을 수정한다.
	 *
	 * @param sampleVO - 수정할 정보가 담긴 VO
	 * @param status
	 * @return "forward:/egovSampleList.do"
	 * @throws Exception
	 */
	@PostMapping("/updateSample.do")
	public String updateSample(@Valid @ModelAttribute("sampleVO") SampleVO sampleVO, BindingResult bindingResult,
							   Model model, RedirectAttributes redirectAttributes, SessionStatus status) throws Exception {
		if (bindingResult.hasErrors()) {
			model.addAttribute("sampleVO", sampleVO);
			return "sample/egovSampleRegister";
		}

		sampleService.updateSample(sampleVO);
		status.setComplete();
		addRedirectAttributes(redirectAttributes, sampleVO);

		return "redirect:/egovSampleList.do";
	}

	/**
	 * 글을 삭제한다.
	 *
	 * @param sampleVO - 삭제할 정보가 담긴 VO
	 * @param status
	 * @return "forward:/egovSampleList.do"
	 * @throws Exception
	 */
	@PostMapping("/deleteSample.do")
	public String deleteSample(@ModelAttribute("sampleVO") SampleVO sampleVO, RedirectAttributes redirectAttributes, SessionStatus status) throws Exception {
		sampleService.deleteSample(sampleVO);
		status.setComplete();
		addRedirectAttributes(redirectAttributes, sampleVO);

		return "redirect:/egovSampleList.do";
	}

	/**
	 * 페이징 정보를 초기화 및 설정한다.
	 */
	private PaginationInfo initPaginationInfo(SampleVO sampleVO) {
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(sampleVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(sampleVO.getPageUnit());
		paginationInfo.setPageSize(sampleVO.getPageSize());

		sampleVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		sampleVO.setLastIndex(paginationInfo.getLastRecordIndex());
		sampleVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		return paginationInfo;
	}

	/**
	 * 검색 조건을 설정한다.
	 */
	private void setSearchConditions(SampleVO target, SampleVO source) {
		target.setSearchCondition(source.getSearchCondition());
		target.setSearchKeyword(source.getSearchKeyword());
		target.setPageIndex(source.getPageIndex());
	}

	/**
	 * 리다이렉트 속성에 검색 조건을 추가한다.
	 */
	private void addRedirectAttributes(RedirectAttributes redirectAttributes, SampleVO sampleVO) {
		redirectAttributes.addAttribute("searchCondition", sampleVO.getSearchCondition());
		redirectAttributes.addAttribute("searchKeyword", sampleVO.getSearchKeyword());
		redirectAttributes.addAttribute("pageIndex", sampleVO.getPageIndex());
	}
}
