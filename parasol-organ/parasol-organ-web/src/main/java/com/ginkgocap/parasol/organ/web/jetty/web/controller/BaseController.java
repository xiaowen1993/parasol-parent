package com.ginkgocap.parasol.organ.web.jetty.web.controller;

import com.ginkgocap.parasol.organ.web.jetty.web.utils.CommonUtil;
import com.ginkgocap.parasol.organ.web.jetty.web.utils.Constants;
import com.ginkgocap.parasol.organ.web.jetty.web.utils.RedisKeyUtils;
import com.ginkgocap.parasol.organ.web.jetty.web.utils.Utils;
import com.ginkgocap.ywxt.cache.Cache;
import com.ginkgocap.ywxt.person.model.Person;
import com.ginkgocap.ywxt.user.model.User;
import com.ginkgocap.ywxt.user.service.UserService;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jbqiu on 2016/6/10.
 * controller 基类
 */
public abstract class BaseController {
	private static Logger logger = Logger.getLogger(BaseController.class);

    @Autowired
    private UserService userService;
    /**
     * 获取head中的json参数串
     *
     * @param request
     * @return result
     * @throws java.io.IOException
     *
     */
    public String getJsonParamStr(HttpServletRequest request) throws IOException {
        String result = Utils.getJsonIn(request);
        return result;
    }

    public JSONObject convertJson(HttpServletRequest request) throws IOException {
        String result = Utils.getJsonIn(request);
        if (StringUtils.isEmpty(result))
            return null;
        JSONObject json = null;
        try {
            json = JSONObject.fromObject(result);
        } catch (net.sf.json.JSONException e) {
            logger.warn("convert json exception: " + e.getMessage());
            return null;
        }
        return json;
    }

    public void setSessionAndErr(HttpServletRequest request, HttpServletResponse response, String errCode, String errMessage) {

        response.setHeader("errorCode", errCode);
       // response.setHeader("errorMessage", Encodes.encodeBase64(errMessage.getBytes()));
    }

    /**
     * 如果为null，设置空值
     *
     * @param str
     *            待处理对象
     * @return str
     */
    public Object getJsonValueByKey(JSONObject j, String key) {
        // json中是否包含{Param:key}
        if (j.containsKey(key)) {
            return j.get(key);
        } else {
            throw new RuntimeException("JSONObject hasn't key :" + key);
        }
    }

    /**
     * 如果为null，设置空值
     *
     * @param str
     *            待处理对象
     * @return str
     */
    public Boolean getBooleanJsonValueByKey(JSONObject j, String key) {
        // json中是否包含{Param:key}
        Object obj = getJsonValueByKey(j, key);
        if (obj instanceof Boolean) {
            return j.getBoolean(key);
        } else {
            return Boolean.valueOf(String.valueOf(j.get(key)));
        }
    }

    /**
     * 如果为null，设置空值
     *
     * @param str
     *            待处理对象
     * @return str
     */
    public String getStringJsonValueByKey(JSONObject j, String key) {
        // json中是否包含{Param:key}
        Object obj = getJsonValueByKey(j, key);
        if (obj instanceof String) {
            return j.getString(key);
        } else {
            return String.valueOf(j.get(key));
        }
    }

    /**
     * 如果为null，设置空值
     *
     * @param str
     *            待处理对象
     * @return str
     */
    public Long geLongJsonValueByKey(JSONObject j, String key) {
        // json中是否包含{Param:key}
        Object obj = getJsonValueByKey(j, key);
        if (obj instanceof Long) {
            return CommonUtil.optLongFromJSONObject(j, key);
        } else {
            return Long.valueOf(String.valueOf(j.get(key)));
        }
    }

    protected static boolean isWebRequest(HttpServletRequest request) {
        String s = getRequestSource(request);
        if (StringUtils.isBlank(s)) {
            return false;
        }
        return true;
    }

    public static String getRequestSource(HttpServletRequest request) {
        return request.getHeader("s");
    }


