package com.myf.controller;

import com.myf.service.IHelloWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author myf
 * @since 2022-07-27
 */
@RestController
@RequestMapping("/hello-word")
@RequiredArgsConstructor
public class HelloWordController {

    private final IHelloWordService helloWordService;

    @GetMapping("/hello")
    public String Hello(){
        return helloWordService.getById(1).getName();
    }

}
