package com.project.palette.controller;

import com.project.palette.entity.Field;
import com.project.palette.service.OAuth2MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final OAuth2MemberService oAuth2MemberService;
    @GetMapping("/member/field")
    public String updateMemberFieldForm(Model model) {
        model.addAttribute("fieldValues", Field.values());
        return "field";
    }

    @PostMapping("/member/field")
    public String updateMemberField(@RequestParam("selectedFiled") Field selectedField) {
        log.info("filedinfo ={} ",selectedField);
        oAuth2MemberService.updateMemberField(selectedField);

        return "redirect:/";
    }

}
