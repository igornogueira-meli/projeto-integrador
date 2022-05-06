package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.dto.BatchStockDTO;
import br.com.meli.projetointegrador.dto.InboundOrderDTO;
import br.com.meli.projetointegrador.dto.ProductDTOi;
import br.com.meli.projetointegrador.dto.ProductDTOiImpl;
import br.com.meli.projetointegrador.model.Product;
import br.com.meli.projetointegrador.model.request.LoginRequest;
import br.com.meli.projetointegrador.model.response.JwtResponse;
import br.com.meli.projetointegrador.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ProductIntegrationTest {
    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private static boolean init = false;
    private static String CUSTOMER_JWT = "";
    private static String STOCK_MANAGER_JWT = "";

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
                "            \"minTemperature\": 10.5,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "            \"manufacturingDate\": \"2022-01-01\",\n" +
                "            \"manufacturingTime\": \"2022-01-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-02-02\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
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

    private String getAllProduct(ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String getAllProductByCategory(ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/list/")
                .header("Authorization", "Bearer " + jwt)
                .param("category","FS"))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
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
                "    \"warehouse_id\": 2,\n" +
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
            InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {
            });

            postInboundOrder(inboundOrderDTO, status().isForbidden(), CUSTOMER_JWT);
            postInboundOrder(inboundOrderDTO, status().isCreated(), STOCK_MANAGER_JWT);

            init = true;
        }
    }

    @Test
    void existentProductList() throws Exception {

        List<ProductDTOi> productDTOis = productRepository.findAllByBatchListExists();

        List<ProductDTOiImpl> productDTOiList = objectMapper.readValue(getAllProduct(status().isOk(), CUSTOMER_JWT),
                new TypeReference<>() {
        });

        assertEquals(productDTOis.size(), productDTOiList.size());

    }

    @Test
    void validProductListByCategory() throws Exception {

        List<ProductDTOi> productDTOis = productRepository.findAllByBatchListExistsBySection("FRESH");

        List<ProductDTOiImpl> productDTOiList = objectMapper.readValue(getAllProductByCategory(status().isOk(), CUSTOMER_JWT),
                new TypeReference<>() {  });

        assertEquals(productDTOis.size(), productDTOiList.size());

    }

}
