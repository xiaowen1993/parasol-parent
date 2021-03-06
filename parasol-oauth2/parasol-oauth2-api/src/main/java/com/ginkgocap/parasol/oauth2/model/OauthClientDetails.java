package com.ginkgocap.parasol.oauth2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;



/**
 * oauth2客户端资料
 */
@Entity
@Table(name = "oauth_client_details", catalog = "parasol_oauth2")
public class OauthClientDetails implements ClientDetails {

	private static final long serialVersionUID = 5367175029358723856L;

	private Set<String> scope = Collections.emptySet();
	
	private Set<String> resourceIds = Collections.emptySet();
	
	private Set<String> authorizedGrantTypes = Collections.emptySet();

	private Set<String> registeredRedirectUris;

	private Set<String> autoApproveScopes;

	private List<GrantedAuthority> authorities = Collections.emptyList();
	
	private Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();

	private long id;
	/**
	 * 客户端(client), 在实际应用中的另一个名称叫appKey,与client_id是同一个概念.
	 */
	private String clientId;
	/**
	 * 客户端所能访问的资源id集合,多个资源时用逗号(,).
	 */
	private String resourceIds_;
	/**
	 * 用于指定客户端(client)的访问密匙,由服务端自动生成.
	 */
	private String clientSecret;
	/**
	 * 指定客户端申请的权限范围,可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔,如: "read,write"..
	 */
	private String scope_;
	/**
	 * 指定客户端支持的grant_type,可选值包括authorization_code,password,refresh_token,implicit,client_credentials, 若支持多个grant_type用逗号(,)分隔,如: "authorization_code,password"..
	 */
	private String authorizedGrantTypes_;
	/**
	 * 客户端的重定向URI,在Oauth的流程中会使用并检查与注册时填写的redirect_uri是否一致.
	 */
	private String registeredRedirectUris_;
	/**
	 * 指定客户端所拥有的Spring Security的权限值.
	 */
	private String authorities_;
	/**
	 * 设定客户端的access_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时). .
	 */
	private Integer accessTokenValiditySeconds;
	/**
	 * 设定客户端的refresh_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天). .
	 */
	private Integer refreshTokenValiditySeconds;
	/**
	 * 这是一个预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据,.
	 */
	private String additionalInformation_;
	/**
	 * 设置用户是否自动Approval操作, 默认值为 'false', 可选值包括 'true','false' .
	 */
	private String autoapprove;
	/**
	 * 公司名或者个人名
	 */
	private String companyName;
	/**
	 * 应用名称
	 */
	private String applicationName;
	/**
	 * 创建时间.
	 */
	private Long ctime;
	/**
	 * 修改时间.
	 */
	private Long utime;
	/**
	 * 用户IP.
	 */
	private String ip;

	private boolean archived = false;
	/**
	 * 设置客户端是否为受信任的,默认为'0'(即不受信任的,1为受信任的). .
	 */
	private boolean trusted = false;
	/**
	 * 创建人Id
	 */	
	private Long userId;
	/**
	 * 应用状态:1.测试状态，2.提交申请，3审核上线。
	 */	
	private Byte status;
	/**
	 * 应用类型：1.app应用，2.web应用
	 */	
	private Byte type;
	/**
	 * 公司logo文件ID
	 */	
	private Long logoId;
	
	public OauthClientDetails() {
	}

	public OauthClientDetails(String clientId, Long ctime) {
		this.clientId = clientId;
		this.ctime = ctime;
	}

	public OauthClientDetails(String clientId, String resourceIds,
			String clientSecret, String scope, String authorizedGrantTypes,
			String registeredRedirectUris, String authorities,
			Integer accessTokenValiditySeconds, Integer refreshTokenValiditySeconds,
			String additionalInformation, String autoapprove, Long ctime,
			Boolean archived, Boolean trusted) {
		this.clientId = clientId;
		this.resourceIds_ = resourceIds;
		this.clientSecret = clientSecret;
		this.scope_ = scope;
		this.authorizedGrantTypes_ = authorizedGrantTypes;
		this.registeredRedirectUris_ = registeredRedirectUris;
		this.authorities_ = authorities;
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
		this.additionalInformation_ = additionalInformation;
		this.autoapprove = autoapprove;
		this.ctime = ctime;
		this.archived = archived;
		this.trusted = trusted;
	}
	

	public OauthClientDetails(ClientDetails prototype) {
		this();
		setAccessTokenValiditySeconds(prototype.getAccessTokenValiditySeconds());
		setRefreshTokenValiditySeconds(prototype
				.getRefreshTokenValiditySeconds());
		setAuthorities(prototype.getAuthorities());
		setAuthorizedGrantTypes(prototype.getAuthorizedGrantTypes());
		setClientId(prototype.getClientId());
		setClientSecret(prototype.getClientSecret());
		setRegisteredRedirectUri(prototype.getRegisteredRedirectUri());
		setScope(prototype.getScope());
		setResourceIds(prototype.getResourceIds());
	}

