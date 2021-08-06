package kr.co.gochang.controller.api;

import kr.co.gochang.service.api.ContentApiLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {
    @Autowired
    private ContentApiLogicService contentApiLogicService;

    @RequestMapping(value = {"", "/main", "/search"})
    public ModelAndView main(){
        return new ModelAndView("pages/main");
    }

    @RequestMapping(value = "/contents/{id}")
    public ModelAndView content(@PathVariable Long id){
        return new ModelAndView("pages/content")
                .addObject("prevTitle", contentApiLogicService.getContentTitle(id+1))
                .addObject("nextTitle", contentApiLogicService.getContentTitle(id-1));
    }

}
