package com.ginkgocap.parasol.user.test;

import javax.annotation.Resource;

import junit.framework.Test;
import junit.framework.TestResult;

import com.ginkgocap.parasol.user.service.UserBasicService;

public class UserBasicServiceTest  extends TestBase implements Test  {
	@Resource
	private UserBasicService userBasicService;
	@Override
	public int countTestCases() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run(TestResult arg0) {
		// TODO Auto-generated method stub
		
	}
	@org.junit.Test
	public void getUserBasicListByUserNameTest() throws Exception{
		userBasicService.getUserBasicListByUserName(0, 2, "li");
	}
//	@Resource
//	private UserInterestIndustryService userInterestIndustryService;
//	@Resource
//	private UserLoginRegisterService userLoginRegisterService;
//	@Resource
//	private UserBasicService userBasicService;
//	
//	public int countTestCases() { 
//		return 0;  
//	}
//
//	public void run(TestResult result) {
//		
//	}
//	/**
//	 * 创建用户基本信息
//	 */
//	@org.junit.Test
//	public void testCreateUserBasic(){
//		try {
//			Long id=userLoginRegisterService.getId("13677687629");
//			Long id2 =userBasicService.createUserBasic(setUserBasic(id));
//			Assert.assertTrue(id2!=null && id2>0l);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 修改用户基本信息
//	 */
//	@org.junit.Test
//	public void testUpdateUserBasic(){
//		try {
//			Long id=userLoginRegisterService.getId("13677687623");
//			boolean bl =userBasicService.updateUserBasic(setUserBasic(id));
//			Assert.assertTrue(bl);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 根据行业id获取userId分页列表
//	 */
//	@org.junit.Test
//	public void testGetUserIdListByIndustryId(){
//		try {
//			List<Long> list =userInterestIndustryService.getUserIdListByIndustryId(1, 2, 2l);
//			Assert.assertTrue(list.size()>0);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 根据用户Id获取感兴趣的id
//	 */
//	@org.junit.Test
//	public void testGetIdList(){
//		try {
//			Long id=userLoginRegisterService.getId("13677687623");
//			List<Long> ids = userInterestIndustryService.getIdList(id);
//			Assert.assertTrue(ids!=null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 根据id列表批量删除用户感兴趣行业
//	 */
//	@org.junit.Test
//	public void testRealDeleteUserInterestIndustryList(){
//		try {
//			Long id=userLoginRegisterService.getId("13677687623");
//			boolean bl = userInterestIndustryService.realDeleteUserInterestIndustryList(userInterestIndustryService.getIdList(id));
//			Assert.assertTrue(bl);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 根据userId列表获取用户基本信息列表
//	 */
//	@org.junit.Test
//	public void testGetUserBasecList(){
//		try {
//			List<Long> userIds=userInterestIndustryService.getUserIdListByIndustryId(0, 1, 3l);
//			List<UserBasic> list = userBasicService.getUserBasecList(userIds);
//			Assert.assertTrue(list.size()>0);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 初始化用户基本信息对象
//	 * @return userLoginRegister
//	 */
//	public UserBasic setUserBasic(Long userId){
//		try {
//			Long ctime=System.currentTimeMillis();
//			UserBasic userBasic = new UserBasic();
//			userBasic.setUserId(userId);
//			userBasic.setName("张家口");
//			Byte sex=1;
//			userBasic.setSex(sex);
////			userBasic.setProvinceId(null);
////			userBasic.setCityId(null);
////			userBasic.setCountyId(null);
//			userBasic.setUtime(ctime);
//			userBasic.setCompanyName("金桐网");
////			userBasic.setCompanyJob("科学家");
////			userBasic.setShortName("张家口");
//			userBasic.setPicId(1l);
////			userBasic.setDescription("");
//			Byte status=1;
//			userBasic.setStatus(status);
//			userBasic.setCtime(System.currentTimeMillis());
//			userBasic.setUtime(System.currentTimeMillis());
////			userBasic.setIp("192.168.110.119");
//			return userBasic;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
