package uk.co.glamoor.gateway.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class AppSettingsStatus {
	
	private String version;
	private int settingsVersion;
	private boolean mandatoryUpdate;
	private String latestVersion;

}
