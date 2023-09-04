package com.leucine.streem.util;

import com.leucine.streem.constant.Type;
import com.leucine.streem.model.helper.parameter.*;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public final class ParameterUtils {
  public static final Map<Type.Parameter, Class> classMap;

  static {
    Map<Type.Parameter, Class> classMapTemp = new Hashtable<>();
    classMapTemp.put(Type.Parameter.CALCULATION, CalculationParameter.class);
    classMapTemp.put(Type.Parameter.CHECKLIST, ChecklistParameter.class);
    classMapTemp.put(Type.Parameter.DATE, DateParameter.class);
    classMapTemp.put(Type.Parameter.MEDIA, MediaParameter.class);
    classMapTemp.put(Type.Parameter.FILE_UPLOAD, MediaParameter.class);
    classMapTemp.put(Type.Parameter.MULTISELECT, MultiSelectParameter.class);
    classMapTemp.put(Type.Parameter.NUMBER, NumberParameter.class);
    classMapTemp.put(Type.Parameter.RESOURCE, ResourceParameter.class);
    classMapTemp.put(Type.Parameter.SIGNATURE, SignatureParameter.class);
    classMapTemp.put(Type.Parameter.SINGLE_SELECT, SingleSelectParameter.class);
    classMapTemp.put(Type.Parameter.SHOULD_BE, ShouldBeParameter.class);
    classMapTemp.put(Type.Parameter.MULTI_LINE, TextParameter.class);
    classMapTemp.put(Type.Parameter.YES_NO, YesNoParameter.class);
    classMap = Collections.unmodifiableMap(classMapTemp);
  }

  private ParameterUtils() {
  }

  public static Class getClassForParameter(Type.Parameter parameter) {
    return classMap.get(parameter);
  }
}
