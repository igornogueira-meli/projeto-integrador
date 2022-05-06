package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.dto.BatchStockDueDateDTO;
import br.com.meli.projetointegrador.dto.InboundOrderDTO;
import br.com.meli.projetointegrador.model.request.LoginRequest;
import br.com.meli.projetointegrador.model.response.JwtResponse;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DueDateIntegrationTests {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static boolean init = false;
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
                "            \"manufacturingDate\": \"2022-01-01\",\n" +
                "            \"manufacturingTime\": \"2022-01-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-03-27\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"productId\": 2,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": -10.5,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "            \"manufacturingDate\": \"2022-01-01\",\n" +
                "            \"manufacturingTime\": \"2022-01-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-05-27\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"productId\": 3,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": -10.5,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "            \"manufacturingDate\": \"2022-01-01\",\n" +
                "            \"manufacturingTime\": \"2022-01-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-06-27\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"productId\": 3,\n" +
                "            \"currentTemperature\": 25.5,\n" +
                "            \"minTemperature\": -10.5,\n" +
                "            \"initialQuantity\": 20,\n" +
                "            \"currentQuantity\": 20,\n" +
                "            \"manufacturingDate\": \"2022-01-01\",\n" +
                "            \"manufacturingTime\": \"2022-01-01T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-07-27\"\n" +
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

    private String getCheckBatchStockDueDate(String days, String sectionId,ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/due-date")
                .header("Authorization", "Bearer " + jwt)
                .param("days", days)
                .param("id", sectionId))
                .andExpect(resultMatcher)
                .andReturn();


        return response.getResponse().getContentAsString();
    }

    private String getCheckBatchStockDueDateWithCategory(String days, String category, String order, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/due-date/list")
                .header("Authorization", "Bearer " + jwt)
                .param("days", days)
                .param("category", category)
                .param("order", order)
        )
                .andExpect(resultMatcher)
                .andReturn();


        return response.getResponse().getContentAsString();
    }

    @BeforeEach
    void initialSetup() throws Exception {

        if (!init) {
            String signUpDTOStockManager = signUpStockManagerBody();
            signUpPost(status().isOk(), signUpDTOStockManager);

            LoginRequest loginBody = new LoginRequest("stockmanagertest", "abcd1234");
            String signInResponse = signInPost(loginBody, status().isOk());
            JwtResponse jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {});
            STOCK_MANAGER_JWT = jwtResponse.getToken();

            String inboundOrderString = getStandardInboundOrder();
            InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});

            postInboundOrder(inboundOrderDTO, status().isCreated(), STOCK_MANAGER_JWT);
            init = true;
        }
    }

    @Test
    void getBatchStockByDueDate() throws Exception {

        String checkBatchStockDueDate = getCheckBatchStockDueDate("64", "1",status().isOk(), STOCK_MANAGER_JWT);

        List<BatchStockDueDateDTO> batchStockDueDateDTOList = objectMapper.readValue(checkBatchStockDueDate, new TypeReference<>() {});

        assertEquals(2, batchStockDueDateDTOList.size());
    }

    @Test
    void getBatchStockByDueDateWithCategory() throws Exception {

        String checkBatchStockDueDateWithCategory1 = getCheckBatchStockDueDateWithCategory("95", "FROZEN", "asc",
                status().isOk(), STOCK_MANAGER_JWT);
        String checkBatchStockDueDateWithCategory2 = getCheckBatchStockDueDateWithCategory("95", "FROZEN", "desc",
                status().isOk(), STOCK_MANAGER_JWT);

        List<BatchStockDueDateDTO> batchStockDueDateDTOList1 = objectMapper.readValue(checkBatchStockDueDateWithCategory1, new TypeReference<>() {});
        List<BatchStockDueDateDTO> batchStockDueDateDTOList2 = objectMapper.readValue(checkBatchStockDueDateWithCategory2, new TypeReference<>() {});

        assertEquals(3, batchStockDueDateDTOList1.size());
        assertEquals(3, batchStockDueDateDTOList2.size());

        assertEquals(2, batchStockDueDateDTOList1.get(0).getId());
        assertEquals(4, batchStockDueDateDTOList2.get(0).getId());

    }
}
