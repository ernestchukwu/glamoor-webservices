package uk.co.glamoor.gateway.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import uk.co.glamoor.gateway.config.AppConfig;
import uk.co.glamoor.gateway.config.ClientConfig;
import uk.co.glamoor.gateway.config.bookings.BookingsConfig;
import uk.co.glamoor.gateway.config.stylists.StylistsConfig;

import java.util.Map;

@Data
public class AppSettings {

	private String appName;
	private String appUrl;

	private AppConfig.Images images;
	private String supportEmailAddress;
	private boolean showPostLikeCount;
	private int defaultMaxDistance;
	private double defaultLongitude;
	private double defaultLatitude;
	private int futureBookingDaysExtent;
	private Map<String, String> deleteAccountReasons;
	private String staticResourcesPath;
	private int timeSlotReservationDurationSeconds;
	private int timeSlotHeartbeatCadenceSeconds;

	private StylistsSettings stylistsSettings = new StylistsSettings();
	private BookingsSettings bookingsSettings = new BookingsSettings();

	private String version;
	private int settingsVersion;
	private boolean mandatoryUpdate;
	private String latestVersion;
	private double defaultMapZoom;
	private ClientConfig.SignInOptions signInOptions;

	public void applyClientSettings(ClientConfig.ClientSettings clientSettings) {
		this.version = clientSettings.getVersion();
		this.settingsVersion = clientSettings.getSettingsVersion();
		this.mandatoryUpdate = clientSettings.isMandatoryUpdate();
		this.latestVersion = clientSettings.getLatestVersion();
		this.defaultMapZoom = clientSettings.getDefaultMapZoom();
		this.signInOptions = clientSettings.getSignInOptions();
	}


	public void applyAppConfig(AppConfig appConfig) {
		this.appName = appConfig.getAppName();
		this.appUrl = appConfig.getAppUrl();
		this.images = appConfig.getImages();
		this.supportEmailAddress = appConfig.getSupportEmailAddress();
		this.showPostLikeCount = appConfig.isShowPostLikeCount();
		this.defaultMaxDistance = appConfig.getDefaultMaxDistance();
		this.defaultLongitude = appConfig.getDefaultLongitude();
		this.defaultLatitude = appConfig.getDefaultLatitude();
		this.deleteAccountReasons = appConfig.getDeleteAccountReasons();
		this.futureBookingDaysExtent = appConfig.getFutureBookingDaysExtent();
		this.staticResourcesPath = appConfig.getStaticResourcesPath();
		this.timeSlotReservationDurationSeconds = appConfig.getTimeSlotReservationDurationSeconds();
		this.timeSlotHeartbeatCadenceSeconds = appConfig.getTimeSlotHeartbeatCadenceSeconds();
	}

	public void applyStylistsConfig (StylistsConfig stylistsConfig) {
		stylistsSettings.setServiceRequestBatchSize(stylistsConfig.getServiceRequestBatchSize());
		stylistsSettings.setAddonRequestBatchSize(stylistsConfig.getAddonRequestBatchSize());
		stylistsSettings.setStylistsRequestBatchSize(stylistsConfig.getStylistsRequestBatchSize());
		stylistsSettings.setServiceCategoryRequestBatchSize(stylistsConfig.getServiceCategoryRequestBatchSize());
		stylistsSettings.setServiceSpecificationRequestBatchSizeMini(stylistsConfig.getServiceSpecificationRequestBatchSizeMini());
		stylistsSettings.setServiceSpecificationRequestBatchSize(stylistsConfig.getServiceSpecificationRequestBatchSize());
		stylistsSettings.setStylistsRequestBatchSizeForHomeView(stylistsConfig.getStylistsRequestBatchSizeForHomeView());
	}

	public void applyBookingsConfig(BookingsConfig bookingsConfig) {
		bookingsSettings.setBookingsRequestBatchSize(bookingsConfig.getBookingsRequestBatchSize());
		bookingsSettings.setMessagesRequestBatchSize(bookingsConfig.getMessagesRequestBatchSize());
	}

}

