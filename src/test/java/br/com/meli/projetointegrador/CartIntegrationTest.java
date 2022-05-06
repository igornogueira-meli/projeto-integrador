package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.dto.CartDTO;
import br.com.meli.projetointegrador.dto.CartWithStatusDTO;
import br.com.meli.projetointegrador.dto.InboundOrderDTO;

import br.com.meli.projetointegrador.model.Cart;
import br.com.meli.projetointegrador.model.request.LoginRequest;
import br.com.meli.projetointegrador.model.response.JwtResponse;
import br.com.meli.projetointegrador.repository.CartRepository;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CartIntegrationTest {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartRepository cartRepository;

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
                "            \"quantity\": 14\n" +
                "        },\n" +
                "        {\n" +
                "            \"advertisementId\": 2,\n" +
                "            \"quantity\": 14\n" +
                "        }\n" +
                "    ]\n" +
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

    private String postPurchaseOrder(CartDTO cartDTO, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(post("/api/v1/fresh-products/orders")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(cartDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String putPurchaseOrder(ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(put("/api/v1/fresh-products/orders/1")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String getPurchaseOrder(ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/orders/1")
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

            postInboundOrder(inboundOrderDTO, status().isCreated(), STOCK_MANAGER_JWT);
            postPurchaseOrder(cartDTO, status().isCreated(), CUSTOMER_JWT);
            init = true;
        }
    }

    @Test
    void registerValidPurchaseOrder() throws Exception {

        Cart cart = cartRepository.findById(1L).orElse(new Cart());

        assertAll(
                () -> assertEquals(BigDecimal.valueOf(840).setScale(2), cart.getTotalCart()),
                () -> assertEquals(1, cart.getCustomer().getId())
        );

    }
    @Test
    void registerInvalidPurchaseOrder() throws Exception {

        Cart cart = cartRepository.findById(1L).orElse(new Cart());

        assertAll(
                () -> assertEquals(BigDecimal.valueOf(840).setScale(2), cart.getTotalCart()),
                () -> assertEquals(1, cart.getCustomer().getId())
        );

    }

    @Test
    void updatePurchaseOrder() throws Exception {

        putPurchaseOrder(status().isOk(), CUSTOMER_JWT);
        Cart cart = cartRepository.findById(1L).orElse(new Cart());

        assertEquals("PURCHASE", cart.getOrderStatus().getCartStatusCode().name());

    }

    @Test
    void validPurchaseOrder() throws  Exception{

        CartWithStatusDTO cartWithStatusDTO = objectMapper.readValue(getPurchaseOrder(status().isOk(), CUSTOMER_JWT),
                new TypeReference<CartWithStatusDTO>() {
        });

        Cart cart = cartRepository.findById(1L).orElse(new Cart());

        assertEquals( cartWithStatusDTO.getCartStatusCode(),cart.getOrderStatus().getCartStatusCode());
    }


}
