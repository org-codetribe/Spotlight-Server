package com.yappyapps.spotlight.repository;

import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The IViewerRepository interface provides the CRUD operations on Viewer domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IWalletRepository extends JpaRepository<Wallet, Integer> {

     Wallet findByViewerId(Integer integer);

}
