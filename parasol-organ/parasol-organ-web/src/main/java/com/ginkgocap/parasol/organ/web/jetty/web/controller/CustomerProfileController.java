package com.ginkgocap.parasol.organ.web.jetty.web.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.ginkgocap.parasol.organ.web.jetty.web.resource.ResourcePathExposer;
import com.ginkgocap.parasol.organ.web.jetty.web.service.DealCustomerConnectInfoService;
import com.ginkgocap.parasol.organ.web.jetty.web.utils.Constants;
import com.ginkgocap.parasol.organ.web.jetty.web.utils.Utils;
import com.ginkgocap.parasol.organ.web.jetty.web.vo.organ.BigDataModel;
import com.ginkgocap.parasol.organ.web.jetty.web.vo.organ.CustomerProfileVoNew;
import com.ginkgocap.parasol.organ.web.jetty.web.vo.organ.TemplateVo;
import com.ginkgocap.ywxt.dynamic.model.DynamicComment;
import com.ginkgocap.ywxt.dynamic.model.DynamicNews;
import com.ginkgocap.ywxt.dynamic.model.Location;
import com.ginkgocap.ywxt.dynamic.model.Picture;
import com.ginkgocap.ywxt.dynamic.model.RelationUserInfo;
import com.ginkgocap.ywxt.dynamic.service.DynamicNewsService;
import com.ginkgocap.ywxt.organ.model.Constants2;
import com.ginkgocap.ywxt.organ.model.Customer;
import com.ginkgocap.ywxt.organ.model.CustomerGroup;
import com.ginkgocap.ywxt.organ.model.CustomerTag;
import com.ginkgocap.ywxt.organ.model.bigdata.BigDataReport;
import com.ginkgocap.ywxt.organ.model.profile.CustomerPersonalLine;
import com.ginkgocap.ywxt.organ.model.template.Template;
import com.ginkgocap.ywxt.organ.service.CustomerCollectService;
import com.ginkgocap.ywxt.organ.service.CustomerCountService;
import com.ginkgocap.ywxt.organ.service.CustomerGroupService;
import com.ginkgocap.ywxt.organ.service.CustomerIdService;
import com.ginkgocap.ywxt.organ.service.CustomerService;
import com.ginkgocap.ywxt.organ.service.SimpleCustomerService;
import com.ginkgocap.ywxt.organ.service.tag.RCustomerTagService;
import com.ginkgocap.ywxt.organ.service.template.TemplateService;
import com.ginkgocap.ywxt.user.model.User;
import com.ginkgocap.ywxt.user.service.FriendsRelationService;
import com.ginkgocap.ywxt.user.service.UserService;
import com.ginkgocap.ywxt.util.DateFunc;
import com.ginkgocap.ywxt.util.JsonUtil;
import com.gintong.common.phoenix.permission.ResourceType;
import com.gintong.common.phoenix.permission.entity.Permission;
import com.gintong.common.phoenix.permission.entity.PermissionQuery;
import com.gintong.common.phoenix.permission.service.PermissionRepositoryService;
import com.gintong.frame.util.dto.InterfaceResult;
import com.gintong.frame.util.dto.Notification;

/**
 * Created by jbqiu on 2016/6/10. controller 组织点评controller
 */
@Controller
@RequestMapping("/organ")
public class CustomerProfileController extends BaseController {

	@Autowired
	private UserService userService;

	@Resource
	private CustomerService customerService;

	@Autowired
	private TemplateService templateService;

	@Autowired
	PermissionRepositoryService permissionRepositoryService;

	@Resource
	private CustomerCollectService customerCollectService;
	@Resource
	private ResourcePathExposer rpe;

	@Resource
	private CustomerCountService customerCountService;

	@Autowired
	private SimpleCustomerService simpleCustomerService;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private CustomerGroupService customerGroupService;
	
	@Autowired
	private DynamicNewsService dynamicNewService;
	
	@Autowired
	private FriendsRelationService friendsRelationService;
	
	@Resource
	private RCustomerTagService rCustomerTagService;

	@Resource
	private DealCustomerConnectInfoService dealCustomerConnectInfoService;
	
	 @Resource
	 private CustomerIdService customerIdService;
	long appId = 1;
	long sourceType = 3;

