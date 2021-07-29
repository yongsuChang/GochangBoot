package kr.co.gochang.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {

    @RequestMapping(value = {"", "/main", "/search"})
    public ModelAndView main(){
        return new ModelAndView("/pages/main");
    }

}
