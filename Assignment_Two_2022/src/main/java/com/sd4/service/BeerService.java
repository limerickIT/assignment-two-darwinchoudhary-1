/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.service;

import com.sd4.model.Beer;
import com.sd4.repository.BeerRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author darwi
 */
@Service
public class BeerService {
    @Autowired
    private BeerRepository beerRepo;
    
    public Optional<Beer> findOne(Long id){        
        return beerRepo.findById(id);
    }
    
    public Iterable<Beer> findAllPlain(){        
        return beerRepo.findAll();
    }
    
    public Page<Beer> findAll(){
        Pageable page = PageRequest.of(0, 10);              
        return (Page<Beer>) beerRepo.findAll(page);
    }
    
    public Page<Beer> findPaginated(Pageable pageable) {
        List<Beer> beers = (List<Beer>) beerRepo.findAll();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Beer> list;

        if (beers.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, beers.size());
            list = beers.subList(startItem, toIndex);
        }

        Page<Beer> beerPage
          = new PageImpl<Beer>(list, PageRequest.of(currentPage, pageSize), beers.size());

        return beerPage;
    }
    
    public void deleteByID(long id){
        beerRepo.deleteById(id);
    }
    
    public void saveBeer(Beer b){
        beerRepo.save(b);
    }
    
     
}
