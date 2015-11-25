package com.ginkgocap.parasol.user.service;

import java.util.List;

import com.ginkgocap.parasol.user.exception.UserInterestIndustryServiceException;
import com.ginkgocap.parasol.user.exception.UserLoginRegisterServiceException;
import com.ginkgocap.parasol.user.model.UserInterestIndustry;

/**
 * 操作用户感兴趣接口定义
 * 
 */
public interface UserInterestIndustryService {

	/**
	 * 添加感兴趣行业
	 * @param userInterestIndustry 
	 * @return
	 * @throws UserLoginRegisterServiceException
	 */
	public Long createUserInterestIndustry(UserInterestIndustry userInterestIndustry) throws UserInterestIndustryServiceException; 
	
	
	/**
	 * 修改感兴趣行业
	 * @param id
	 * @param firstInterestId 一级行业
	 * @param secondInterestId 二级行业
	 * @param thireInterestId 三级行业
	 * @param ip
	 * @return
	 * @throws UserInterestIndustryServiceException
	 */
	public boolean updateUserInterestIndustry(Long id,Long firstIndustryId,Long secondIndustryId,Long thirdIndustryId,String ip) throws UserInterestIndustryServiceException;
	
	/**
	 * 根据userId获取id
	 * @param userId
	 * @return
	 * @throws UserInterestIndustryServiceException
	 */
	public Long getId(Long userId) throws UserInterestIndustryServiceException;
	
	/**
	 * 根据行业id获取userId分页列表
	 * @param start 
	 * @param count 
	 * @param firstInterestId 一级行业
	 * @param secondInterestId 二级行业
	 * @param thireInterestId 三级行业
	 * @param level 其值为1,2,3;分别表示按1,2,3级行业id查 此参数必填
	 * @return
	 * @throws UserInterestIndustryServiceException
	 */
	public List<Long> getUserIdList(int start,int count,Long firstInterestId,Long secondInterestId,Long thireInterestId,int level) throws UserInterestIndustryServiceException;
	
	/**
	 * 用户Id是否存在
	 * @param userId 
	 * @return
	 * @throws UserInterestIndustryServiceException
	 */
	public boolean userIdExists(Long userId) throws UserInterestIndustryServiceException;
	
}