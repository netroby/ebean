package io.ebeaninternal.server.transaction;

import io.ebean.BackgroundExecutor;
import io.ebean.config.ServerConfig;
import io.ebeaninternal.api.SpiProfileHandler;
import io.ebeaninternal.server.cluster.ClusterManager;
import io.ebeaninternal.server.deploy.BeanDescriptorManager;
import io.ebeanservice.docstore.api.DocStoreUpdateProcessor;

/**
 * Objects supplied for constructing the TransactionManager.
 */
public class TransactionManagerOptions {

  final boolean localL2Caching;
  final ServerConfig config;
  final ClusterManager clusterManager;
  final BackgroundExecutor backgroundExecutor;

  final DocStoreUpdateProcessor docStoreUpdateProcessor;
  final BeanDescriptorManager descMgr;
  final DataSourceSupplier dataSourceSupplier;
  final SpiProfileHandler profileHandler;
  final TransactionScopeManager scopeManager;

  public TransactionManagerOptions(boolean localL2Caching, ServerConfig config, TransactionScopeManager scopeManager, ClusterManager clusterManager,
                                   BackgroundExecutor backgroundExecutor, DocStoreUpdateProcessor docStoreUpdateProcessor,
                                   BeanDescriptorManager descMgr, DataSourceSupplier dataSourceSupplier, SpiProfileHandler profileHandler) {

    this.localL2Caching = localL2Caching;
    this.config = config;
    this.scopeManager = scopeManager;
    this.clusterManager = clusterManager;
    this.backgroundExecutor = backgroundExecutor;
    this.docStoreUpdateProcessor = docStoreUpdateProcessor;
    this.descMgr = descMgr;
    this.dataSourceSupplier = dataSourceSupplier;
    this.profileHandler = profileHandler;
  }

}
