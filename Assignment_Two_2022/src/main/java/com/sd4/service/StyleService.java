/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.service;

import com.sd4.model.Style;
import com.sd4.repository.StyleRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author darwi
 */
@Service
public class StyleService {
    @Autowired
    private StyleRepository repo;
    
    public Optional<Style> findOne(Integer id){
        
        return repo.findById(id);
    }
}
