package com.litongjava.file.server.config;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.litongjava.jfinal.aop.annotation.AConfiguration;
import com.litongjava.jfinal.aop.annotation.AInitialization;
import com.litongjava.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.litongjava.jfinal.plugin.activerecord.OrderedFieldContainerFactory;
import com.litongjava.jfinal.plugin.activerecord.dialect.Sqlite3Dialect;
import com.litongjava.jfinal.plugin.hikaricp.DsContainer;
import com.litongjava.tio.boot.constatns.ConfigKeys;
import com.litongjava.tio.boot.server.TioBootServer;
import com.litongjava.tio.utils.environment.EnvironmentUtils;

@AConfiguration
public class TableToJsonConfig {

  /**
   * create datasource
   * @return
   */
  @AInitialization(priority = 10)
  public DataSource dataSource() {
    // get parameter from config file
    String jdbcUrl = EnvironmentUtils.get("jdbc.url");
    String jdbcUser = EnvironmentUtils.get("jdbc.user");
    String jdbcPswd = EnvironmentUtils.get("jdbc.pswd");
    String jdbcValidationQuery = EnvironmentUtils.get("jdbc.validationQuery");

    // create datasource
    DruidDataSource druidDataSource = new DruidDataSource();

    // set basic parameter
    druidDataSource.setUrl(jdbcUrl);
    druidDataSource.setUsername(jdbcUser);
    druidDataSource.setPassword(jdbcPswd);
    druidDataSource.setValidationQuery(jdbcValidationQuery);
    // save datasource
    DsContainer.setDataSource(druidDataSource);
    // close datasource while server close
    TioBootServer.me().addDestroyMethod(druidDataSource::close);
    return druidDataSource;
  }

  /**
   * create ActiveRecordPlugin
   * @return
   * @throws Exception
   */
  @AInitialization
  public ActiveRecordPlugin activeRecordPlugin() throws Exception {
    // get datasource from DsContainer
    DataSource dataSource = DsContainer.ds;

    // get parameter from config file
    Boolean tioDevMode = EnvironmentUtils.getBoolean(ConfigKeys.TIO_DEV_MODE, false);
    boolean jdbcShowSql = EnvironmentUtils.getBoolean("jdbc.showSql", false);
    // cretae plugin
    ActiveRecordPlugin arp = new ActiveRecordPlugin(dataSource);
    // set parameter
    arp.setDialect(new Sqlite3Dialect());
    arp.setContainerFactory(new OrderedFieldContainerFactory());
    arp.setShowSql(jdbcShowSql);

    if (tioDevMode) {
      arp.setDevMode(true);
    }

    // config engine
    Engine engine = arp.getEngine();
    engine.setSourceFactory(new ClassPathSourceFactory());
    engine.setCompressorOn(' ');
    engine.setCompressorOn('\n');
    // arp.addSqlTemplate("/sql/all_sqls.sql");
    // start plugin
    arp.start();
    // close plugin while server close
    TioBootServer.me().addDestroyMethod(arp::stop);
    return arp;
  }
}
