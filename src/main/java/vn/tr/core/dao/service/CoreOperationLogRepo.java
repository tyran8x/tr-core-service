package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreOperationLog;

@Repository
public interface CoreOperationLogRepo extends JpaRepository<CoreOperationLog, Long>, JpaSpecificationExecutor<CoreOperationLog> {

}