	/**
	 * 新的 添加客户详情
	 * 
	 * @author caizhigang
	 */
	@ResponseBody
	@RequestMapping(value = "/customer/saveCustomerProfile.json", method = RequestMethod.POST)
	public Map<String, Object> saveCustomerProfile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String requestJson = getJsonParamStr(request);
		;
		Map<String, Object> responseDataMap = new HashMap<String, Object>();
		User user = null;
		boolean isAdd = false; // 是否增加
		Customer customer=null;
		Permission per=null;
		int updateDynamic=0;
		if (requestJson != null && !"".equals(requestJson)) {
			try {
				JSONObject jo = JSONObject.fromObject(requestJson);
				
				if(jo.has("updateDynamic")){// 是否同步到动态   无许保存数据库 在  removeUnSerilabeAndUseLessField 方法中 会把 jo 中的updateDynamic去掉
					updateDynamic=jo.getInt("updateDynamic");
				}
			
				removeUnSerilabeAndUseLessField(jo);
				user = getUser(request);

				System.out.println("requestJson:" + requestJson);
				ObjectMapper objectMapper = new ObjectMapper();
				 customer = objectMapper.readValue(jo.toString(),
						Customer.class);
						
			     if(!OrganUtils.initCustomerOldField(customer)){
			    	 
			    	 return returnFailMSGNew("01", "保存失败,请检查传入参数");
			     }
				 
				Customer oldCustomer = null;
				
				if(customer.getCustomerId()!=0){
				    oldCustomer=customerService.findCustomerDataInTemplate(customer.getCustomerId(), customer.getTemplateId());
				    
				}
				
			
				if (oldCustomer != null
						&& user.getId() != oldCustomer.getCreateById()) {// 修改
					setSessionAndErr(request, response, "-1", "您没有权限进行此操作");
					return returnFailMSGNew("01", "您没有权限进行此操作");
				}
				
				if(oldCustomer!=null){
					customer.setId(oldCustomer.getId());
				}
				
				
				if (isNullOrEmpty(customer.getName())) {
					setSessionAndErr(request, response, "-1", "客户名称必须填写");
					return returnFailMSGNew("01", "客户简称必须填写");
				}
				if (isNullOrEmpty(customer.getPicLogo())) {
					customer.setPicLogo(Constants.ORGAN_DEFAULT_PIC_PATH);
				}
				customer.setCreateById(user.getId());
				customer.setVirtual("0");
				customer.setAuth(-1);// 客户都是未进行认证的。
				customer.setUtime(DateFunc.getDate());
				// TODO
				// 关联json r:事件,p:人脉,o:组织,k:知识 type:1 需求 2 人脉 3 全平台普通用户 4
				// 组织(全平台组织用户) 5 客户 6 知识
				// {"r":[{"tag":"33","conn":[{"type":1,"id":2111,"title":"d d d d","ownerid":1,"ownername":"的的的的的的的","requirementtype":"tzxq"}]}],
				// "p":[{"tag":"111","conn":[{"type":2,"id":"141527672992500079","name":"五小六","ownerid":1,"ownername":"","caree":"","company":""}]}],
				// "o":[{"tag":"22","conn":[{"type":5,"id":618,"name":"我的测试客户","ownerid":"","ownername":"","address":"","hy":","},{"type":5,"id":617,"name":" 中国平安","ownerid":"","ownername":"","address":"","hy":",保险公司,"}]}],
				// "k":[{"tag":"44","conn":[{"type":5,"id":618,"title":"测试知识","ownerid":"","ownername":"","columntype":"","columnpath":""]}
				 String relevance=JsonUtil.getJsonNode(requestJson,
				 "relevance").toString();
				 if(StringUtils.isBlank(relevance) ||
				 StringUtils.equals(relevance, "{}")){
				 relevance="{\"r\":[],\"p\":[],\"o\":[],\"k\":[]}";
				 }
//				 目录
//				 [2,3,4]
				 String directory=JsonUtil.getJsonNode(requestJson,
				 "directory").toString();
				 if(StringUtils.isBlank(directory)){
				 directory="[]";
				 }
//				 标签
//				 [{"tagId":1,"tagName":"飞凤舞"}]
				 String tagJson=JsonUtil.getJsonNode(requestJson,
				 "lableList").toString();
				 if(StringUtils.isBlank(tagJson)){
					 tagJson="[]";
				 }
				 
			
			     customer.setDirectory(directory);
			     customer.setRelevance(relevance);

				 //标签转成CustomerTag对象
				 List<CustomerTag> tagList= new ArrayList<CustomerTag>();
				 if(StringUtils.isNotBlank(tagJson)){
				 tagList = objectMapper.readValue(tagJson,new
						 TypeReference<List<CustomerTag>>() {});
				 }

				 customer.setLableList(tagList);

				 
				if (oldCustomer != null) {
					customer.setCustomerId(oldCustomer.getCustomerId());
				}

				
				JSONObject customerPermissions = JSONObject.fromObject(JsonUtil.getJsonNode(requestJson, "customerPermissions").toString());
				
				customer.setCustomerPermissions(customerPermissions.toString());
				
			
				if(customer.getCustomerId()==0){
					isAdd=true;
				}
				customer=customerService.saveOrUpdateCustomerData(customer);
				
				
				responseDataMap.put("success", true);
				responseDataMap.put("msg", "操作成功");
				responseDataMap.put("customerId", customer.getCustomerId());
				
				System.out.println("增加客户" + customer.getCustomerId() + "成功");
				
			
//				if (customer.getCustomerId() == 0) {
//
//					customer = customerService.addCustomerData(customer);
//					responseDataMap.put("success", true);
//					responseDataMap.put("msg", "操作成功");
//					responseDataMap.put("customerId", customer.getCustomerId());
//					isAdd = true;
//					System.out.println("增加客户" + customer.getCustomerId() + "成功");
//							
//				} else {
//
//					PermissionQuery p = new PermissionQuery();
//					p.setUserId(user.getId());
//					p.setResId(customer.getCustomerId());
//					p.setResType((short) sourceType);
//					InterfaceResult interfaceReslut = customerService
//							.updateCustomerData(customer, p);
//					System.out.println("interfaceReslut:" + interfaceReslut);
//					System.out.println("responseData:"
//							+ interfaceReslut.getResponseData());
//
//					Notification notification = interfaceReslut
//							.getNotification();
//					if (notification.getNotifCode().equals("0")) {
//
//						responseDataMap.put("success", true);
//						responseDataMap.put("msg", "操作成功");
//					
//						System.out.println("修改客户:" + customer.getCustomerId()
//								+ "成功");
//
//					} else {
//
//						responseDataMap.put("success", false);
//						responseDataMap.put("msg", notification.getNotifInfo());
//						return responseDataMap;
//					}
//
//				}

				
				if(!isNullOrEmpty(relevance)){  // 保存关联
					Map<String, Object> map =  dealCustomerConnectInfoService.insertCustomerConnectInfo(relevance, customer.getCustomerId(), user.getId());
					String result = String.valueOf(map.get(Constants2.status));//0 关联失败 1 关联成功
				}
				
				
				String groupIds="";
				if(StringUtils.isNotBlank(directory)&& !StringUtils.equals(directory, "[]")){// 保存目录
					JSONArray arr = JSONArray.fromObject(directory);
					groupIds = StringUtils.join(arr, ",");
				}
				customerGroupService.updateGroup(customer.getCustomerId()+"", groupIds); // 保存目录
				System.out.println(customer.getCustomerId()+"--保存目录--"+groupIds);
				//if(!"[]".equals(tagJson)){
					rCustomerTagService.saveAll(customer.getCustomerId(), tagJson, user.getId());
				//}
				
				System.out.println("客户e:" + customer.getCustomerId());

				// 保存权限
			
				if (customerPermissions != null) {

					System.out.println("customerPermissions:"
							+ customerPermissions);
					
					if (!isAdd) {
						System.out.println("查找权限：资源ID"
								+ customer.getCustomerId());
						per = permissionRepositoryService.selectByRes(
								customer.getCustomerId(), ResourceType.ORG)
								.getResponseData();
						
						if(per!=null){
							System.out.println("查到权限：perId"
									+ per.getPerId());
						}
					}
					
					if(per==null){
						per = new Permission();
					}
					
					
					per.setResOwnerId(user.getId());// 资源所有者id
					per.setPublicFlag(customerPermissions.getInt("publicFlag"));// 公开-1，私密-0
					per.setShareFlag(customerPermissions.getInt("shareFlag"));// 可分享-1,不可分享-0
					per.setConnectFlag(customerPermissions
							.getInt("connectFlag"));// 可对接-1,不可对接-0
					per.setResType(ResourceType.ORG.getVal());// 资源类型
					per.setResId(customer.getCustomerId());// 资源id
				
					System.out.println("xxxxper:"+per);
				
					if (per.getPerId()==null) {
						InterfaceResult<Boolean> result = permissionRepositoryService
								.insert(per);
						System.out.println(" insert permission relsut :"+result.getNotification().getNotifInfo());
					} else {
						permissionRepositoryService.update(per);
					}

				}
		
				 if(customerPermissions.getInt("publicFlag")==1&&updateDynamic==1&&isAdd){// 同步到动态
						createDynamicNews(user, customer);
				 }
				
			
				
			} catch (Exception e) {
	
				
				if(isAdd&&customer.getCustomerId()!=0){
					PermissionQuery pQuery= new PermissionQuery();
					pQuery.setResId(customer.getId());
					pQuery.setUserId(user.getId());
					pQuery.setResType((short) 3);// 3组织
					customerService.deleteCustomerByCustomerId(customer.getCustomerId());
				}
				
				e.printStackTrace();
				return returnFailMSGNew("01", "系统异常,请稍后再试");
			}
			
		} else {
			setSessionAndErr(request, response, "-1", "非法操作！");
			return returnFailMSGNew("01", "非法操作！");
		}

