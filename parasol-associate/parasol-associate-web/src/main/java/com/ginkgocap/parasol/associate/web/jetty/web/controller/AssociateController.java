/*
 * Copyright 2012-2013 the original author or authors.
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

package com.ginkgocap.parasol.associate.web.jetty.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ginkgocap.parasol.associate.exception.AssociateServiceException;
import com.ginkgocap.parasol.associate.model.Associate;
import com.ginkgocap.parasol.associate.model.AssociateType;
import com.ginkgocap.parasol.associate.service.AssociateService;
import com.ginkgocap.parasol.oauth2.web.jetty.LoginUserContextHolder;

/**
 * 
 * @author allenshen
 * @date 2015年11月20日
 * @time 下午1:19:18
 * @Copyright Copyright©2015 www.gintong.com
 */
@RestController
public class AssociateController extends BaseControl {
	private static Logger logger = Logger.getLogger(AssociateController.class);

	private static final String parameterFields = "fields";
	private static final String parameterDebug = "debug";

	private static final String parameterId = "id"; // id 标识

	private static final String parameterSourceTypeId = "sourceTypeId"; // 什么资源类型，比如资源是一个知识类型，参考AssociateType
	private static final String parameterSourceId = "sourceId"; // 资源的ID

	private static final String parameterAssocDesc = "assocDesc"; // 关联时候填写的标签关联关系
	private static final String parameterAssocTypeId = "assocTypeId"; // 关联到什么类型上（比如一篇知识，一个事物、一个需求等）
	private static final String parameterAssocId = "assocId"; // 被关联资源ID
	private static final String parameterAssocTitle = "assocTitle"; // 显示的标题，主要是为了显示
	private static final String parameterAssocMetadata = "assocMetadata"; // 关联的附件消息（比如图片地址，URL连接等，应用根据需求自己定义）

	@Autowired
	private AssociateService associateService;

	/**
	 * 1.创建关联
	 * 
	 * @param request
	 * @return
	 * @throws AssociateerviceException
	 * @throws CodeServiceException
	 */
	// @formatter:off
	@RequestMapping(path = { "/associate/associate/createAssociate" }, method = { RequestMethod.POST })
	public MappingJacksonValue createAssociateRoot(
			@RequestParam(name = AssociateController.parameterFields, defaultValue = "") String fileds,
			@RequestParam(name = AssociateController.parameterDebug, defaultValue = "") String debug,
			@RequestParam(name = AssociateController.parameterSourceTypeId, required = true) Long sourceTypeId,
			@RequestParam(name = AssociateController.parameterSourceId, required = true) Long sourceId,
			@RequestParam(name = AssociateController.parameterAssocDesc, required = true) String assocDesc,
			@RequestParam(name = AssociateController.parameterAssocTypeId, required = true) Long assocTypeId,
			@RequestParam(name = AssociateController.parameterAssocId, required = true) Long assocId,
			@RequestParam(name = AssociateController.parameterAssocTitle, required = true) String assocTitle,
			@RequestParam(name = AssociateController.parameterAssocMetadata, required = false) String assocMetadata) throws AssociateServiceException {
		MappingJacksonValue mappingJacksonValue = null;
	// @formatter:on
		try {

			// 登陆人的信息
			Long loginAppId = LoginUserContextHolder.getAppKey();
			Long loginUserId = LoginUserContextHolder.getUserId();

			// 检查数据的参数
			if (StringUtils.isEmpty(assocDesc)) {
				throw new AssociateServiceException(107, "Required string parameter assocDesc is not empty or blank");
			}
			if (StringUtils.isEmpty(assocTitle)) {
				throw new AssociateServiceException(107, "Required string parameter assocTitle is not empty or blank");
			}

			Associate associate = new Associate();
			associate.setAppId(loginAppId);
			associate.setUserId(loginUserId);
			associate.setSourceTypeId(sourceTypeId);
			associate.setSourceId(sourceTypeId);

			associate.setAssocTypeId(assocTypeId);
			associate.setAssocId(assocId);
			associate.setAssocDesc(assocDesc);
			associate.setAssocTitle(assocTitle);
			associate.setAssocMetadata(assocMetadata);

			Long id = associateService.createAssociate(loginAppId, loginUserId, associate);
			Map<String, Long> reusltMap = new HashMap<String, Long>();
			reusltMap.put("id", id);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(reusltMap);
			return mappingJacksonValue;
		} catch (AssociateServiceException e) {
			throw e;
		}
	}

	/**
	 * 2.删除一个关联
	 * 
	 * @param request
	 * @return
	 * @throws AssociateerviceException
	 * @throws CodeServiceException
	 */
	// @formatter:off
	@RequestMapping(path = { "/associate/associate/deleteAssociate" }, method = { RequestMethod.POST })
	public MappingJacksonValue deleteAssociate(
			@RequestParam(name = AssociateController.parameterDebug, defaultValue = "") String debug, 
			@RequestParam(name = AssociateController.parameterId, required = true) Long id ) throws AssociateServiceException {
	// @formatter:on
		MappingJacksonValue mappingJacksonValue = null;
		try {

			Long loginAppId = LoginUserContextHolder.getAppKey();
			Long loginUserId = LoginUserContextHolder.getUserId();

			// 0.校验输入参数（框架搞定，如果业务业务搞定）
			boolean bResult = associateService.removeAssociate(loginAppId, loginUserId, id);
			Map<String, Boolean> reusltMap = new HashMap<String, Boolean>();
			reusltMap.put("success", bResult);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(reusltMap);
			return mappingJacksonValue;
		} catch (AssociateServiceException e) {
			throw e;
		}
	}

