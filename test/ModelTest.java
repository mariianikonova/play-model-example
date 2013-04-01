import static org.junit.Assert.assertEquals;
import java.util.List;
import models.Product;
import models.StockItem;
import models.Tag;
import models.Warehouse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.FakeApplication;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.start;
import static play.test.Helpers.stop;


public class ModelTest {
  private FakeApplication application; 
  
  @Before 
  public void startApp() { 
    application = fakeApplication(); 
    start(application); 
  } 

  @After 
  public void stopApp() { 
    stop(application); 
  } 
  
  @Test
  public void testModel() {
    // Create 1 tag that's associated with 1 product.
    Tag tag = new Tag("Tag");
    Product product = new Product("Product", "Description");
    product.tags.add(tag);
    tag.products.add(product);
    
    // Create 1 warehouse that's associated with 1 StockItem for 1 Product
    Warehouse warehouse = new Warehouse("Warehouse");
    StockItem stockitem = new StockItem(warehouse, product, 100);
    warehouse.stockitems.add(stockitem);
    stockitem.warehouse = warehouse;

    // Persist the sample model by saving all entities and relationships.
    warehouse.save();
    tag.save();
    product.save();
    stockitem.save();
    
    // Retrieve the entire model from the database.
    List<Warehouse> warehouses = Warehouse.find().findList();
    List<Tag> tags = Tag.find().findList();
    List<Product> products = Product.find().findList();
    List<StockItem> stockitems = StockItem.find().findList();
    
    // Check that we've recovered all our entities.
    assertEquals("Checking warehouse", warehouses.size(), 1);
    assertEquals("Checking tags", tags.size(), 1);
    assertEquals("Checking products", products.size(), 1);
    assertEquals("Checking stockitems", stockitems.size(), 1);  
    
    // Check that we've recovered all relationships
    assertEquals("Warehouse-StockItem", warehouses.get(0).stockitems.get(0), stockitems.get(0));
    
    // Check that deleting a tag deletes it from the product.
    
    // Check that deleting a warehouse deletes everything.
  }
}
