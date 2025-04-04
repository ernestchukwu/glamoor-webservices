package uk.co.glamoor.customers.model;

import lombok.Data;
import uk.co.glamoor.customers.enums.DarkTheme;

@Data
public class UserSettings {
	
	DarkTheme darkTheme = DarkTheme.OFF;

}