	public OauthClientDetails(String clientId, String resourceIds,
			String scopes, String grantTypes, String authorities) {
		this(clientId, resourceIds, scopes, grantTypes, authorities, null);
	}

	public OauthClientDetails(String clientId, String resourceIds,
			String scopes, String grantTypes, String authorities,
			String redirectUris) {

		this.clientId = clientId;

		if (StringUtils.hasText(resourceIds)) {
			Set<String> resources = StringUtils
					.commaDelimitedListToSet(resourceIds);
			if (!resources.isEmpty()) {
				this.resourceIds = resources;
			}
		}

		if (StringUtils.hasText(scopes)) {
			Set<String> scopeList = StringUtils.commaDelimitedListToSet(scopes);
			if (!scopeList.isEmpty()) {
				this.scope = scopeList;
			}
		}

		if (StringUtils.hasText(grantTypes)) {
			this.authorizedGrantTypes = StringUtils
					.commaDelimitedListToSet(grantTypes);
		} else {
			this.authorizedGrantTypes = new HashSet<String>(Arrays.asList(
					"authorization_code", "refresh_token"));
		}

		if (StringUtils.hasText(authorities)) {
			this.authorities = AuthorityUtils
					.commaSeparatedStringToAuthorityList(authorities);
		}

		if (StringUtils.hasText(redirectUris)) {
			this.registeredRedirectUris = StringUtils
					.commaDelimitedListToSet(redirectUris);
		}
	}
	
