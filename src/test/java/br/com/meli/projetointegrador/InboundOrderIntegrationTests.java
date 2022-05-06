package br.com.meli.projetointegrador;


import br.com.meli.projetointegrador.dto.*;
import br.com.meli.projetointegrador.model.Batch;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class InboundOrderIntegrationTests {


    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BatchRepository batchRepository;

    private static boolean init = false;
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

    private String getStandardUpdateInboundOrder(){
        return "{\n" +
                "    \"inboundOrderId\": 1,\n" +
                "    \"orderDate\": \"2022-01-10\",\n" +
                "    \"batchStock\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"manufacturingDate\": \"2022-11-09\",\n" +
                "            \"manufacturingTime\": \"2022-11-09T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-11-15\"\n" +
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

    private String putInboundOrder(InboundOrderPutDTO inboundOrderPutDTO, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(put("/api/v1/fresh-products/inboundorder")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(inboundOrderPutDTO)))
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
    void registerValidInboundOrder() throws Exception {

        Batch batch = batchRepository.findBySectionId(6L);

        assertAll(
                () -> assertEquals(25.5, batch.getCurrentTemperature()),
                () -> assertEquals(20, batch.getInitialQuantity())
        );

    }

    @Test
    void updateValidInboundOrder() throws Exception {

        String inboundOrderUpdateString = getStandardUpdateInboundOrder();
        InboundOrderPutDTO inboundOrderPutDTO = objectMapper.readValue(inboundOrderUpdateString, new TypeReference<>() {});

        putInboundOrder(inboundOrderPutDTO, status().isCreated(), STOCK_MANAGER_JWT);
        Batch batch = batchRepository.findById(inboundOrderPutDTO.getBatchStock().get(0).getId()).orElse(new Batch());

        assertEquals(LocalDate.of(2022, 11, 9), batch.getManufacturingDate());

    }

    @Test
    void InvalidSectionAndWarehouse() throws Exception {

        String inboundOrderString = getStandardInboundOrder();
        InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});

        SectionDTO inboundSectionDto = inboundOrderDTO.getSection();
        inboundSectionDto.setSectionCode(666L);
        inboundOrderDTO.setSection(inboundSectionDto);

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest(), STOCK_MANAGER_JWT);
        ErrorDTO errorDtoSection = objectMapper.readValue(responseStr, new TypeReference<>() {});

        assertEquals("Section 666 does not exists!", errorDtoSection.getDescription());


        inboundSectionDto.setSectionCode(6L);
        inboundSectionDto.setWarehouseCode(777L);
        inboundOrderDTO.setSection(inboundSectionDto);

        responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest(), STOCK_MANAGER_JWT);
        ErrorDTO errorDtoWarehouse = objectMapper.readValue(responseStr, new TypeReference<>() {});

        assertEquals("Warehouse 777 does not exists!", errorDtoWarehouse.getDescription());

    }

    @Test
    void InexistentProduct() throws Exception {

        String inboundOrderString = getStandardInboundOrder();
        InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});

        List<BatchStockDTO> batchStockDTOList = inboundOrderDTO.getBatchStock();
        BatchStockDTO batchStockDTO = batchStockDTOList.get(0);

        batchStockDTO.setProductId(111L);
        batchStockDTOList.remove(0);
        batchStockDTOList.add(batchStockDTO);

        inboundOrderDTO.setBatchStock(batchStockDTOList);

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest(), STOCK_MANAGER_JWT);
        ErrorDTO errorDtoSection = objectMapper.readValue(responseStr, new TypeReference<>() {});

        assertEquals("Product 111 does not exists!", errorDtoSection.getDescription());

    }

    @Test
    void SectionAndBatchMismatch() throws Exception {
        String inboundOrderString = getStandardInboundOrder();
        InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});

        List<BatchStockDTO> batchStockDTOList = inboundOrderDTO.getBatchStock();
        BatchStockDTO batchStockDTO = batchStockDTOList.get(0);

        batchStockDTO.setMinTemperature(-1.0);
        batchStockDTOList.remove(0);
        batchStockDTOList.add(batchStockDTO);

        inboundOrderDTO.setBatchStock(batchStockDTOList);

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest(), STOCK_MANAGER_JWT);
        ErrorDTO errorDtoSection = objectMapper.readValue(responseStr, new TypeReference<>() {});

        assertEquals("Batch does not match this section!", errorDtoSection.getDescription());
    }

    @Test
    void SectionAndWarehouseMismatch() throws Exception {
        String inboundOrderString = getStandardInboundOrder();
        InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});

        SectionDTO inboundSectionDto = inboundOrderDTO.getSection();
        inboundSectionDto.setSectionCode(2L);
        inboundSectionDto.setWarehouseCode(2L);
        inboundOrderDTO.setSection(inboundSectionDto);

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest(), STOCK_MANAGER_JWT);
        ErrorDTO errorDtoSection = objectMapper.readValue(responseStr, new TypeReference<>() {});

        assertEquals("The informed Section does not match with informed Warehouse!", errorDtoSection.getDescription());

    }

    @Test
    void SectionUnavailableSpace() throws Exception {
        String inboundOrderString = getStandardInboundOrder();
        InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});

        SectionDTO inboundSectionDto = inboundOrderDTO.getSection();
        inboundSectionDto.setSectionCode(4L);
        inboundOrderDTO.setSection(inboundSectionDto);

        List<BatchStockDTO> batchStockDTOList = inboundOrderDTO.getBatchStock();
        BatchStockDTO batchStockDTO = batchStockDTOList.get(0);
        batchStockDTO.setMinTemperature(-1.0);
        batchStockDTOList.remove(0);

        for (int i = 0; i < 101; i++) {
            batchStockDTOList.add(batchStockDTO);
        }

        inboundOrderDTO.setBatchStock(batchStockDTOList);

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest(), STOCK_MANAGER_JWT);
        ErrorDTO errorDtoSection = objectMapper.readValue(responseStr, new TypeReference<>() {});

        assertEquals("Section has no available space.", errorDtoSection.getDescription());
    }
}
