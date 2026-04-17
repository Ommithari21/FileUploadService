package order_management.Service;


import order_management.Entity.DocType;
import order_management.Entity.SellerDocument;
import order_management.Entity.Status;
import order_management.Repository.SellerDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3service {

    @Autowired
    private S3Client client;

    @Value("${aws.bucket.name}")
    private String BucketName;

    @Autowired
    private SellerDocumentRepository sellerDocumentRepository;


    public SellerDocument uploadFile(MultipartFile file, String docType) throws IOException {


        if (file.getContentType() == null || !file.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("File must have .pdf extension");
        }

        String originalName = file.getOriginalFilename();
        String extension = ".pdf";

        String uniqueKey = docType + "/" + System.currentTimeMillis() + extension;


        client.putObject(
                PutObjectRequest.builder()
                        .bucket(BucketName)
                        .key(uniqueKey)
                        .contentType("application/pdf")
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );


        SellerDocument doc = new SellerDocument();


        doc.setDocType(DocType.valueOf(docType));
        doc.setS3Key(uniqueKey);
        doc.setOriginalFileName(originalName);
        doc.setFileType("application/pdf");
        doc.setFileSize(file.getSize());
        doc.setStatus(Status.PENDING);
        doc.setUploadedAt(java.time.LocalDateTime.now());

        sellerDocumentRepository.save(doc);

        return doc;
    }


    public ResponseEntity<byte[]> downloadFile(Long SellerDocumentId) {

//        SellerDocument doc = sellerDocumentRepository
//                .findByUserIdAndDocType(userId, docType);
SellerDocument Do=sellerDocumentRepository.findById(Math.toIntExact(SellerDocumentId))
        .orElseThrow(()->new RuntimeException("Selller not fount"));



//        if (Do == null) {
//            throw new RuntimeException("Document not found");
//        }

       String key = Do.getS3Key();

        ResponseBytes<GetObjectResponse> object = client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(BucketName)
                        .key(key)
                        .build()
        );


        String fileName =  Do.getOriginalFileName(); ;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(object.asByteArray());
    }




}