    protected User getUser(HttpServletRequest request) {
        // 判断客户端请求方式
        if (isWebRequest(request)) {
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
        
        System.out.println("无法获取用户");
        User user = new User();
        user.setId(1);
        user.setName("yinxing");
        user.setPicPath("/public/c/phoenix-fe/0.0.1/common/images/default200.jpg");
        return user;
        
    }

    private User getUser(HttpServletRequest request, String key) {
        WebApplicationContext wac = WebApplicationContextUtils
                .getWebApplicationContext(request.getSession()
                        .getServletContext());
        Cache cache = (Cache) wac.getBean("cache");
        User user = (User) cache.getByRedis(key);
        
        if(user==null){
        	System.out.println("无法获取用户");
        	   user = new User();
              user.setId(1);
              user.setName("yinxing");
              user.setPicPath("/public/c/phoenix-fe/0.0.1/common/images/default200.jpg");
              return user;
        }
        return user;
    }
    /**
     * 设置金桐脑用户
     *
     * @param request
     * @return
     */
    public User getJTNUser(HttpServletRequest request) {
        User user = getUser(request);
        if (null == user) {
            user = new User();
            user.setId(0);// 金桐脑
            return user;
        }
        return user;
    }
    /**
     * 判断对象是否为null或空
     *
     * @param obj
     *            return IOException
     */
    public static boolean isNullOrEmpty(Object obj) {
        return Utils.isNullOrEmpty(obj);
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
    protected Map<String, Object> returnFailMSGNew(String errRespCode, String errRespMsg) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> errorResult = new HashMap<String, Object>();
        Map<String, Object> successResult = new HashMap<String, Object>();

        successResult.put("success", false);
        errorResult.put("notifCode", errRespCode);
        errorResult.put("notifInfo", errRespMsg);

        result.put("responseData", successResult);
        result.put("notification", errorResult);
        return result;
    }

    /**
     *
     * 定义成功返回信息
     *
     * @param successResult
     * @return
     * @author haiyan
     */
    protected Map<String, Object> returnSuccessMSG(Map<String, Object> successResult) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> errorResult = new HashMap<String, Object>();

        successResult.put("success", true);
        errorResult.put("notifCode", "");
        errorResult.put("notifInfo", "");

        result.put("responseData", successResult);
        result.put("notification", errorResult);
        return result;
    }

    /**
     * 讲notification统一包装起来
     *
     * @param model
     *            ： 讲要返回给客户端的model对象
     * @param responseDataMap
     *            协议的消息体部分， 对应 responseData
     * @param notificationMap
     *            协议的消息部分， 对应 notification
     */
    public Map<String, Object> genRespBody(Map<String, Object> responseDataMap, Map<String, Object> notificationMap) {
        if (notificationMap == null) {
            notificationMap = new HashMap<String, Object>();
            notificationMap.put("notifCode", "");
            notificationMap.put("notifInfo", "");
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("responseData", responseDataMap);
        model.put("notification", notificationMap);
        return model;
    }
    
    
	/**
	 * 获取用户头像
	 * 
	 * @param id
	 *            用户id
	 * @param module
	 *            1 用户 2 人脉 3机构 4客户
	 * @return
	 */
	public String getUserImg(HttpServletRequest request, Long id, int module) {
		User user = null;
		Person person = null;
		// Corporation c=null;
		// PersonalCustomer customer = null;
		StringBuilder link = new StringBuilder();
		link.append(CommonUtil.getServerDomainPath(request));
		link.append("/img/user/image/?module=");
		String imgPath = "";
		String newImgPath = "";
		boolean isHaveHttp = false;
		switch (module) {
		case 1:
			link.append("user");
			// user = userService.selectByPrimaryKey(id);
			if (!isNullOrEmpty(user)) {
				imgPath = user.getPicPath();
				newImgPath = user.getPicPath();
				if (newImgPath != null) {
					isHaveHttp = newImgPath.contains("http");
				} else {
					newImgPath = Constants.USER_DEFAULT_PIC_PATH_FAMALE;
					isHaveHttp = false;
				}
			}
			break;
		case 2:
			link.append("people");
			try {
				// person=personService.get(id);
			} catch (Exception e) {
			}
			if (!isNullOrEmpty(person))
				imgPath = person.getPortrait();
			break;
		case 3:
			link.append("user");
			// user = userService.selectByPrimaryKey(id);
			// imgPath = user.getPicPath();
			break;
		case 4:
			link.append("customer");
			// customer = personalCustomerService.find(id);
			break;
		default:
			link.append("user");
			break;
		}
		link.append("&uId=" + id + "&userId=" + id);
		if (StringUtils.isNotBlank(imgPath)) {
			int start = imgPath.lastIndexOf("/");
			int end = imgPath.lastIndexOf(".");
			if (end == -1 || start > end) {
				imgPath = imgPath.substring(start + 1);
			} else {
				imgPath = imgPath.substring(start + 1, end);
			}
			link.append("&imgPath=" + imgPath);
		}
		// XXX 临时替换本地测试用
		String linkStr = link.toString();
		if (linkStr.contains("http://localhost:8080")) {
			linkStr = linkStr.replaceAll("http://localhost:8080", "http://192.168.101.131:3333");
		} else if (linkStr.contains("http://192.168.120.243:8080")) {// 秦国超机器
			linkStr = linkStr.replaceAll("http://192.168.120.243:8080", "http://192.168.101.90:5555");
		} else if (linkStr.contains("http://192.168.120.239:8080")) {// 秦国超机器
			linkStr = linkStr.replaceAll("http://192.168.120.243:8080", "http://192.168.101.90:5555");
		}
		if (module == 1 && isHaveHttp) {
			return newImgPath;
		}
		return linkStr;
	}
}
