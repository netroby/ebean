package io.ebean;

import io.ebean.annotation.Platform;
import io.ebean.util.StringHelper;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.api.SpiQuery;
import io.ebeaninternal.server.core.HelpCreateQueryRequest;
import io.ebeaninternal.server.core.OrmQueryRequest;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import org.avaje.agentloader.AgentLoader;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tests.model.basic.Country;

import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ConditionalTestRunner.class)
public abstract class BaseTestCase {

  protected static Logger logger = LoggerFactory.getLogger(BaseTestCase.class);

  static {
    logger.debug("... preStart");
    if (!AgentLoader.loadAgentFromClasspath("ebean-agent", "debug=1")) {
      logger.info("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
    }
    try {
      // First try, if we get the default server. If this fails, all tests will fail.
      Ebean.getDefaultServer();
    } catch (Throwable e) {
      logger.error("Fatal error while getting ebean-server. Exiting...", e);
      System.exit(1);
    }
  }

  /**
   * Return the generated sql trimming column alias if required.
   */
  protected String sqlOf(Query<?> query) {
    return trimSql(query.getGeneratedSql(), 0);
  }

  /**
   * Return the generated sql trimming column alias if required.
   */
  protected String sqlOf(Query<?> query, int columns) {
    return trimSql(query.getGeneratedSql(), columns);
  }

  /**
   * Trim out column alias if required from the generated sql.
   */
  protected String trimSql(String sql, int columns) {
    for (int i = 0; i <= columns; i++) {
      sql = StringHelper.replaceString(sql, " c" + i + ",", ",");
    }
    for (int i = 0; i <= columns; i++) {
      sql = StringHelper.replaceString(sql, " c" + i + " ", " ");
    }
    return sql;
  }

  /**
   * MS SQL Server does not allow setting explicit values on identity columns
   * so tests that do this need to be skipped for SQL Server.
   */
  public boolean isSqlServer() {
    return Platform.SQLSERVER == platform();
  }

  public boolean isH2() {
    return Platform.H2 == platform();
  }

  public boolean isHSqlDb() {
    return Platform.HSQLDB == platform();
  }

  public boolean isOracle() {
    return Platform.ORACLE == platform();
  }

  public boolean isDb2() {
    return Platform.DB2 == platform();
  }

  public boolean isPostgres() {
    return Platform.POSTGRES == platform();
  }

  public boolean isMySql() {
    return Platform.MYSQL == platform();
  }

  public boolean isPlatformBooleanNative() {
    return Types.BOOLEAN == spiEbeanServer().getDatabasePlatform().getBooleanDbType();
  }

  public boolean isPlatformOrderNullsSupport() {
    return isH2() || isPostgres();
  }

  /**
   * Wait for the L2 cache to propagate changes post-commit.
   */
  protected void awaitL2Cache() {
    // do nothing, used to thread sleep
  }

  protected <T> BeanDescriptor<T> getBeanDescriptor(Class<T> cls) {
    return spiEbeanServer().getBeanDescriptor(cls);
  }

  protected Platform platform() {
    return spiEbeanServer().getDatabasePlatform().getPlatform();
  }

  protected SpiEbeanServer spiEbeanServer() {
    return (SpiEbeanServer) Ebean.getDefaultServer();
  }

  protected EbeanServer server() {
    return Ebean.getDefaultServer();
  }

  protected void loadCountryCache() {

    Ebean.find(Country.class)
      .setBeanCacheMode(CacheMode.PUT)
      .findList();
  }

  /**
   * Platform specific IN clause assert.
   */
  protected void platformAssertIn(String sql, String containsIn) {
    if (isPostgres()) {
      assertThat(sql).contains(containsIn+" = any(");
    } else {
      assertThat(sql).contains(containsIn+" in ");
    }
    // H2 contains("where t0.name in (select * from table(x varchar = ?)");
  }

  /**
   * Platform specific NOT IN clause assert.
   */
  protected void platformAssertNotIn(String sql, String containsIn) {
    if (isPostgres()) {
      assertThat(sql).contains(containsIn+" != all(");
    } else {
      assertThat(sql).contains(containsIn+" not in ");
    }
  }

  protected <T> OrmQueryRequest<T> createQueryRequest(SpiQuery.Type type, Query<T> query, Transaction t) {
    return HelpCreateQueryRequest.create(server(), type, query, t);
  }
}
