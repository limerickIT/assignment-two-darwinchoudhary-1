/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.controller;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
 
import com.sd4.model.Beer;
import com.sd4.model.Brewery;
import com.sd4.model.Category;
import com.sd4.model.Style;
import com.sd4.service.BeerService;
import com.sd4.service.BreweryService;
import com.sd4.service.CategoryService;
import com.sd4.service.StyleService;
 
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
 
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.HttpHeaders;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
 

/**
 *
 * @author darwi
 */
@Controller
public class BeerController {
    @Autowired
    private BeerService beerService;
    @Autowired
    private BreweryService breweryService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StyleService styleService;
    
    @GetMapping(value = "beerSearch/{beerID}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Beer> getOne(@PathVariable long beerID){
        //long id = Long.parseLong(searchById);        
        Optional<Beer> b = beerService.findOne(beerID);
        if(!b.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        else{
            Link selfLink = linkTo(methodOn(BeerController.class).getOne(beerID)).withSelfRel();
            b.get().add(selfLink);
            return ResponseEntity.ok(b.get());
        }
    }
    
    @GetMapping(value = "beerDesc/{beerID}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Object> getDescByID(@PathVariable long beerID){
        //long id = Long.parseLong(searchById);        
        Optional<Beer> b = beerService.findOne(beerID);
        if(!b.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        else{
            Optional<Brewery> brewery = breweryService.findOne(b.get().getBrewery_id());
            Link selfLink = linkTo(methodOn(BeerController.class).getOne(beerID)).withSelfRel();
            b.get().add(selfLink);
            Map<String, Object> map = new HashMap<String, Object>();
            try{
                map.put("beerName", b.get().getName());
                map.put("beerDesc", b.get().getDescription());
                map.put("breweryName", brewery.get().getName());
                return ResponseEntity.ok(map);
            }
            catch(Exception e){
                map.clear();
                map.put("beerName", "Error! Try Again");
                map.put("beerDesc", "Error! Try Again");
                map.put("breweryName", "Error! Try Again");
                return ResponseEntity.ok(map);
            }            
        }
    }
    
    @GetMapping(value = "/beers", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Page<Beer>> getAll(@RequestParam("page") Optional<Integer> page, 
      @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        List<Beer> beerList = (List<Beer>) beerService.findAllPlain();        
        Page<Beer> beerPage = beerService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
        if(beerPage.isEmpty()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }else{
        
            for(final Beer b : beerPage){
                long id = b.getId();
                Link selfLink = linkTo(methodOn(BeerController.class).getOne(id)).withSelfRel();
                b.add(selfLink);                
                
                Link descLink = linkTo(methodOn(BeerController.class).getDescByID(id)).withRel("descriptive");
                b.add(descLink);
            }
            
            Link link = linkTo(methodOn(BeerController.class)).withSelfRel();
            
            //CollectionModel<Beer> result = CollectionModel.of(beerList, link);
            return ResponseEntity.ok(beerPage);
        }        
    }
    
    @GetMapping(value = "beerImage/{beerID}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getBeerImageByID(@PathVariable long beerID, 
            @RequestParam("size") String size){
        Optional<Beer> beer = beerService.findOne(beerID);
        if(beer.isEmpty()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }else{
            if(size.equalsIgnoreCase("large")){
                 var imgFile = new ClassPathResource("static/assets/images/large/");
                 return ResponseEntity.ok(imgFile.createRelative(beer.get().getImage()));
            }else if(size.equalsIgnoreCase("thumb")){
                var imgFile = new ClassPathResource("static/assets/images/thumbs/");
                return ResponseEntity.ok(imgFile.createRelative(beer.get().getImage()));
            }else{
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        }
    }
    
    @GetMapping(value = "/beerImageZip", produces = "application/zip")
    public ResponseEntity<StreamingResponseBody> getBeerImagesZip(){
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"images.zip\"")
            .body(out -> {
                var zipOutputStream = new ZipOutputStream(out);

                // create a list to add files to be zipped
                ArrayList<File> files = new ArrayList<>(2);
                var largeFile = new ClassPathResource("static/assets/images/large");
                var thumbFile = new ClassPathResource("static/assets/images/thumbs");
                files.add(largeFile.getFile());
                files.add(thumbFile.getFile());
                // package files
                for (File file : files) {
                    //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
//                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
//                    FileInputStream fileInputStream = new FileInputStream(file);
//                    IOUtils.copy(fileInputStream, zipOutputStream);
//                    fileInputStream.close();
                    zipFile(file,file.getName(),zipOutputStream);
                    zipOutputStream.closeEntry();
                }

                zipOutputStream.close();
            });
    }
    
    @GetMapping(value = "beerPdf/{beerID}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getBeerPdf(@PathVariable long beerID){
        Optional<Beer> beer = beerService.findOne(beerID);
        if(beer.isPresent()){
        Optional<Brewery> brewery = null;        
        Optional<Category> category = null;
        Optional<Style> style = null;
        if(beer.get().getBrewery_id() != -1){
            brewery = breweryService.findOne(beer.get().getBrewery_id());
        }
        if(beer.get().getCat_id() != -1){
            category = categoryService.findOne((long)beer.get().getCat_id());
        }
        if(beer.get().getStyle_id() != -1){
            style = styleService.findOne(beer.get().getStyle_id());
        }
        ByteArrayInputStream bis = pdfReport(beer,brewery,category,style);
        
        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
        }else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    
    //Subordinate Function
    public static ByteArrayInputStream pdfReport(Optional<Beer> beer, Optional<Brewery> brewery, Optional<Category> category, Optional<Style> style) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(80);
            table.setWidths(new int[]{4, 6});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);          

                PdfPCell cell;

                cell = new PdfPCell(new Phrase("Beer Name", headFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(beer.get().getName()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("ABV", headFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(beer.get().getAbv().toString()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Phrase("Description", headFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(beer.get().getDescription()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
               
                cell = new PdfPCell(new Phrase("Image", headFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                
                
                 try {
                var thumbFile = new ClassPathResource("static/assets/images/thumbs/" + beer.get().getImage());
                Path path = Paths.get(ClassLoader.getSystemResource("static/assets/images/thumbs/" + beer.get().getImage()).toURI());                 
                Image img = Image.getInstance(path.toAbsolutePath().toString());
                cell = new PdfPCell(img);
                cell.setPaddingLeft(5);
                cell.setPaddingTop(5);
                cell.setPaddingBottom(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            } catch (BadElementException badElementException) {
            } catch (IOException iOException) {
            }catch (URISyntaxException e) {
            }
                
                cell = new PdfPCell(new Phrase("Sell Price", headFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(beer.get().getSell_price().toString()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
                if(brewery != null){
                    cell = new PdfPCell(new Phrase("Brewery Name", headFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(brewery.get().getName()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Phrase("Website", headFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(brewery.get().getWebsite()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                }
                
                if(category != null){
                cell = new PdfPCell(new Phrase("Beer Category", headFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(category.get().getCat_name()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                }
                
                if(style != null){
                cell = new PdfPCell(new Phrase("Beer Style", headFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(style.get().getStyle_name()));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                }
            

            PdfWriter.getInstance(document, out);
            document.open();
            document.add(table);

            document.close();

        } catch (DocumentException ex) {

            
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
     
    //Subordinate Function
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
    
    
}
