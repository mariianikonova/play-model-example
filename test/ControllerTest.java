import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;
import static play.test.Helpers.status;
import static play.test.Helpers.stop;
import java.util.HashMap;
import java.util.Map;
import models.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.FakeRequest;


public class ControllerTest {
    private FakeApplication application; 
    
    @Before 
    public void startApp() { 
      application = fakeApplication(inMemoryDatabase()); 
      start(application); 
    } 

    @After 
    public void stopApp() { 
      stop(application); 
    } 
    
    @Test
    public void testProductController() {
      // Test GET /product on an empty database.
      Result result = callAction(controllers.routes.ref.Product.index());
      assertTrue("Empty products", contentAsString(result).contains("No products"));

      // Test GET /product on a non-empty database.
      String productId = "Product-01";
      Product product = new Product(productId, "French Press", "Coffee Maker");
      product.save();
      result = callAction(controllers.routes.ref.Product.index());
      assertTrue("One product", contentAsString(result).contains(productId));
      
      // Test GET /product/Product-01
      result = callAction(controllers.routes.ref.Product.details(productId));
      assertTrue("Product detail", contentAsString(result).contains(productId));
      
      // Test GET /product/BadProductId and make sure we get a 404
      result = callAction(controllers.routes.ref.Product.details("BadProductId"));
      assertEquals("Product detail (bad)", NOT_FOUND, status(result));
      
      // Test POST /products (with simulated, valid form data).
      Map<String, String> productData = new HashMap<String, String>();
      productData.put("productId", "Product-02");
      productData.put("name", "Baby Gaggia");
      productData.put("description", "Espresso machine");
      FakeRequest request = fakeRequest();
      request.withFormUrlEncodedBody(productData);
      result = callAction(controllers.routes.ref.Product.newProduct(), request);
      assertEquals("Create new product", OK, status(result));
      
      // Test POST /products (with simulated, invalid form data).
      request = fakeRequest();
      result = callAction(controllers.routes.ref.Product.newProduct(), request);
      assertEquals("Create bad product fails", BAD_REQUEST, status(result));
      
      // Test DELETE /products/Product-O1 (a valid ProductId)
      result = callAction(controllers.routes.ref.Product.delete(productId));
      assertEquals("Delete current product OK", OK, status(result));
      result = callAction(controllers.routes.ref.Product.details(productId));
      assertEquals("Deleted product gone", NOT_FOUND, status(result));
      result = callAction(controllers.routes.ref.Product.delete(productId));
      assertEquals("Delete missing product also OK", OK, status(result));
    }
}
