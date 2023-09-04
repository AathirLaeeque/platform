package com.leucine.streem.email.util;

import com.leucine.streem.email.exception.FreeMarkerException;
import freemarker.template.Template;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

public final class FreeMarkerUtil {
  private FreeMarkerUtil() {}

  public static String processTemplate(Template template, Object model) throws FreeMarkerException {
    try {
      return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    } catch (Exception ex) {
      throw new FreeMarkerException("Failed to process template " + template.getName(), ex);
    }
  }
}
