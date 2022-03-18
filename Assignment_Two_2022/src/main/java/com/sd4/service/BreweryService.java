/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.service;

import com.sd4.model.Brewery;
import com.sd4.repository.BreweryRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author darwi
 */
@Service
public class BreweryService {
    @Autowired
    private BreweryRepository repo;
    
    public Optional<Brewery> findOne(Long id){
        
        return repo.findById(id);
    }
    
}