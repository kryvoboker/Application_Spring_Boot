package ru.live.kamaz_cs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.live.kamaz_cs.controller.MainController;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class) // в этой аннотации мы указываем окружение, в котором будет стартовать тест
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("admin") // это аннотация нужна для того, чтобы тест mainPageTest() не рухнул, так как для выполнения этого теста пользователь должен быть авторизован
// эту аннотацию можно ставить для работы с классом полностью или только для работы с конкретным методом
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainController controller; // мы заинжектили MainController потому что мы его тестим

    @Test
    public void mainPageTest() throws Exception { // для работы этого теста вверху влепили аннотацию @WithUserDetails("admin")
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("normalize-space(//*[@id='navbarSupportedContent']/div)").string("admin"));
    }

}
