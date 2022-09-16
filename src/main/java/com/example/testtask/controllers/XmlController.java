package com.example.testtask.controllers;

import com.example.testtask.services.XmlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class XmlController {

    private final XmlService xmlService;

    @GetMapping
    public String healthCheck() {
        return "Ok";
    }


    @PostMapping
    public String xmlToJson(@RequestBody String xmlBody, @RequestParam(defaultValue = "") String params) {
        return xmlService.xmlToJson(xmlBody, params);
    }


}
