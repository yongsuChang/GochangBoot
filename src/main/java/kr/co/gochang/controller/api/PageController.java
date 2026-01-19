package kr.co.gochang.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    // SPA fallback - forward all non-API routes to React's index.html
    // This allows React Router to handle client-side routing
    @RequestMapping(value = { "", "/", "/main", "/search", "/contents/**" })
    public String forward() {
        return "forward:/index.html";
    }
}
