package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.dto.BatchStockDueDateDTO;
import br.com.meli.projetointegrador.dto.CarrierDTO;
import br.com.meli.projetointegrador.model.Carrier;
import br.com.meli.projetointegrador.model.request.LoginRequest;
import br.com.meli.projetointegrador.model.response.JwtResponse;
import br.com.meli.projetointegrador.repository.CarrierRepository;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CarrierIntegrationTest {


    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarrierRepository carrierRepository;

    private static boolean init = false;
    private static String STOCK_MANAGER_JWT = "";

    private String getStandardCarrier() {
        return "{\n" +
                "    \"licensePlate\": \"ABC-DEF\",\n" +
                "    \"brand\": \"Fiat\",\n" +
                "    \"model\": \"Fiorino\",\n" +
                "    \"warehouseId\": 1\n" +
                "}";
    }

    private String getStandardCarrier2() {
        return "{\n" +
                "    \"licensePlate\": \"DEF-ADF\",\n" +
                "    \"brand\": \"Fiat\",\n" +
                "    \"model\": \"Montana\",\n" +
                "    \"warehouseId\": 2\n" +
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

    private String postCarrier(CarrierDTO carrierDTO, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(post("/api/v1/fresh-products/carriers")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(carrierDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String getAllCarriers(ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/carriers")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String getCarrierByPlate(ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/carriers/ABC-DEF")
                .header("Authorization", "Bearer " + jwt))
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
            JwtResponse jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {
            });

            STOCK_MANAGER_JWT = jwtResponse.getToken();

            String carrierString = getStandardCarrier();
            String carrierString2 = getStandardCarrier2();
            CarrierDTO carrierDTO = objectMapper.readValue(carrierString, new TypeReference<CarrierDTO>() {
            });
            CarrierDTO carrierDTO2 = objectMapper.readValue(carrierString2, new TypeReference<CarrierDTO>() {
            });


            postCarrier(carrierDTO, status().isCreated(), STOCK_MANAGER_JWT);
            postCarrier(carrierDTO2, status().isCreated(), STOCK_MANAGER_JWT);

            init = true;
        }
    }

    @Test
    void registerValidCarrier() throws Exception {

        Carrier carrier = carrierRepository.findById(1L).orElse(new Carrier());

        assertAll(
                () -> assertEquals("ABC-DEF", carrier.getLicensePlate()),
                () -> assertEquals("Fiorino", carrier.getModel())
        );
    }

    @Test
    void getAllCarriers() throws Exception {

        String AllCarriers = getAllCarriers(status().isOk(), STOCK_MANAGER_JWT);

        List<CarrierDTO> carrierDTOS = objectMapper.readValue(AllCarriers, new TypeReference<>() {});

        assertEquals(2, carrierDTOS.size());
    }

    @Test
    void getCarrierByPlate() throws Exception {

        String carrier = getCarrierByPlate(status().isOk(), STOCK_MANAGER_JWT);

        CarrierDTO carrierDTO = objectMapper.readValue(carrier, new TypeReference<>() {});

        assertEquals("Fiorino", carrierDTO.getModel());
    }








}
