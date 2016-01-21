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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ginkgocap.parasol.directory.exception.DirectoryServiceException;
import com.ginkgocap.parasol.directory.model.Directory;
import com.ginkgocap.parasol.directory.service.DirectoryService;
import com.ginkgocap.parasol.oauth2.model.SecurityUserDetails;
import com.ginkgocap.parasol.oauth2.web.jetty.LoginUserContextHolder;
import com.ginkgocap.parasol.user.model.UserLoginRegister;

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
	private static final String paramenterPid = "pid"; // 查询的子目录
	private static final String paramenterName = "name"; // 目录名称
	private static final String paramenterDirectoryId = "directoryId"; // 目录ID
	private static final String paramenterToDirectoryId = "toDirectoryId"; // 移动目录的生活，移动那个目录下

	@Autowired
	private DirectoryService directoryService;

	/**
	 * 2.创建分类下的根目录
	 * 
	 * @param request
	 * @return
	 * @throws DirectoryServiceException
	 * @throws CodeServiceException
	 */
	@RequestMapping(path = { "/directory/directory/createDirectoryRoot" }, method = { RequestMethod.GET,RequestMethod.POST })
	public MappingJacksonValue createDirectoryRoot(@RequestParam(name = DirectoryController.paramenterFields, defaultValue = "") String fileds,
			@RequestParam(name = DirectoryController.paramenterDebug, defaultValue = "") String debug,
			@RequestParam(name = DirectoryController.paramenterName, required = true) String name,
			@RequestParam(name = DirectoryController.paramenterRootType, required = true) long rootType) throws DirectoryServiceException {
		MappingJacksonValue mappingJacksonValue = null;
		try {
			
			Long loginAppId = LoginUserContextHolder.getAppKey();
			Long loginUserId = LoginUserContextHolder.getUserId();
			
			// 0.校验输入参数（框架搞定，如果业务业务搞定）
			Directory directory = new Directory();
			directory.setAppId(loginAppId);
			directory.setUserId(loginUserId);
			directory.setName(name);
			directory.setTypeId(rootType);

			Long id = directoryService.createDirectoryForRoot(rootType, directory);
			Map<String, Long> reusltMap = new HashMap<String, Long>();
			reusltMap.put("id", id);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(reusltMap);
			return mappingJacksonValue;
		}  catch (DirectoryServiceException e) {
			throw e;
		}
	}

	/**
	 * 删除目录
	 * 
	 * @param debug
	 * @param appId
	 * @param userId
	 * @param directoryId
	 * @return
	 * @throws DirectoryServiceException
	 */
	@RequestMapping(path = { "/directory/directory/deleteDirectory" }, method = { RequestMethod.GET, RequestMethod.DELETE })
	public MappingJacksonValue deleteDirectoryRoot(@RequestParam(name = DirectoryController.paramenterDebug, defaultValue = "") String debug,
			@RequestParam(name = DirectoryController.paramenterDirectoryId, required = true) Long directoryId) throws DirectoryServiceException {
		MappingJacksonValue mappingJacksonValue = null;
		try {
			Long loginAppId = LoginUserContextHolder.getAppKey();
			Long loginUserId = LoginUserContextHolder.getUserId();
			// 0.校验输入参数（框架搞定，如果业务业务搞定）
			Boolean b = directoryService.removeDirectory(loginAppId, loginUserId, directoryId);
			Map<String, Boolean> reusltMap = new HashMap<String, Boolean>();
			reusltMap.put("success", b);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(reusltMap);
			return mappingJacksonValue;
		}  catch (DirectoryServiceException e) {
			throw e;
		}
	}

	/**
	 * 更新目录
	 * 
	 * @param debug
	 * @param appId
	 * @param userId
	 * @param directoryId
	 * @return
	 * @throws DirectoryServiceException
	 */
	@RequestMapping(path = { "/directory/directory/updateDirectory" }, method = { RequestMethod.POST })
	public MappingJacksonValue updateDirectoryRoot(@RequestParam(name = DirectoryController.paramenterFields, defaultValue = "") String fileds,
			@RequestParam(name = DirectoryController.paramenterDebug, defaultValue = "") String debug,
			@RequestParam(name = DirectoryController.paramenterDirectoryId, required = true) Long directoryId,
			@RequestParam(name = DirectoryController.paramenterName, required = true) String name) throws DirectoryServiceException {
		MappingJacksonValue mappingJacksonValue = null;
		try {
			Long loginAppId = LoginUserContextHolder.getAppKey();
			Long loginUserId = LoginUserContextHolder.getUserId();
			
			Directory directory = new Directory();
			directory.setAppId(loginAppId);
			directory.setName(name);
			directory.setId(directoryId);

			Boolean b = directoryService.updateDirectory(loginAppId, loginUserId, directory);
			Map<String, Boolean> reusltMap = new HashMap<String, Boolean>();
			reusltMap.put("success", b);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(reusltMap);
			return mappingJacksonValue;
		} catch (DirectoryServiceException e) {
			throw e;
		}
	}

	/**
	 * 移动目录
	 * @param debug
	 * @param appId
	 * @param userId
	 * @param directoryId
	 * @return
	 * @throws DirectoryServiceException
	 */
	@RequestMapping(path = { "/directory/directory/moveDirectory" }, method = { RequestMethod.POST })
	public MappingJacksonValue moveDirectory(@RequestParam(name = DirectoryController.paramenterFields, defaultValue = "") String fileds,
			@RequestParam(name = DirectoryController.paramenterDebug, defaultValue = "") String debug,
			@RequestParam(name = DirectoryController.paramenterDirectoryId, required = true) Long directoryId,
			@RequestParam(name = DirectoryController.paramenterToDirectoryId, required = true) Long toDirectoryId) throws DirectoryServiceException {
		MappingJacksonValue mappingJacksonValue = null;
		try {
			Long loginAppId = LoginUserContextHolder.getAppKey();
			Long loginUserId = LoginUserContextHolder.getUserId();
			Boolean b = directoryService.moveDirectoryToDirectory(loginAppId, loginUserId, directoryId,toDirectoryId);
			Map<String, Boolean> reusltMap = new HashMap<String, Boolean>();
			reusltMap.put("success", b);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(reusltMap);
			return mappingJacksonValue;
		}  catch (DirectoryServiceException e) {
			throw e;
		}
	}

	/**
	 * 查询根目录
	 * 
	 * @param request
	 * @return
	 * @throws DirectoryServiceException
	 * @throws CodeServiceException
	 */
	@RequestMapping(path = { "/directory/directory/getRootList" }, method = { RequestMethod.GET })
	public MappingJacksonValue getRootList(@RequestParam(name = DirectoryController.paramenterFields, defaultValue = "") String fileds,
			@RequestParam(name = DirectoryController.paramenterDebug, defaultValue = "") String debug,
			@RequestParam(name = DirectoryController.paramenterRootType, required = true) Long rootType) throws DirectoryServiceException {
		MappingJacksonValue mappingJacksonValue = null;
		try {
			Long loginAppId = LoginUserContextHolder.getAppKey();
			Long loginUserId = LoginUserContextHolder.getUserId();
			
			// 0.校验输入参数（框架搞定，如果业务业务搞定）
			// 1.查询后台服务
			List<Directory> directories = directoryService.getDirectorysForRoot(loginAppId, loginUserId, rootType);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(directories);
			// 3.创建页面显示数据项的过滤器
			SimpleFilterProvider filterProvider = builderSimpleFilterProvider(fileds);
			mappingJacksonValue.setFilters(filterProvider);
			// 4.返回结果
			return mappingJacksonValue;
		} catch (DirectoryServiceException e) {
			throw e;
		}
	}

	/**
	 * 查询子目录
	 * 
	 * @param request
	 * @return
	 * @throws DirectoryServiceException
	 * @throws CodeServiceException
	 */
	@RequestMapping(path = { "/directory/directory/getSubList" }, method = { RequestMethod.GET })
	public MappingJacksonValue getSubList(@RequestParam(name = DirectoryController.paramenterFields, defaultValue = "") String fileds,
			@RequestParam(name = DirectoryController.paramenterDebug, defaultValue = "") String debug,
			@RequestParam(name = DirectoryController.paramenterPid, required = true) Long pid) throws DirectoryServiceException {
		MappingJacksonValue mappingJacksonValue = null;
		try {
			Long loginAppId = LoginUserContextHolder.getAppKey();
			Long loginUserId = LoginUserContextHolder.getUserId();
			// 0.校验输入参数（框架搞定，如果业务业务搞定）
			// 1.查询后台服务
			List<Directory> directories = directoryService.getDirectorysByParentId(loginAppId, loginUserId, pid);
			// 2.转成框架数据
			mappingJacksonValue = new MappingJacksonValue(directories);
			// 3.创建页面显示数据项的过滤器
			SimpleFilterProvider filterProvider = builderSimpleFilterProvider(fileds);
			mappingJacksonValue.setFilters(filterProvider);
			// 4.返回结果
			return mappingJacksonValue;
		} catch (DirectoryServiceException e) {
			throw e;
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