	/**
	 * 3.查询一个关联对象的详情, 通过关联Id
	 * 
	 * @param request
	 * @return
	 * @throws AssociateerviceException
	 * @throws CodeServiceException
	 */
	// @formatter:off
	@RequestMapping(path = { "/associate/associate/getAssociateDetail" }, method = { RequestMethod.POST })
	public MappingJacksonValue getAssociateDetail(
			@RequestParam(name = AssociateController.parameterFields, defaultValue = "") String fileds,
			@RequestParam(name = AssociateController.parameterDebug, defaultValue = "") String debug,
			@RequestParam(name = AssociateController.parameterId, required = true) Long id ) throws AssociateServiceException {
	// @formatter:on
		MappingJacksonValue mappingJacksonValue = null;
		try {

			Long loginAppId = LoginUserContextHolder.getAppKey();
			Long loginUserId = LoginUserContextHolder.getUserId();
			Associate associate = associateService.getAssociate(loginAppId, loginUserId, id);
			Map<String, Object> reusltMap = new HashMap<String, Object>();
			reusltMap.put("data", associate);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(reusltMap);
			mappingJacksonValue.setFilters(builderSimpleFilterProvider(fileds));
			return mappingJacksonValue;
		} catch (AssociateServiceException e) {
			throw e;
		}
	}

	/**
	 * 3.查询一个资源有多少关联资源
	 * 
	 * @param request
	 * @return
	 * @throws AssociateerviceException
	 * @throws CodeServiceException
	 */
	// @formatter:off
	@RequestMapping(path = { "/associate/associate/getSourceAssociates" }, method = { RequestMethod.POST })
	public MappingJacksonValue getSourceAssociates(
			@RequestParam(name = AssociateController.parameterFields, defaultValue = "") String fileds,
			@RequestParam(name = AssociateController.parameterDebug, defaultValue = "") String debug,
			@RequestParam(name = AssociateController.parameterSourceTypeId, required = true) Long sourceTypeId,
			@RequestParam(name = AssociateController.parameterSourceId, required = true) long sourceId) throws AssociateServiceException {
	// @formatter:on
		MappingJacksonValue mappingJacksonValue = null;
		
		Long loginAppId = LoginUserContextHolder.getAppKey();
		Long loginUserId = LoginUserContextHolder.getUserId();
		Map<AssociateType, List<Associate>> associateMap = associateService.getAssociatesBy(loginAppId, sourceTypeId, sourceId);

		if (MapUtils.isNotEmpty(associateMap)) {
			
		}

		Map<String, Object> reusltMap = new HashMap<String, Object>();
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		for (AssociateType associateType : associateMap.keySet()) {
			Map<String,Object> recordMap = new HashMap<String,Object>();
			recordMap.put("id", associateType.getId());
			recordMap.put("name", associateType.getName());
			List<Map> associateList = new ArrayList<Map>();
			recordMap.put("data", associateList);
			Map<String, List<Object>> descMap = new HashMap<String,List<Object>>();
			List<Associate> associates =  associateMap.get(associateType);
			for (Associate associate : associates) {
				if (associate != null) {
					String associate_desc = associate.getAssocDesc();
					List<Object> desc_collect = descMap.get(associate_desc);
					if (desc_collect == null) {
						desc_collect = new ArrayList<Object>();
						descMap.put(associate_desc, desc_collect);
					}
					desc_collect.add(associate);
				}
			}
			
			for (String desc : descMap.keySet()) {
				Map<String, Object> desc_List_map = new HashMap<String,Object>();
				desc_List_map.put("associateCollect", desc);
				desc_List_map.put("associateList", descMap.get(desc));
				associateList.add(desc_List_map);
			}
			//--分类下
			resultList.add(recordMap);
		}
		
		reusltMap.put("data", resultList);
		mappingJacksonValue = new MappingJacksonValue(reusltMap);
		mappingJacksonValue.setFilters(builderSimpleFilterProvider(fileds));
		return mappingJacksonValue;
	}

	/**
	 * 指定显示那些字段
	 * 
	 * @param fileds
	 * @return
	 */
	private SimpleFilterProvider builderSimpleFilterProvider(String fileds) {
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		// 请求指定字段
		String[] filedNames = StringUtils.split(fileds, ",");
		Set<String> filter = new HashSet<String>();
		if (filedNames != null && filedNames.length > 0) {
			for (int i = 0; i < filedNames.length; i++) {
				String filedName = filedNames[i];
				if (!StringUtils.isEmpty(filedName)) {
					filter.add(filedName);
				}
			}
		} else {
			filter.add("id"); // id',
			filter.add("assocDesc"); // '关联描述，比如文章的作者，或者编辑等；关联标签描述',
			filter.add("assocTypeId"); // '被关联的类型可以参考AssociateType对象，如：知识, 人脉,组织，需求，事件等',
			filter.add("assocId"); // '被关联数据ID',
			filter.add("assocTitle"); // '被关联数据标题',
			filter.add("assocMetadata"); // '被关联数据的的摘要用Json存放，如图片，连接URL定义等',

		}

		filterProvider.addFilter(Associate.class.getName(), SimpleBeanPropertyFilter.filterOutAllExcept(filter));
		return filterProvider;
	}
}