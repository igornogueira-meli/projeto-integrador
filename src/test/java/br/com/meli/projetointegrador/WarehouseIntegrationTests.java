package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.dto.*;
import br.com.meli.projetointegrador.model.request.LoginRequest;
import br.com.meli.projetointegrador.model.response.JwtResponse;
import br.com.meli.projetointegrador.repository.BatchRepository;
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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WarehouseIntegrationTests {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BatchRepository batchRepository;

    private static boolean init = false;
    private static String STOCK_MANAGER_JWT = "";
    private static String SELLER_JWT = "";
    private static String CUSTOMER_JWT = "";

    private String getStandardInboundOrder() {
        return "{\n" +
                "    \"orderNumber\": 2,\n" +
                "    \"orderDate\": \"2020-01-05\",\n" +
                "    \"section\": {\n" +
                "        \"sectionCode\": 6,\n" +
                "        \"warehouseCode\": 2\n" +
                "    },\n" +
                "    \"batchStock\": [\n" +
                "        {\n" +
                "            \"productId\": 1,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": 10.6,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "            \"manufacturingDate\": \"2022-01-01\",\n" +
                "            \"manufacturingTime\": \"2022-01-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-02-02\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"productId\": 2,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": 10.5,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "            \"manufacturingDate\": \"2022-01-01\",\n" +
                "            \"manufacturingTime\": \"2022-01-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-02-02\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"productId\": 2,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": 10.7,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "            \"manufacturingDate\": \"2022-01-01\",\n" +
                "            \"manufacturingTime\": \"2022-01-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-02-02\"\n" +
                "        }\n" +
                "    ]\n" +
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
                "    \"warehouse_id\": 2,\n" +
                "    \"role\" : [\"manager\"]\n" +
                "}";
    }

    public String signUpSellerBody() {
        return "{\n" +
                "    \"name\" : \"sellertest\",\n" +
                "    \"username\" : \"sellertest\",\n" +
                "    \"email\" : \"sellertest@teste.com.br\",\n" +
                "    \"cpf\" : \"000-000-000-02\",\n" +
                "    \"address\" : \"Rua 1\",\n" +
                "    \"password\" : \"abcd1234\",\n" +
                "    \"role\" : [\"seller\"]\n" +
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

    private String getProductsByWarehouse(Long productId, ResultMatcher resultMatcher, String jwt) throws Exception{
        MvcResult result = mockmvc.perform(get("/api/v1/fresh-products/warehouse")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .param("productId", String.valueOf(productId)))
                .andExpect(resultMatcher)
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    @BeforeEach
    void initialSetup() throws Exception {
        if (!init) {
            String signUpDTOStockManager = signUpStockManagerBody();
            signUpPost(status().isOk(), signUpDTOStockManager);

            String signUpDTOSeller = signUpSellerBody();
            signUpPost(status().isOk(), signUpDTOSeller);

            String signUpDTOCustomer = signUpCustomerBody();
            signUpPost(status().isOk(), signUpDTOCustomer);

            LoginRequest loginBody = new LoginRequest("stockmanagertest", "abcd1234");
            String signInResponse = signInPost(loginBody, status().isOk());
            JwtResponse jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {});
            STOCK_MANAGER_JWT = jwtResponse.getToken();

            loginBody = new LoginRequest("sellertest", "abcd1234");
            signInResponse = signInPost(loginBody, status().isOk());
            jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {});
            SELLER_JWT = jwtResponse.getToken();

            loginBody = new LoginRequest("customertest", "abcd1234");
            signInResponse = signInPost(loginBody, status().isOk());
            jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {});
            CUSTOMER_JWT = jwtResponse.getToken();

            init = true;
        }
    }

    @Test
    void getValidProductStockByWarehouse() throws Exception {
        String inboundOrderString = getStandardInboundOrder();
        InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});
        postInboundOrder(inboundOrderDTO, status().isCreated(), STOCK_MANAGER_JWT);

        String resultStr = getProductsByWarehouse(1L, status().isOk(), STOCK_MANAGER_JWT);
        ProductStockWarehouseDTO productByWarehouse = objectMapper.readValue(resultStr, new TypeReference<>() {});

        List<QuantityByWarehouseDTO> quantityByWarehouseDTOList = Collections.singletonList(new QuantityByWarehouseDTO(BigDecimal.valueOf(20), BigInteger.valueOf(2)));
        ProductStockWarehouseDTO productByWarehouseTest = new ProductStockWarehouseDTO(1L, quantityByWarehouseDTOList);

        assertEquals(productByWarehouse.getQuantityByWarehouseList().toString(), productByWarehouseTest.getQuantityByWarehouseList().toString());
    }

    @Test
    void getInvalidProductStockByWarehouse() throws Exception {
        getProductsByWarehouse(333L, status().isForbidden(), CUSTOMER_JWT);
        getProductsByWarehouse(333L, status().isForbidden(), SELLER_JWT);
        String resultStr = getProductsByWarehouse(333L, status().isNotFound(), STOCK_MANAGER_JWT);
        ErrorDTO errorDTO = objectMapper.readValue(resultStr, new TypeReference<>() {});

        assertEquals("The Product ID: 333 does not exists!", errorDTO.getDescription());
    }


}
