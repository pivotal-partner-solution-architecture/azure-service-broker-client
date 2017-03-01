package io.pivotal.azuresb.autoconfigure;

import javax.annotation.PostConstruct;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("azure.storage")
public class AzureStorageProperties extends AzureProperties
{
	private static final Logger LOG = LoggerFactory.getLogger(AzureStorageProperties.class);

	private static final String AZURE_STORAGE = "azure-storage";
	private static final String STORAGE_ACCOUNT_NAME = "storage_account_name";
	private static final String PRIMARY_ACCESS_KEY = "primary_access_key";
	
	@Value("${storage.account.name:TBD}") 
	private String accountName;
	
	@Value("${storage.account.key:TBD}") 
	private String accountKey;

	@PostConstruct
	private void populateProperties()
	{
		super.populate(AZURE_STORAGE);
	}

	@Override
	protected void populateCallback(JSONObject creds)
	{
		try
		{
			accountName = creds.getString(STORAGE_ACCOUNT_NAME);
			accountKey = creds.getString(PRIMARY_ACCESS_KEY);
		} catch (JSONException e)
		{
			LOG.error("Error parsing credentials for " + VCAP_SERVICES, e);
		}
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}
	
	public String buildStorageConnectString()
	{
		LOG.debug("storage account name = " + getAccountName());
		LOG.debug("storage account key = " + getAccountKey());
		String storageConnectionString = "DefaultEndpointsProtocol=http;"
				+ "AccountName=" + getAccountName() + ";"
				+ "AccountKey=" + getAccountKey();
	    return storageConnectionString;
	}

}
