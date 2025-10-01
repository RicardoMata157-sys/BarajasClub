package com.mx.web.bajarasClub.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.mx.web.bajarasClub.repository")
public class ConfigDataBases {

	@Bean
	@Primary
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://dpg-d3e0plbe5dus73fbifq0-a.oregon-postgres.render.com:5432/sistema_rifas"); // modifica la url y el usuario del ambiente de aurora openbank
//		dataSource.setUsername("ricardomatacrisostomo");
//	dataSource.setPassword("postgres");
		dataSource.setUsername("root");
		dataSource.setPassword("VWSdkSmwdGVWXJ8oQFw7cbO4rqP3jB6f");
		
//		dataSource.setUsername("root");
//		dataSource.setPassword("VWSdkSmwdGVWXJ8oQFw7cbO4rqP3jB6f");
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setPackagesToScan("com.mx.web.bajarasClub.model");
		factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factoryBean.setJpaPropertyMap(properties());
		return factoryBean;
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManagger(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private Map<String, Object> properties() {
		Map<String, Object> jpaProperties = new HashMap<>();
		jpaProperties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
		jpaProperties.put(AvailableSettings.HBM2DDL_AUTO, "update"); // se cambia dependiendo a la necesidad modificar a
																		// none si no se desea modificar el ddl
		jpaProperties.put(AvailableSettings.SHOW_SQL, true);
		jpaProperties.put(AvailableSettings.FORMAT_SQL, true);
		return jpaProperties;
	}

}