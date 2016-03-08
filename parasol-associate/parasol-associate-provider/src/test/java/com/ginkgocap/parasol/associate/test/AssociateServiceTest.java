package com.ginkgocap.parasol.associate.test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.json.JSONObject;
import com.alibaba.dubbo.common.serialize.support.json.JsonObjectOutput;
import com.ginkgocap.parasol.associate.exception.AssociateServiceException;
import com.ginkgocap.parasol.associate.exception.AssociateTypeServiceException;
import com.ginkgocap.parasol.associate.model.Associate;
import com.ginkgocap.parasol.associate.model.AssociateType;
import com.ginkgocap.parasol.associate.service.AssociateService;
import com.ginkgocap.parasol.associate.service.AssociateTypeService;

import junit.framework.Test;
import junit.framework.TestResult;

public class AssociateServiceTest extends TestBase implements Test {
	private static long System_AppId = 7647448850l;
	private static Long userId = 111l;

	@Autowired
	private AssociateService associateService;

	@Autowired
	private AssociateTypeService associateTypeService;

	@Override
	public int countTestCases() {
		return 0;
	}

	@Override
	public void run(TestResult result) {

	}

	@org.junit.Test
	public void testSaveAssociate() throws AssociateTypeServiceException {
		for (int i = 0; i < 100; i++) {
			List<AssociateType> associateTypes = associateTypeService.getAssociateTypessByAppId(System_AppId);
			Assert.assertTrue(CollectionUtils.isNotEmpty(associateTypes));
			for (AssociateType associateType : associateTypes) {
				Associate associate = new Associate();
				associate.setAppId(System_AppId);
				associate.setUserId(userId);

				associate.setSourceId(1l);
				associate.setSourceTypeId(associateType.getId());

				associate.setAssocDesc(RandomStringUtils.random(3, "中国人人名"));
				associate.setAssocId(RandomUtils.nextLong());
				associate.setAssocMetadata("");
				associate.setAssocTitle(RandomStringUtils.random(15, "中华人民共和国中央人民政府"));
				associate.setAssocTypeId(associateTypes.get(RandomUtils.nextInt(associateTypes.size())).getId());
				try {
					associateService.createAssociate(associate.getAppId(), associate.getUserId(), associate);
				} catch (AssociateServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * @throws AssociateTypeServiceException
	 * @throws AssociateServiceException 
	 * @throws IOException 
	 */
	@org.junit.Test
	public void testGetAssociate() throws AssociateTypeServiceException, AssociateServiceException, IOException {
		List<AssociateType> associateTypes = associateTypeService.getAssociateTypessByAppId(System_AppId);
		Assert.assertTrue(CollectionUtils.isNotEmpty(associateTypes));
		for (AssociateType associateType : associateTypes) {
			Map<AssociateType, List<Associate>> resultMap = associateService.getAssociatesBy(System_AppId, associateType.getId(), 1l);
			Assert.assertTrue(MapUtils.isNotEmpty(resultMap));
			JsonObjectOutput joo = new JsonObjectOutput(System.out);
			joo.writeObject(resultMap);
		}
		// associateService.getAssociatesBy(System_AppId, sourceType, sourceId)
	}
	
	@org.junit.Test
	public void testRemoveAssociate() throws AssociateTypeServiceException, AssociateServiceException, IOException {
		List<AssociateType> associateTypes = associateTypeService.getAssociateTypessByAppId(System_AppId);
		Assert.assertTrue(CollectionUtils.isNotEmpty(associateTypes));
		for (AssociateType associateType : associateTypes) {
			Map<AssociateType, List<Associate>> resultMap = associateService.getAssociatesBy(System_AppId, associateType.getId(), 1l);
			Assert.assertTrue(MapUtils.isNotEmpty(resultMap));
			JsonObjectOutput joo = new JsonObjectOutput(System.out);
			joo.writeObject(resultMap);
			
			//delete
			for (AssociateType associateType2 : resultMap.keySet()) {
				List<Associate> associates = resultMap.get(associateType2);
				for (Associate associate : associates) {
					associateService.removeAssociate(System_AppId, userId+1, associate.getId());
				}
			}
		}
		
	}
}