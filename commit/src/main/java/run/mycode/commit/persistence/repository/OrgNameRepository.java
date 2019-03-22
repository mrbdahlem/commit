/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run.mycode.commit.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import run.mycode.commit.persistence.model.OrgName;

/**
 *
 * @author bdahl
 */
public interface OrgNameRepository extends JpaRepository<OrgName, Long> {
    
}
