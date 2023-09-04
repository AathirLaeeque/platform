package com.leucine.streem.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

  @Bean
  public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  @Bean
  public TransactionTemplate transactionTemplate(@Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
    return new TransactionTemplate(transactionManager);
  }

  // TODO
  // multi datasource transactions are not working as expected in async mode
  // take example of process job class create job, transaction context isn't getting passed
  // temporary fix is to use chained transaction manager, needs understanding of multidatasource transactions in async mode
  // for the right fix
  @Bean(name = "chainedTransactionManager")
  @Primary
  public PlatformTransactionManager chainedTransactionManager(
    @Qualifier("mongoTransactionManager") MongoTransactionManager mongoTransactionManager,
    @Qualifier("transactionManager") PlatformTransactionManager jpaTransactionManager) {
    return new ChainedTransactionManager(mongoTransactionManager, jpaTransactionManager);
  }
}
