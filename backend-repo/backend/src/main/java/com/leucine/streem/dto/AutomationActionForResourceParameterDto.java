package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Used in Automation Entity "action details" where the property that is
 * used in the automation belongs to the object type of one of the resource parameter of the checklist
 * e.g. Object
 * {
 *   "sortOrder": 1,
 *   "parameterId": "256988417034518528",
 *   "propertyId": "62c0465a3eb5e038241db884",
 *   "propertyInputType": "NUMBER",
 *   "propertyExternalId": "disinfectantLotAvailableQuantity",
 *   "propertyDisplayName": "Disinfectant Lot Available Quantity",
 *   "referencedParameterId": "256980989656137728",
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutomationActionForResourceParameterDto extends AutomationSetPropertyBaseDto implements Serializable {
  private static final long serialVersionUID = 6365478547069540236L;

  private String objectTypeDisplayName;
  private Integer sortOrder;
  private String parameterId; // Number/Parameter/Calculation Parameter having the value, which then is used for automation
  private String referencedParameterId; // Resource parameter ID
}
