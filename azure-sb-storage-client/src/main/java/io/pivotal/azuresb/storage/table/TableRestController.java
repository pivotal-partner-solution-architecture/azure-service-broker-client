package io.pivotal.azuresb.storage.table;

import io.pivotal.azuresb.storage.AzureSbProperties;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;

@RestController
@EnableConfigurationProperties(AzureSbProperties.class)
public class TableRestController {

	private static final Logger LOG = LoggerFactory
			.getLogger(TableRestController.class);
	private static final String CR = "</BR>";

	@Autowired
	private AzureSbProperties properties;

	@RequestMapping(value = "/table", method = RequestMethod.GET)
	public String processTable(HttpServletResponse response) {
		LOG.info("TableRestController processTable start...");
		StringBuffer result = new StringBuffer();

		try {
			result.append("Connecting to storage account..." + CR);
			CloudStorageAccount account = CloudStorageAccount.parse(properties
					.buildStorageConnectString());

			// Create the table client.
			CloudTableClient tableClient = account.createCloudTableClient();

			// Create the table if it doesn't exist.
			result.append("Creating product table..." + CR);
			String tableName = "product";
			CloudTable cloudTable = tableClient.getTableReference(tableName);
			cloudTable.createIfNotExists();

			ProductEntity product = new ProductEntity(ProductType.SOFTWARE,
					"pcf", "Pivotal Cloud Foundry");

			result.append("Storing new product in table..." + CR);
			TableOperation insert = TableOperation.insertOrReplace(product);
			cloudTable.execute(insert);

			result.append("Retrieving product from table..." + CR);
			TableOperation retrieve = TableOperation
					.retrieve(ProductType.SOFTWARE.toString(), "pcf",
							ProductEntity.class);

			ProductEntity specificEntity = cloudTable.execute(retrieve).getResultAsType();
			
			result.append("Product = " + specificEntity + CR);
			

		} catch (Exception e) {
			LOG.error("Error processing request ", e);
		}

		LOG.info("TableRestController processTable end");
		return result.toString();
	}

}