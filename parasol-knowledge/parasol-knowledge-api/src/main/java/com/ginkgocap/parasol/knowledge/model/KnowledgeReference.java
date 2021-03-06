package com.ginkgocap.parasol.knowledge.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Title: 知识来源信息
 * @author 周仕奇
 * @date 2016年1月11日 下午3:38:53
 * @version V1.0.0
 */
@Entity
@Table(name = "tb_knowledge_reference", catalog = "phoenix_knowledge")
public class KnowledgeReference implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4445285733282369227L;
	
	/**主键*/
	private long id;
	
	/**知识主键*/
	private long knowledgeId;
	
	/**引用资料文章名称*/
	private String articleName;
	
	/**引用网址*/
	private String url;
	
	/**应用网址名称*/
	private String websiteName;
	
	/**标示本条资料是否有效，1：为有效，0：为无效*/
	private String status;
	
	/**引用时间*/
	private long refDate;
	
	/**创建时间*/
	private long createDate;
	
	/**修改时间*/
	private long modifyDate;

	@Id
	@GeneratedValue(generator = "id")
	@GenericGenerator(name = "id", strategy = "com.ginkgocap.ywxt.framework.dal.dao.id.util.TimeIdGenerator", parameters = { @Parameter(name = "sequence", value = "t_knowledge_reference_label") })
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "knowledge_id")
	public long getKnowledgeId() {
		return knowledgeId;
	}

	public void setKnowledgeId(long knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

	@Column(name = "article_name")
	public String getArticleName() {
		return articleName;
	}

	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}

	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "website_name")
	public String getWebsiteName() {
		return websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "ref_date")
	public long getRefDate() {
		return refDate;
	}

	public void setRefDate(long refDate) {
		this.refDate = refDate;
	}

	@Column(name = "create_date")
	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	@Column(name = "modify_date")
	public long getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(long modifyDate) {
		this.modifyDate = modifyDate;
	}
	
	
	public static List<DataCollection> putReferenceList2BaseList(List<KnowledgeBase> knowledgeBaseList,List<KnowledgeReference> knowledgeReferenceList) {
		List<DataCollection> returnList = new ArrayList<DataCollection>();
		
		if(knowledgeBaseList == null || knowledgeBaseList.isEmpty())
			return returnList;
		if(knowledgeReferenceList == null)
			knowledgeReferenceList = new ArrayList<KnowledgeReference>();
		
		Iterator<KnowledgeBase> baseIt = knowledgeBaseList.iterator();
		while(baseIt.hasNext()) {
			KnowledgeBase base = baseIt.next();
			Iterator<KnowledgeReference> referenceIt = knowledgeReferenceList.iterator();
			KnowledgeReference reference = null;
			while(referenceIt.hasNext()) {
				KnowledgeReference referenceTemp = referenceIt.next();
				if(base.getId() == referenceTemp.getId()) {
					reference = referenceTemp;
					break;
				}
			}
			
			DataCollection dataCollection = new DataCollection();
			dataCollection.setKnowledge(base);
			dataCollection.setReference(reference);
			returnList.add(dataCollection);
		}
		
		return returnList;
	}

    public static List<KnowledgeData> putReferenceListToKnowledgeList(List<KnowledgeBase> knowledgeBaseList,List<KnowledgeReference> knowledgeReferenceList) {
        List<KnowledgeData> returnList = new ArrayList<KnowledgeData>();

        if(knowledgeBaseList == null || knowledgeBaseList.isEmpty())
            return returnList;
        if(knowledgeReferenceList == null)
            knowledgeReferenceList = new ArrayList<KnowledgeReference>();

        Iterator<KnowledgeBase> baseIt = knowledgeBaseList.iterator();
        while(baseIt.hasNext()) {
            KnowledgeBase base = baseIt.next();
            Iterator<KnowledgeReference> referenceIt = knowledgeReferenceList.iterator();
            KnowledgeReference reference = null;
            while(referenceIt.hasNext()) {
                KnowledgeReference referenceTemp = referenceIt.next();
                if(base.getId() == referenceTemp.getId()) {
                    reference = referenceTemp;
                    break;
                }
            }

            KnowledgeData knowledgeData = new KnowledgeData();
            knowledgeData.setKnowledgeBase(base);
            knowledgeData.setReference(reference);
            returnList.add(knowledgeData);
        }

        return returnList;
    }
}