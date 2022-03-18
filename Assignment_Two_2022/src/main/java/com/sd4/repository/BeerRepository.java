/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.repository;


import com.sd4.model.Beer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author darwi
 */
@Repository
public interface BeerRepository extends CrudRepository<Beer, Long>, PagingAndSortingRepository<Beer, Long>{
    
}
