/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.controller;

import com.sd4.model.Brewery;
import com.sd4.model.QRCodeUtil;
import com.sd4.service.BeerService;
import com.sd4.service.BreweryService;
import java.awt.image.BufferedImage;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author darwi
 */
@Controller
public class BreweryController {
    @Autowired
    private BeerService beerService;
    @Autowired
    private BreweryService breweryService;
    
    @GetMapping(value = "map/{breweryID}", produces = MediaType.TEXT_HTML_VALUE)    
    public ResponseEntity<String> breweryMapPlot(@PathVariable long breweryID) {
        Optional<Brewery> brewery = breweryService.findOne(breweryID);
        String mapHtml = "<h3>" + brewery.get().getAddress1() + "</h3><div id=\"map\"></div><script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyB558haQj4j35DEx8EOoHI7jPvq3S9p5Ls&callback=initMap\" async></script>";
        String mapScript = "<script>function initMap() {"     
   + " var shop = { lat: -25.344, lng: 131.036 };"
   + " var map = new google.maps.Map(document.getElementById('map'), { zoom: 4, center: shop });"
   + " var marker = new google.maps.Marker({ position: shop, map: map });}</script>";     
    
        return ResponseEntity.ok(mapHtml + mapScript);
    }
    
    @GetMapping(value = "qrCode/{breweryID}", produces = MediaType.IMAGE_PNG_VALUE)    
    public ResponseEntity<BufferedImage> breweryQRCode(@PathVariable long breweryID) throws Exception {
         
        Optional<Brewery> breweryOpt = breweryService.findOne(breweryID);
        
        if(!breweryOpt.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }else{
        Brewery brewery = breweryOpt.get();
        //Create Contact QR Code in vCard format.
         String header_info="BEGIN:VCARD";
         //vCard format has a new line separator at the end of the code, we only need to put that later
         String Contact_name="N:" + brewery.getName(); //N: is the prefix for MECARD Name
         String Contact_company="ORG:" + brewery.getName(); //ORG: is the prefix for company
         String phone_number="TEL:" + brewery.getPhone();//TEL: is the prefix for telephone number
         String website="URL:" + brewery.getWebsite();//URL: is the prefix for website
         String contact_email="EMAIL:" + brewery.getEmail();//EMAIL: is the prefix for email address
         String address="ADR:" + brewery.getAddress1() + " " + brewery.getCity() + " " + brewery.getState() + " " + brewery.getCountry();//ADR: is the prefix for address         
         String footer="END:VCARD";
         //Construct one final contact string in MECARD format
         String final_vCard=String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s", header_info,Contact_name, Contact_company, phone_number, website,contact_email,address,footer);
         
        return ResponseEntity.ok(QRCodeUtil.generateQRCodeImage(final_vCard));
        }
    }
}
