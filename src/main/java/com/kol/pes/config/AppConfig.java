/*-----------------------------------------------------------

-- PURPOSE

--    设置数据库的基本信息

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.config;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import com.kol.pes.utils.Constants;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@PropertySources({ 
	@PropertySource("classpath:config.properties") 
})
@ComponentScan(basePackages="com.kol.pes")
public class AppConfig {
	
	@Autowired
	@Qualifier("dataSourceConfig")
	private DataSourceConfig dataSourceConfig;
	
	
	/**
	 * Database data source
	 * 
	 * @return DataSource
	 * @throws PropertyVetoException in case of bad driver class
	 */
	@Bean(name="dataSource")
	public DataSource dataSource() throws PropertyVetoException {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setJdbcUrl(Constants.IS_USE_TEST ? this.dataSourceConfig.jdbcUrlTest:this.dataSourceConfig.jdbcUrl);
		dataSource.setDriverClass(this.dataSourceConfig.driverClass);
		dataSource.setUser(this.dataSourceConfig.user);
		dataSource.setPassword(this.dataSourceConfig.password);
		dataSource.setInitialPoolSize(this.dataSourceConfig.initialPoolSize);
		dataSource.setMinPoolSize(this.dataSourceConfig.minPoolSize);
		dataSource.setMaxPoolSize(this.dataSourceConfig.maxPoolSize);
		dataSource.setAutoCommitOnClose(true);
		dataSource.setIdleConnectionTestPeriod(this.dataSourceConfig.idleConnectionTestPeriod);
		return dataSource;
	}
	
	
	/**
	 * Required by @PropertySources annotation
	 * @return PropertySourcesPlaceholderConfigurer
	 */
	@Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }
}


/**
 * Encapsulated data source related configuration
 * See c3p0 for detail
 */
@Component("dataSourceConfig")
class DataSourceConfig {
	@Value("${dataSource.jdbcUrl}")
	String jdbcUrl;
	
	@Value("${dataSource.jdbcUrlTest}")
	String jdbcUrlTest;
	
	@Value("${dataSource.driverClass}")
	String driverClass;
	
	@Value("${dataSource.user}")
	String user;
	
	@Value("${dataSource.password}")
	String password;
	
	@Value("${dataSource.initialPoolSize}")
	int initialPoolSize = 20;
	
	@Value("${dataSource.minPoolSize}")
	int minPoolSize = 10;
	
	@Value("${dataSource.maxPoolSize}")
	int maxPoolSize = 60;
	
	@Value("${dataSource.idleConnectionTestPeriod}")
	int idleConnectionTestPeriod = 150;
}
