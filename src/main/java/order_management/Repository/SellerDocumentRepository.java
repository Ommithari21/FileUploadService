package order_management.Repository;


import order_management.Entity.DocType;
import order_management.Entity.SellerDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerDocumentRepository extends JpaRepository<SellerDocument,Integer> {


}
