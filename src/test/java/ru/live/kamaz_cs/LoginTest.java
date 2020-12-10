package ru.live.kamaz_cs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.live.kamaz_cs.controller.MainController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class) // в этой аннотации мы указываем окружение, в котором будет стартовать тест
@SpringBootTest
@AutoConfigureMockMvc
// spring пытается автоматически создать структуру классов, которую подменяет слой MVC из фреймворка spring
// это дает более удобный метод тестирования приложения, все будет происходить в фейковом окружении - это чуть быстрее, проще и контролиремо
// и как результат мы не должны создавать RestTemplate, мы можем просто использовать Mock версию MVC слоя
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainController controller;

    /*@Test
    public void contextLoads() {
        assertThat(controller).isNotNull(); // проверка на наличие контекста на месте (в контроллере)
    }*/

    @Test
    public void contextLoads() throws Exception {
        this.mockMvc.perform(get("/")) // методом perform мы хотим сделать тест get запроса на главную страницу проекта
                .andDo(print()) // выводит результат теста в консоль
                .andExpect(status().isOk()) // andExpect - обертка assertThat(), которая позволяет сравнивать результат который возвращается тестируемым кодом с тем результатом, который мы ожидаем и бросить исключение, если что то идет не так
                .andExpect(content().string(containsString("Hello, amigo :))"))); // тест на наличие контента, а именно строки
    }

    @Test
    public void loginTest() throws Exception { // проверка на требования авторизации, если пользователь не авторизован
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void correctLogin() throws Exception { // проверка авторизации пользователя
        this.mockMvc.perform(formLogin().user("admin").password("123")) // builder для обращения к формы логина в spring security
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void badCredentials() throws Exception { // проверка на неправильные данные пользователя
        this.mockMvc.perform(post("/login").param("user", "Roman"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}