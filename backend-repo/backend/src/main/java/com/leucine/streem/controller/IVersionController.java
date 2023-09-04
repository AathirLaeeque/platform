package com.leucine.streem.controller;

import com.leucine.streem.dto.ProjectVersionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version.json")
public interface IVersionController {
  @GetMapping
  @ResponseBody
  ProjectVersionDto get();
}