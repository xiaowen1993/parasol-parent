package com.ginkgocap.parasol.knowledge.model;

import java.io.Serializable;
import java.util.Date;
/**
 *   
 * <p>栏目订阅</p>  
 * <p>非Transient属性类型和名称依据表tb_knowledge_column_subscribe的字段名称和类型创建 </p>
 * @author  guangyuan  
 * @since 1.2.1-SNAPSHOT
 * @createdtime 2014-9-1
 */
public class KnowledgeColumnSubscribe implements Serializable {

    private static final long serialVersionUID = 1243330354704937038L;
    
    private long id;//主键
    private long userId;//订阅人的id
    private long columnId;//栏目id
    private String columnType;
    private Date subDate;
    
//    @Transient
//    private String userName;//订阅人的用户名 
//    @Transient
//    private String knowledgeColumnName;//栏目名称
//    @Transient
//    private long parentKColumnId;//父栏目id
//    @Transient
//    private String parentKColumnName;//父栏目名称

    public KnowledgeColumnSubscribe() {
    }
   
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
//    public String getUserName() {
//        return userName;
//    }
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
    public long getColumnId() {
        return columnId;
    }
    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }
//    public String getKnowledgeColumnName() {
//        return knowledgeColumnName;
//    }
//    public void setKnowledgeColumnName(String knowledgeColumnName) {
//        this.knowledgeColumnName = knowledgeColumnName;
//    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Date getSubDate() {
        return subDate;
    }

    public void setSubDate(Date subDate) {
        this.subDate = subDate;
    }

//    public long getParentKColumnId() {
//        return parentKColumnId;
//    }
//
//    public void setParentKColumnId(long parentKColumnId) {
//        this.parentKColumnId = parentKColumnId;
//    }

//    public String getParentKColumnName() {
//        return parentKColumnName;
//    }
//
//    public void setParentKColumnName(String parentKColumnName) {
//        this.parentKColumnName = parentKColumnName;
//    }
    
//  private Date createdTime;//创建时间
//  private Date updateTime;//更新时间
//  private String xid;//唯一主键
//  private int delstatus;//状态（0正常使用 -1 删除 ）
    
}