	@Id
	@GeneratedValue(generator = "id")
	@GenericGenerator(name = "id", strategy = "com.ginkgocap.ywxt.framework.dal.dao.id.util.TimeIdGenerator")
	@Column(name = "id")
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name = "client_id", unique = true, nullable = false)
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setAutoApproveScopes(Collection<String> autoApproveScopes) {
		this.autoApproveScopes = new HashSet<String>(autoApproveScopes);
	}

	@Override
	public boolean isAutoApprove(String scope) {
		if (autoApproveScopes == null) {
			return false;
		}
		for (String auto : autoApproveScopes) {
			if (auto.equals("true") || scope.matches(auto)) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public Set<String> getAutoApproveScopes() {
		return autoApproveScopes;
	}

	public void setAutoapprove(String autoapprove) {
		this.autoapprove = autoapprove;
	}
	
	@Column(name = "autoapprove")
	public String getAutoapprove() {
		return this.autoapprove;
	}
	
	@Column(name ="company_name")
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Column(name ="application_name")
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	@Transient
	public boolean isSecretRequired() {
		return this.clientSecret != null;
	}
	
	@Column(name = "client_secret")
	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	@Transient
	public boolean isScoped() {
		return this.scope != null && !this.scope.isEmpty();
	}

	@Transient
	public Set<String> getScope() {
		return scope;
	}

	public void setScope(Collection<String> scope) {
		this.scope = scope == null ? Collections.<String> emptySet()
				: new LinkedHashSet<String>(scope);
	}

	@Transient
	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(Collection<String> resourceIds) {
		this.resourceIds = resourceIds == null ? Collections
				.<String> emptySet() : new LinkedHashSet<String>(resourceIds);
	}

	@Transient
	public Set<String> getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes(Collection<String> authorizedGrantTypes) {
		this.authorizedGrantTypes = new LinkedHashSet<String>(
				authorizedGrantTypes);
	}

	@Transient
	public Set<String> getRegisteredRedirectUri() {
		return registeredRedirectUris;
	}

	public void setRegisteredRedirectUri(Set<String> registeredRedirectUris) {
		this.registeredRedirectUris = registeredRedirectUris == null ? null
				: new LinkedHashSet<String>(registeredRedirectUris);
	}
	
	@Transient
	private List<String> getAuthoritiesAsStrings() {
		return new ArrayList<String>(
				AuthorityUtils.authorityListToSet(authorities));
	}

	private void setAuthoritiesAsStrings(Set<String> values) {
		setAuthorities(AuthorityUtils.createAuthorityList(values
				.toArray(new String[values.size()])));
	}

	@Transient
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(
			Collection<? extends GrantedAuthority> authorities) {
		this.authorities = new ArrayList<GrantedAuthority>(authorities);
	}

	@Column(name = "access_token_validity")
	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	@Column(name = "refresh_token_validity")
	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

	public void setAdditionalInformation(Map<String, ?> additionalInformation) {
		this.additionalInformation = new LinkedHashMap<String, Object>(
				additionalInformation);
	}
	
    @Transient
	public Map<String, Object> getAdditionalInformation() {
		return Collections.unmodifiableMap(this.additionalInformation);
	}
	
	public void addAdditionalInformation(String key, Object value) {
		this.additionalInformation.put(key, value);
	}
	
	@Column(name = "trusted")
	public Boolean getTrusted() {
		return this.trusted;
	}

	public void setTrusted(Boolean trusted) {
		this.trusted = trusted;
	}

	@Column(name = "archived")
	public Boolean getArchived() {
		return this.archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}
	
	@Column(name = "ctime", nullable = false, length = 19)
	public Long getCtime() {
		return this.ctime;
	}

	public void setCtime(Long ctime) {
		this.ctime = ctime;
	}
	@Column(name = "utime", nullable = false, length = 19)
	public Long getUtime() {
		return this.utime;
	}
	
	public void setUtime(Long utime) {
		this.utime = utime;
	}
	

	@Column(name = "ip", nullable = false, length = 16)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@Column(name = "scope")
	public String getScope_() {
		return scope_;
	}

	public void setScope_(String scope_) {
		this.scope_ = scope_;
	}

	@Column(name = "resource_ids")
	public String getResourceIds_() {
		return resourceIds_;
	}

	public void setResourceIds_(String resourceIds_) {
		this.resourceIds_ = resourceIds_;
	}

	@Column(name = "authorized_grant_types")
	public String getAuthorizedGrantTypes_() {
		return authorizedGrantTypes_;
	}

	public void setAuthorizedGrantTypes_(String authorizedGrantTypes_) {
		this.authorizedGrantTypes_ = authorizedGrantTypes_;
	}

	@Column(name = "web_server_redirect_uri")
	public String getRegisteredRedirectUris_() {
		return registeredRedirectUris_;
	}

	public void setRegisteredRedirectUris_(String registeredRedirectUris_) {
		this.registeredRedirectUris_ = registeredRedirectUris_;
	}

	@Column(name = "authorities")
	public String getAuthorities_() {
		return authorities_;
	}

	public void setAuthorities_(String authorities_) {
		this.authorities_ = authorities_;
	}

	@Column(name = "additional_information", length = 65535)
	public String getAdditionalInformation_() {
		return additionalInformation_;
	}

	public void setAdditionalInformation_(String additionalInformation_) {
		this.additionalInformation_ = additionalInformation_;
	}
	@Column(name = "userId")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@Column(name = "status")
	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}
	@Column(name = "type")
	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}
	@Column(name = "logoId")
	public Long getLogoId() {
		return logoId;
	}

	public void setLogoId(Long logoId) {
		this.logoId = logoId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((accessTokenValiditySeconds == null) ? 0
						: accessTokenValiditySeconds);
		result = prime
				* result
				+ ((refreshTokenValiditySeconds == null) ? 0
						: refreshTokenValiditySeconds);
		result = prime * result
				+ ((authorities == null) ? 0 : authorities.hashCode());
		result = prime
				* result
				+ ((authorizedGrantTypes == null) ? 0 : authorizedGrantTypes
						.hashCode());
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result
				+ ((clientSecret == null) ? 0 : clientSecret.hashCode());
		result = prime
				* result
				+ ((registeredRedirectUris == null) ? 0
						: registeredRedirectUris.hashCode());
		result = prime * result
				+ ((resourceIds == null) ? 0 : resourceIds.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((additionalInformation == null) ? 0 : additionalInformation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OauthClientDetails other = (OauthClientDetails) obj;
		if (accessTokenValiditySeconds == null) {
			if (other.accessTokenValiditySeconds != null)
				return false;
		} else if (!accessTokenValiditySeconds.equals(other.accessTokenValiditySeconds))
			return false;
		if (refreshTokenValiditySeconds == null) {
			if (other.refreshTokenValiditySeconds != null)
				return false;
		} else if (!refreshTokenValiditySeconds.equals(other.refreshTokenValiditySeconds))
			return false;
		if (authorities == null) {
			if (other.authorities != null)
				return false;
		} else if (!authorities.equals(other.authorities))
			return false;
		if (authorizedGrantTypes == null) {
			if (other.authorizedGrantTypes != null)
				return false;
		} else if (!authorizedGrantTypes.equals(other.authorizedGrantTypes))
			return false;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (clientSecret == null) {
			if (other.clientSecret != null)
				return false;
		} else if (!clientSecret.equals(other.clientSecret))
			return false;
		if (registeredRedirectUris == null) {
			if (other.registeredRedirectUris != null)
				return false;
		} else if (!registeredRedirectUris.equals(other.registeredRedirectUris))
			return false;
		if (resourceIds == null) {
			if (other.resourceIds != null)
				return false;
		} else if (!resourceIds.equals(other.resourceIds))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (additionalInformation == null) {
			if (other.additionalInformation != null)
				return false;
		} else if (!additionalInformation.equals(other.additionalInformation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OauthClientDetails [clientId=" + clientId + ", clientSecret="
				+ clientSecret + ", scope=" + scope + ", resourceIds="
				+ resourceIds + ", authorizedGrantTypes="
				+ authorizedGrantTypes + ", registeredRedirectUris="
				+ registeredRedirectUris + ", authorities=" + authorities
				+ ", accessTokenValiditySeconds=" + accessTokenValiditySeconds
				+ ", refreshTokenValiditySeconds="
				+ refreshTokenValiditySeconds + ", additionalInformation="
				+ additionalInformation + "]";
	}
}