		return returnSuccessMSG(responseDataMap);
	}
	
	
	
	
	

	
	
	/**
	 * 去掉一下 不能 反序列化 和 没有的字段
	 * @param jo
	 */
	private void removeUnSerilabeAndUseLessField(JSONObject jo){
		
		// android端多传的属性
		jo.remove("relevance");
		jo.remove("directory");
		jo.remove("lableList");
		jo.remove("customerPermissions");
		jo.remove("friends");
		jo.remove("tagList");
		jo.remove("directory");
		jo.remove("associateList");
		jo.remove("templateName");
		jo.remove("templateType");
		jo.remove("columns");// 不知道什么东西暂时去掉
		jo.remove("updateDynamic");
	}
	
	
	

	/**
	 * 查询客户详情(新客户详情)
	 * 
	 * @param customerId
	 *            查询考客户详情
	 * @return
	 * @throws Exception
	 * @author caizhigang
	 */
	@ResponseBody
	@RequestMapping(value = "/customer/findCustomerByCustomerId.json", method = RequestMethod.POST)
	public Map<String, Object> findCustomerByCustomerId(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String requestJson = getJsonParamStr(request);
		Map<String, Object> responseData = new HashMap<String, Object>();
		System.out.println("json:" + requestJson);
		JSONObject j = JSONObject.fromObject(requestJson);
		long customerId = JsonUtil.getNodeToLong(j, "customerId");
		String view = JsonUtil.getNodeToString(j, "view"); // 如果从转发中进入客户详情，前端app传入view=1
															// 2:转发到第三方,不登录查看组织详情
		CustomerProfileVoNew customer_new = new CustomerProfileVoNew();
		Customer customer_temp = customerService.findCustomerCurrentData(
				customerId, "0");// 组织详情基本资料
		
		
	   if(customer_temp==null){// 兼容关联老数据
		   customer_temp= customerService.findOne(customerId);
	   }
		
		
	   
		User userBasic = getUser(request);
		
		
		
		
		
		String sckNum = "";
		
		if (customer_temp != null) {
			
			long createById=customer_temp.getCreateById();
			
//			if(createById!=userBasic.getId()){
//				String customerPermissions =customer_temp.getCustomerPermissions();
//				JSONObject  permissonJo=JSONObject.fromObject(customerPermissions);
//				int publicFlag= permissonJo.getInt("publicFlag");
//				if(publicFlag==0){
//					return returnFailMSGNew("-1", "该资源为私密资源，您没有权限查看");
//				}
//			}
			
			
			sckNum = customer_temp.getStockNum();// 证券号码
			customer_new.setCustomerId(customer_temp.getCustomerId());
			customer_new.setName(customer_temp.getName());
			customer_new.setOrganAllName(customer_temp.getOrganAllName());
			customer_new.setIndustry(customer_temp.getIndustry());
			customer_new.setIndustryId(customer_temp.getIndustryId());
			customer_new.setIsListing(customer_temp.getIsListing());
			
			customer_new.setLoginUserId(userBasic.getId());
			// 新增是否收藏
			customer_new.setIsCollect(customerCollectService
					.findByUserIdAndCustomerId(userBasic.getId(),
							customer_temp.getCustomerId()) != null ? "1" : "0");

			customer_new.setStockNum(sckNum);
			customer_new.setLinkMobile(customer_temp.getLinkMobile());
			customer_new.setLinkEmail(customer_temp.getLinkEmail());
			customer_new
					.setPicLogo("".equals(StringUtils.trimToEmpty(customer_temp
							.getPicLogo())) ? Constants.ORGAN_DEFAULT_PIC_PATH
							: Utils.alterImageUrl(customer_temp.getPicLogo()));
			customer_new.setDiscribe(customer_temp.getDiscribe());
			/*
			 * CustomerProfile cpr=customerProfileService.findOne(orgId);
			 * if(cpr!=null)
			 */
			customer_new.setPersonalPlateList(customer_temp
					.getPersonalPlateList());
			customer_new.setPropertyList(customer_temp.getPropertyList());
			customer_new.setVirtual(customer_temp.getVirtual());
			customer_new.setCreateById(customer_temp.getCreateById());
			// 修改组织来源
			customer_new.setComeId(customer_temp.getComeId());
			

			setCreateTypeAndName(customer_new, customer_temp.getCreateById());
			
			 
			customer_new.setIndustryObj(customer_temp.getIndustryObj());

			customer_new.setOrgType(customer_temp.getOrgType());
			customer_new.setAreaid(customer_temp.getAreaid());
			customer_new.setAreaString(customer_temp.getAreaString());
			customer_new.setAddress(customer_temp.getAddress());
			customer_new.setLinkManName(customer_temp.getLinkManName());

			customer_new.setId(customer_temp.getId());
			customer_new.setCustomerId(customer_temp.getCustomerId());
			// 设置模板ID
			customer_new.setTemplateId(customer_temp.getTemplateId());
			// 设置模块

			
		
			
			if (customer_temp.getMoudles() != null
					&& customer_temp.getMoudles().size() > 0) {
				customer_new.setMoudles(customer_temp.getMoudles());
			} else {

				customer_new.setMoudles(OrganUtils
						.createMoudles(customer_temp));

			}
			
			
//			customer_new.setCustomerPermissions(JSON.parseObject(customer_temp.getCustomerPermissions(), Map.class));// 权限
			String  permissonsStr=customer_temp.getCustomerPermissions();
			if(isNullOrEmpty(permissonsStr)||(!permissonsStr.contains("publicFlag"))){
				
				Map permissonMap=new HashMap();
				permissonMap.put("publicFlag", "0");
				permissonMap.put("shareFlag", "1");
				permissonMap.put("connectFlag", "1");
				customer_new.setCustomerPermissions(permissonMap);
				
			}else{
				customer_new.setCustomerPermissions(JSON.parseObject(customer_temp.getCustomerPermissions(), Map.class));// 权限

			}
			
			
			if(isNullOrEmpty(customer_temp.getRelevance())){
				customer_new.setRelevance("{\"r\":[],\"p\":[],\"o\":[],\"k\":[]}");// 关联
			}else{
				customer_new.setRelevance(customer_temp.getRelevance());
			}
			
			
			if(isNullOrEmpty(customer_temp.getDirectory())){
				customer_new.setDirectory("[]");// 目录
			}else{
				customer_new.setDirectory(customer_temp.getDirectory());
			}
			
			customer_new.setLableList(rCustomerTagService.getTagListByCustomerId(customerId));//标签

			findFourModule(customer_temp,responseData);// 目录 和关联

			System.out.println(customer_temp);
			responseData.put("customer", customer_new);
			try {
				customerCountService
						.updateCustomerCount(
								com.ginkgocap.ywxt.organ.model.Constants.customerCountType.read
										.getType(), customerId);
			} catch (Exception e) {
				logger.error("插入组织数据统计功能报错,请求参数json: ", e);
			}

		} else {
			setSessionAndErr(request, response, "-1", "客户不存在!");
			return returnFailMSGNew("01", "客户不存在!");
		}
		return returnSuccessMSG(responseData);
	}


	
	
	/**
	 * 查询客户详情(大数据客户)
	 * 
	 * @param name
	 *            查询考客户详情
	 * @return
	 * @throws Exception
	 * @author zbb
	 */
	@ResponseBody
	@RequestMapping(value = "/customer/findDigDataByCustomerId.json", method = RequestMethod.POST)
	public Map<String, Object> findDigDataByCustomerId(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String requestJson = getJsonParamStr(request);
		Map<String, Object> responseData = new HashMap<String, Object>();
		System.out.println("json:" + requestJson);
		JSONObject j = JSONObject.fromObject(requestJson);
		String name1 = JsonUtil.getNodeToString(j, "name");
		String name=URLDecoder.decode(name1,"UTF-8");
		BigDataModel bigDataCustomer = new BigDataModel();

		Customer customer_temp = customerService.findCustomerBigData(name, "2");
				
		if (customer_temp != null) {
			String xukezheng="";
			String fuzeren="";
			List<CustomerPersonalLine> pro = customer_temp.getPropertyList();
	    	if(!pro.isEmpty()){
		    	for (CustomerPersonalLine customerPersonalLine : pro) {
		    		String namee = customerPersonalLine.getName();
		    		if(namee.equals("许可证号")){
		    			 xukezheng = customerPersonalLine.getContent();
		    		}
		    		if(namee.equals("负责人")){
		    			fuzeren =customerPersonalLine.getContent();
		    		}
				}
	    	}
			bigDataCustomer.setId(Long.toString(customer_temp.getId()));
			bigDataCustomer.setName(customer_temp.getName());
			bigDataCustomer.setCreateById(customer_temp.getCreateById());
			bigDataCustomer.setCustomerId(customer_temp.getCustomerId());
			bigDataCustomer.setCtime(customer_temp.getCtime());
			bigDataCustomer.setCurrent(customer_temp.isCurrent());
			bigDataCustomer.setVirtual(customer_temp.getVirtual());
			bigDataCustomer.setUrl(customer_temp.getUrl());
			bigDataCustomer.setUtime(customer_temp.getUtime());
			bigDataCustomer.setSource(customer_temp.getSource());
			bigDataCustomer.setTaskid(customer_temp.getTaskid());
			bigDataCustomer
					.setCrawl_datetime(customer_temp.getCrawl_datetime());
			String zhucehao=customer_temp.getRegistration_number();
			if(zhucehao==null){
				bigDataCustomer.setRegistration_number(xukezheng);
			}else{
				bigDataCustomer.setRegistration_number(zhucehao);
			}
			bigDataCustomer.setOrganization_code(customer_temp
					.getOrganization_code());
			bigDataCustomer.setCredit_code(customer_temp.getCredit_code());
			bigDataCustomer.setOperating_state(customer_temp
					.getOperating_state());
			bigDataCustomer.setCtype(customer_temp.getCtype());
			bigDataCustomer.setSet_up_time(customer_temp.getSet_up_time());
			String faren=customer_temp.getLegal_representative();
			if(faren==null){
				bigDataCustomer.setLegal_representative(fuzeren);
			}else{
				bigDataCustomer.setLegal_representative(faren);
			}
			bigDataCustomer.setRegistered_capital(customer_temp
					.getRegistered_capital());
			bigDataCustomer.setBusiness_term(customer_temp.getBusiness_term());
			bigDataCustomer.setRegistration_authority(customer_temp
					.getRegistration_authority());
			bigDataCustomer.setDate_issue(customer_temp.getDate_issue());
			bigDataCustomer.setAddress(customer_temp.getAddress());
			bigDataCustomer.setIndustry_involved(customer_temp
					.getIndustry_involved());
			bigDataCustomer
					.setBusiness_scope(customer_temp.getBusiness_scope());
			bigDataCustomer.setCompany_profile(customer_temp
					.getCompany_profile());
			bigDataCustomer.setComment(customer_temp.getComment());
			bigDataCustomer.setChange(customer_temp.getChange());
			bigDataCustomer.setInvestment(customer_temp.getInvestment());
			bigDataCustomer.setPeople(customer_temp.getPeople());
			bigDataCustomer.setFinfo(customer_temp.getFinfo());
			List<BigDataReport> aa = customer_temp.getReport();
			for (BigDataReport bigDataReport : aa) {
				String report = bigDataReport.getReport();
				String newreport=report.replace("</div>", "");
				bigDataReport.setReport(newreport);
			}
			bigDataCustomer.setReport(aa);
			bigDataCustomer.setShareholders(customer_temp.getShareholders());
			System.out.println("大数据客户id==========="+customer_temp.getId()+"====名称====="+customer_temp.getName());
			responseData.put("customer", bigDataCustomer);
			try {
				customerCountService.updateCustomerCount(com.ginkgocap.ywxt.organ.model.Constants.customerCountType.read.getType(), customer_temp.getId());
			} catch (Exception e) {
				logger.error("插入查询大数据客户功能报错,请求参数json: ", e);
			}

		} else {
			setSessionAndErr(request, response, "-1", "客户不存在!");
			return returnFailMSGNew("01", "客户不存在!");
		}
		return returnSuccessMSG(responseData);
	}
	
	/**
	 * 查询客户详情(大数据客户)
	 * 
	 * @param customerId
	 *            查询考客户详情
	 * @return
	 * @throws Exception
	 * @author zbb
	 */
	@ResponseBody
	@RequestMapping(value = "/customer/findDigDataById.json", method = RequestMethod.POST)
	public Map<String, Object> findDigDataById(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String requestJson = getJsonParamStr(request);
		Map<String, Object> responseData = new HashMap<String, Object>();
		System.out.println("json:" + requestJson);
		JSONObject j = JSONObject.fromObject(requestJson);
		Long customerId = j.getLong("customerId");
		BigDataModel bigDataCustomer = new BigDataModel();

		Customer customer_temp = customerService.findCustomerCurrentData(customerId, "2");
		if(customer_temp == null){
		    Customer oldCustomer = customerService.findOne(customerId);
		    if(oldCustomer!=null){
		    	List<CustomerPersonalLine> pro = oldCustomer.getPropertyList();
		    	if(!pro.isEmpty()){
			    	for (CustomerPersonalLine customerPersonalLine : pro) {
			    		String namee = customerPersonalLine.getName();
			    		if(namee.equals("许可证号")){
			    			bigDataCustomer.setRegistration_number(customerPersonalLine.getContent());
			    		}
			    		if(namee.equals("负责人")){
			    			bigDataCustomer.setLegal_representative(customerPersonalLine.getContent());
			    		}
					}
		    	}
		    }else{
				setSessionAndErr(request, response, "-1", "客户不存在!");
				return returnFailMSGNew("01", "客户不存在!");
		    }
		}else if (customer_temp != null) {
			bigDataCustomer.setId(Long.toString(customer_temp.getId()));
			bigDataCustomer.setName(customer_temp.getName());
			bigDataCustomer.setCreateById(customer_temp.getCreateById());
			bigDataCustomer.setCustomerId(customer_temp.getCustomerId());
			bigDataCustomer.setCtime(customer_temp.getCtime());
			bigDataCustomer.setCurrent(customer_temp.isCurrent());
			bigDataCustomer.setVirtual(customer_temp.getVirtual());
			bigDataCustomer.setUrl(customer_temp.getUrl());
			bigDataCustomer.setUtime(customer_temp.getUtime());
			bigDataCustomer.setSource(customer_temp.getSource());
			bigDataCustomer.setTaskid(customer_temp.getTaskid());
			bigDataCustomer
					.setCrawl_datetime(customer_temp.getCrawl_datetime());
			bigDataCustomer.setRegistration_number(customer_temp
					.getRegistration_number());
			bigDataCustomer.setOrganization_code(customer_temp
					.getOrganization_code());
			bigDataCustomer.setCredit_code(customer_temp.getCredit_code());
			bigDataCustomer.setOperating_state(customer_temp
					.getOperating_state());
			bigDataCustomer.setCtype(customer_temp.getCtype());
			bigDataCustomer.setSet_up_time(customer_temp.getSet_up_time());
			bigDataCustomer.setLegal_representative(customer_temp
					.getLegal_representative());
			bigDataCustomer.setRegistered_capital(customer_temp
					.getRegistered_capital());
			bigDataCustomer.setBusiness_term(customer_temp.getBusiness_term());
			bigDataCustomer.setRegistration_authority(customer_temp
					.getRegistration_authority());
			bigDataCustomer.setDate_issue(customer_temp.getDate_issue());
			bigDataCustomer.setAddress(customer_temp.getAddress());
			bigDataCustomer.setIndustry_involved(customer_temp
					.getIndustry_involved());
			bigDataCustomer
					.setBusiness_scope(customer_temp.getBusiness_scope());
			bigDataCustomer.setCompany_profile(customer_temp
					.getCompany_profile());
			bigDataCustomer.setComment(customer_temp.getComment());
			bigDataCustomer.setChange(customer_temp.getChange());
			bigDataCustomer.setInvestment(customer_temp.getInvestment());
			bigDataCustomer.setPeople(customer_temp.getPeople());
			bigDataCustomer.setFinfo(customer_temp.getFinfo());
			List<BigDataReport> aa = customer_temp.getReport();
			for (BigDataReport bigDataReport : aa) {
				String report = bigDataReport.getReport();
				String newreport=report.replace("</div>", "");
				bigDataReport.setReport(newreport);
			}
			bigDataCustomer.setReport(aa);
			bigDataCustomer.setShareholders(customer_temp.getShareholders());
			System.out.println("大数据客户id==========="+customer_temp.getId()+"====名称====="+customer_temp.getName());
			responseData.put("customer", bigDataCustomer);
			try {
				customerCountService.updateCustomerCount(com.ginkgocap.ywxt.organ.model.Constants.customerCountType.read.getType(), customer_temp.getId());
			} catch (Exception e) {
				logger.error("插入查询大数据客户功能报错,请求参数json: ", e);
			}
		} 
		return returnSuccessMSG(responseData);
	}
	/**
	 * 返回 客户 数据 和 模板 切换模板时 用
	 * 
	 * @param customerId
	 *            查询考客户详情
	 * @author caizhigang
	 */
	@ResponseBody
	@RequestMapping(value = "/customer/findTemplateAndCustomerData.json", method = RequestMethod.POST)
	public Map<String, Object> findTemplateAndCustomerData(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String requestJson = getJsonParamStr(request);
		Map<String, Object> responseData = new HashMap<String, Object>();
		System.out.println("json:" + requestJson);
		JSONObject j = JSONObject.fromObject(requestJson);
		long customerId = JsonUtil.getNodeToLong(j, "customerId");
		long templateId = JsonUtil.getNodeToLong(j, "templateId");
		String view = JsonUtil.getNodeToString(j, "view"); // 如果从转发中进入客户详情，前端app传入view=1
															// 2:转发到第三方,不登录查看组织详情
		CustomerProfileVoNew customer_new = new CustomerProfileVoNew();
		Customer customer_temp = customerService.findCustomerDataInTemplate(
				customerId, templateId);// 组织详情基本资料
		
		  if(customer_temp==null){//  兼容关联老数据
			   customer_temp= customerService.findOne(customerId);
			   
			   if(customer_temp!=null&&customer_temp.getTemplateId()!=templateId){
				   customer_temp=null;
			   }
		   }
		String sckNum = "";
		User userBasic = null;
		if (customer_temp != null) {
			userBasic = getUser(request);
			sckNum = customer_temp.getStockNum();// 证券号码
			customer_new.setCustomerId(customer_temp.getCustomerId());
			customer_new.setName(customer_temp.getName());
			customer_new.setOrganAllName(customer_temp.getOrganAllName());
			customer_new.setIndustry(customer_temp.getIndustry());
			customer_new.setIndustryId(customer_temp.getIndustryId());
			customer_new.setIsListing(customer_temp.getIsListing());
			customer_new.setLoginUserId(userBasic.getId());
			// 新增是否收藏
			customer_new.setIsCollect(customerCollectService
					.findByUserIdAndCustomerId(userBasic.getId(),
							customer_temp.getCustomerId()) != null ? "1" : "0");
			// cusotmerCommonService.findCustomerAuth(view, customer_new,
			// customer_temp,user);

			customer_new.setStockNum(sckNum);
			customer_new.setLinkMobile(customer_temp.getLinkMobile());
			customer_new.setLinkEmail(customer_temp.getLinkEmail());
			customer_new
					.setPicLogo("".equals(StringUtils.trimToEmpty(customer_temp
							.getPicLogo())) ? Constants.ORGAN_DEFAULT_PIC_PATH
							: Utils.alterImageUrl(customer_temp.getPicLogo()));
			customer_new.setDiscribe(customer_temp.getDiscribe());
			/*
			 * CustomerProfile cpr=customerProfileService.findOne(orgId);
			 * if(cpr!=null)
			 */
			customer_new.setPersonalPlateList(customer_temp
					.getPersonalPlateList());
			customer_new.setPropertyList(customer_temp.getPropertyList());
			customer_new.setVirtual(customer_temp.getVirtual());
			customer_new.setCreateById(customer_temp.getCreateById());
			// 修改组织来源
			customer_new.setComeId(customer_temp.getComeId());
			setCreateTypeAndName(customer_new, customer_temp.getCreateById());
			
			customer_new.setDirectory(customer_temp.getDirectory());

			// customer_new.setLableList(rCustomerTagService.getTagListByCustomerId(customerId));
			// cusotmerCommonService.findFourModule(responseData,
			// customer_temp,rpe.getNginxRoot());
			customer_new.setIndustryObj(customer_temp.getIndustryObj());

			customer_new.setOrgType(customer_temp.getOrgType());
			customer_new.setAreaid(customer_temp.getAreaid());
			customer_new.setAreaString(customer_temp.getAreaString());
			customer_new.setAddress(customer_temp.getAddress());
			customer_new.setLinkManName(customer_temp.getLinkManName());
			customer_new.setId(customer_temp.getId());

			// 设置模板ID
			customer_new.setTemplateId(customer_temp.getTemplateId());
			
			
			
			
			if (customer_temp.getMoudles() != null
					&& customer_temp.getMoudles().size() > 0) {
				customer_new.setMoudles(customer_temp.getMoudles());
			} else {

				customer_new.setMoudles(OrganUtils
						.createMoudles(customer_temp));

			}
			
			
//			customer_new.setCustomerPermissions(JSON.parseObject(customer_temp.getCustomerPermissions(), Map.class));// 权限
			String  permissonsStr=customer_temp.getCustomerPermissions();
			if(isNullOrEmpty(permissonsStr)||(!permissonsStr.contains("publicFlag"))){
				
				Map permissonMap=new HashMap();
				permissonMap.put("publicFlag", "0");
				permissonMap.put("shareFlag", "1");
				permissonMap.put("connectFlag", "1");
				customer_new.setCustomerPermissions(permissonMap);
				
			}else{
				customer_new.setCustomerPermissions(JSON.parseObject(customer_temp.getCustomerPermissions(), Map.class));// 权限

			}
			
			
			
			if(isNullOrEmpty(customer_temp.getRelevance())){
				customer_new.setRelevance("{\"r\":[],\"p\":[],\"o\":[],\"k\":[]}");// 关联
			}else{
				customer_new.setRelevance(customer_temp.getRelevance());
			}
			
			
			if(isNullOrEmpty(customer_temp.getDirectory())){
				customer_new.setDirectory("[]");// 目录
			}else{
				customer_new.setDirectory(customer_temp.getDirectory());
			}
			
			

			customer_new.setLableList(rCustomerTagService.getTagListByCustomerId(customerId));//标签

			findFourModule(customer_temp,responseData);// 目录 和关联

			System.out.println(customer_temp);
			responseData.put("customer", customer_new);
			responseData.put("hasData", true);
			try {
				customerCountService
						.updateCustomerCount(
								com.ginkgocap.ywxt.organ.model.Constants.customerCountType.read
										.getType(), customerId);
			} catch (Exception e) {
				logger.error("插入组织数据统计功能报错,请求参数json: ", e);
			}

		} else {
			responseData.put("hasData", false);
		}

		Template template = templateService.findTemplateById(templateId);
		
		TemplateVo templateVo = new TemplateVo();
		templateVo.setTemplateName(template.getTemplateName());
		templateVo.setTemplateId(template.getTemplateId());
		templateVo.setTemplateType(template.getType());
		templateVo.setMoudles(template.getMoudles());
		templateVo.setVisibleMoudles(template.getVisibleMoudles());
		responseData.put("template", templateVo);

		return returnSuccessMSG(responseData);
	}

	/**
	 * 删除客户
	 * 
	 * @param request
	 * @param response
	 * @author cazhigang
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/customer/deleteCustomer.json", method = RequestMethod.POST)
	public Map<String, Object> delete(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("/customer/deleteCustomer.json");
		User userBasic = getUser(request);
		// 获取json参数串
		String requestJson = "";
		try {
			requestJson = getJsonParamStr(request);
		} catch (IOException e) {
			logger.error("参数读取异常");
		}
		System.out.println("requestJson:" + requestJson);
		// 封装 response
		Map<String, Object> responseDataMap = new HashMap<String, Object>();
		if (!isNullOrEmpty(requestJson)) {
			JSONObject j = JSONObject.fromObject(requestJson);
			if (userBasic == null) {
				setSessionAndErr(request, response, "-1", "请登录以后再操作");
				responseDataMap.put("successs", false);
				responseDataMap.put("msg", "无法获取用户");
				System.out.println("无法获取用户");
				return responseDataMap;

			} else {

				long customerId = j.getLong("customerId");
				Customer customer=customerService.findCustomerCurrentData(customerId, "0");
				if(customer.getCreateById()!=userBasic.getId()){
					return returnFailMSGNew("01","您没有权利力删除该客户");
				}

				try {

					boolean result = customerService
							.deleteCustomerByCustomerId(customerId);
				
					if (result) {
						responseDataMap.put("success", true);
						responseDataMap.put("msg", "操作成功");

					} else {
						customerService.deleteById(String.valueOf(customerId));
						responseDataMap.put("success", false);
						responseDataMap.put("msg", "删除失败");

					}
					InterfaceResult<Permission> intre=permissionRepositoryService.selectByRes(customerId,  ResourceType.ORG );
					if(intre!=null){
						Permission permission=intre.getResponseData();
						if(permission!=null){
							permissionRepositoryService.delete(permission);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();

					responseDataMap.put("successs", false);
					responseDataMap.put("msg", "系统异常");
					return responseDataMap;
				}
			}

		} else {
			setSessionAndErr(request, response, "-1", "输入参数不合法");
			System.out.println("没有参数");
		}
		
		return responseDataMap;
	}

	/**
	 * 
	 * 定义成功返回信息
	 * 
	 * @param successResult
	 * @return
	 * @author haiyan
	 */
	protected Map<String, Object> returnSuccessMSG(
			Map<String, Object> successResult) {

		successResult.put("success", true);
		return successResult;
	}

	/**
	 * 定义错误返回信息
	 * 
	 * @param result
	 * @param errRespCode
	 * @param errRespMsg
	 * @return
	 * @author wangfeiliang
	 */
	protected Map<String, Object> returnFailMSGNew(String errRespCode,
			String errRespMsg) {
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("success", false);
		result.put("msg", errRespMsg);
		return result;
	}
	
	
	
	/**创建客户生成动态
     * @param user
     * @param customer
     * @param customerPermissions
     * @author wfl
     */
    protected  void saveCustomerDynamicNews(User user,Customer customer){
//    	Map<String, Object> params =new HashMap<String,Object>();
//	    params.put("type", "62");
//	    if(user.getType()==2){
//	    	  params.put("lowType", "61");//组织
//	    	  params.put("createType","2");
//	    	  params.put("gender", user.getSex());
//	    }
//	    if(user.getType()==1){
//	    	  params.put("lowType", "60");//个人
//	    	  params.put("createType","1");
//	    	  params.put("gender", user.getSex());
//	    }
//	    
//	    params.put("createrId",String.valueOf(user.getId()));
//	    if(!"".equals(StringUtils.trimToEmpty(customer.getShotName()))){
//	    	 params.put("title", customer.getShotName());
//	    }else{
//	    	params.put("title", customer.getName());
//	    }
//	    
//	    params.put("content", customer.getArea().getCity()+"#"+Utils.listToString(customer.getIndustrys()));
//	    params.put("targetId", String.valueOf(customer.getId()));
//	    params.put("imgPath", Utils.alterImageUrl(customer.getPicLogo()));
//	    params.put("picPath", Utils.alterImageUrl(user.getPicPath()));
//	    params.put("virtual", "1");// 0 表示客户  1 表示组织
	    
	    
	    
	    
	    
	    
	    Map news = new HashMap();
	   // news.put("title","这是我的测试动态");
	    
	    if(!"".equals(StringUtils.trimToEmpty(customer.getShotName()))){
	    	news.put("title", customer.getShotName());
	    }else{
	    	news.put("title", customer.getName());
	    }
	    
	    
	    
//	    news.put("content","测试动态很好玩。");
	    news.put("content", customer.getArea().getCity()+"#"+Utils.listToString(customer.getIndustrys()));
	    news.put("contentPath","");
//	    news.put("lowType","1");
	    news.put("virtual", "0");// 0 表示客户  1 表示组织
//	    news.put("targetId",64545);
	    news.put("targetId",String.valueOf(customer.getCustomerId()));
	    news.put("clearContent","");
	    news.put("ctime",new Date().getTime());
	    news.put("imagePath", Utils.alterImageUrl(user.getPicPath()));
	    
	    
	    news.put("type", "62");
	    
	    if(user.getType()==2){
	    	news.put("lowType", "61");//组织
	    	news.put("createType","2");
	    	news.put("gender", user.getSex());
	    }
	    if(user.getType()==1){
	    	news.put("lowType", "60");//个人
	    	news.put("createType","1");
	    	news.put("gender", user.getSex());
	    }
	    
	    
	    
	    
	    
//	    DynamicRelation relation = new DynamicRelation();
	    Map relation = new HashMap();
	    relation.put("ctime",new Date().getTime());
	    relation.put("lowType","1");
	    if(user.getType()==1){
	    	  relation.put("lowType","1");
	    }else{
	    	relation.put("lowType","2");
	    }
	    relation.put("type","62");
	    relation.put("targetId",String.valueOf(customer.getCustomerId()));
	    List relations = new ArrayList();
	    relations.add(relation);
	    news.put("targetRelations", relations);
	    long result = dynamicNewService.insert(news);
	  
	    
	    System.out.println("插入动态成功:id"+result);
	    
//	    Map<String, List<Long>> receiverIds =new HashMap<String,List<Long>>();
//	    JSONObject dyna=JSONObject.fromObject(customerPermissions);
//	    List<Long> dales=new ArrayList<Long>();
//	    List<Long> zhongles=new ArrayList<Long>();
//	    boolean dule=dyna.getBoolean("dule");
//	    if(!dule){
//	    	JSONArray jarray=JsonUtil.getJsonArray(dyna, "dales");
//	    	if(jarray!=null&&jarray.size()>0){
//	    		 for(int i=0;i<jarray.size();i++){
//	    			 JSONObject jso=jarray.getJSONObject(i);
//		    			 if(!jso.isNullObject()){
//		    				 if("-1".equals(jso.getString("id"))){//全平台
//		    				      List<User> users=friendsRelationService.findAllFriendsByUserId(user.getId());
//		    				      if(users!=null&&users.size()>0){
//		    				    	  for(int j=0;j<users.size();j++){
//		    				    		  User ruser=users.get(j);
//		    				    		  dales.add(ruser.getId());
//		    				    	  }
//		    				      }
//		    				 }
//	    				 dales.add(JsonUtil.getNodeToLong(jso, "id"));
//	    			 }
//	    		 }
//	    	}
//	    	
//	    	JSONArray zls=JsonUtil.getJsonArray(dyna, "zhongles");
//	    	if(zls!=null&&zls.size()>0){
//	    		 for(int i=0;i<zls.size();i++){
//	    			 JSONObject jso=zls.getJSONObject(i);
//	    			 if(!jso.isNullObject()){
//	    				 
//	    				 if("-1".equals(jso.getString("id"))){//全平台
//	    				      List<User> users=friendsRelationService.findAllFriendsByUserId(user.getId());
//	    				      if(users!=null&&users.size()>0){
//	    				    	  for(int j=0;j<users.size();j++){
//	    				    		  User ruser=users.get(j);
//	    				    		  zhongles.add(ruser.getId());
//	    				    	  }
//	    				      }
//	    				 }
//	    				 
//	    				 zhongles.add(JsonUtil.getNodeToLong(jso, "id"));
//	    			 }
//	    		 }
//	    	}
//	    	
//	    }
//	    receiverIds.put("dale", dales);
//	    receiverIds.put("zhongle", zhongles);
//	    params.put("receiverIds", receiverIds);
//	    params.put("createrId", String.valueOf(user.getId()));
//	    if(!"".equals(StringUtils.trimToEmpty(user.getShortName()))){
//	    	params.put("createrName", user.getShortName());
//	    }else{
//	    	params.put("createrName", user.getName());
//	    }
//	    
//	    params.put("picPath", user.getPicPath());
//	    
//	    long id=dynamicNewService.insert(params);
//	    System.out.println("插入动态成功:id"+id);
    }

    
    
    
    
    private void createDynamicNews(User user,Customer customer)
    {
        DynamicNews dynamic = new DynamicNews();
        dynamic.setType("62"); //创建客户
     
        dynamic.setTargetId(customer.getCustomerId());
        dynamic.setTitle(customer.getShotName());
       // dynamic.setContent(customer.getArea().getCity()+"#"+Utils.listToString(customer.getIndustrys()));
//        dynamic.setContentPath(detail.getS_addr());
        dynamic.setCreaterId(user.getId());
       // String clearContent = HtmlToText.html2Text(detail.getContent());
//        clearContent = clearContent.length() > 250 ? clearContent.substring(0,250) : clearContent;
//        dynamic.setClearContent(clearContent);
        
        dynamic.setClearContent(customer.getIndustry() +"   "+customer.getAreaString());
        dynamic.setPicPath(user.getPicPath());
        dynamic.setCreaterName(user.getName());
        dynamic.setCtime(new Date().getTime());
        //dynamic.setDemandCount());
        //dynamic.setId();
        dynamic.setImgPath(customer.getPicLogo());
//        dynamic.setKnowledgeCount(0);
        
        dynamic.setCreateType(user.getType()+"");
        dynamic.setScope(String.valueOf(0));
        Location location = new Location();
        location.setDetailName("");
        location.setDimension("");
        location.setMobile("");
        location.setName("");
        location.setSecondLevel("");
        location.setType("");
        dynamic.setLocation(location);
        dynamic.setPeopleRelation(new ArrayList<RelationUserInfo>(0));
        dynamic.setComments(new ArrayList<DynamicComment>(0));
        dynamic.setPicturePaths(new ArrayList<Picture>(0));
        //dynamic.setVirtual(knowledge.getVirtual());
        
		ObjectMapper objectMapper = new ObjectMapper();
	    Map 	dynamicMap=objectMapper.convertValue(dynamic, Map.class);
        
	    dynamicMap.put("asso", new HashMap<String,Object>(1));
	    
	    
	    
	    try{
            List<Long> receiverIds = null;
            long user_id = user.getId() > 0 ? user.getId() : 1l;
            Map<Long, String> friends = friendsRelationService.findAllFriend2Map(user_id);
            if (friends != null && friends.size() > 0){
                receiverIds = new ArrayList<Long>(friends.size()+1);
                receiverIds.addAll(friends.keySet());
            }

            if (receiverIds == null) {
                receiverIds = new ArrayList<Long>(1);
                receiverIds.add(user_id);
            }
            else if(!receiverIds.contains(user_id)) {
                receiverIds.add(user_id);//加上自己
            }
           
            long id=dynamicNewService.insertNewsAndRelation(dynamicMap,receiverIds);
            System.out.println("创建客户生成动态:"+id);
	    
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
       
    }
    
    
    public void setCreateTypeAndName(CustomerProfileVoNew customer_new,long createById){
    	
    	 User createUser=userService.selectByPrimaryKey(createById);
			if(createUser!=null){
				customer_new.setCreateName(createUser.getName());
				System.out.println("customer createName:"+createUser.getName());
				customer_new.setCreateType(createUser.getType()+"");
				
			}
    }
    
	public void findFourModule(Customer customer_temp,Map responseData){
			
		try{
		
			// 新增目录列表
			List<Map<String, Object>> directoryMap = new ArrayList<Map<String, Object>>();
			List<CustomerGroup> groups = customerGroupService
					.findByCustomerId(customer_temp.getCustomerId());
			if (groups != null && groups.size() > 0) {
				for (int i = 0; i < groups.size(); i++) {
					CustomerGroup group = groups.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", group.getId());
					map.put("name", group.getName());
					directoryMap.add(map);
				}
				
				System.out.println("到目录:"+JSON.toJSONString(directoryMap));
			}else{
				System.out.println("未查到目录");
			}
			
		
			
			// 关联
			String relevance = customer_temp.getRelevance();
			Map<String, Object> relevanceMap = new HashMap<String, Object>();
			if (StringUtils.isNotBlank(relevance)) {
				ObjectMapper objectMapper = new ObjectMapper();
				relevanceMap = objectMapper.readValue(relevance,
						new TypeReference<HashMap<String, Object>>() {
						});
			}
			
			
			
			
			
			responseData.put("directory", directoryMap);
			responseData.put("relevance", relevanceMap);
			responseData.put("lableList", rCustomerTagService.getTagListByCustomerId(customer_temp.getCustomerId()));
			
			
		   String permissonsStr=customer_temp.getCustomerPermissions();
	       if(isNullOrEmpty(permissonsStr)||(!permissonsStr.contains("publicFlag"))){
				
				Map permissonMap=new HashMap();
				permissonMap.put("publicFlag", "0");
				permissonMap.put("shareFlag", "1");
				permissonMap.put("connectFlag", "1");
				responseData.put("customerPermissions", permissonMap);
				
			}else{
				responseData.put("customerPermissions", JSON.parse(customer_temp.getCustomerPermissions()));

			}
			
			
			

		}catch(Exception e){
			logger.info("查询组织客户详情时,四大组织报错，报错信息为:",e);
		}
		
	}

	  /**
     * 得到一个不重复的客户主键
     * @author wfl
     */
    @ResponseBody
	@RequestMapping(value = "/customer/getId.json", method = RequestMethod.POST)
	public Map<String, Object> customerGetId(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String requestJson = "";
		requestJson = getJsonParamStr(request);
		Map<String, Object> responseDataMap = new HashMap<String, Object>();
			try {
				long orgId=customerIdService.getCustomerId();
				responseDataMap.put("orgId", orgId);
			} catch (Exception e) {
				setSessionAndErr(request, response, "-1", "系统异常,请稍后再试");
				logger.error("得到一个不重复的客户主键报错,请求参数:{}"+requestJson,e);
				return returnFailMSGNew("01", "系统异常,请稍后再试");
			}
		return returnSuccessMSG(responseDataMap);
	}
	
	
	
}
