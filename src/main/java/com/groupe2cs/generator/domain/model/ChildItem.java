package com.groupe2cs.generator.domain.model;

import com.groupe2cs.generator.shared.Utils;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildItem {
	private String childModel;
	private String childTable;
	private String childTitle;

	//used in template
	public String getChildModelLowerCase() {
		return Utils.unCapitalize(childModel);
	}
}
