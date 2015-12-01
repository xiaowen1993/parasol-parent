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

package com.ginkgocap.parasol.directory.web.jetty.web.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.rpc.RpcException;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ginkgocap.parasol.directory.exception.DirectoryServiceException;
import com.ginkgocap.parasol.directory.exception.DirectoryTypeServiceException;
import com.ginkgocap.parasol.directory.model.Directory;
import com.ginkgocap.parasol.directory.model.DirectoryType;
import com.ginkgocap.parasol.directory.service.DirectoryService;
import com.ginkgocap.parasol.directory.service.DirectoryTypeService;
import com.ginkgocap.parasol.directory.web.jetty.web.ResponseError;

/**
 * 
 * @author allenshen
 * @date 2015年11月20日
 * @time 下午1:19:18
 * @Copyright Copyright©2015 www.gintong.com
 */
@RestController
public class DirectoryController extends BaseControl {
	private static Logger logger = Logger.getLogger(DirectoryController.class);

	private static final String paramenterFields = "fields";
	private static final String paramenterDebug = "debug";
	private static final String paramenterAppId = "appKey"; // 应用的Key
	private static final String paramenterUserId = "userId"; // 访问的用户参数
	private static final String paramenterRootType = "rootType"; // 查询的应用分类
	private static final String paramenterPid = "pid"; 			// 查询的子目录


	@Autowired
	private DirectoryService directoryService;

	/**
	 * 1. （查询类）查询分类下的根目录
	 * 
	 * @param request
	 * @return
	 * @throws DirectoryServiceException
	 * @throws CodeServiceException
	 */
	@RequestMapping(path = {"/directory/directory/getRootList"}, method = { RequestMethod.GET })
	public MappingJacksonValue getFunctionClassList(@RequestParam(name = DirectoryController.paramenterFields, defaultValue = "") String fileds,
			@RequestParam(name = DirectoryController.paramenterDebug, defaultValue = "") String debug,
			@RequestParam(name = DirectoryController.paramenterAppId, required = true) Long appId,
			@RequestParam(name = DirectoryController.paramenterUserId, required = true) Long userId,
			@RequestParam(name = DirectoryController.paramenterRootType, required = true) Long rootType) throws DirectoryServiceException {
		MappingJacksonValue mappingJacksonValue = null;
		try {
			// 0.校验输入参数（框架搞定，如果业务业务搞定）
			// 1.查询后台服务
			List<Directory> directories = directoryService.getDirectorysForRoot(appId, userId, rootType);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(directories);
			// 3.创建页面显示数据项的过滤器
			SimpleFilterProvider filterProvider = builderSimpleFilterProvider(fileds);
			mappingJacksonValue.setFilters(filterProvider);
			// 4.返回结果
			return mappingJacksonValue;
		} catch (RpcException e) {
			Map<String, Serializable> resultMap = new HashMap<String, Serializable>();
			ResponseError error = processResponseError(e);
			if (error != null) {
				resultMap.put("error", error);
			}
			if (ObjectUtils.equals(debug, "all")) {
				// if (e.getErrorCode() > 0 ) {
				resultMap.put("__debug__", e.getMessage());
				// }
			}
			mappingJacksonValue = new MappingJacksonValue(resultMap);
			e.printStackTrace(System.err);
			return mappingJacksonValue;
		} catch (DirectoryServiceException e) {
			throw e;
		}
	}

	
	
	/**
	 * 1. （查询类)查询一个子目录
	 * 
	 * @param request
	 * @return
	 * @throws DirectoryServiceException
	 * @throws CodeServiceException
	 */
	@RequestMapping(path = {"/directory/directory/getSubList"}, method = { RequestMethod.GET })
	public MappingJacksonValue getSubList(@RequestParam(name = DirectoryController.paramenterFields, defaultValue = "") String fileds,
			@RequestParam(name = DirectoryController.paramenterDebug, defaultValue = "") String debug,
			@RequestParam(name = DirectoryController.paramenterAppId, required = true) Long appId,
			@RequestParam(name = DirectoryController.paramenterUserId, required = true) Long userId,
			@RequestParam(name = DirectoryController.paramenterPid, required = true) Long pid) throws DirectoryServiceException {
		MappingJacksonValue mappingJacksonValue = null;
		try {
			// 0.校验输入参数（框架搞定，如果业务业务搞定）
			// 1.查询后台服务
			List<Directory> directories = directoryService.getDirectorysByParentId(appId, userId, pid);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(directories);
			// 3.创建页面显示数据项的过滤器
			SimpleFilterProvider filterProvider = builderSimpleFilterProvider(fileds);
			mappingJacksonValue.setFilters(filterProvider);
			// 4.返回结果
			return mappingJacksonValue;
		} catch (RpcException e) {
			Map<String, Serializable> resultMap = new HashMap<String, Serializable>();
			ResponseError error = processResponseError(e);
			if (error != null) {
				resultMap.put("error", error);
			}
			if (ObjectUtils.equals(debug, "all")) {
				// if (e.getErrorCode() > 0 ) {
				resultMap.put("__debug__", e.getMessage());
				// }
			}
			mappingJacksonValue = new MappingJacksonValue(resultMap);
			e.printStackTrace(System.err);
			return mappingJacksonValue;
		} catch (DirectoryServiceException e) {
			throw e;
		}
	}
	
	
	@Override
	protected void processBusinessException(ResponseError error, Exception ex) {
		if (ex instanceof DirectoryTypeServiceException) {
			DirectoryTypeServiceException dtex = (DirectoryTypeServiceException) ex;
			error.setType("BizException");
			error.setCode(dtex.getErrorCode());
			error.setMessage(dtex.getMessage());
		}
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
			filter.add("name"); // '分类名称',
			filter.add("typeId"); // '应用的分类分类ID',
			filter.add("appId"); // '应用的分类分类ID',
			filter.add("userId"); // '应用的分类分类ID',
		}

		filterProvider.addFilter(Directory.class.getName(), SimpleBeanPropertyFilter.filterOutAllExcept(filter));
		return filterProvider;
	}
}
