package com.leucine.streem.controller;

import com.leucine.streem.dto.response.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/challenge-questions")
public interface IChallengeQuestionController {
  @GetMapping
  @ResponseBody
  Response<Object> getAll(@RequestParam(name = "archived", defaultValue = "false", required = false) boolean archived);

}
