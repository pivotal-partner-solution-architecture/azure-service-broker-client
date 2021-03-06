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

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microsoft.azure.storage.CloudStorageAccount;

@Configuration
@ConditionalOnMissingBean(CloudStorageAccount.class)
@EnableConfigurationProperties(AzureStorageProperties.class)
public class AzureStorageAutoConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger(AzureStorageAutoConfiguration.class);

	private final AzureStorageProperties properties;

	public AzureStorageAutoConfiguration(AzureStorageProperties properties) {
		this.properties = properties;
	}

	@Bean
	public CloudStorageAccount cloudStorageAccount()
	{
		LOG.debug("cloudStorageAccount called, account name = " + properties.getName());
		return createCloudStorageAccount();
	}
	
	@Bean
	public CloudStorageAccountFactory cloudStorageAccountFactory()
	{
		return new CloudStorageAccountFactory();
	}
	
	public class CloudStorageAccountFactory
	{
		public CloudStorageAccount createAccountByServiceInstanceName(String serviceInstanceName) throws ServiceInstanceNotFoundException, AzureServiceException
		{
			LOG.debug("creating CloudStorageAccount for serviceInstanceName = " + serviceInstanceName);
			properties.populateStoragePropertiesForServiceInstance(serviceInstanceName);
			return createCloudStorageAccount();
		}
	}
	
	private CloudStorageAccount createCloudStorageAccount() 
	{
		LOG.debug("createCloudStorageAccount called, account name = " + properties.getName());
		
		CloudStorageAccount account = null;
		if (properties.getName() !=  null)
		{
			try
			{
				String connectionString = properties.buildStorageConnectString();
				LOG.debug("Connection String is " + connectionString);
				account = CloudStorageAccount.parse(connectionString);
				LOG.debug("createCloudStorageAccount created account " + account);
			} catch (InvalidKeyException e)
			{
				String msg = "Error creating CloudStorageAccount: " + e.getMessage();
				LOG.error(msg, e);
				throw new AzureServiceException(msg, e);
			} catch (URISyntaxException e)
			{
				String msg = "Error creating CloudStorageAccount: " + e.getMessage();
				LOG.error(msg, e);
				throw new AzureServiceException(msg, e);
			}
		}
		return account;
	}
}
