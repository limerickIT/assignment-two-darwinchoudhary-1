/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.service;

import com.sd4.model.Category;
import com.sd4.repository.CategoryRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author darwi
 */
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repo;
    
    public Optional<Category> findOne(Long id){
        
        return repo.findById(id);
    }
}
