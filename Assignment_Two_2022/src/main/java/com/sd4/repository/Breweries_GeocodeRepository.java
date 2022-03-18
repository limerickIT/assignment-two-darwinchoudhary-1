/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.repository;

import com.sd4.model.Breweries_Geocode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author darwi
 */
@Repository
public interface Breweries_GeocodeRepository extends CrudRepository<Breweries_Geocode, Long>{
    
}
