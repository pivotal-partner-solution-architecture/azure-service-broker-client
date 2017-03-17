/**
 Copyright (C) 2017-Present Pivotal Software, Inc. All rights reserved.

 This program and the accompanying materials are made available under
 the terms of the under the Apache License, Version 2.0 (the "License”);
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package io.pivotal.ecosystem.azure.autoconfigure;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

/**
 * 
 * @author pbopardikar This class will be instantiated by Spring Boot. The
 *         attributes defined in this class will get their values from the Cloud
 *         Foundry DocumentDB service instance. The name of the CF Service
 *         Instance and the DocumentDB Resource ID is defined in the
 *         azure-sb-documentdb-client project's application.properties
 */
@ConfigurationProperties("azure.documentdb")
public class AzureDocumentDBProperties
{
	private static final Logger LOG = LoggerFactory.getLogger(AzureDocumentDBProperties.class);

	private static final String DOCUMENTDB_SERVICE = "azure-documentdb";
	private static final String HOST_ENDPOINT = "documentdb_host_endpoint";
	private static final String MASTER_KEY = "documentdb_master_key";
	private static final String DATABASE_ID = "documentdb_database_id";
	private static final String DATABASE_LINK = "documentdb_database_link";

	@Autowired
	private VcapParser parser;

	@Autowired
	private Environment environment;

	private String resourceId = "myresource";

	/**
	 * Adding default values for the below attributes just so the Tests pass
	 * when running Maven builds
	 */
	private String hostEndpoint = "TBD";
	private String masterKey = "TBD";
	private String databaseId = "TBD";
	private String link = "TBD";

	@PostConstruct
	private void populateProperties()
	{
		String vcapServices = environment.getProperty(VcapParser.VCAP_SERVICES);
		VcapResult result = parser.parse(vcapServices); 
		VcapPojo[] pojos = result.getPojos();
		for (int i = 0; i < pojos.length; i++)
		{
			VcapPojo pojo = pojos[i];
			if (DOCUMENTDB_SERVICE.equals(pojo.getServiceBrokerName()))
			{
				LOG.debug("Found the documentdb key");
				hostEndpoint = pojo.getCredentials().get(HOST_ENDPOINT);
				masterKey = pojo.getCredentials().get(MASTER_KEY);
				databaseId = pojo.getCredentials().get(DATABASE_ID);
				link = pojo.getCredentials().get(DATABASE_LINK);
			}
		}
		LOG.debug("INSIDE populateProperties");
	}

	public String getResourceId()
	{
		return resourceId;
	}

	public void setResourceId(String resourceId)
	{
		this.resourceId = resourceId;
	}

	public String getHostEndpoint()
	{
		return hostEndpoint;
	}

	public void setHostEndpoint(String hostEndpoint)
	{
		this.hostEndpoint = hostEndpoint;
	}

	public String getMasterKey()
	{
		return masterKey;
	}

	public void setMasterKey(String masterKey)
	{
		this.masterKey = masterKey;
	}

	public String getDatabaseId()
	{
		return databaseId;
	}

	public void setDatabaseId(String databaseId)
	{
		this.databaseId = databaseId;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	@Override
	public String toString()
	{
		return "AzureDocumentDBProperties [parser=" + parser + ", environment=" + environment + ", resourceId=" + resourceId
				+ ", hostEndpoint=" + hostEndpoint + ", masterKey=" + masterKey + ", databaseId=" + databaseId + ", link=" + link + "]";
	}

	
}
