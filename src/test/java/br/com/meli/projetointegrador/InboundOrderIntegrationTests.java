package br.com.meli.projetointegrador;


import br.com.meli.projetointegrador.dto.*;
import br.com.meli.projetointegrador.model.Batch;
import br.com.meli.projetointegrador.repository.BatchRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class InboundOrderIntegrationTests {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BatchRepository batchRepository;


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
                "    \"inboundOrderId\": 2,\n" +
                "    \"orderDate\": \"2022-01-10\",\n" +
                "    \"batchStock\": [\n" +
                "        {\n" +
                "            \"id\": 5,\n" +
                "            \"manufacturingDate\": \"2022-11-09\",\n" +
                "            \"manufacturingTime\": \"2022-11-09T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-11-15\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    private String postInboundOrder(InboundOrderDTO inboundOrderDTO, ResultMatcher resultMatcher) throws Exception {

        MvcResult response = mockmvc.perform(post("/api/v1/fresh-products/inboundorder")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(inboundOrderDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String putInboundOrder(InboundOrderPutDTO inboundOrderPutDTO, ResultMatcher resultMatcher) throws Exception {

        MvcResult response = mockmvc.perform(put("/api/v1/fresh-products/inboundorder")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(inboundOrderPutDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    @Test
    void registerValidInboundOrder() throws Exception {

        String inboundOrderString = getStandardInboundOrder();
        InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});

        postInboundOrder(inboundOrderDTO, status().isCreated());
        Batch batch = batchRepository.findBySectionId(6L);

        assertAll(
                () -> assertEquals(25.5, batch.getCurrentTemperature()),
                () -> assertEquals(20, batch.getInitialQuantity())
        );

    }

    @Test
    void updateValidInboundOrder() throws Exception {

        String inboundOrderString = getStandardUpdateInboundOrder();
        InboundOrderPutDTO inboundOrderPutDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});

        putInboundOrder(inboundOrderPutDTO, status().isCreated());
        Batch batch = batchRepository.findBySectionId(6L);

        assertEquals(LocalDate.of(2022, 11, 9), batch.getManufacturingDate());

    }

    @Test
    void InvalidSectionAndWarehouse() throws Exception {

        String inboundOrderString = getStandardInboundOrder();
        InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});

        SectionDTO inboundSectionDto = inboundOrderDTO.getSection();
        inboundSectionDto.setSectionCode(666L);
        inboundOrderDTO.setSection(inboundSectionDto);

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest());
        ErrorDTO errorDtoSection = objectMapper.readValue(responseStr, new TypeReference<>() {});

        assertEquals("Section 666 does not exists!", errorDtoSection.getDescription());


        inboundSectionDto.setSectionCode(6L);
        inboundSectionDto.setWarehouseCode(777L);
        inboundOrderDTO.setSection(inboundSectionDto);

        responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest());
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

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest());
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

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest());
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

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest());
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

        String responseStr = postInboundOrder(inboundOrderDTO, status().isBadRequest());
        ErrorDTO errorDtoSection = objectMapper.readValue(responseStr, new TypeReference<>() {});

        assertEquals("Section has no available space.", errorDtoSection.getDescription());
    }
}
