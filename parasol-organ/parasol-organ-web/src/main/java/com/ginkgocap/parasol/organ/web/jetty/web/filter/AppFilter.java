package com.ginkgocap.parasol.organ.web.jetty.web.filter;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ginkgocap.parasol.organ.web.jetty.web.utils.CommonUtil;
import com.ginkgocap.parasol.organ.web.jetty.web.utils.Constants;
import com.ginkgocap.parasol.organ.web.jetty.web.utils.RedisKeyUtils;
import com.ginkgocap.parasol.organ.web.jetty.web.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ginkgocap.ywxt.cache.Cache;
import com.ginkgocap.ywxt.user.model.User;
import com.ginkgocap.ywxt.util.Encodes;
import com.gintong.ywxt.organization.model.OrganRegister;

public class AppFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(AppFilter.class);

	String excludedUrl = "";
	String[] excludedUrlArray = {};// 任何情况都不需要登录，在web.xml里面配置
	// 允许游客状态的接口
	String[] webExcludedUrl = { "mobileApp/getVCodeForPassword.json",
			"mobileApp/setNewPassword.json", "register/pwd/reset.json",
			"org/dynamicNews/dynamicNewsList.json",
			"dynamicNews/getDynamicComment", "person/peopleHomeList.json",
			"person/getPeopleDetail.json", "code/peopleCodeList.json",
			"code/peopleCodeListByName.json", "get/area.json",
			"/dynamicNews/getListDynamicNews.json",
			"/register/sendValidateEmail.json",
			"person/getPeopleTemplate.json",
			"/register/isExistByMobileOrByEmail.json",
			"/knowledge/getKnowledgeDetails.json", "/file/nginx/downloadFJ",
			"/org/orgAndProInfo.json", "/customer/findCusProfile.json",
			"/demand/getDemandDetail.json", "/demand/findDemandFile.json",
			"/webknowledge/home/separate.json",
			"/webknowledge/home/getAggregationRead.json",
			"/webknowledge/home/getHotTag.json",
			"/webknowledge/home/getRecommendedKnowledge.json",
			"/webknowledge/getKnowledgeByColumnAndSource.json",
			"/webknowledge/queryMore.json",
			"/webknowledge/home/getHotList.json",
			"/webknowledge/home/getCommentList.json",
			"set/checkMailByStatus.json", "/register/checkOrganRegister.json",
			"metadata/search.json", "get/code.json",
			"/knowledge/getKnowledgeComment.json", "/org/getDiscoverList.json",
			"/resource/hotList.json", "/register/checkIdentifyCode.json",
			"/demand/getDemandList.json",
			"/demandComment/getDemandCommentList.json",
			"/file/nginx/downloadFJ2", "/organ/registerOne.json",
			"/organ/regValidateEmail.json", "/organ/registerSecond.json",
			"/organ/registerThird.json", "/organ/registerFourth.json",
			"/organ/registerFourthAPP.json", "/organ/isExistEmail.json",
			"/organ/isExistOrganNumber.json",
			"/organ/isExistOrganAllName.json", "/organ/getVcode.json",
			"/organ/getMobileVCode.json", "/organ/queryIndustry.json" };

	@Override
	public void destroy() {

	}

	private User getUser(HttpServletRequest request) {
		// 判断客户端请求方式
		if (CommonUtil.getRequestIsFromWebFlag()) {
			String sessionId = request.getHeader("sessionID");
			if (StringUtils.isNotBlank(sessionId)) {
				String key = RedisKeyUtils.getSessionIdKey(sessionId);
				return getUser(request, key);
			}
		} else {
			String sessionId = request.getHeader("sessionID");
			if (sessionId != null && !"null".equals(sessionId)
					&& !"".equals(sessionId)) {
				String key = "user" + sessionId;
				return getUser(request, key);
			}
		}
		return null;
	}

	private OrganRegister getOrg(HttpServletRequest request) {
		String sessionId = request.getHeader("sessionID");
		if (StringUtils.isNotBlank(sessionId)) {
			String key = "organ" + sessionId;
			return getOrg(request, key);
		}
		return null;
	}

	// 获取组织信息
	private OrganRegister getOrg(HttpServletRequest request, String key) {
		WebApplicationContext wac = WebApplicationContextUtils
				.getWebApplicationContext(request.getSession()
						.getServletContext());
		Cache cache = (Cache) wac.getBean("cache");
		OrganRegister organ = (OrganRegister) cache.getByRedis(key);
		if (organ != null) {
			cache.setByRedis(key, organ, 60 * 60 * 24);
		}
		return organ;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param request
	 *            request
	 * @param key
	 *            sessionId key
	 * @return user
	 * @author haiyan
	 */
	private User getUser(HttpServletRequest request, String key) {
		WebApplicationContext wac = WebApplicationContextUtils
				.getWebApplicationContext(request.getSession()
						.getServletContext());
		Cache cache = (Cache) wac.getBean("cache");
		User user = (User) cache.getByRedis(key);
		// if (user != null) {
		// cache.setByRedis(key, user, 60 * 60 * 24);
		// }
		return user;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String s = req.getHeader("s");
		boolean requestIsFromWeb = Constants.WEB.equals(s);

		if (requestIsFromWeb) {
			CommonUtil.setRequestIsFromWebFlag(true);
		}

		User user = getUser(req);

		String url = req.getRequestURI();
		if (url.contains("file/") || url.contains("update/bindEmailStatus")
				|| url.contains("/organ/uploadIdCardImg.json")) {
			chain.doFilter(request, response);
			CommonUtil.setRequestIsFromWebFlag(false);
			return;
		}
		boolean loginFlag = true;
		for (String excludedUrl : excludedUrlArray) {
			if (url.contains(Utils
                    .replaceSpecial(excludedUrl))) {
				loginFlag = false;
				break;
			}
		}

		if (requestIsFromWeb) {
			for (String excludedUrl : webExcludedUrl) {
				if (url.contains(excludedUrl)) {
					loginFlag = false;
					break;
				}
			}
		}

	 if (loginFlag && null == user) {
			response.setCharacterEncoding("utf-8");
			res.setHeader("errorCode", "-1");
			try {
				String str = Encodes.encodeBase64("用户长时间未操作或已过期,请重新登录"
						.getBytes());
				res.setHeader("errorMessage", str);
			} catch (Exception e) {
			}
			response.getWriter()
					.write("{\"notification\":{\"notifCode\": \"1000\",\"notifInfo\": \"用户长时间未操作或已过期,请重新登录\"}}");
			CommonUtil.setRequestIsFromWebFlag(false);
			return;
		}

		// 敏感词过滤
		BufferedReader reader = request.getReader();
		String line = null;
		StringBuffer jsonIn = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			jsonIn.append(line);
		}
		String result = jsonIn.toString();
		System.out.println(result);
		if (result.equals("")) {
			result = request.getParameter("json");
		}
		String requestJson = result;
		reader.close();

		if (requestJson != null && !"".equals(requestJson)) {
			request.setAttribute("requestJson", requestJson);
			CommonUtil.setRequestIsFromWebFlag(false);
			chain.doFilter(request, response);
		} else {
			request.setAttribute("requestJson", "{}");
			CommonUtil.setRequestIsFromWebFlag(false);
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		excludedUrl = config.getInitParameter("excludedUrl");
		if (!Utils.isNullOrEmpty(excludedUrl)) {
			excludedUrlArray = excludedUrl.split(",");
		}
	}
	/*
	 * public void setUser(HttpServletRequest request){ String
	 * sessionId=request.getHeader("sessionID"); if(sessionId!=null &&
	 * !"null".equals(sessionId) && !"".equals(sessionId)){
	 * WebApplicationContext wac=
	 * WebApplicationContextUtils.getWebApplicationContext
	 * (request.getSession().getServletContext()); Cache cache=(Cache)
	 * wac.getBean("cache"); User user=(User)
	 * cache.getByRedis("user"+sessionId); if(user!=null){
	 * cache.setByRedis("user"+sessionId, user ,60 * 60 * 24);//设定过期日期为1天
	 * 如需修改可滞后 } } }
	 */
}
