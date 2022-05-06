package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.dto.*;
import br.com.meli.projetointegrador.model.ShippingOrder;
import br.com.meli.projetointegrador.model.request.LoginRequest;
import br.com.meli.projetointegrador.model.response.JwtResponse;
import br.com.meli.projetointegrador.repository.ShippingOrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ShippingOrderIntegrationTest {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShippingOrderRepository shippingOrderRepository;

    private static boolean init = false;
    private static String CUSTOMER_JWT = "";
    private static String STOCK_MANAGER_JWT = "";

    private String getStandardInboundOrder() {
        return "{\n" +
                "    \"orderNumber\": 10,\n" +
                "    \"orderDate\": \"2022-04-27\",\n" +
                "    \"section\": {\n" +
                "        \"sectionCode\": 1,\n" +
                "        \"warehouseCode\": 1\n" +
                "    },\n" +
                "    \"batchStock\": [\n" +
                "        {\n" +
                "            \"productId\": 1,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": -10.5,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "            \"manufacturingDate\": \"2022-05-01\",\n" +
                "            \"manufacturingTime\": \"2022-05-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-09-09\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"productId\": 2,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": -10.5,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "             \"manufacturingDate\": \"2022-05-01\",\n" +
                "            \"manufacturingTime\": \"2022-05-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-09-09\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"productId\": 3,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": -10.5,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "             \"manufacturingDate\": \"2022-05-01\",\n" +
                "            \"manufacturingTime\": \"2022-05-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-09-09\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"productId\": 3,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": -10.5,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "             \"manufacturingDate\": \"2022-05-01\",\n" +
                "            \"manufacturingTime\": \"2022-05-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-09-09\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    private String getStandardCarrier() {
        return "{\n" +
                "    \"licensePlate\": \"ABC-DEF\",\n" +
                "    \"brand\": \"Fiat\",\n" +
                "    \"model\": \"Fiorino\",\n" +
                "    \"warehouseId\": 1\n" +
                "}";
    }

    public String postPurchaseOrder(){
        return   "{\n" +
                "    \"orderDate\": \"2022-02-02\",\n" +
                "    \"customerId\": 1,\n" +
                "    \"orderStatus\": {\n" +
                "        \"cartStatusCode\": \"CART\"\n" +
                "    },\n" +
                "    \"items\": [\n" +
                "        {\n" +
                "            \"advertisementId\": 1,\n" +
                "            \"quantity\": 8\n" +
                "        },\n" +
                "        {\n" +
                "            \"advertisementId\": 2,\n" +
                "            \"quantity\": 8\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    public String postShippingOrder(){
        return   "{\n" +
                "    \"purchaseId\" : 1\n" +
                "}";
    }

    public String signUpCustomerBody() {
        return "{\n" +
                "    \"name\" : \"customertest\",\n" +
                "    \"username\" : \"customertest\",\n" +
                "    \"email\" : \"customertest@teste.com.br\",\n" +
                "    \"cpf\" : \"000-000-000-03\",\n" +
                "    \"address\" : \"Rua 1\",\n" +
                "    \"password\" : \"abcd1234\",\n" +
                "    \"role\" : [\"customer\"]\n" +
                "}";
    }

    public String signUpStockManagerBody() {
        return "{\n" +
                "    \"name\" : \"stockmanagertest\",\n" +
                "    \"username\" : \"stockmanagertest\",\n" +
                "    \"email\" : \"stockmanagertest@teste.com.br\",\n" +
                "    \"cpf\" : \"000-000-000-01\",\n" +
                "    \"address\" : \"Rua 1\",\n" +
                "    \"password\" : \"abcd1234\",\n" +
                "    \"warehouse_id\": 1,\n" +
                "    \"role\" : [\"manager\"]\n" +
                "}";
    }

    public void signUpPost(ResultMatcher resultMatcher, String signUpDTO) throws Exception {

        mockmvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(signUpDTO))
                .andExpect(resultMatcher);

    }

    public String signInPost(LoginRequest loginRequest, ResultMatcher resultMatcher) throws Exception {

        MvcResult result = mockmvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(resultMatcher).andReturn();

        return result.getResponse().getContentAsString();

    }

    private String postInboundOrder(InboundOrderDTO inboundOrderDTO, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(post("/api/v1/fresh-products/inboundorder")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(inboundOrderDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String postShippingOrder(PurchaseOrderDTO purchaseOrderDTO, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(post("/api/v1/fresh-products/shipping_orders")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(purchaseOrderDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String postPurchaseOrder(CartDTO cartDTO, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(post("/api/v1/fresh-products/orders")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(cartDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String postCarrier(CarrierDTO carrierDTO, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(post("/api/v1/fresh-products/carriers")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(carrierDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String putPurchaseOrder(String orderId, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(put("/api/v1/fresh-products/orders/" + orderId)
                .header("Authorization", "Bearer " + jwt))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String getAllShippingOrders(ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/shipping_orders")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String getShippingOrderById(ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/shipping_orders/1")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    @BeforeEach
    void initialSetup() throws Exception {

        if (!init) {

            String signUpDTOCustomer = signUpCustomerBody();
            signUpPost(status().isOk(), signUpDTOCustomer);

            String signUpDTOStockManager = signUpStockManagerBody();
            signUpPost(status().isOk(), signUpDTOStockManager);

            LoginRequest loginBody = new LoginRequest("customertest", "abcd1234");
            String signInResponse = signInPost(loginBody, status().isOk());
            JwtResponse jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {
            });
            CUSTOMER_JWT = jwtResponse.getToken();

            loginBody = new LoginRequest("stockmanagertest", "abcd1234");
            signInResponse = signInPost(loginBody, status().isOk());
            jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {
            });
            STOCK_MANAGER_JWT = jwtResponse.getToken();

            String inboundOrderString = getStandardInboundOrder();
            String purchaseOrderString = postPurchaseOrder();
            CartDTO cartDTO = objectMapper.readValue(purchaseOrderString, new TypeReference<CartDTO>() {
            });

            InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {
            });

            String carrierString = getStandardCarrier();
            CarrierDTO carrierDTO = objectMapper.readValue(carrierString, new TypeReference<CarrierDTO>() {
            });

            String shippingOrderString = postShippingOrder();
            PurchaseOrderDTO purchaseOrderDTO = objectMapper.readValue(shippingOrderString, new TypeReference<PurchaseOrderDTO>() {
            });

            postCarrier(carrierDTO, status().isCreated(), STOCK_MANAGER_JWT);
            postInboundOrder(inboundOrderDTO, status().isCreated(), STOCK_MANAGER_JWT);
            postPurchaseOrder(cartDTO, status().isCreated(), CUSTOMER_JWT);
            postPurchaseOrder(cartDTO, status().isCreated(), CUSTOMER_JWT);

            putPurchaseOrder("1" ,status().isOk(), CUSTOMER_JWT);
            putPurchaseOrder("2" ,status().isOk(), CUSTOMER_JWT);

            postShippingOrder(purchaseOrderDTO, status().isCreated(), STOCK_MANAGER_JWT);
            init = true;
        }
    }

    @Test
    void registerValidShippingOrder() throws Exception {

        ShippingOrder shippingOrder = shippingOrderRepository.findById(1L).orElse(new ShippingOrder());

        assertEquals("customertest", shippingOrder.getReceiverName());
    }

    @Test
    void getAllShippingOrders() throws Exception {

        String AllShippingOrders = getAllShippingOrders(status().isOk(), STOCK_MANAGER_JWT);

        List<ShippingOrderResponseDTO> shippingOrderResponseDTOS = objectMapper.readValue(AllShippingOrders, new TypeReference<>() {});

        assertEquals(2, shippingOrderResponseDTOS.size());
    }

    @Test
    void getShippingOrderById() throws Exception {

        String shippingOrder = getShippingOrderById(status().isOk(), STOCK_MANAGER_JWT);

        ShippingOrderResponseDTO shippingOrderResponseDTO = objectMapper.readValue(shippingOrder, new TypeReference<>() {});

        assertEquals(1, shippingOrderResponseDTO.getId());
    }



}
