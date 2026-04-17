package order_management.Controller;


import order_management.Entity.DocType;
import order_management.Entity.SellerDocument;
import order_management.Repository.SellerDocumentRepository;
import order_management.Service.S3service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class S3controller {

    @Autowired
    private S3service service;

  @Autowired
  private SellerDocumentRepository sellerDocumentRepository;


    @PostMapping("/Upload/{userId}/{docType}")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @PathVariable String docType) throws IOException {

        SellerDocument savedFile = service.uploadFile(file, docType);

        return ResponseEntity.ok(Map.of(
                "fileId", savedFile.getId(),
                "message", "Uploaded successfully"
        ));
    }

    @GetMapping("/Download/{fileId}")
    public ResponseEntity<byte[]> download(@PathVariable Long fileId) {
        return service.downloadFile(fileId);
    }

    @GetMapping("/Get/role_change/{id}")
    public ResponseEntity<SellerDocument>getRoleChangeId( @PathVariable int id){
          SellerDocument sellerDocument=sellerDocumentRepository
                  .findById(id).orElseThrow(()->new RuntimeException("Error"));

          return ResponseEntity.ok(sellerDocument);

    }


}
