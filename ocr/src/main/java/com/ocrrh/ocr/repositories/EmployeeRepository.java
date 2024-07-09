package com.ocrrh.ocr.repositories;

import com.ocrrh.ocr.entites.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByCuid(String cuid);

}
